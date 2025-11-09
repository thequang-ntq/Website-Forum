-- Cơ sở dữ liệu MySQL 8.0
CREATE DATABASE IF NOT EXISTS `22t1020362_forum` 
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `22t1020362_forum`;

-- Bảng TaiKhoan
CREATE TABLE IF NOT EXISTS `TaiKhoan` (
    `TenDangNhap` VARCHAR(150) NOT NULL,
    `MatKhau` VARCHAR(255) NOT NULL,
    `Quyen` VARCHAR(50) DEFAULT 'User',
    `TrangThai` VARCHAR(50) DEFAULT 'Active',
    `ThoiDiemTao` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `ThoiDiemCapNhat` DATETIME DEFAULT NULL,
    PRIMARY KEY (`TenDangNhap`),
    CONSTRAINT `chk_taikhoan_quyen` CHECK (`Quyen` IN ('Admin', 'User')),
    CONSTRAINT `chk_taikhoan_trangthai` CHECK (`TrangThai` IN ('Active', 'Deleted', 'Hidden'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng TheLoai
CREATE TABLE IF NOT EXISTS `TheLoai` (
    `MaTheLoai` INT AUTO_INCREMENT,
    `TenTheLoai` VARCHAR(200) NOT NULL,
    `TrangThai` VARCHAR(50) DEFAULT 'Active',
    `ThoiDiemTao` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `ThoiDiemCapNhat` DATETIME DEFAULT NULL,
    PRIMARY KEY (`MaTheLoai`),
    UNIQUE KEY `uq_theloai` (`TenTheLoai`),
    CONSTRAINT `chk_theloai_trangthai` CHECK (`TrangThai` IN ('Active', 'Deleted', 'Hidden'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng BaiViet
CREATE TABLE IF NOT EXISTS `BaiViet` (
    `MaBaiViet` BIGINT AUTO_INCREMENT,
    `TieuDe` VARCHAR(255) NOT NULL,
    `NoiDung` TEXT NOT NULL,
    `Url` VARCHAR(255) DEFAULT NULL,
    `TaiKhoanTao` VARCHAR(150) NOT NULL,
    `MaTheLoai` INT NOT NULL,
    `DanhGia` DECIMAL(5,2) DEFAULT NULL,
    `TrangThai` VARCHAR(50) DEFAULT 'Active',
    `ThoiDiemTao` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `ThoiDiemCapNhat` DATETIME DEFAULT NULL,
    PRIMARY KEY (`MaBaiViet`),
    KEY `fk_baiviet_taikhoan` (`TaiKhoanTao`),
    KEY `fk_baiviet_theloai` (`MaTheLoai`),
    CONSTRAINT `fk_baiviet_taikhoan` FOREIGN KEY (`TaiKhoanTao`) REFERENCES `TaiKhoan` (`TenDangNhap`),
    CONSTRAINT `fk_baiviet_theloai` FOREIGN KEY (`MaTheLoai`) REFERENCES `TheLoai` (`MaTheLoai`),
    CONSTRAINT `chk_baiviet_danhgia` CHECK (`DanhGia` BETWEEN 0 AND 5),
    CONSTRAINT `chk_baiviet_trangthai` CHECK (`TrangThai` IN ('Active', 'Deleted', 'Hidden'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng BinhLuan
CREATE TABLE IF NOT EXISTS `BinhLuan` (
    `MaBinhLuan` BIGINT AUTO_INCREMENT,
    `TieuDe` VARCHAR(255) DEFAULT NULL,
    `NoiDung` TEXT NOT NULL,
    `Url` VARCHAR(255) DEFAULT NULL,
    `TaiKhoanTao` VARCHAR(150) NOT NULL,
    `MaBaiViet` BIGINT NOT NULL,
    `SoLuotThich` INT DEFAULT 0,
    `TrangThai` VARCHAR(50) DEFAULT 'Active',
    `ThoiDiemTao` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `ThoiDiemCapNhat` DATETIME DEFAULT NULL,
    PRIMARY KEY (`MaBinhLuan`),
    KEY `fk_binhluan_taikhoan` (`TaiKhoanTao`),
    KEY `fk_binhluan_baiviet` (`MaBaiViet`),
    CONSTRAINT `fk_binhluan_taikhoan` FOREIGN KEY (`TaiKhoanTao`) REFERENCES `TaiKhoan` (`TenDangNhap`),
    CONSTRAINT `fk_binhluan_baiviet` FOREIGN KEY (`MaBaiViet`) REFERENCES `BaiViet` (`MaBaiViet`),
    CONSTRAINT `chk_binhluan_soluotthich` CHECK (`SoLuotThich` >= 0),
    CONSTRAINT `chk_binhluan_trangthai` CHECK (`TrangThai` IN ('Active', 'Deleted', 'Hidden'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng LuotThichBinhLuan
CREATE TABLE IF NOT EXISTS `LuotThichBinhLuan` (
    `MaLuotThich` BIGINT AUTO_INCREMENT,
    `MaBinhLuan` BIGINT NOT NULL,
    `TenDangNhap` VARCHAR(150) NOT NULL,
    `ThoiDiemThich` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`MaLuotThich`),
    UNIQUE KEY `uq_luotthichbinhluan` (`MaBinhLuan`, `TenDangNhap`),
    KEY `fk_luotthichbinhluan_binhluan` (`MaBinhLuan`),
    KEY `fk_luotthichbinhluan_taikhoan` (`TenDangNhap`),
    CONSTRAINT `fk_luotthichbinhluan_binhluan` FOREIGN KEY (`MaBinhLuan`) REFERENCES `BinhLuan` (`MaBinhLuan`),
    CONSTRAINT `fk_luotthichbinhluan_taikhoan` FOREIGN KEY (`TenDangNhap`) REFERENCES `TaiKhoan` (`TenDangNhap`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bảng DanhGiaBaiViet
CREATE TABLE IF NOT EXISTS `DanhGiaBaiViet` (
    `MaDanhGia` BIGINT AUTO_INCREMENT,
    `MaBaiViet` BIGINT NOT NULL,
    `TenDangNhap` VARCHAR(150) NOT NULL,
    `Diem` DECIMAL(5,2) NOT NULL,
    `ThoiDiemDanhGia` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`MaDanhGia`),
    UNIQUE KEY `uq_danhgiabaiviet` (`MaBaiViet`, `TenDangNhap`),
    KEY `fk_danhgiabaiviet_baiviet` (`MaBaiViet`),
    KEY `fk_danhgiabaiviet_taikhoan` (`TenDangNhap`),
    CONSTRAINT `fk_danhgiabaiviet_baiviet` FOREIGN KEY (`MaBaiViet`) REFERENCES `BaiViet` (`MaBaiViet`),
    CONSTRAINT `fk_danhgiabaiviet_taikhoan` FOREIGN KEY (`TenDangNhap`) REFERENCES `TaiKhoan` (`TenDangNhap`),
    CONSTRAINT `chk_danhgiabaiviet_diem` CHECK (`Diem` BETWEEN 0 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TRIGGERS
-- =====================================================

-- Trigger: Soft Delete TaiKhoan
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_TaiKhoan_SoftDelete`$$
CREATE TRIGGER `trg_TaiKhoan_SoftDelete`
BEFORE DELETE ON `TaiKhoan`
FOR EACH ROW
BEGIN
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
END$$
DELIMITER ;

-- Trigger: Soft Delete TheLoai
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_TheLoai_SoftDelete`$$
CREATE TRIGGER `trg_TheLoai_SoftDelete`
BEFORE DELETE ON `TheLoai`
FOR EACH ROW
BEGIN
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
END$$
DELIMITER ;

-- Trigger: Soft Delete BaiViet
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_BaiViet_SoftDelete`$$
CREATE TRIGGER `trg_BaiViet_SoftDelete`
BEFORE DELETE ON `BaiViet`
FOR EACH ROW
BEGIN
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
END$$
DELIMITER ;

-- Trigger: Soft Delete BinhLuan
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_BinhLuan_SoftDelete`$$
CREATE TRIGGER `trg_BinhLuan_SoftDelete`
BEFORE DELETE ON `BinhLuan`
FOR EACH ROW
BEGIN
    -- Xóa Lượt thích
    DELETE FROM `LuotThichBinhLuan`
    WHERE `MaBinhLuan` = OLD.`MaBinhLuan`;
    
    -- Đánh dấu xóa bình luận
    UPDATE `BinhLuan`
    SET `TrangThai` = 'Deleted', `ThoiDiemCapNhat` = NOW()
    WHERE `MaBinhLuan` = OLD.`MaBinhLuan`;
    
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Soft delete completed';
END$$
DELIMITER ;

-- Trigger: Auto Update ThoiDiemCapNhat cho TaiKhoan
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_TaiKhoan_Update`$$
CREATE TRIGGER `trg_TaiKhoan_Update`
BEFORE UPDATE ON `TaiKhoan`
FOR EACH ROW
BEGIN
    SET NEW.`ThoiDiemCapNhat` = NOW();
END$$
DELIMITER ;

-- Trigger: Auto Update ThoiDiemCapNhat cho TheLoai
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_TheLoai_Update`$$
CREATE TRIGGER `trg_TheLoai_Update`
BEFORE UPDATE ON `TheLoai`
FOR EACH ROW
BEGIN
    SET NEW.`ThoiDiemCapNhat` = NOW();
END$$
DELIMITER ;

-- Trigger: Auto Update ThoiDiemCapNhat cho BaiViet
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_BaiViet_Update`$$
CREATE TRIGGER `trg_BaiViet_Update`
BEFORE UPDATE ON `BaiViet`
FOR EACH ROW
BEGIN
    SET NEW.`ThoiDiemCapNhat` = NOW();
END$$
DELIMITER ;

-- Trigger: Auto TieuDe và ThoiDiemCapNhat cho BinhLuan (INSERT & UPDATE)
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_BinhLuan_BeforeInsert`$$
CREATE TRIGGER `trg_BinhLuan_BeforeInsert`
BEFORE INSERT ON `BinhLuan`
FOR EACH ROW
BEGIN
    SET NEW.`TieuDe` = IF(CHAR_LENGTH(NEW.`NoiDung`) <= 50, 
                          NEW.`NoiDung`, 
                          CONCAT(LEFT(NEW.`NoiDung`, 50), '...'));
END$$
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS `trg_BinhLuan_BeforeUpdate`$$
CREATE TRIGGER `trg_BinhLuan_BeforeUpdate`
BEFORE UPDATE ON `BinhLuan`
FOR EACH ROW
BEGIN
    IF NEW.`NoiDung` != OLD.`NoiDung` OR OLD.`NoiDung` IS NULL THEN
        SET NEW.`TieuDe` = IF(CHAR_LENGTH(NEW.`NoiDung`) <= 50, 
                              NEW.`NoiDung`, 
                              CONCAT(LEFT(NEW.`NoiDung`, 50), '...'));
        SET NEW.`ThoiDiemCapNhat` = NOW();
    END IF;
END$$
DELIMITER ;

-- Trigger: Tăng SoLuotThich khi INSERT LuotThichBinhLuan
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_LuotThichBinhLuan_Insert`$$
CREATE TRIGGER `trg_LuotThichBinhLuan_Insert`
AFTER INSERT ON `LuotThichBinhLuan`
FOR EACH ROW
BEGIN
    UPDATE `BinhLuan`
    SET `SoLuotThich` = `SoLuotThich` + 1
    WHERE `MaBinhLuan` = NEW.`MaBinhLuan`;
END$$
DELIMITER ;

-- Trigger: Giảm SoLuotThich khi DELETE LuotThichBinhLuan
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_LuotThichBinhLuan_Delete`$$
CREATE TRIGGER `trg_LuotThichBinhLuan_Delete`
AFTER DELETE ON `LuotThichBinhLuan`
FOR EACH ROW
BEGIN
    UPDATE `BinhLuan`
    SET `SoLuotThich` = IF(`SoLuotThich` > 0, `SoLuotThich` - 1, 0)
    WHERE `MaBinhLuan` = OLD.`MaBinhLuan`;
END$$
DELIMITER ;

-- Trigger: Cập nhật DanhGia khi INSERT DanhGiaBaiViet
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_DanhGiaBaiViet_Insert`$$
CREATE TRIGGER `trg_DanhGiaBaiViet_Insert`
AFTER INSERT ON `DanhGiaBaiViet`
FOR EACH ROW
BEGIN
    UPDATE `BaiViet`
    SET `DanhGia` = (
        SELECT AVG(`Diem`)
        FROM `DanhGiaBaiViet`
        WHERE `MaBaiViet` = NEW.`MaBaiViet`
    )
    WHERE `MaBaiViet` = NEW.`MaBaiViet`;
END$$
DELIMITER ;

-- Trigger: Cập nhật DanhGia khi DELETE DanhGiaBaiViet
DELIMITER $$
DROP TRIGGER IF EXISTS `trg_DanhGiaBaiViet_Delete`$$
CREATE TRIGGER `trg_DanhGiaBaiViet_Delete`
AFTER DELETE ON `DanhGiaBaiViet`
FOR EACH ROW
BEGIN
    UPDATE `BaiViet`
    SET `DanhGia` = (
        SELECT AVG(`Diem`)
        FROM `DanhGiaBaiViet`
        WHERE `MaBaiViet` = OLD.`MaBaiViet`
    )
    WHERE `MaBaiViet` = OLD.`MaBaiViet`;
END$$
DELIMITER ;

-- =====================================================
-- CHÈN DỮ LIỆU MẪU
-- =====================================================

INSERT IGNORE INTO `TaiKhoan` (`TenDangNhap`, `MatKhau`, `Quyen`)
VALUES  ('admin1', 'mkadmin', 'Admin'),
        ('user1', 'mkuser', 'User');

INSERT IGNORE INTO `TheLoai` (`TenTheLoai`) 
VALUES  ('Công nghệ & Tin học'),
        ('Học tập & Kinh nghiệm học'),
        ('Đời sống & Xã hội'),
        ('Giải trí & Âm nhạc'),
        ('Thể thao & Sức khỏe'),
        ('Khoa học & Công nghệ'),
        ('Phần mềm & Ứng dụng'),
        ('Lập trình & Phát triển web'),
        ('Trò chơi & eSports'),
        ('Thời trang & Làm đẹp'),
        ('Ẩm thực & Nấu ăn'),
        ('Du lịch & Khám phá'),
        ('Sự kiện & Tin tức'),
        ('Giao lưu & Kết bạn'),
        ('Góp ý & Hỏi đáp'),
        ('Thông báo từ quản trị viên'),
        ('Khác');

-- =====================================================
-- KIỂM TRA DỮ LIỆU
-- =====================================================
/*
SHOW triggers
SELECT * FROM `BinhLuan`;
SELECT * FROM `BaiViet`;
SELECT * FROM `TheLoai`;
SELECT * FROM `TaiKhoan`;
SELECT * FROM `LuotThichBinhLuan`;
SELECT * FROM `DanhGiaBaiViet`;
SHOW TRIGGERS;
*/

-- =====================================================
-- RESET DATABASE (CHỈ DÙNG KHI CẦN XÓA TOÀN BỘ)
-- =====================================================
/*
DROP TABLE IF EXISTS `LuotThichBinhLuan`;
DROP TABLE IF EXISTS `DanhGiaBaiViet`;
DROP TABLE IF EXISTS `BinhLuan`;
DROP TABLE IF EXISTS `BaiViet`;
DROP TABLE IF EXISTS `TheLoai`;
DROP TABLE IF EXISTS `TaiKhoan`;
*/