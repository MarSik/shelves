-- There was a bug in backup restore that created buggy LotHistory entities
-- with empty action/state when it was supposed to be a SPLIT
UPDATE lot_history SET action = "SPLIT" WHERE action = "" OR action is null;
