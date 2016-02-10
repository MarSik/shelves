ALTER TABLE lot ADD COLUMN used BOOLEAN;
ALTER TABLE lot ADD COLUMN used_in_past BOOLEAN;
ALTER TABLE lot ADD COLUMN valid BOOLEAN;
UPDATE lot SET used = (status = "SOLDERED"), used_in_past = (status = "SOLDERED"), valid = (status != "DESTROYED" AND status != "MIXED");
ALTER TABLE lot DROP COLUMN status;
