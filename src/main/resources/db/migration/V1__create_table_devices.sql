CREATE TABLE IF NOT EXISTS devices
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR (100) NOT NULL,
    brand VARCHAR (50) NOT NULL,
    state VARCHAR (20) NOT NULL DEFAULT 'AVAILABLE',
    creation_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                                );

-- Update the index to match the VARCHAR column
CREATE INDEX IF NOT EXISTS idx_devices_brand ON devices (LOWER(brand));
CREATE INDEX IF NOT EXISTS idx_devices_state ON devices (state);

--Function to prevent creation_time update
CREATE OR REPLACE FUNCTION prevent_creation_time_update()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.creation_time IS DISTINCT FROM NEW.creation_time THEN
        RAISE EXCEPTION 'Cannot update creation_time';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_prevent_creation_time_update
BEFORE UPDATE ON devices
FOR EACH ROW EXECUTE FUNCTION prevent_creation_time_update();