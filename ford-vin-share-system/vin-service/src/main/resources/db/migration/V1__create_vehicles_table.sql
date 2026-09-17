CREATE TABLE IF NOT EXISTS vehicles (
    id BIGSERIAL PRIMARY KEY,
    vin VARCHAR(17) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    make VARCHAR(50),
    model VARCHAR(50),
    year INTEGER,
    mileage INTEGER DEFAULT 0,
    color VARCHAR(30),
    engine_type VARCHAR(50),
    transmission VARCHAR(30),
    last_service_date DATE,
    next_service_date DATE,
    warranty_status VARCHAR(20) DEFAULT 'ACTIVE',
    warranty_expiry_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vin ON vehicles(vin);
CREATE INDEX IF NOT EXISTS idx_customer_id ON vehicles(customer_id);
CREATE INDEX IF NOT EXISTS idx_warranty_status ON vehicles(warranty_status);
CREATE INDEX IF NOT EXISTS idx_next_service_date ON vehicles(next_service_date);
CREATE INDEX IF NOT EXISTS idx_is_active ON vehicles(is_active);

COMMENT ON TABLE vehicles IS 'Tabela de veículos cadastrados no sistema Ford VIN Share';
COMMENT ON COLUMN vehicles.vin IS 'Vehicle Identification Number - identificador único de 17 caracteres';
COMMENT ON COLUMN vehicles.customer_id IS 'ID do cliente proprietário do veículo';
COMMENT ON COLUMN vehicles.warranty_status IS 'Status da garantia: ACTIVE, EXPIRED, VOID';
COMMENT ON COLUMN vehicles.is_active IS 'Indica se o veículo está ativo no sistema (soft delete)';
