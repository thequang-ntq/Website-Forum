-- Cơ sở dữ liệu trong SQL Server:
USE [22T1020362_Forum]
IF NOT EXISTS(SELECT * FROM sys.tables WHERE name = 'TaiKhoan')
BEGIN
	CREATE TABLE TaiKhoan (
		TenDangNhap NVARCHAR(150) NOT NULL UNIQUE,
		MatKhau NVARCHAR(255) NOT NULL, -- Tăng lên 512 nếu cần Hashing sau này
		Quyen NVARCHAR(50) DEFAULT N'User' CHECK (Quyen IN (N'Admin', N'User')),
		ThoiDiemTao DATETIME DEFAULT GETDATE(),
		ThoiDiemCapNhat DATETIME,
		CONSTRAINT pk_taikhoan PRIMARY KEY(TenDangNhap)
	);
END
GO

IF NOT EXISTS(SELECT * FROM sys.tables WHERE name = 'TheLoai')
BEGIN
	CREATE TABLE TheLoai (
		MaTheLoai INT IDENTITY(1,1),
		TenTheLoai NVARCHAR(200) NOT NULL,
		CONSTRAINT pk_theloai PRIMARY KEY (MaTheLoai),
		CONSTRAINT uq_theloai UNIQUE (TenTheLoai)
	);
END
GO

IF NOT EXISTS(SELECT * FROM sys.tables WHERE name = 'BaiViet')
BEGIN
	CREATE TABLE BaiViet (
		MaBaiViet BIGINT IDENTITY(1, 1),
		TieuDe NVARCHAR(255) NOT NULL,
		NoiDung NVARCHAR(MAX) NOT NULL,
		Url NVARCHAR(255),
		TaiKhoanTao NVARCHAR(150) NOT NULL,
		MaTheLoai INT NOT NULL,
		DanhGia DECIMAL(5,2) CHECK (DanhGia BETWEEN 0 AND 5),
		TrangThai NVARCHAR(50) DEFAULT N'Active' CHECK (TrangThai IN (N'Active', N'Deleted', N'Hidden')),
		ThoiDiemTao DATETIME DEFAULT GETDATE(),
		ThoiDiemCapNhat DATETIME,
		CONSTRAINT pk_baiviet PRIMARY KEY (MaBaiViet),
		CONSTRAINT fk_baiviet_taikhoan FOREIGN KEY (TaiKhoanTao) REFERENCES TaiKhoan (TenDangNhap) ON DELETE NO ACTION,
		CONSTRAINT fk_baiviet_theloai FOREIGN KEY (MaTheLoai) REFERENCES TheLoai (MaTheLoai) ON DELETE NO ACTION
	);
END
GO

IF NOT EXISTS(SELECT * FROM sys.tables WHERE name = 'BinhLuan')
BEGIN
	CREATE TABLE BinhLuan (
		MaBinhLuan BIGINT IDENTITY(1, 1),
		TieuDe NVARCHAR(255),
		NoiDung NVARCHAR(MAX) NOT NULL,
		Url NVARCHAR(255),
		TaiKhoanTao NVARCHAR(150) NOT NULL,
		MaBaiViet BIGINT NOT NULL,
		SoLuotThich INT DEFAULT 0 CHECK (SoLuotThich >= 0),
		TrangThai NVARCHAR(50) DEFAULT N'Active' CHECK (TrangThai IN (N'Active', N'Deleted', N'Hidden')),
		ThoiDiemTao DATETIME DEFAULT GETDATE(),
		ThoiDiemCapNhat DATETIME,
		CONSTRAINT pk_binhluan PRIMARY KEY (MaBinhLuan),
		CONSTRAINT fk_binhluan_taikhoan FOREIGN KEY (TaiKhoanTao) REFERENCES TaiKhoan (TenDangNhap) ON DELETE NO ACTION,
		CONSTRAINT fk_binhluan_baiviet FOREIGN KEY (MaBaiViet) REFERENCES BaiViet (MaBaiViet) ON DELETE NO ACTION
	);
END
GO

