-- MySQL dump 10.14  Distrib 5.5.44-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: elshelves
-- ------------------------------------------------------
-- Server version	5.5.44-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `authorization`
--

DROP TABLE IF EXISTS `authorization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authorization` (
  `created` datetime DEFAULT NULL,
  `last_used` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `secret` varchar(255) NOT NULL,
  `db_id` bigint(20) NOT NULL,
  `owner` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  KEY `FK_6ycuoh3l7eb62fa3n6jhd5irr` (`owner`),
  CONSTRAINT `FK_6ycuoh3l7eb62fa3n6jhd5irr` FOREIGN KEY (`owner`) REFERENCES `user` (`db_id`),
  CONSTRAINT `FK_i3t850r8kf4edxosy35wmwycd` FOREIGN KEY (`db_id`) REFERENCES `identified_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `box`
--

DROP TABLE IF EXISTS `box`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `box` (
  `db_id` bigint(20) NOT NULL,
  `parent` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`db_id`),
  KEY `FK_2byqkm71y34wbwpwr7s5m0enc` (`parent`),
  CONSTRAINT `FK_2byqkm71y34wbwpwr7s5m0enc` FOREIGN KEY (`parent`) REFERENCES `box` (`db_id`),
  CONSTRAINT `FK_jtxo1h8ufcsrlrkur9eko2n4` FOREIGN KEY (`db_id`) REFERENCES `named_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `code`
--

DROP TABLE IF EXISTS `code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `code` (
  `code` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `db_id` bigint(20) NOT NULL,
  `reference` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  KEY `FK_ol3gp8vicheo9uh81phpna0jv` (`reference`),
  CONSTRAINT `FK_hwrmsdhyxggh5c1dg9mqy41tm` FOREIGN KEY (`db_id`) REFERENCES `owned_entity` (`db_id`),
  CONSTRAINT `FK_ol3gp8vicheo9uh81phpna0jv` FOREIGN KEY (`reference`) REFERENCES `named_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document` (
  `content_type` varchar(255) NOT NULL,
  `created` datetime DEFAULT NULL,
  `size` bigint(20) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `db_id` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  CONSTRAINT `FK_76tgbxifrnstws7br7l03okr1` FOREIGN KEY (`db_id`) REFERENCES `named_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `document_describes`
--

DROP TABLE IF EXISTS `document_describes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document_describes` (
  `described_by` bigint(20) NOT NULL,
  `describes` bigint(20) NOT NULL,
  PRIMARY KEY (`described_by`,`describes`),
  KEY `FK_hj80mcx93nawsp4nrh063wyu8` (`describes`),
  CONSTRAINT `FK_hj80mcx93nawsp4nrh063wyu8` FOREIGN KEY (`describes`) REFERENCES `named_entity` (`db_id`),
  CONSTRAINT `FK_q08wd2i0jxruk8sglea9rd1wj` FOREIGN KEY (`described_by`) REFERENCES `document` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `footprint`
--

DROP TABLE IF EXISTS `footprint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `footprint` (
  `holes` int(11) DEFAULT NULL,
  `kicad` varchar(255) DEFAULT NULL,
  `npth` int(11) DEFAULT NULL,
  `pads` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `db_id` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  CONSTRAINT `FK_ikgsbldsengpmcl4asjw4fvvb` FOREIGN KEY (`db_id`) REFERENCES `named_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `footprint_see_also`
--

DROP TABLE IF EXISTS `footprint_see_also`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `footprint_see_also` (
  `see_also_incoming` bigint(20) NOT NULL,
  `see_also` bigint(20) NOT NULL,
  PRIMARY KEY (`see_also_incoming`,`see_also`),
  KEY `FK_4ujxkxp0vvk9jhl20pedcgow` (`see_also`),
  CONSTRAINT `FK_3fpt8vq6y17qlfxdtlydpi5s1` FOREIGN KEY (`see_also_incoming`) REFERENCES `footprint` (`db_id`),
  CONSTRAINT `FK_4ujxkxp0vvk9jhl20pedcgow` FOREIGN KEY (`see_also`) REFERENCES `footprint` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups` (
  `db_id` bigint(20) NOT NULL,
  `parent` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`db_id`),
  KEY `FK_2hkvxj7rehrkn56x2tjh5uh1n` (`parent`),
  CONSTRAINT `FK_2hkvxj7rehrkn56x2tjh5uh1n` FOREIGN KEY (`parent`) REFERENCES `groups` (`db_id`),
  CONSTRAINT `FK_pnp06fwodwdr3va3fudwyckk2` FOREIGN KEY (`db_id`) REFERENCES `named_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groups_show_properties`
--

DROP TABLE IF EXISTS `groups_show_properties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups_show_properties` (
  `groups` bigint(20) NOT NULL,
  `show_properties` bigint(20) NOT NULL,
  PRIMARY KEY (`groups`,`show_properties`),
  KEY `FK_hr4tw6nptm1vjruxoebie0m2u` (`show_properties`),
  CONSTRAINT `FK_hr4tw6nptm1vjruxoebie0m2u` FOREIGN KEY (`show_properties`) REFERENCES `numeric_property` (`db_id`),
  CONSTRAINT `FK_n1ea93iwuxbq5l48fkraqpf18` FOREIGN KEY (`groups`) REFERENCES `groups` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groups_types`
--

DROP TABLE IF EXISTS `groups_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups_types` (
  `groups` bigint(20) NOT NULL,
  `types` bigint(20) NOT NULL,
  PRIMARY KEY (`groups`,`types`),
  KEY `FK_hroba54122ub83q97yuqixdh8` (`types`),
  CONSTRAINT `FK_hroba54122ub83q97yuqixdh8` FOREIGN KEY (`types`) REFERENCES `type` (`db_id`),
  CONSTRAINT `FK_t6yakd5yrcetq9tgcs1cfgns7` FOREIGN KEY (`groups`) REFERENCES `groups` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `identified_entity`
--

DROP TABLE IF EXISTS `identified_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identified_entity` (
  `db_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `id` binary(16) DEFAULT NULL,
  `last_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`db_id`),
  UNIQUE KEY `UK_jx5jhwourmkfp763c8rp957gt` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4793 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item` (
  `finished` bit(1) DEFAULT NULL,
  `db_id` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  CONSTRAINT `FK_3u9olab9t5wunpb0c6jkhi3ux` FOREIGN KEY (`db_id`) REFERENCES `lot` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lot`
--

DROP TABLE IF EXISTS `lot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lot` (
  `count` bigint(20) NOT NULL,
  `expiration` datetime DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `db_id` bigint(20) NOT NULL,
  `history` bigint(20) NOT NULL,
  `location` bigint(20) DEFAULT NULL,
  `purchase` bigint(20) NOT NULL,
  `used_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`db_id`),
  KEY `FK_8ad9cn6ybpyfcr7e25prwxotx` (`history`),
  KEY `FK_lon5n6tl8gu913w5ckx2ie8vi` (`location`),
  KEY `FK_cfw0dhqhirrb8achgg0s5oo46` (`purchase`),
  KEY `FK_qos73550924p62ylol09y2kiq` (`used_by`),
  CONSTRAINT `FK_8ad9cn6ybpyfcr7e25prwxotx` FOREIGN KEY (`history`) REFERENCES `lot_history` (`db_id`),
  CONSTRAINT `FK_cfw0dhqhirrb8achgg0s5oo46` FOREIGN KEY (`purchase`) REFERENCES `purchase` (`db_id`),
  CONSTRAINT `FK_jqgfnxy27f6kbhour6rksh046` FOREIGN KEY (`db_id`) REFERENCES `owned_entity` (`db_id`),
  CONSTRAINT `FK_lon5n6tl8gu913w5ckx2ie8vi` FOREIGN KEY (`location`) REFERENCES `box` (`db_id`),
  CONSTRAINT `FK_qos73550924p62ylol09y2kiq` FOREIGN KEY (`used_by`) REFERENCES `requirement` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lot_history`
--

DROP TABLE IF EXISTS `lot_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lot_history` (
  `action` int(11) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `db_id` bigint(20) NOT NULL,
  `assigned_to` bigint(20) DEFAULT NULL,
  `location` bigint(20) DEFAULT NULL,
  `performed_by` bigint(20) DEFAULT NULL,
  `previous` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`db_id`),
  KEY `FK_cx8e1gyhcob2g3s0o0jkiw28p` (`assigned_to`),
  KEY `FK_p0c2efe72s304srbjokjo9kfe` (`location`),
  KEY `FK_ocm4t5k3c3aoxaapnqqxygcj6` (`performed_by`),
  KEY `FK_msam2mol45my6h06siwlc0u5o` (`previous`),
  CONSTRAINT `FK_51wfmpm2tg4nfusvaecs98x9v` FOREIGN KEY (`db_id`) REFERENCES `identified_entity` (`db_id`),
  CONSTRAINT `FK_cx8e1gyhcob2g3s0o0jkiw28p` FOREIGN KEY (`assigned_to`) REFERENCES `requirement` (`db_id`),
  CONSTRAINT `FK_msam2mol45my6h06siwlc0u5o` FOREIGN KEY (`previous`) REFERENCES `lot_history` (`db_id`),
  CONSTRAINT `FK_ocm4t5k3c3aoxaapnqqxygcj6` FOREIGN KEY (`performed_by`) REFERENCES `user` (`db_id`),
  CONSTRAINT `FK_p0c2efe72s304srbjokjo9kfe` FOREIGN KEY (`location`) REFERENCES `box` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lot_serials`
--

DROP TABLE IF EXISTS `lot_serials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lot_serials` (
  `lot` bigint(20) NOT NULL,
  `serials` varchar(255) DEFAULT NULL,
  KEY `FK_b8m5mqoh3ddtyhd6owoipg4jv` (`lot`),
  CONSTRAINT `FK_b8m5mqoh3ddtyhd6owoipg4jv` FOREIGN KEY (`lot`) REFERENCES `lot` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `named_entity`
--

DROP TABLE IF EXISTS `named_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `named_entity` (
  `description` longtext,
  `flagged` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `summary` varchar(255) DEFAULT NULL,
  `db_id` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  CONSTRAINT `FK_baakffqs8ad1ab2yh3hew3yuq` FOREIGN KEY (`db_id`) REFERENCES `owned_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `numeric_property`
--

DROP TABLE IF EXISTS `numeric_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `numeric_property` (
  `base` int(11) NOT NULL,
  `symbol` varchar(255) DEFAULT NULL,
  `db_id` bigint(20) NOT NULL,
  `unit` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  KEY `FK_j1ysr7ye271p6q2djdmuo18sj` (`unit`),
  CONSTRAINT `FK_363ms2c50ph2p4g7gk2lfufdg` FOREIGN KEY (`db_id`) REFERENCES `named_entity` (`db_id`),
  CONSTRAINT `FK_j1ysr7ye271p6q2djdmuo18sj` FOREIGN KEY (`unit`) REFERENCES `unit` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `numeric_property_value`
--

DROP TABLE IF EXISTS `numeric_property_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `numeric_property_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` bigint(20) NOT NULL,
  `entity` bigint(20) NOT NULL,
  `property` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_a2h6iaj21gvpt3g8y5io0pg1p` (`entity`),
  KEY `FK_t60cd2tlp72dnbbewg2e6sdh4` (`property`),
  CONSTRAINT `FK_a2h6iaj21gvpt3g8y5io0pg1p` FOREIGN KEY (`entity`) REFERENCES `named_entity` (`db_id`),
  CONSTRAINT `FK_t60cd2tlp72dnbbewg2e6sdh4` FOREIGN KEY (`property`) REFERENCES `numeric_property` (`db_id`)
) ENGINE=InnoDB AUTO_INCREMENT=271 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `owned_entity`
--

DROP TABLE IF EXISTS `owned_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `owned_entity` (
  `db_id` bigint(20) NOT NULL,
  `owner` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  KEY `FK_nki7ifggubdlks1vi1fvith2d` (`owner`),
  CONSTRAINT `FK_1lakn0kufopyttbwjn36p5se0` FOREIGN KEY (`db_id`) REFERENCES `identified_entity` (`db_id`),
  CONSTRAINT `FK_nki7ifggubdlks1vi1fvith2d` FOREIGN KEY (`owner`) REFERENCES `user` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `purchase`
--

DROP TABLE IF EXISTS `purchase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `purchase` (
  `count` bigint(20) NOT NULL,
  `single_price` double DEFAULT NULL,
  `total_price` double DEFAULT NULL,
  `vat` double DEFAULT NULL,
  `vat_included` bit(1) DEFAULT NULL,
  `db_id` bigint(20) NOT NULL,
  `transaction` bigint(20) NOT NULL,
  `type` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  KEY `FK_gvc87teaoti5tckqy4k05mjmn` (`transaction`),
  KEY `FK_cbqxgagncs7hnuubbakmomax2` (`type`),
  CONSTRAINT `FK_cbqxgagncs7hnuubbakmomax2` FOREIGN KEY (`type`) REFERENCES `type` (`db_id`),
  CONSTRAINT `FK_gvc87teaoti5tckqy4k05mjmn` FOREIGN KEY (`transaction`) REFERENCES `transaction` (`db_id`),
  CONSTRAINT `FK_lunrp764g36mlv7xovajry7jh` FOREIGN KEY (`db_id`) REFERENCES `owned_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `requirement`
--

DROP TABLE IF EXISTS `requirement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `requirement` (
  `count` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `summary` varchar(255) DEFAULT NULL,
  `db_id` bigint(20) NOT NULL,
  `item` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  KEY `FK_31f4o5vpmj276wbwu6aj2oita` (`item`),
  CONSTRAINT `FK_31f4o5vpmj276wbwu6aj2oita` FOREIGN KEY (`item`) REFERENCES `item` (`db_id`),
  CONSTRAINT `FK_mhlxyah0elwxyq5995bqj9g4c` FOREIGN KEY (`db_id`) REFERENCES `identified_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `requirement_type`
--

DROP TABLE IF EXISTS `requirement_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `requirement_type` (
  `used_in` bigint(20) NOT NULL,
  `type` bigint(20) NOT NULL,
  PRIMARY KEY (`used_in`,`type`),
  KEY `FK_alkneuxhq0s0ubo1enyjm7g7i` (`type`),
  CONSTRAINT `FK_alkneuxhq0s0ubo1enyjm7g7i` FOREIGN KEY (`type`) REFERENCES `type` (`db_id`),
  CONSTRAINT `FK_apqdt7mfpvnjhw1rsi5t6kf8y` FOREIGN KEY (`used_in`) REFERENCES `requirement` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `source`
--

DROP TABLE IF EXISTS `source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `source` (
  `shipping_calculator` varchar(255) NOT NULL,
  `source_downloader` varchar(255) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `db_id` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  CONSTRAINT `FK_dd7apqcho3sjhs41axqi4s6lw` FOREIGN KEY (`db_id`) REFERENCES `named_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction` (
  `date` datetime DEFAULT NULL,
  `db_id` bigint(20) NOT NULL,
  `source` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  KEY `FK_jildeuvglapfff86b56pxl9ul` (`source`),
  CONSTRAINT `FK_jildeuvglapfff86b56pxl9ul` FOREIGN KEY (`source`) REFERENCES `source` (`db_id`),
  CONSTRAINT `FK_soeci4qd5wkuqmjrbhyyrpxwr` FOREIGN KEY (`db_id`) REFERENCES `named_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `type`
--

DROP TABLE IF EXISTS `type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `type` (
  `buy_multiple` bigint(20) DEFAULT NULL,
  `custom_id` varchar(255) DEFAULT NULL,
  `manufacturable` bit(1) DEFAULT NULL,
  `minimum_count` bigint(20) DEFAULT NULL,
  `serials` bit(1) DEFAULT NULL,
  `vendor` varchar(255) DEFAULT NULL,
  `db_id` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  CONSTRAINT `FK_eu5dhhrsch21luyyy0915xh6a` FOREIGN KEY (`db_id`) REFERENCES `named_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `type_footprints`
--

DROP TABLE IF EXISTS `type_footprints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `type_footprints` (
  `types` bigint(20) NOT NULL,
  `footprints` bigint(20) NOT NULL,
  PRIMARY KEY (`types`,`footprints`),
  KEY `FK_5cudwuv142a9mb0xc8g8nsp6l` (`footprints`),
  CONSTRAINT `FK_2lbsodfgt3w78b57cc0dwmuxd` FOREIGN KEY (`types`) REFERENCES `type` (`db_id`),
  CONSTRAINT `FK_5cudwuv142a9mb0xc8g8nsp6l` FOREIGN KEY (`footprints`) REFERENCES `footprint` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `type_see_also`
--

DROP TABLE IF EXISTS `type_see_also`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `type_see_also` (
  `see_also_incoming` bigint(20) NOT NULL,
  `see_also` bigint(20) NOT NULL,
  PRIMARY KEY (`see_also_incoming`,`see_also`),
  KEY `FK_f8h8saxahs5yxdgnfwdejqjwm` (`see_also`),
  CONSTRAINT `FK_f8h8saxahs5yxdgnfwdejqjwm` FOREIGN KEY (`see_also`) REFERENCES `type` (`db_id`),
  CONSTRAINT `FK_nkoq6o4nql9h9q48hal4onke1` FOREIGN KEY (`see_also_incoming`) REFERENCES `type` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `unit`
--

DROP TABLE IF EXISTS `unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `unit` (
  `symbol` varchar(255) NOT NULL,
  `db_id` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  CONSTRAINT `FK_pkjh56aw3006tax59hww9wotf` FOREIGN KEY (`db_id`) REFERENCES `named_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `unit_prefixes`
--

DROP TABLE IF EXISTS `unit_prefixes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `unit_prefixes` (
  `unit` bigint(20) NOT NULL,
  `prefixes` varchar(255) DEFAULT NULL,
  KEY `FK_jhc1moy14s3fqkmuqd30c0hvo` (`unit`),
  CONSTRAINT `FK_jhc1moy14s3fqkmuqd30c0hvo` FOREIGN KEY (`unit`) REFERENCES `unit` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `registration_date` datetime DEFAULT NULL,
  `verification_code` varchar(255) DEFAULT NULL,
  `verification_start_time` datetime DEFAULT NULL,
  `db_id` bigint(20) NOT NULL,
  PRIMARY KEY (`db_id`),
  CONSTRAINT `FK_lgx84fntc5gyrcufdn3nx30y7` FOREIGN KEY (`db_id`) REFERENCES `identified_entity` (`db_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-10-21  0:46:35
