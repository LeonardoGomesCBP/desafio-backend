UPDATE products
SET code = CAST(SUBSTRING(code FROM 6) AS INTEGER)
WHERE code LIKE 'PROD-%';

ALTER TABLE products
ALTER COLUMN code TYPE INTEGER USING (CAST(code AS INTEGER));


