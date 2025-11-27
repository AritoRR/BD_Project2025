-- 1. Хранимая процедура для общей стоимости обслуживания отечественных и импортных автомобилей
CREATE OR REPLACE FUNCTION get_service_costs_by_origin(
    start_date DATE DEFAULT NULL,
    end_date DATE DEFAULT NULL
)
RETURNS TABLE (
    car_type TEXT,
    total_services BIGINT,
    total_cost NUMERIC
) 
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        CASE 
            WHEN c.is_foreign THEN 'Иномарки'
            ELSE 'Отечественные'
        END::TEXT AS car_type,
        COUNT(w.id)::BIGINT AS total_services,
        COALESCE(SUM(
            CASE 
                WHEN c.is_foreign THEN s.cost_foreign
                ELSE s.cost_our 
            END
        ), 0)::NUMERIC AS total_cost
    FROM cars c
    LEFT JOIN works w ON w.car_id = c.id
    LEFT JOIN services s ON w.service_id = s.id
    WHERE 
        (start_date IS NULL OR w.date_work >= start_date) AND
        (end_date IS NULL OR w.date_work <= end_date)
    GROUP BY c.is_foreign
    ORDER BY c.is_foreign;
END;
$$;

-- 2. Хранимая процедура для топ-5 мастеров по количеству работ за месяц
CREATE OR REPLACE FUNCTION get_top_masters_by_month(
    month_year DATE DEFAULT NULL
)
RETURNS TABLE (
    master_name TEXT,
    unique_cars_serviced BIGINT,
    total_works BIGINT
) 
LANGUAGE plpgsql
AS $$
DECLARE
    target_month DATE;
BEGIN
    -- Если дата не указана, используем текущий месяц
    IF month_year IS NULL THEN
        target_month := DATE_TRUNC('month', CURRENT_DATE);
    ELSE
        target_month := DATE_TRUNC('month', month_year);
    END IF;
    
    RETURN QUERY
    SELECT 
        m.name::TEXT AS master_name,  -- Явное приведение к TEXT
        COUNT(DISTINCT w.car_id)::BIGINT AS unique_cars_serviced,
        COUNT(w.id)::BIGINT AS total_works
    FROM masters m
    JOIN works w ON w.master_id = m.id
    WHERE DATE_TRUNC('month', w.date_work) = target_month
    GROUP BY m.id, m.name
    ORDER BY unique_cars_serviced DESC, total_works DESC
    LIMIT 5;
END;
$$;

-- 3. Дополнительная процедура для удобства - общая статистика по автомобилям
CREATE OR REPLACE FUNCTION get_car_service_statistics()
RETURNS TABLE (
    car_mark TEXT,
    car_number TEXT,
    car_type TEXT,
    total_services BIGINT,
    total_cost NUMERIC
) 
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        c.mark::TEXT AS car_mark,
        c.num::TEXT AS car_number,
        CASE 
            WHEN c.is_foreign THEN 'Иномарка'
            ELSE 'Отечественный'
        END::TEXT AS car_type,
        COUNT(w.id)::BIGINT AS total_services,
        COALESCE(SUM(
            CASE 
                WHEN c.is_foreign THEN s.cost_foreign
                ELSE s.cost_our 
            END
        ), 0)::NUMERIC AS total_cost
    FROM cars c
    LEFT JOIN works w ON w.car_id = c.id
    LEFT JOIN services s ON w.service_id = s.id
    GROUP BY c.id, c.mark, c.num, c.is_foreign
    ORDER BY total_cost DESC;
END;
$$;