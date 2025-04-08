CREATE INDEX idx_category_name ON categories(name);
CREATE INDEX idx_category_created_at ON categories(created_at);
CREATE INDEX idx_category_modified_at ON categories(modified_at);

CREATE INDEX idx_product_name ON products(name);
CREATE INDEX idx_product_price ON products(price);
CREATE INDEX idx_product_status ON products(status);
CREATE INDEX idx_product_category ON products(category_id);
CREATE INDEX idx_product_created_at ON products(created_at);
CREATE INDEX idx_product_modified_at ON products(modified_at);

ALTER TABLE products ADD CONSTRAINT uk_product_code UNIQUE (code);
CREATE INDEX idx_product_code ON products(code);
