DROP TRIGGER IF EXISTS trg_prevent_car_deletion_with_works ON cars;
DROP FUNCTION IF EXISTS prevent_car_deletion_with_works();

-- Создаем улучшенную функцию триггера
CREATE OR REPLACE FUNCTION prevent_car_deletion_with_works()
RETURNS TRIGGER 
LANGUAGE plpgsql
AS $$
DECLARE
    works_count INTEGER;
BEGIN
    -- Проверяем, есть ли работы для этого автомобиля
    SELECT COUNT(*) INTO works_count 
    FROM works 
    WHERE car_id = OLD.id;
    
    -- Если есть работы, запрещаем удаление
    IF works_count > 0 THEN
        RAISE EXCEPTION 'Нельзя удалить автомобиль "%" (номер: %), так как по нему есть % выполненных работ', 
              OLD.mark, OLD.num, works_count;
    END IF;
    
    RETURN OLD;
END;
$$;

-- Создаем триггер
CREATE TRIGGER trg_prevent_car_deletion_with_works
    BEFORE DELETE ON cars
    FOR EACH ROW
    EXECUTE FUNCTION prevent_car_deletion_with_works();