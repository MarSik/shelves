CREATE TABLE sku (
    db_id bigint(20) NOT NULL,
    sku varchar(255) NOT NULL,
    source bigint(20) NOT NULL,
    type bigint(20) NOT NULL,

    CONSTRAINT PRIMARY KEY (db_id),
    CONSTRAINT FK_sku_identified_entity FOREIGN KEY (db_id) REFERENCES identified_entity (db_id),
    CONSTRAINT FK_sku_source FOREIGN KEY (source) REFERENCES source (db_id),
    CONSTRAINT FK_sku_type FOREIGN KEY (type) REFERENCES type (db_id)
);

ALTER TABLE purchase ADD COLUMN sku bigint(20);
ALTER TABLE purchase ADD CONSTRAINT FK_purchase_sku FOREIGN KEY (sku) REFERENCES sku (db_id);
