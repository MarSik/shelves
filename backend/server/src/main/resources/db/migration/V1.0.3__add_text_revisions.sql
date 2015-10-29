CREATE TABLE text_revision (
    db_id bigint(20) NOT NULL,
    parent bigint(20),
    description longtext,
    name varchar(255) NOT NULL,
    summary varchar(255) DEFAULT NULL,
    performed_by bigint(20) NOT NULL,
    CONSTRAINT FK_text_revision_performed_by FOREIGN KEY (performed_by) REFERENCES user (db_id),
    CONSTRAINT FK_text_revision_identified_entity FOREIGN KEY (db_id) REFERENCES identified_entity (db_id),
    CONSTRAINT FK_text_revision_parent FOREIGN KEY (parent) REFERENCES text_revision (db_id)
);

ALTER TABLE named_entity ADD COLUMN previous_revision bigint(20) DEFAULT null;
ALTER TABLE named_entity ADD CONSTRAINT FK_named_entity_previous_revision FOREIGN KEY (previous_revision) REFERENCES text_revision (db_id);
