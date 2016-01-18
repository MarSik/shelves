CREATE TABLE user_external_ids (
    user bigint(20) NOT NULL,
    external_ids varchar(255) NOT NULL,
    PRIMARY KEY (external_ids),
    KEY (user),
    CONSTRAINT FK_user_external_ids FOREIGN KEY (user) REFERENCES user (db_id) ON DELETE CASCADE ON UPDATE CASCADE
);

