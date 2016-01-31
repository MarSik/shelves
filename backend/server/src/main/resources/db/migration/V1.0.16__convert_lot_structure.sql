/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- Create shared type column
ALTER TABLE lot ADD COLUMN type BIGINT(20) NOT NULL;
ALTER TABLE lot ADD CONSTRAINT lot_type FOREIGN KEY (type) REFERENCES type (db_id);

-- Create tables for children classes
CREATE TABLE purchased_lot (
    db_id bigint(20) NOT NULL,
    purchase bigint(20) NOT NULL,
    CONSTRAINT purchased_lot_lot FOREIGN KEY (db_id) REFERENCES lot (db_id),
    CONSTRAINT purchased_lot_purchase FOREIGN KEY (purchase) REFERENCES purchase (db_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE mixed_lot (
    db_id bigint(20) NOT NULL,
    CONSTRAINT mixed_lot_lot FOREIGN KEY (db_id) REFERENCES lot (db_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE mixed_lot_parents (
    mixed_lot bigint(20) NOT NULL,
    parents bigint(20) NOT NULL,
    CONSTRAINT mixed_lot_actual FOREIGN KEY (mixed_lot) REFERENCES mixed_lot (db_id),
    CONSTRAINT mixed_lot_parents FOREIGN KEY (parents) REFERENCES lot (db_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Convert data to right tables
UPDATE lot l LEFT JOIN purchase p ON p.db_id = l.purchase SET l.type = p.type;
INSERT INTO purchased_lot (db_id, purchase) SELECT db_id, purchase FROM lot;

-- Drop the old field
ALTER TABLE lot DROP FOREIGN KEY FK_cfw0dhqhirrb8achgg0s5oo46;
ALTER TABLE lot DROP COLUMN purchase;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
