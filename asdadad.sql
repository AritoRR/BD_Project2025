
-- 1. Триггер на вставку: не позволяет добавить автомобиль с уже существующим номером
CREATE OR REPLACE FUNCTION check_car_number_unique()
RETURNS TRIGGER 
LANGUAGE plpgsql
AS $$
BEGIN
    -- Проверяем, существует ли автомобиль с таким же номером (игнорируя регистр)
    IF EXISTS (SELECT 1 FROM cars WHERE UPPER(num) = UPPER(NEW.num) AND id != COALESCE(NEW.id, -1)) THEN
        RAISE EXCEPTION 'Автомобиль с номером % уже существует', NEW.num;
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_check_car_number_unique
    BEFORE INSERT OR UPDATE ON cars
    FOR EACH ROW
    EXECUTE FUNCTION check_car_number_unique();

-- 2. Триггер на модификацию: не позволяет изменить дату работы более чем на один день
CREATE OR REPLACE FUNCTION check_work_date_change()
RETURNS TRIGGER 
LANGUAGE plpgsql
AS $$
BEGIN
    -- Проверяем, изменилась ли дата более чем на 1 день
    IF OLD.date_work IS NOT NULL AND NEW.date_work IS NOT NULL THEN
        IF ABS(NEW.date_work - OLD.date_work) > 1 THEN
            RAISE EXCEPTION 'Нельзя изменять дату работы более чем на 1 день. Старая дата: %, новая дата: %', OLD.date_work, NEW.date_work;
        END IF;
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_check_work_date_change
    BEFORE UPDATE ON works
    FOR EACH ROW
    EXECUTE FUNCTION check_work_date_change();

-- 3. Триггер на удаление: при удалении автомобиля, если по нему были работы, откатывает транзакцию
CREATE OR REPLACE FUNCTION prevent_car_deletion_with_works()
RETURNS TRIGGER 
LANGUAGE plpgsql
AS $$
BEGIN
    -- Проверяем, есть ли работы для этого автомобиля
    IF EXISTS (SELECT 1 FROM works WHERE car_id = OLD.id) THEN
        RAISE EXCEPTION 'Нельзя удалить автомобиль с номером %, так как по нему есть выполненные работы', OLD.num;
    END IF;
    RETURN OLD;
END;
$$;

CREATE TRIGGER trg_prevent_car_deletion_with_works
    BEFORE DELETE ON cars
    FOR EACH ROW
    EXECUTE FUNCTION prevent_car_deletion_with_works();

-- 4. Дополнительный триггер: не позволяет добавить мастера, если их уже больше 10
CREATE OR REPLACE FUNCTION check_masters_limit()
RETURNS TRIGGER 
LANGUAGE plpgsql
AS $$
DECLARE
    masters_count INTEGER;
BEGIN
    -- Считаем текущее количество мастеров
    SELECT COUNT(*) INTO masters_count FROM masters;
    
    -- Если уже 10 или больше мастеров, запрещаем добавление
    IF masters_count >= 10 THEN
        RAISE EXCEPTION 'Нельзя добавить более 10 мастеров. Текущее количество: %', masters_count;
    END IF;
    
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_check_masters_limit
    BEFORE INSERT ON masters
    FOR EACH ROW
    EXECUTE FUNCTION check_masters_limit();

-- 5. Дополнительный триггер: не позволяет дать работу мастеру, если он в этот день уже выполнил больше одной работы
CREATE OR REPLACE FUNCTION check_master_daily_work_limit()
RETURNS TRIGGER 
LANGUAGE plpgsql
AS $$
DECLARE
    daily_works_count INTEGER;
BEGIN
    -- Считаем количество работ мастера за указанный день
    SELECT COUNT(*) INTO daily_works_count 
    FROM works 
    WHERE master_id = NEW.master_id 
      AND date_work = NEW.date_work
      AND id != COALESCE(NEW.id, -1); -- Исключаем текущую запись при обновлении
    
    -- Если уже есть 2 или больше работ, запрещаем
    IF daily_works_count >= 2 THEN
        RAISE EXCEPTION 'Мастер уже выполнил % работ за дату %. Нельзя назначить более 2 работ в день.', 
              daily_works_count, NEW.date_work;
    END IF;
    
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_check_master_daily_work_limit
    BEFORE INSERT OR UPDATE ON works
    FOR EACH ROW
    EXECUTE FUNCTION check_master_daily_work_limit();