IF NOT EXISTS(SELECT * FROM sys.tables WHERE name = 'LuotThichBinhLuan')
BEGIN
	CREATE TABLE LuotThichBinhLuan (
		MaLuotThich BIGINT IDENTITY(1,1),
		MaBinhLuan BIGINT NOT NULL,
		TenDangNhap NVARCHAR(150) NOT NULL,
		ThoiDiemThich DATETIME DEFAULT GETDATE(),
		CONSTRAINT pk_luotthichbinhluan PRIMARY KEY (MaLuotThich),
		CONSTRAINT fk_luotthichbinhluan_binhluan FOREIGN KEY (MaBinhLuan) REFERENCES BinhLuan (MaBinhLuan) ON DELETE CASCADE,
		CONSTRAINT fk_luotthichbinhluan_taikhoan FOREIGN KEY (TenDangNhap) REFERENCES TaiKhoan (TenDangNhap) ON DELETE CASCADE,
		CONSTRAINT uq_luotthichbinhluan UNIQUE (MaBinhLuan, TenDangNhap)
	);
END
GO

IF NOT EXISTS(SELECT * FROM sys.tables WHERE name = 'DanhGiaBaiViet')
BEGIN
	CREATE TABLE DanhGiaBaiViet (
		MaDanhGia BIGINT IDENTITY(1,1),
		MaBaiViet BIGINT NOT NULL,
		TenDangNhap NVARCHAR(150) NOT NULL,
		Diem DECIMAL(5,2) NOT NULL CHECK (Diem BETWEEN 0 AND 5),
		ThoiDiemDanhGia DATETIME DEFAULT GETDATE(),
		CONSTRAINT pk_danhgiabaiviet PRIMARY KEY (MaDanhGia),
		CONSTRAINT fk_danhgiabaiviet_baiviet FOREIGN KEY (MaBaiViet) REFERENCES BaiViet (MaBaiViet) ON DELETE CASCADE,
		CONSTRAINT fk_danhgiabaiviet_taikhoan FOREIGN KEY (TenDangNhap) REFERENCES TaiKhoan (TenDangNhap) ON DELETE CASCADE,
		CONSTRAINT uq_danhgiabaiviet UNIQUE (MaBaiViet, TenDangNhap)
	);
END
GO

-- Khi xóa tài khoản thì chuyển trạng thái bài viết, bình luận thành Deleted, xóa thẳng lượt thích, đánh giá, tài khoản
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_TaiKhoan_SoftDelete')
	DROP TRIGGER trg_TaiKhoan_SoftDelete;
GO
CREATE TRIGGER trg_TaiKhoan_SoftDelete
ON TaiKhoan
INSTEAD OF DELETE
AS
BEGIN
	SET NOCOUNT ON;
	-- Bài viết
	UPDATE bv
	SET bv.TrangThai = N'Deleted',
		bv.ThoiDiemCapNhat = GETDATE()
	FROM BaiViet bv
	INNER JOIN DELETED d ON bv.TaiKhoanTao = d.TenDangNhap
	
	-- Bình luận
	UPDATE bl
	SET bl.TrangThai = N'Deleted',
		bl.ThoiDiemCapNhat = GETDATE()
	FROM BinhLuan bl
	INNER JOIN DELETED d ON bl.TaiKhoanTao = d.TenDangNhap

	-- Lượt thích
	DELETE ltbl
	FROM LuotThichBinhLuan ltbl
	INNER JOIN DELETED d ON ltbl.TenDangNhap = d.TenDangNhap

	-- Đánh giá
	DELETE dgbv
	FROM DanhGiaBaiViet dgbv
	INNER JOIN DELETED d ON dgbv.TenDangNhap = d.TenDangNhap
	
	-- Xóa tài khoản
	DELETE tk
	FROM TaiKhoan tk
	INNER JOIN DELETED d ON tk.TenDangNhap = d.TenDangNhap
END
GO

-- Khi xóa thể loại thì chuyển trạng thái bài viết, bình luận thành Deleted, xóa thẳng lượt thích, đánh giá, thể loại
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_TheLoai_SoftDelete')
	DROP TRIGGER trg_TheLoai_SoftDelete;
