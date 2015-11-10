ALTER TABLE purchase CHANGE COLUMN single_price single_price DECIMAL(10, 3);
ALTER TABLE purchase CHANGE COLUMN total_price total_price DECIMAL(10, 3);
ALTER TABLE purchase ADD COLUMN currency VARCHAR(3) DEFAULT 'CZK';

ALTER TABLE purchase ADD COLUMN paid_single_price DECIMAL(10, 3);
ALTER TABLE purchase ADD COLUMN paid_total_price DECIMAL(10, 3);
ALTER TABLE purchase ADD COLUMN paid_currency VARCHAR(3);

ALTER TABLE purchase CHANGE COLUMN vat vat DECIMAL(4, 1);

UPDATE purchase SET paid_single_price = single_price, paid_total_price = total_price, paid_currency = currency;
