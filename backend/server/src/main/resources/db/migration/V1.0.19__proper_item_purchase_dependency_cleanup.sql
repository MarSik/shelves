-- remove purchased_lot entries for items
DELETE purchased_lot FROM item NATURAL JOIN purchased_lot;

-- delete project transactions
DELETE identified_entity, owned_entity, purchase FROM identified_entity INNER JOIN owned_entity INNER JOIN purchase WHERE identified_entity.db_id = owned_entity.db_id AND purchase.db_id = owned_entity.db_id AND NOT EXISTS (SELECT 1 FROM purchased_lot WHERE purchased_lot.purchase = purchase.db_id);
DELETE identified_entity, owned_entity, named_entity, transaction FROM identified_entity INNER JOIN owned_entity INNER JOIN named_entity INNER JOIN transaction WHERE identified_entity.db_id = owned_entity.db_id AND owned_entity.db_id = named_entity.db_id AND transaction.db_id = named_entity.db_id AND NOT EXISTS (SELECT 1 FROM purchase WHERE purchase.transaction = transaction.db_id);