GO
CREATE TRIGGER trg_TheLoai_SoftDelete
ON TheLoai
INSTEAD OF DELETE
AS
BEGIN
	SET NOCOUNT ON;
	-- Bài viết
	UPDATE bv
	SET bv.TrangThai = N'Deleted',
		bv.ThoiDiemCapNhat = GETDATE()
	FROM BaiViet bv
	INNER JOIN DELETED d ON bv.MaTheLoai = d.MaTheLoai

	-- Bình luận
	UPDATE bl
	SET bl.TrangThai = N'Deleted',
		bl.ThoiDiemCapNhat = GETDATE()
	FROM BinhLuan bl
	INNER JOIN BaiViet bv ON bl.MaBaiViet = bv.MaBaiViet
	INNER JOIN DELETED d ON bv.MaTheLoai = d.MaTheLoai

	-- Lượt thích
	DELETE ltbl
	FROM LuotThichBinhLuan ltbl
	INNER JOIN BinhLuan bl ON ltbl.MaBinhLuan = bl.MaBinhLuan
	INNER JOIN BaiViet bv ON bl.MaBaiViet = bv.MaBaiViet
	INNER JOIN DELETED d ON bv.MaTheLoai = d.MaTheLoai

	-- Đánh giá
	DELETE dgbv
	FROM DanhGiaBaiViet dgbv
	INNER JOIN BaiViet bv ON dgbv.MaBaiViet = bv.MaBaiViet
	INNER JOIN DELETED d ON bv.MaTheLoai = d.MaTheLoai
	
	-- Xóa thể loại
	DELETE tl
	FROM TheLoai tl
	INNER JOIN DELETED d ON tl.MaTheLoai = d.MaTheLoai
END
GO

-- Khi xóa mềm bài viết thì chuyển trạng thái bình luận thành Deleted, xóa thẳng lượt thích, đánh giá
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_BaiViet_SoftDelete')
	DROP TRIGGER trg_BaiViet_SoftDelete;
GO
CREATE TRIGGER trg_BaiViet_SoftDelete
ON BaiViet
INSTEAD OF DELETE
AS
BEGIN
	SET NOCOUNT ON;
	-- Bình luận
	UPDATE bl
	SET bl.TrangThai = N'Deleted',
		bl.ThoiDiemCapNhat = GETDATE()
	FROM BinhLuan bl
	INNER JOIN DELETED d ON bl.MaBaiViet = d.MaBaiViet

	-- Lượt thích
	DELETE ltbl
	FROM LuotThichBinhLuan ltbl
	INNER JOIN BinhLuan bl ON ltbl.MaBinhLuan = bl.MaBinhLuan
	INNER JOIN DELETED d ON bl.MaBaiViet = d.MaBaiViet

	-- Đánh giá
	DELETE dgbv
	FROM DanhGiaBaiViet dgbv
	INNER JOIN DELETED d ON dgbv.MaBaiViet = d.MaBaiViet
	
	-- Xóa mềm bài viết
    UPDATE bv
    SET bv.TrangThai = N'Deleted',
        bv.ThoiDiemCapNhat = GETDATE()
    FROM BaiViet bv
	INNER JOIN DELETED d ON bv.MaBaiViet = d.MaBaiViet
END
GO

-- Khi xóa mềm bình luận thì xóa thẳng lượt thích
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_BinhLuan_SoftDelete')
	DROP TRIGGER trg_BinhLuan_SoftDelete;
GO
CREATE TRIGGER trg_BinhLuan_SoftDelete
ON BinhLuan
INSTEAD OF DELETE
AS
BEGIN
	SET NOCOUNT ON;
	-- Lượt thích
	DELETE ltbl
	FROM LuotThichBinhLuan ltbl
	INNER JOIN DELETED d ON ltbl.MaBinhLuan = d.MaBinhLuan

	-- Xóa mềm bình luận
	UPDATE BinhLuan
	SET TrangThai = N'Deleted',
		ThoiDiemCapNhat = GETDATE()
	WHERE MaBinhLuan IN (SELECT MaBinhLuan FROM DELETED);
	
END
GO

-- Khi Update Tài khoản thì tự động cập nhật ThoiDiemCapNhat
IF  EXISTS(SELECT * FROM sys.triggers WHERE name = 'trg_TaiKhoan_Update')
	DROP TRIGGER trg_TaiKhoan_Update;
GO
CREATE TRIGGER trg_TaiKhoan_Update
ON TaiKhoan
AFTER UPDATE
AS
BEGIN
	SET NOCOUNT ON;
	-- Ngăn không cho trigger kích hoạt đệ quy
    IF TRIGGER_NESTLEVEL() > 1 RETURN;
    UPDATE tk
    SET ThoiDiemCapNhat = GETDATE()
    FROM TaiKhoan tk
    INNER JOIN INSERTED i ON tk.TenDangNhap = i.TenDangNhap;
