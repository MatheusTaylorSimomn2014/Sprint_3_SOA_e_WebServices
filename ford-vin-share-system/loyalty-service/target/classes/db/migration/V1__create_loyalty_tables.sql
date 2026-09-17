CREATE TABLE IF NOT EXISTS loyalty_tiers (
    id BIGSERIAL PRIMARY KEY,
    tier_name VARCHAR(20) UNIQUE NOT NULL,
    min_points INTEGER DEFAULT 0,
    discount_percentage DECIMAL(5,2) DEFAULT 0.00,
    warranty_extension_months INTEGER DEFAULT 0,
    free_services_per_year INTEGER DEFAULT 0,
    priority_support BOOLEAN DEFAULT FALSE,
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS loyalty_programs (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT UNIQUE NOT NULL,
    points INTEGER DEFAULT 0,
    tier_id BIGINT,
    total_services INTEGER DEFAULT 0,
    total_spent DECIMAL(10,2) DEFAULT 0.00,
    last_service_date TIMESTAMP,
    member_since TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loyalty_tier FOREIGN KEY (tier_id) REFERENCES loyalty_tiers(id)
);

INSERT INTO loyalty_tiers (tier_name, min_points, discount_percentage, warranty_extension_months, free_services_per_year, priority_support, description) VALUES
('BRONZE', 0, 0.00, 0, 0, FALSE, 'Nível básico - Acesso a ofertas exclusivas'),
('SILVER', 1000, 5.00, 3, 1, FALSE, 'Nível prata - 5% de desconto em peças e 1 revisão grátis por ano'),
('GOLD', 5000, 10.00, 6, 2, TRUE, 'Nível ouro - 10% de desconto, 2 revisões grátis e suporte prioritário'),
('PLATINUM', 10000, 15.00, 12, 4, TRUE, 'Nível platina - 15% de desconto, 4 revisões grátis, extensão de garantia de 1 ano');

CREATE INDEX IF NOT EXISTS idx_loyalty_customer_id ON loyalty_programs(customer_id);
CREATE INDEX IF NOT EXISTS idx_loyalty_tier_id ON loyalty_programs(tier_id);
CREATE INDEX IF NOT EXISTS idx_loyalty_points ON loyalty_programs(points);

COMMENT ON TABLE loyalty_tiers IS 'Tabela de níveis do programa de fidelidade';
COMMENT ON TABLE loyalty_programs IS 'Tabela de membros do programa de fidelidade';
