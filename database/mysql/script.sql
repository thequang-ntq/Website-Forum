-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: 22t1020362_forum
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `baiviet`
--

DROP TABLE IF EXISTS `baiviet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `baiviet` (
  `MaBaiViet` bigint NOT NULL AUTO_INCREMENT,
  `TieuDe` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `NoiDung` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TaiKhoanTao` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaTheLoai` int NOT NULL,
  `DanhGia` decimal(5,2) DEFAULT NULL,
  `TrangThai` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Active',
  `ThoiDiemTao` datetime DEFAULT CURRENT_TIMESTAMP,
  `ThoiDiemCapNhat` datetime DEFAULT NULL,
  PRIMARY KEY (`MaBaiViet`),
  KEY `fk_baiviet_taikhoan` (`TaiKhoanTao`),
  KEY `fk_baiviet_theloai` (`MaTheLoai`),
  CONSTRAINT `fk_baiviet_taikhoan` FOREIGN KEY (`TaiKhoanTao`) REFERENCES `taikhoan` (`TenDangNhap`),
  CONSTRAINT `fk_baiviet_theloai` FOREIGN KEY (`MaTheLoai`) REFERENCES `theloai` (`MaTheLoai`),
  CONSTRAINT `chk_baiviet_danhgia` CHECK ((`DanhGia` between 0 and 5)),
  CONSTRAINT `chk_baiviet_trangthai` CHECK ((`TrangThai` in (_utf8mb4'Active',_utf8mb4'Deleted',_utf8mb4'Hidden')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `baiviet`
--

LOCK TABLES `baiviet` WRITE;
/*!40000 ALTER TABLE `baiviet` DISABLE KEYS */;
/*!40000 ALTER TABLE `baiviet` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_BaiViet_Update` BEFORE UPDATE ON `baiviet` FOR EACH ROW BEGIN
    SET NEW.`ThoiDiemCapNhat` = NOW();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_BaiViet_SoftDelete` BEFORE DELETE ON `baiviet` FOR EACH ROW BEGIN
    -- Cập nhật Bình luận
    UPDATE `BinhLuan`
    SET `TrangThai` = 'Deleted', `ThoiDiemCapNhat` = NOW()
    WHERE `MaBaiViet` = OLD.`MaBaiViet`;
    
    -- Xóa Lượt thích
    DELETE ltbl FROM `LuotThichBinhLuan` ltbl
    INNER JOIN `BinhLuan` bl ON ltbl.`MaBinhLuan` = bl.`MaBinhLuan`
    WHERE bl.`MaBaiViet` = OLD.`MaBaiViet`;
    
    -- Xóa Đánh giá
    DELETE FROM `DanhGiaBaiViet`
    WHERE `MaBaiViet` = OLD.`MaBaiViet`;
    
    -- Đánh dấu xóa bài viết
    UPDATE `BaiViet`
    SET `TrangThai` = 'Deleted', `ThoiDiemCapNhat` = NOW()
    WHERE `MaBaiViet` = OLD.`MaBaiViet`;
    
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Soft delete completed';
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `binhluan`
--

DROP TABLE IF EXISTS `binhluan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `binhluan` (
  `MaBinhLuan` bigint NOT NULL AUTO_INCREMENT,
  `TieuDe` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `NoiDung` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `Url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TaiKhoanTao` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `MaBaiViet` bigint NOT NULL,
  `SoLuotThich` int DEFAULT '0',
  `TrangThai` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Active',
  `ThoiDiemTao` datetime DEFAULT CURRENT_TIMESTAMP,
  `ThoiDiemCapNhat` datetime DEFAULT NULL,
  PRIMARY KEY (`MaBinhLuan`),
  KEY `fk_binhluan_taikhoan` (`TaiKhoanTao`),
  KEY `fk_binhluan_baiviet` (`MaBaiViet`),
  CONSTRAINT `fk_binhluan_baiviet` FOREIGN KEY (`MaBaiViet`) REFERENCES `baiviet` (`MaBaiViet`),
  CONSTRAINT `fk_binhluan_taikhoan` FOREIGN KEY (`TaiKhoanTao`) REFERENCES `taikhoan` (`TenDangNhap`),
  CONSTRAINT `chk_binhluan_soluotthich` CHECK ((`SoLuotThich` >= 0)),
  CONSTRAINT `chk_binhluan_trangthai` CHECK ((`TrangThai` in (_utf8mb4'Active',_utf8mb4'Deleted',_utf8mb4'Hidden')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `binhluan`
--

LOCK TABLES `binhluan` WRITE;
/*!40000 ALTER TABLE `binhluan` DISABLE KEYS */;
/*!40000 ALTER TABLE `binhluan` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_BinhLuan_BeforeInsert` BEFORE INSERT ON `binhluan` FOR EACH ROW BEGIN
    SET NEW.`TieuDe` = IF(CHAR_LENGTH(NEW.`NoiDung`) <= 50, 
                          NEW.`NoiDung`, 
                          CONCAT(LEFT(NEW.`NoiDung`, 50), '...'));
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_BinhLuan_BeforeUpdate` BEFORE UPDATE ON `binhluan` FOR EACH ROW BEGIN
    IF NEW.`NoiDung` != OLD.`NoiDung` OR OLD.`NoiDung` IS NULL THEN
        SET NEW.`TieuDe` = IF(CHAR_LENGTH(NEW.`NoiDung`) <= 50, 
                              NEW.`NoiDung`, 
                              CONCAT(LEFT(NEW.`NoiDung`, 50), '...'));
        SET NEW.`ThoiDiemCapNhat` = NOW();
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_BinhLuan_SoftDelete` BEFORE DELETE ON `binhluan` FOR EACH ROW BEGIN
    -- Xóa Lượt thích
    DELETE FROM `LuotThichBinhLuan`
    WHERE `MaBinhLuan` = OLD.`MaBinhLuan`;
    
    -- Đánh dấu xóa bình luận
    UPDATE `BinhLuan`
    SET `TrangThai` = 'Deleted', `ThoiDiemCapNhat` = NOW()
    WHERE `MaBinhLuan` = OLD.`MaBinhLuan`;
    
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Soft delete completed';
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `danhgiabaiviet`
--

DROP TABLE IF EXISTS `danhgiabaiviet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `danhgiabaiviet` (
  `MaDanhGia` bigint NOT NULL AUTO_INCREMENT,
  `MaBaiViet` bigint NOT NULL,
  `TenDangNhap` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Diem` decimal(5,2) NOT NULL,
  `ThoiDiemDanhGia` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`MaDanhGia`),
  UNIQUE KEY `uq_danhgiabaiviet` (`MaBaiViet`,`TenDangNhap`),
  KEY `fk_danhgiabaiviet_baiviet` (`MaBaiViet`),
  KEY `fk_danhgiabaiviet_taikhoan` (`TenDangNhap`),
  CONSTRAINT `fk_danhgiabaiviet_baiviet` FOREIGN KEY (`MaBaiViet`) REFERENCES `baiviet` (`MaBaiViet`),
  CONSTRAINT `fk_danhgiabaiviet_taikhoan` FOREIGN KEY (`TenDangNhap`) REFERENCES `taikhoan` (`TenDangNhap`),
  CONSTRAINT `chk_danhgiabaiviet_diem` CHECK ((`Diem` between 0 and 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `danhgiabaiviet`
--

LOCK TABLES `danhgiabaiviet` WRITE;
/*!40000 ALTER TABLE `danhgiabaiviet` DISABLE KEYS */;
/*!40000 ALTER TABLE `danhgiabaiviet` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_DanhGiaBaiViet_Insert` AFTER INSERT ON `danhgiabaiviet` FOR EACH ROW BEGIN
    UPDATE `BaiViet`
    SET `DanhGia` = (
        SELECT AVG(`Diem`)
        FROM `DanhGiaBaiViet`
        WHERE `MaBaiViet` = NEW.`MaBaiViet`
    )
    WHERE `MaBaiViet` = NEW.`MaBaiViet`;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_DanhGiaBaiViet_Delete` AFTER DELETE ON `danhgiabaiviet` FOR EACH ROW BEGIN
    UPDATE `BaiViet`
    SET `DanhGia` = (
        SELECT AVG(`Diem`)
        FROM `DanhGiaBaiViet`
        WHERE `MaBaiViet` = OLD.`MaBaiViet`
    )
    WHERE `MaBaiViet` = OLD.`MaBaiViet`;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `luotthichbinhluan`
--

DROP TABLE IF EXISTS `luotthichbinhluan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `luotthichbinhluan` (
  `MaLuotThich` bigint NOT NULL AUTO_INCREMENT,
  `MaBinhLuan` bigint NOT NULL,
  `TenDangNhap` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ThoiDiemThich` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`MaLuotThich`),
  UNIQUE KEY `uq_luotthichbinhluan` (`MaBinhLuan`,`TenDangNhap`),
  KEY `fk_luotthichbinhluan_binhluan` (`MaBinhLuan`),
  KEY `fk_luotthichbinhluan_taikhoan` (`TenDangNhap`),
  CONSTRAINT `fk_luotthichbinhluan_binhluan` FOREIGN KEY (`MaBinhLuan`) REFERENCES `binhluan` (`MaBinhLuan`),
  CONSTRAINT `fk_luotthichbinhluan_taikhoan` FOREIGN KEY (`TenDangNhap`) REFERENCES `taikhoan` (`TenDangNhap`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `luotthichbinhluan`
--

LOCK TABLES `luotthichbinhluan` WRITE;
/*!40000 ALTER TABLE `luotthichbinhluan` DISABLE KEYS */;
/*!40000 ALTER TABLE `luotthichbinhluan` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_LuotThichBinhLuan_Insert` AFTER INSERT ON `luotthichbinhluan` FOR EACH ROW BEGIN
    UPDATE `BinhLuan`
    SET `SoLuotThich` = `SoLuotThich` + 1
    WHERE `MaBinhLuan` = NEW.`MaBinhLuan`;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_LuotThichBinhLuan_Delete` AFTER DELETE ON `luotthichbinhluan` FOR EACH ROW BEGIN
    UPDATE `BinhLuan`
    SET `SoLuotThich` = IF(`SoLuotThich` > 0, `SoLuotThich` - 1, 0)
    WHERE `MaBinhLuan` = OLD.`MaBinhLuan`;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `taikhoan`
--

DROP TABLE IF EXISTS `taikhoan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `taikhoan` (
  `TenDangNhap` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `MatKhau` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `Quyen` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'User',
  `TrangThai` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Active',
  `ThoiDiemTao` datetime DEFAULT CURRENT_TIMESTAMP,
  `ThoiDiemCapNhat` datetime DEFAULT NULL,
  PRIMARY KEY (`TenDangNhap`),
  CONSTRAINT `chk_taikhoan_quyen` CHECK ((`Quyen` in (_utf8mb4'Admin',_utf8mb4'User'))),
  CONSTRAINT `chk_taikhoan_trangthai` CHECK ((`TrangThai` in (_utf8mb4'Active',_utf8mb4'Deleted',_utf8mb4'Hidden')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `taikhoan`
--

LOCK TABLES `taikhoan` WRITE;
/*!40000 ALTER TABLE `taikhoan` DISABLE KEYS */;
INSERT INTO `taikhoan` VALUES ('admin1','mkadmin','Admin','Active','2025-11-09 08:44:59',NULL),('test','12','User','Active','2025-11-09 09:00:21','2025-11-09 09:01:02'),('user1','mkuser','User','Active','2025-11-09 08:44:59',NULL);
/*!40000 ALTER TABLE `taikhoan` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_TaiKhoan_Update` BEFORE UPDATE ON `taikhoan` FOR EACH ROW BEGIN
    SET NEW.`ThoiDiemCapNhat` = NOW();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_TaiKhoan_SoftDelete` BEFORE DELETE ON `taikhoan` FOR EACH ROW BEGIN
    -- Cập nhật Bài viết
    UPDATE `BaiViet`
    SET `TrangThai` = 'Deleted', `ThoiDiemCapNhat` = NOW()
    WHERE `TaiKhoanTao` = OLD.`TenDangNhap`;
    
    -- Cập nhật Bình luận
    UPDATE `BinhLuan`
    SET `TrangThai` = 'Deleted', `ThoiDiemCapNhat` = NOW()
    WHERE `TaiKhoanTao` = OLD.`TenDangNhap`;
    
    -- Xóa Lượt thích
    DELETE FROM `LuotThichBinhLuan`
    WHERE `TenDangNhap` = OLD.`TenDangNhap`;
    
    -- Xóa Đánh giá
    DELETE FROM `DanhGiaBaiViet`
    WHERE `TenDangNhap` = OLD.`TenDangNhap`;
    
    -- Đánh dấu xóa tài khoản (cập nhật trực tiếp)
    UPDATE `TaiKhoan`
    SET `TrangThai` = 'Deleted', `ThoiDiemCapNhat` = NOW()
    WHERE `TenDangNhap` = OLD.`TenDangNhap`;
    
    -- Ngăn việc xóa thực sự
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Soft delete completed';
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `theloai`
--

DROP TABLE IF EXISTS `theloai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `theloai` (
  `MaTheLoai` int NOT NULL AUTO_INCREMENT,
  `TenTheLoai` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TrangThai` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Active',
  `ThoiDiemTao` datetime DEFAULT CURRENT_TIMESTAMP,
  `ThoiDiemCapNhat` datetime DEFAULT NULL,
  PRIMARY KEY (`MaTheLoai`),
  UNIQUE KEY `uq_theloai` (`TenTheLoai`),
  CONSTRAINT `chk_theloai_trangthai` CHECK ((`TrangThai` in (_utf8mb4'Active',_utf8mb4'Deleted',_utf8mb4'Hidden')))
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `theloai`
--

LOCK TABLES `theloai` WRITE;
/*!40000 ALTER TABLE `theloai` DISABLE KEYS */;
INSERT INTO `theloai` VALUES (1,'Công nghệ & Tin học','Active','2025-11-09 08:44:59',NULL),(2,'Học tập & Kinh nghiệm học','Active','2025-11-09 08:44:59',NULL),(3,'Đời sống & Xã hội','Active','2025-11-09 08:44:59',NULL),(4,'Giải trí & Âm nhạc','Active','2025-11-09 08:44:59',NULL),(5,'Thể thao & Sức khỏe','Active','2025-11-09 08:44:59',NULL),(6,'Khoa học & Công nghệ','Active','2025-11-09 08:44:59',NULL),(7,'Phần mềm & Ứng dụng','Active','2025-11-09 08:44:59',NULL),(8,'Lập trình & Phát triển web','Active','2025-11-09 08:44:59',NULL),(9,'Trò chơi & eSports','Active','2025-11-09 08:44:59',NULL),(10,'Thời trang & Làm đẹp','Active','2025-11-09 08:44:59',NULL),(11,'Ẩm thực & Nấu ăn','Active','2025-11-09 08:44:59',NULL),(12,'Du lịch & Khám phá','Active','2025-11-09 08:44:59',NULL),(13,'Sự kiện & Tin tức','Active','2025-11-09 08:44:59',NULL),(14,'Giao lưu & Kết bạn','Active','2025-11-09 08:44:59',NULL),(15,'Góp ý & Hỏi đáp','Active','2025-11-09 08:44:59',NULL),(16,'Thông báo từ quản trị viên','Active','2025-11-09 08:44:59',NULL),(17,'Khác','Active','2025-11-09 08:44:59',NULL);
/*!40000 ALTER TABLE `theloai` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_TheLoai_Update` BEFORE UPDATE ON `theloai` FOR EACH ROW BEGIN
    SET NEW.`ThoiDiemCapNhat` = NOW();
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_TheLoai_SoftDelete` BEFORE DELETE ON `theloai` FOR EACH ROW BEGIN
    -- Cập nhật Bài viết
    UPDATE `BaiViet`
    SET `TrangThai` = 'Deleted', `ThoiDiemCapNhat` = NOW()
    WHERE `MaTheLoai` = OLD.`MaTheLoai`;
    
    -- Cập nhật Bình luận
    UPDATE `BinhLuan` bl
    INNER JOIN `BaiViet` bv ON bl.`MaBaiViet` = bv.`MaBaiViet`
    SET bl.`TrangThai` = 'Deleted', bl.`ThoiDiemCapNhat` = NOW()
    WHERE bv.`MaTheLoai` = OLD.`MaTheLoai`;
    
    -- Xóa Lượt thích
    DELETE ltbl FROM `LuotThichBinhLuan` ltbl
    INNER JOIN `BinhLuan` bl ON ltbl.`MaBinhLuan` = bl.`MaBinhLuan`
    INNER JOIN `BaiViet` bv ON bl.`MaBaiViet` = bv.`MaBaiViet`
    WHERE bv.`MaTheLoai` = OLD.`MaTheLoai`;
    
    -- Xóa Đánh giá
    DELETE dgbv FROM `DanhGiaBaiViet` dgbv
    INNER JOIN `BaiViet` bv ON dgbv.`MaBaiViet` = bv.`MaBaiViet`
    WHERE bv.`MaTheLoai` = OLD.`MaTheLoai`;
    
    -- Đánh dấu xóa thể loại
    UPDATE `TheLoai`
    SET `TrangThai` = 'Deleted', `ThoiDiemCapNhat` = NOW()
    WHERE `MaTheLoai` = OLD.`MaTheLoai`;
    
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Soft delete completed';
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping events for database '22t1020362_forum'
--

--
-- Dumping routines for database '22t1020362_forum'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-09 10:00:46
