ALTER TABLE authorization DROP FOREIGN KEY FK_6ycuoh3l7eb62fa3n6jhd5irr;
ALTER TABLE authorization ADD CONSTRAINT FK_authorization_user FOREIGN KEY (owner) REFERENCES user (db_id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE box DROP FOREIGN KEY FK_2byqkm71y34wbwpwr7s5m0enc;
ALTER TABLE box ADD CONSTRAINT `FK_box_box` FOREIGN KEY (`parent`) REFERENCES `box` (`db_id`) ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE code DROP FOREIGN KEY FK_ol3gp8vicheo9uh81phpna0jv;
ALTER TABLE code ADD CONSTRAINT FK_core_named_ent FOREIGN KEY (`reference`) REFERENCES `named_entity` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE document_describes DROP FOREIGN KEY FK_hj80mcx93nawsp4nrh063wyu8;
ALTER TABLE document_describes ADD CONSTRAINT `FK_document_describes_named_ent` FOREIGN KEY (`describes`) REFERENCES `named_entity` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE document_describes DROP FOREIGN KEY FK_q08wd2i0jxruk8sglea9rd1wj;
ALTER TABLE document_describes ADD CONSTRAINT `FK_document_describes_by_doc` FOREIGN KEY (`described_by`) REFERENCES `document` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE footprint_see_also DROP FOREIGN KEY FK_3fpt8vq6y17qlfxdtlydpi5s1;
ALTER TABLE footprint_see_also ADD CONSTRAINT `FK_fp_see_also_inc` FOREIGN KEY (`see_also_incoming`) REFERENCES `footprint` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE footprint_see_also DROP FOREIGN KEY FK_4ujxkxp0vvk9jhl20pedcgow;
ALTER TABLE footprint_see_also ADD CONSTRAINT `FK_fp_see_also` FOREIGN KEY (`see_also`) REFERENCES `footprint` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE groups DROP FOREIGN KEY FK_2hkvxj7rehrkn56x2tjh5uh1n;
ALTER TABLE groups ADD CONSTRAINT `FK_group_parent` FOREIGN KEY (`parent`) REFERENCES `groups` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE groups_show_properties DROP FOREIGN KEY FK_hr4tw6nptm1vjruxoebie0m2u;
ALTER TABLE groups_show_properties ADD CONSTRAINT `FK_group_show_property` FOREIGN KEY (`show_properties`) REFERENCES `numeric_property` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE groups_show_properties DROP FOREIGN KEY FK_n1ea93iwuxbq5l48fkraqpf18;
ALTER TABLE groups_show_properties ADD CONSTRAINT `FK_group_show_property_group` FOREIGN KEY (`groups`) REFERENCES `groups` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE groups_types DROP FOREIGN KEY FK_hroba54122ub83q97yuqixdh8;
ALTER TABLE groups_types ADD CONSTRAINT `FK_group_types_type` FOREIGN KEY (`types`) REFERENCES `type` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE groups_types DROP FOREIGN KEY FK_t6yakd5yrcetq9tgcs1cfgns7;
ALTER TABLE groups_types ADD CONSTRAINT `FK_group_types_group` FOREIGN KEY (`groups`) REFERENCES `groups` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE list_items DROP FOREIGN KEY FK_list_items_list;
ALTER TABLE list_items ADD CONSTRAINT `FK_list_items_list` FOREIGN KEY (`list`) REFERENCES `list` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE list_items DROP FOREIGN KEY FK_list_items_items;
ALTER TABLE list_items ADD CONSTRAINT `FK_list_items_items` FOREIGN KEY (`items`) REFERENCES `identified_entity` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE lot_serials DROP FOREIGN KEY FK_b8m5mqoh3ddtyhd6owoipg4jv;
ALTER TABLE lot_serials ADD CONSTRAINT `FK_lot_serial` FOREIGN KEY (`lot`) REFERENCES `lot` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE numeric_property_value DROP FOREIGN KEY FK_a2h6iaj21gvpt3g8y5io0pg1p;
ALTER TABLE numeric_property_value ADD CONSTRAINT `FK_np_value_entity` FOREIGN KEY (`entity`) REFERENCES `named_entity` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE numeric_property_value DROP FOREIGN KEY FK_t60cd2tlp72dnbbewg2e6sdh4;
ALTER TABLE numeric_property_value ADD CONSTRAINT `FK_np_value_property` FOREIGN KEY (`property`) REFERENCES `numeric_property` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE owned_entity DROP FOREIGN KEY FK_nki7ifggubdlks1vi1fvith2d;
ALTER TABLE owned_entity ADD CONSTRAINT `FK_entity_owner` FOREIGN KEY (`owner`) REFERENCES `user` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE requirement DROP FOREIGN KEY FK_31f4o5vpmj276wbwu6aj2oita;
ALTER TABLE requirement ADD CONSTRAINT `FK_requirement_item` FOREIGN KEY (`item`) REFERENCES `item` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE requirement_type DROP FOREIGN KEY FK_alkneuxhq0s0ubo1enyjm7g7i;
ALTER TABLE requirement_type ADD CONSTRAINT `FK_requirement_type` FOREIGN KEY (`type`) REFERENCES `type` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE requirement_type DROP FOREIGN KEY FK_apqdt7mfpvnjhw1rsi5t6kf8y;
ALTER TABLE requirement_type ADD CONSTRAINT `FK_requirement_type_used_in` FOREIGN KEY (`used_in`) REFERENCES `requirement` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE sku DROP FOREIGN KEY FK_sku_identified_entity;
ALTER TABLE sku ADD CONSTRAINT `FK_sku_identified_entity` FOREIGN KEY (`db_id`) REFERENCES `identified_entity` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE sku DROP FOREIGN KEY FK_sku_source;
ALTER TABLE sku ADD CONSTRAINT `FK_sku_source` FOREIGN KEY (`source`) REFERENCES `source` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE sku DROP FOREIGN KEY FK_sku_type;
ALTER TABLE sku ADD CONSTRAINT `FK_sku_type` FOREIGN KEY (`type`) REFERENCES `type` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE text_revision DROP FOREIGN KEY FK_text_revision_identified_entity;
ALTER TABLE text_revision ADD CONSTRAINT `FK_text_revision_identified_entity` FOREIGN KEY (`db_id`) REFERENCES `identified_entity` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE type_footprints DROP FOREIGN KEY FK_2lbsodfgt3w78b57cc0dwmuxd;
ALTER TABLE type_footprints ADD CONSTRAINT `FK_type_footprint_type` FOREIGN KEY (`types`) REFERENCES `type` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE type_footprints DROP FOREIGN KEY FK_5cudwuv142a9mb0xc8g8nsp6l;
ALTER TABLE type_footprints ADD CONSTRAINT `FK_type_footprint_footprint` FOREIGN KEY (`footprints`) REFERENCES `footprint` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE type_see_also DROP FOREIGN KEY FK_f8h8saxahs5yxdgnfwdejqjwm;
ALTER TABLE type_see_also ADD CONSTRAINT `FK_type_see_also` FOREIGN KEY (`see_also`) REFERENCES `type` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE type_see_also DROP FOREIGN KEY FK_nkoq6o4nql9h9q48hal4onke1;
ALTER TABLE type_see_also ADD CONSTRAINT `FK_type_see_also_incoming` FOREIGN KEY (`see_also_incoming`) REFERENCES `type` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE unit_prefixes DROP FOREIGN KEY FK_jhc1moy14s3fqkmuqd30c0hvo;
ALTER TABLE unit_prefixes ADD CONSTRAINT `FK_unit_prefixes` FOREIGN KEY (`unit`) REFERENCES `unit` (`db_id`) ON UPDATE CASCADE ON DELETE CASCADE;

