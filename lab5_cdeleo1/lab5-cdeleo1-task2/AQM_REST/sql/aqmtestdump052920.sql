-- MySQL dump 10.13  Distrib 5.7.12, for osx10.11 (x86_64)
--
-- Host: localhost    Database: aqmtest
-- ------------------------------------------------------
-- Server version	5.7.12

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
-- Table structure for table `aqmdata_common`
--

DROP TABLE IF EXISTS `aqmdata_common`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aqmdata_common` (
  `deviceId` varchar(64) NOT NULL,
  `dateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `method` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`deviceId`,`dateTime`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aqmdata_common`
--

LOCK TABLES `aqmdata_common` WRITE;
/*!40000 ALTER TABLE `aqmdata_common` DISABLE KEYS */;
INSERT INTO `aqmdata_common` VALUES ('aqm0','2020-03-21 10:28:55',33.309986,-111.6725,'manual'),('SensorDrone00:00:00:00:00:00','2020-03-21 11:15:55',33.309986,-111.6725,'Network'),('SensorDrone00:00:00:00:00:00','2020-03-21 11:16:50',33.309986,-111.6725,'Network'),('aqm0','2020-03-21 11:10:55',33.29859,-111.679532,'manual'),('aqm0','2020-03-21 11:11:55',33.309986,-111.6725,'manual'),('SensorDrone11:00:00:00:00:00','2020-03-21 11:32:55',33.309986,-111.6725,'Network'),('SensorDrone11:00:00:00:00:00','2020-03-21 11:34:55',33.309986,-111.6725,'Network'),('SensorDrone12:00:00:00:00:00','2020-03-21 11:36:55',33.309986,-111.6725,'Network'),('SensorDrone00:00:00:00:00:00','2020-03-13 21:28:55',33.309986,-111.6725,'Network'),('SensorDrone00:00:00:00:00:00','2020-03-21 21:28:55',33.309986,-111.6725,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:02:45',33.299305,-111.68229,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:03:38',33.299255,-111.68229,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:04:38',33.299255,-111.6823,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:05:38',33.299282,-111.68227,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:06:38',33.299282,-111.68227,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:08:01',33.299305,-111.68233,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:09:01',33.29931,-111.682335,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:10:00',33.299305,-111.682335,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:11:16',33.299305,-111.682335,'Network'),('aqm1','2020-03-22 03:24:59',33.309917,-111.6727,'manual'),('aqm1','2020-03-22 03:25:09',33.309917,-111.6727,'manual'),('aqm1','2020-03-22 03:25:29',33.309917,-111.6727,'manual'),('aqm1','2020-03-22 03:25:39',33.309917,-111.6727,'manual'),('aqm1','2020-03-22 03:25:59',33.309917,-111.6727,'manual'),('aqm1','2020-03-22 03:26:09',33.309917,-111.6727,'manual'),('aqm1','2020-03-22 03:26:29',33.309917,-111.6727,'manual'),('aqm1','2020-03-22 03:26:39',33.309917,-111.6727,'manual'),('aqm1','2020-03-22 03:26:59',33.309917,-111.6727,'manual'),('aqm1','2020-03-22 03:27:09',33.309917,-111.6727,'manual'),('aqm0','2020-03-22 03:37:09',33.309917,-111.6727,'manual'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 11:07:25',33.299248,-111.68222,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 11:08:54',33.299255,-111.68229,'Network'),('aqm2','2020-03-26 00:18:15',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:20:13',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:21:12',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:23:11',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:24:10',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:26:08',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:27:07',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:29:06',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:30:05',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:32:03',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:33:02',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:35:01',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:36:00',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:37:58',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:39:57',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:40:56',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:42:54',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:43:53',33.305824,-111.679184,'manual'),('aqm2','2020-03-26 00:44:53',33.305824,-111.679184,'manual'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:16:54',33.29926,-111.68227,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:17:13',33.29932,-111.68236,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:18:19',33.299313,-111.68235,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:19:20',33.29928,-111.68234,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:20:54',33.299274,-111.68227,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:21:29',33.299244,-111.68223,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:22:42',33.29923,-111.68219,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:23:20',33.299267,-111.68228,'Network'),('aqm3','2020-03-27 14:23:14',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:23:34',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:23:44',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:23:54',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:24:04',33.305824,-111.679184,'manual'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:24:12',33.29925,-111.68227,'Network'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:24:15',33.299267,-111.68223,'Network'),('aqm3','2020-03-27 14:24:14',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:24:24',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:24:44',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:25:04',33.305824,-111.679184,'manual'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:25:18',33.29926,-111.68228,'Network'),('aqm3','2020-03-27 14:25:24',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:25:34',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:25:54',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:26:14',33.305824,-111.679184,'manual'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:26:32',33.299305,-111.682335,'Network'),('aqm3','2020-03-27 14:26:34',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:26:44',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:26:54',33.305824,-111.679184,'manual'),('aqm3','2020-03-27 14:27:04',33.305824,-111.679184,'manual'),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:27:34',33.299286,-111.68236,'Network'),('aqm5','2020-03-28 14:23:14',33.299421,-111.681322,'manual'),('aqm5','2020-03-28 14:23:34',33.300326,-111.680764,'manual'),('aqm5','2020-03-28 14:23:44',33.299806,-111.680818,'manual'),('aqm5','2020-03-28 14:23:54',33.300658,-111.678672,'manual'),('aqm5','2020-03-28 14:24:04',33.301878,-111.680775,'manual'),('aqm1','2014-03-21 20:27:09',33.3099177,-111.6726974,'manual'),('aqm1','2020-05-29 20:27:09',33.3099177,-111.6726974,'manual'),('aqm1','2020-05-30 02:27:09',33.3099177,-111.6926974,'manual');
/*!40000 ALTER TABLE `aqmdata_common` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aqmdata_dylos`
--

