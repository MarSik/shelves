-- delete the preferred project source
ALTER TABLE user DROP FOREIGN KEY FK_user_project_source;
ALTER TABLE user DROP COLUMN project_source;

-- delete project transactions
DELETE identified_entity, owned_entity, purchase FROM identified_entity INNER JOIN owned_entity INNER JOIN purchase WHERE identified_entity.db_id = owned_entity.db_id AND purchase.db_id = owned_entity.db_id AND NOT EXISTS (SELECT 1 FROM purchased_lot WHERE purchased_lot.purchase = purchase.db_id);
DELETE identified_entity, owned_entity, named_entity, transaction FROM identified_entity INNER JOIN owned_entity INNER JOIN named_entity INNER JOIN transaction WHERE identified_entity.db_id = owned_entity.db_id AND owned_entity.db_id = named_entity.db_id AND transaction.db_id = named_entity.db_id AND NOT EXISTS (SELECT 1 FROM purchase WHERE purchase.transaction = transaction.db_id);
