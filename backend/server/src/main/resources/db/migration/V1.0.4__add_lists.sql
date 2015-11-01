CREATE TABLE list (
    db_id bigint(20) NOT NULL,
    CONSTRAINT PRIMARY KEY (db_id),
    CONSTRAINT FK_list_identified_entity FOREIGN KEY (db_id) REFERENCES identified_entity (db_id)
);

CREATE TABLE list_items (
    lists bigint(20) NOT NULL,
    items bigint(20) NOT NULL,
    CONSTRAINT PRIMARY KEY (lists, items),
    CONSTRAINT FK_list_items_list FOREIGN KEY (lists) REFERENCES list (db_id),
    CONSTRAINT FK_list_items_items FOREIGN KEY (items) REFERENCES identified_entity (db_id)
);