END
GO

-- Khi Update Bài viết thì tự động cập nhật ThoiDiemCapNhat
IF  EXISTS(SELECT * FROM sys.triggers WHERE name = 'trg_BaiViet_Update')
	DROP TRIGGER trg_BaiViet_Update;
GO
CREATE TRIGGER trg_BaiViet_Update
ON BaiViet
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
	-- Ngăn không cho trigger kích hoạt đệ quy
    IF TRIGGER_NESTLEVEL() > 1 RETURN;
    UPDATE bv
    SET ThoiDiemCapNhat = GETDATE()
	FROM BaiViet bv
	INNER JOIN INSERTED i ON bv.MaBaiViet = i.MaBaiViet;
END
GO

-- Khi Insert, Update Bình luận thì: TieuDe tự động lấy 50 ký tự đầu của cột NoiDung trong bảng BinhLuan, nếu NoiDung dưới 50 ký tự thì lấy hết,
-- còn trên 50 ký tự thì lấy ngang 50 ký tự rồi thêm dấu 3 chấm ở cuối.
-- Tự động cập nhật ThoiDiemCapNhat
-- Chỉ cập nhật khi nội dung thay đổi
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_BinhLuan_Update')
    DROP TRIGGER trg_BinhLuan_Update;
GO
CREATE TRIGGER trg_BinhLuan_Update
ON BinhLuan
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;
	IF TRIGGER_NESTLEVEL() > 1 RETURN;
    UPDATE bl
    SET TieuDe = 
        CASE 
            WHEN LEN(i.NoiDung) <= 50 THEN i.NoiDung
            ELSE LEFT(i.NoiDung, 50) + N'...'
        END,
		ThoiDiemCapNhat = GETDATE()
    FROM BinhLuan bl
    INNER JOIN INSERTED i ON bl.MaBinhLuan = i.MaBinhLuan
    LEFT JOIN DELETED d ON bl.MaBinhLuan = d.MaBinhLuan
    WHERE i.NoiDung != ISNULL(d.NoiDung, '') OR d.NoiDung IS NULL;
END
GO

-- Sau khi thêm lượt thích thì cập nhật lại số lượt thích của bình luận
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_LuotThichBinhLuan_Insert')
	DROP TRIGGER trg_LuotThichBinhLuan_Insert
GO
CREATE TRIGGER trg_LuotThichBinhLuan_Insert
ON LuotThichBinhLuan
AFTER INSERT
AS
BEGIN
	SET NOCOUNT ON;
    IF TRIGGER_NESTLEVEL() > 1 RETURN;
    UPDATE bl
    SET bl.SoLuotThich = bl.SoLuotThich + t.SoLuongMoi
    FROM BinhLuan bl
    INNER JOIN (
        SELECT MaBinhLuan, COUNT(*) AS SoLuongMoi
        FROM INSERTED
        WHERE MaBinhLuan IS NOT NULL
        GROUP BY MaBinhLuan
    ) AS t ON bl.MaBinhLuan = t.MaBinhLuan;
END
GO

-- Sau khi xóa lượt thích bình luận thì cập nhật lại lượt thích trong bảng bình luận
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_LuotThichBinhLuan_Delete')
    DROP TRIGGER trg_LuotThichBinhLuan_Delete;
GO

CREATE TRIGGER trg_LuotThichBinhLuan_Delete
ON LuotThichBinhLuan
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    IF TRIGGER_NESTLEVEL() > 1 RETURN;

    -- Cập nhật số lượt thích trong bảng BinhLuan sau khi xóa
    UPDATE bl
    SET bl.SoLuotThich = 
		CASE 
			WHEN bl.SoLuotThich - t.SoLuongXoa < 0 THEN 0
			ELSE bl.SoLuotThich - t.SoLuongXoa
		END
    FROM BinhLuan bl
    INNER JOIN (
        SELECT MaBinhLuan, COUNT(*) AS SoLuongXoa
        FROM DELETED
        GROUP BY MaBinhLuan
    ) AS t ON bl.MaBinhLuan = t.MaBinhLuan