DROP TABLE IF EXISTS `aqmdata_dylos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aqmdata_dylos` (
  `deviceId` varchar(64) NOT NULL,
  `dateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `smallParticle` int(11) NOT NULL,
  `largeParticle` int(11) NOT NULL,
  `userId` varchar(32) DEFAULT NULL,
  KEY `dylos_common_fk` (`deviceId`,`dateTime`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aqmdata_dylos`
--

LOCK TABLES `aqmdata_dylos` WRITE;
/*!40000 ALTER TABLE `aqmdata_dylos` DISABLE KEYS */;
INSERT INTO `aqmdata_dylos` VALUES ('aqm0','2020-03-21 10:28:55',0,0,'user0'),('aqm0','2020-03-21 11:10:55',8,3,'user0'),('aqm0','2020-03-21 11:11:55',10,2,'user0'),('aqm1','2020-05-28 05:46:00',393,26,'user1'),('aqm1','2020-05-28 05:46:00',393,26,'user1'),('aqm1','2020-05-28 05:46:00',368,16,'user1'),('aqm1','2020-05-28 05:46:00',368,16,'user1'),('aqm1','2020-05-28 05:46:00',368,16,'user1'),('aqm1','2020-05-28 05:46:00',368,16,'user1'),('aqm1','2020-05-28 05:46:00',393,26,'user1'),('aqm1','2020-05-28 05:46:00',393,26,'user1'),('aqm1','2020-05-28 05:46:00',393,26,'user1'),('aqm1','2020-05-28 05:46:00',393,26,'user1'),('aqm0','2020-03-22 03:37:09',93,26,'user0'),('aqm2','2020-05-28 05:46:13',137,342,'user2'),('aqm2','2020-05-28 05:46:13',130,328,'user2'),('aqm2','2020-05-28 05:46:13',114,323,'user2'),('aqm2','2020-05-28 05:46:13',100,322,'user2'),('aqm2','2020-05-28 05:46:13',104,324,'user2'),('aqm2','2020-05-28 05:46:13',90,318,'user2'),('aqm2','2020-05-28 05:46:13',94,318,'user2'),('aqm2','2020-05-28 05:46:13',82,311,'user2'),('aqm2','2020-05-28 05:46:13',84,314,'user2'),('aqm2','2020-05-28 05:46:13',83,312,'user2'),('aqm2','2020-05-28 05:46:13',81,309,'user2'),('aqm2','2020-05-28 05:46:13',76,308,'user2'),('aqm2','2020-05-28 05:46:13',75,310,'user2'),('aqm2','2020-05-28 05:46:13',74,307,'user2'),('aqm2','2020-05-28 05:46:13',77,308,'user2'),('aqm2','2020-05-28 05:46:13',69,307,'user2'),('aqm2','2020-05-28 05:46:13',64,309,'user2'),('aqm2','2020-05-28 05:46:13',73,308,'user2'),('aqm2','2020-05-28 05:46:13',70,309,'user2'),('aqm3','2020-03-27 14:23:14',71,15,'user3'),('aqm3','2020-03-27 14:23:34',71,15,'user3'),('aqm3','2020-03-27 14:23:44',71,15,'user3'),('aqm3','2020-03-27 14:23:54',71,15,'user3'),('aqm3','2020-03-27 14:24:04',71,15,'user3'),('aqm3','2020-03-27 14:24:14',71,15,'user3'),('aqm3','2020-03-27 14:24:24',71,15,'user3'),('aqm3','2020-03-27 14:24:44',71,15,'user3'),('aqm3','2020-03-27 14:25:04',68,16,'user3'),('aqm3','2020-03-27 14:25:24',87,19,'user3'),('aqm3','2020-03-27 14:25:34',87,19,'user3'),('aqm3','2020-03-27 14:25:54',87,19,'user3'),('aqm3','2020-03-27 14:26:14',87,19,'user3'),('aqm3','2020-03-27 14:26:34',91,23,'user3'),('aqm3','2020-03-27 14:26:44',91,23,'user3'),('aqm3','2020-03-27 14:26:54',91,23,'user3'),('aqm3','2020-03-27 14:27:04',91,23,'user3'),('aqm5','2020-03-28 14:23:14',71,12,'user5'),('aqm5','2020-03-28 14:23:34',72,15,'user5'),('aqm5','2020-03-28 14:23:44',74,11,'user5'),('aqm5','2020-03-28 14:23:54',72,15,'user5'),('aqm5','2020-03-28 14:24:04',71,14,'user5'),('aqm1','2014-03-21 20:27:09',93,26,'user1'),('aqm1','2020-05-29 20:27:09',993,26,'user1'),('aqm1','2020-05-30 02:27:09',999,26,'user1');
/*!40000 ALTER TABLE `aqmdata_dylos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aqmdata_sensordrone`
--

DROP TABLE IF EXISTS `aqmdata_sensordrone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aqmdata_sensordrone` (
  `deviceId` varchar(64) NOT NULL,
  `dateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `pressureData` int(11) DEFAULT NULL,
  `tempData` int(11) DEFAULT NULL,
  `coData` int(11) DEFAULT NULL,
  `humidityData` int(11) DEFAULT NULL,
  `co2SensorId` varchar(64) DEFAULT NULL,
  `co2Data` int(11) DEFAULT NULL,
  KEY `sensordrone_common_fk` (`deviceId`,`dateTime`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aqmdata_sensordrone`
--

LOCK TABLES `aqmdata_sensordrone` WRITE;
/*!40000 ALTER TABLE `aqmdata_sensordrone` DISABLE KEYS */;
INSERT INTO `aqmdata_sensordrone` VALUES ('SensorDrone00:00:00:00:00:00','2020-03-21 11:15:55',1,24,1,30,'unknown',-1),('SensorDrone00:00:00:00:00:00','2020-03-21 11:16:50',1,22,0,29,'unknown',-1),('SensorDrone11:00:00:00:00:00','2020-03-21 11:32:55',1,1,1,1,'unknown',-1),('SensorDrone11:00:00:00:00:00','2020-03-21 11:34:55',1,24,1,1,'unknown',-1),('SensorDrone12:00:00:00:00:00','2020-03-21 11:36:55',1,24,1,1,'unknown',-1),('SensorDrone00:00:00:00:00:00','2020-03-13 21:28:55',1,1,1,1,'unknown',-1),('SensorDrone00:00:00:00:00:00','2020-03-21 21:28:55',1,1,1,1,'unknown',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:02:45',96448,26,7,32,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:03:38',96452,26,20,32,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:04:38',96448,26,-3,32,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:05:38',96444,25,39,32,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:06:38',96440,25,-3,32,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:08:01',96440,25,9,32,'UNKNOWN',1700),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:09:01',96432,25,-2,32,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:10:00',96436,25,-2,32,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 03:11:16',96428,25,-2,32,'UNKNOWN',1200),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 11:07:25',96292,25,-2,32,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-22 11:08:54',96280,25,-2,32,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:16:54',96268,25,-2,34,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:17:13',96264,25,-2,34,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:18:19',96260,25,-2,34,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:19:20',96260,25,-2,34,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:20:54',96264,25,-1,34,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:21:29',96268,25,-2,34,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:22:42',96264,25,-2,35,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:23:20',96268,25,-2,34,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:24:12',96264,25,-2,34,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:24:15',96264,25,-2,34,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:25:18',96264,25,-2,34,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:26:32',96264,25,-2,34,'UNKNOWN',-1),('SensorDroneB8:FF:FE:B9:C3:FA','2020-03-27 14:27:34',96264,25,-2,34,'UNKNOWN',-1);
/*!40000 ALTER TABLE `aqmdata_sensordrone` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `server_push_event`
--

DROP TABLE IF EXISTS `server_push_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `server_push_event` (
  `EVENTTIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `RESPONSECODE` int(11) NOT NULL,
  `DEVICETYPE` int(11) DEFAULT NULL,
  `MESSAGE` varchar(1024) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `server_push_event`
--

LOCK TABLES `server_push_event` WRITE;
/*!40000 ALTER TABLE `server_push_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `server_push_event` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-05-29 21:16:51
