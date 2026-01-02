/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `dacs4_edunet` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `dacs4_edunet`;

CREATE TABLE IF NOT EXISTS `client_sessions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `client_name` char(200) DEFAULT NULL,
  `client_token` char(200) NOT NULL DEFAULT '0',
  `connectedAt` datetime DEFAULT NULL,
  `disconnectedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_client_sessions_established_clients` (`client_token`),
  KEY `FK_client_sessions_established_clients_2` (`client_name`),
  CONSTRAINT `FK_client_sessions_established_clients` FOREIGN KEY (`client_token`) REFERENCES `established_clients` (`client_token`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_client_sessions_established_clients_2` FOREIGN KEY (`client_name`) REFERENCES `established_clients` (`client_name`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `established_clients` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `clientInetAddress` char(100) DEFAULT NULL,
  `client_name` char(200) DEFAULT NULL,
  `client_token` char(200) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `client_token` (`client_token`),
  KEY `client_name` (`client_name`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `notifications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `client_name` char(200) DEFAULT NULL,
  `content` text NOT NULL,
  `createdAt` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_notifications_established_clients` (`client_name`),
  CONSTRAINT `FK_notifications_established_clients` FOREIGN KEY (`client_name`) REFERENCES `established_clients` (`client_name`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `server_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `server_password` char(50) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

REPLACE INTO `server_config` (`id`, `server_password`) VALUES
	(1, '123abc');

CREATE TABLE IF NOT EXISTS `system_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `client_name` char(200) DEFAULT NULL,
  `os_name` char(200) NOT NULL DEFAULT '0',
  `cpu_cores` char(50) NOT NULL DEFAULT '0',
  `cpu_load` char(50) NOT NULL DEFAULT '0',
  `ram_usage` tinytext NOT NULL,
  `disk_info` tinytext NOT NULL,
  `createdAt` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_system_info_established_clients` (`client_name`),
  CONSTRAINT `FK_system_info_established_clients` FOREIGN KEY (`client_name`) REFERENCES `established_clients` (`client_name`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
