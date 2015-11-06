CREATE TABLE list (
    db_id bigint(20) NOT NULL,
    CONSTRAINT PRIMARY KEY (db_id),
    CONSTRAINT FK_list_identified_entity FOREIGN KEY (db_id) REFERENCES named_entity (db_id)
);

CREATE TABLE list_items (
    list bigint(20) NOT NULL,
    items bigint(20) NOT NULL,
    CONSTRAINT PRIMARY KEY (list, items),
    CONSTRAINT FK_list_items_list FOREIGN KEY (list) REFERENCES list (db_id),
    CONSTRAINT FK_list_items_items FOREIGN KEY (items) REFERENCES identified_entity (db_id)
);