END
GO


-- Sau khi thêm đánh giá bài viết thì tính lại trung bình cộng cho đánh giá của bảng bài viết
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_DanhGiaBaiViet_Insert')
	DROP TRIGGER trg_DanhGiaBaiViet_Insert;
GO

CREATE TRIGGER trg_DanhGiaBaiViet_Insert
ON DanhGiaBaiViet
AFTER INSERT
AS
BEGIN
	SET NOCOUNT ON;
	IF TRIGGER_NESTLEVEL() > 1 RETURN;

	-- Tính trung bình cộng điểm đánh giá cho từng bài viết được thêm mới đánh giá
	UPDATE bv
	SET bv.DanhGia = t.TrungBinhMoi
	FROM BaiViet bv
	INNER JOIN (
		SELECT dgbv.MaBaiViet, 
			   AVG(CAST(dgbv.Diem AS DECIMAL(5,2))) AS TrungBinhMoi
		FROM DanhGiaBaiViet dgbv
		INNER JOIN INSERTED i ON dgbv.MaBaiViet = i.MaBaiViet
		GROUP BY dgbv.MaBaiViet
	) AS t ON bv.MaBaiViet = t.MaBaiViet;
END;
GO

-- Sau khi xóa đánh giá bài viết thì cập nhật lại đánh giá, NULL nếu không còn
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_DanhGiaBaiViet_Delete')
	DROP TRIGGER trg_DanhGiaBaiViet_Delete;
GO
CREATE TRIGGER trg_DanhGiaBaiViet_Delete
ON DanhGiaBaiViet
AFTER DELETE
AS
BEGIN
	SET NOCOUNT ON;
	IF TRIGGER_NESTLEVEL() > 1 RETURN;

	UPDATE bv
	SET bv.DanhGia = ISNULL(t.TrungBinhMoi, NULL)
	FROM BaiViet bv
	LEFT JOIN (
		SELECT MaBaiViet, AVG(CAST(Diem AS DECIMAL(5,2))) AS TrungBinhMoi
		FROM DanhGiaBaiViet
		WHERE MaBaiViet IN (SELECT MaBaiViet FROM DELETED)
		GROUP BY MaBaiViet
	) AS t ON bv.MaBaiViet = t.MaBaiViet
	WHERE bv.MaBaiViet IN (SELECT MaBaiViet FROM DELETED);
END;
GO


-- Chèn dữ liệu
IF NOT EXISTS (SELECT * FROM TaiKhoan)
BEGIN
	INSERT INTO TaiKhoan (TenDangNhap, MatKhau, Quyen)
	VALUES	(N'admin1', N'mkadmin', N'Admin'),
			(N'user1', N'mkuser', N'User');
END
GO

IF NOT EXISTS (SELECT * FROM TheLoai)
BEGIN
INSERT INTO TheLoai (TenTheLoai) 
VALUES	(N'Công nghệ & Tin học'),
		(N'Học tập & Kinh nghiệm học'),
		(N'Đời sống & Xã hội'),
		(N'Giải trí & Âm nhạc'),
		(N'Thể thao & Sức khỏe'),
		(N'Khoa học & Công nghệ'),
		(N'Phần mềm & Ứng dụng'),
		(N'Lập trình & Phát triển web'),
		(N'Trò chơi & eSports'),
		(N'Thời trang & Làm đẹp'),
		(N'Ẩm thực & Nấu ăn'),
		(N'Du lịch & Khám phá'),
		(N'Sự kiện & Tin tức'),
		(N'Giao lưu & Kết bạn'),
		(N'Góp ý & Hỏi đáp'),
		(N'Thông báo từ quản trị viên'),
		(N'Khác');
END
GO

/* Kiểm tra
SELECT * FROM BinhLuan
SELECT * FROM BaiViet
SELECT * FROM TheLoai
SELECT * FROM TaiKhoan
SELECT * FROM LuotThichBinhLuan
SELECT * FROM DanhGiaBaiViet
SELECT name FROM sys.triggers
*/
/* Reset khi tạo / chèn sai
DROP TABLE BinhLuan
DROP TABLE BaiViet
DROP TABLE TheLoai
DROP TABLE TaiKhoan
DROP TABLE LuotThichBinhLuan
DROP TABLE DanhGiaBaiViet
*/