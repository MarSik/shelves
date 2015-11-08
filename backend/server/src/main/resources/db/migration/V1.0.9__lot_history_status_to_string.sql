ALTER TABLE lot_history CHANGE COLUMN action action0 int;
ALTER TABLE lot_history ADD COLUMN action varchar(10) NOT NULL;

UPDATE lot_history SET action='DELIVERY' where action0=0;
UPDATE lot_history SET action='SPLIT' where action0=1;
UPDATE lot_history SET action='MOVED' where action0=2;
UPDATE lot_history SET action='ASSIGNED' where action0=3;
UPDATE lot_history SET action='UNASSIGNED' where action0=4;
UPDATE lot_history SET action='SOLDERED' where action0=5;
UPDATE lot_history SET action='UNSOLDERED' where action0=6;
UPDATE lot_history SET action='DESTROYED' where action0=7;
UPDATE lot_history SET action='EVENT' where action0=8;
UPDATE lot_history SET action='FINISHED' where action0=9;
UPDATE lot_history SET action='REOPENED' where action0=10;

ALTER TABLE lot_history DROP COLUMN action0;
