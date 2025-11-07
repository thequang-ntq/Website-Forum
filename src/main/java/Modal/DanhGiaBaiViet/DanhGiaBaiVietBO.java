package Modal.DanhGiaBaiViet;

import java.math.BigDecimal;
import java.util.ArrayList;

public class DanhGiaBaiVietBO {
    private DanhGiaBaiVietDAO dao = new DanhGiaBaiVietDAO();
    ArrayList<DanhGiaBaiViet> ds;

    public ArrayList<DanhGiaBaiViet> readDB() throws Exception {
        ds = dao.readDB();
        return ds;
    }

    public void createDB(long MaBaiViet, String TenDangNhap, BigDecimal Diem) throws Exception {
        if ((Object) MaBaiViet == null || MaBaiViet < 1) {
            throw new Exception("Mã bài viết không được để trống");
        }
        if (TenDangNhap == null || TenDangNhap.trim().isEmpty()) {
            throw new Exception("Tên đăng nhập không được để trống");
        }
        if (Diem.compareTo((BigDecimal.valueOf((long) 0))) < 0 || Diem.compareTo((BigDecimal.valueOf((long) 5))) > 0) {
            throw new Exception("Điểm đánh giá phải nằm trong khoảng từ 0 đến 5");
        }
        DanhGiaBaiViet dgbv = new DanhGiaBaiViet(-1, MaBaiViet, TenDangNhap, Diem, null);
        dao.createDB(dgbv);
    }

    public void updateDB(long MaDanhGia, long MaBaiViet, String TenDangNhap, BigDecimal Diem) throws Exception {
        if ((Object) MaDanhGia == null || MaDanhGia < 1) {
            throw new Exception("Mã đánh giá không được để trống");
        }
        if ((Object) MaBaiViet == null || MaBaiViet < 1) {
            throw new Exception("Mã bài viết không được để trống");
        }
        if (TenDangNhap == null || TenDangNhap.trim().isEmpty()) {
            throw new Exception("Tên đăng nhập không được để trống");
        }
        if (Diem.compareTo((BigDecimal.valueOf((long) 0))) < 0 || Diem.compareTo((BigDecimal.valueOf((long) 5))) > 0) {
            throw new Exception("Điểm đánh giá phải nằm trong khoảng từ 0 đến 5");
        }
        if (dao.findByMaDanhGia(MaDanhGia) == null) {
            throw new Exception("Đánh giá không tồn tại!");
        }

        DanhGiaBaiViet dgbv = new DanhGiaBaiViet(MaDanhGia, MaBaiViet, TenDangNhap, Diem, null);
        dao.updateDB(dgbv);
    }

    public void deleteDB(long MaDanhGia) throws Exception {
        if ((Object) MaDanhGia == null || MaDanhGia < 1) {
            throw new Exception("Mã đánh giá không được để trống!");
        }
        if (dao.findByMaDanhGia(MaDanhGia) == null) {
            throw new Exception("Đánh giá không tồn tại!");
        }

        dao.deleteDB(MaDanhGia);
    }
    
	public void deleteDBByMaBaiVietVaTenDangNhap(long MaBaiViet, String TenDangNhap) throws Exception {
		if((Object) MaBaiViet == null || MaBaiViet < 1) {
			throw new Exception("Mã bài viết không được để trống");
		}
		if(TenDangNhap == null || TenDangNhap.trim().isEmpty()) {
			throw new Exception("Tên đăng nhập không được để trống");
		}
		if(dao.findByMaBaiVietVaTenDangNhap(MaBaiViet, TenDangNhap) == null) {
			throw new Exception("Đánh giá không tồn tại");
		}
		
		dao.deleteDBByMaBaiVietVaTenDangNhap(MaBaiViet, TenDangNhap);
	}

    // Tìm theo tài khoản đánh giá
    public DanhGiaBaiViet findDB(String TenDangNhap) throws Exception {
        if (TenDangNhap == null || TenDangNhap.trim().isEmpty()) return null;
        for (DanhGiaBaiViet dgbv : readDB()) {
            if (dgbv.getTenDangNhap().equals(TenDangNhap)) {
                return dgbv;
            }
        }
        return null;
    }
}