package Modal.BaiViet;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;

import Modal.TheLoai.TheLoai;
import Modal.TheLoai.TheLoaiBO;

public class BaiVietBO {
    private BaiVietDAO dao = new BaiVietDAO();
    ArrayList<BaiViet> ds;

    public ArrayList<BaiViet> readDB() throws Exception {
        ds = dao.readDB();
        return ds;
    }

    public void createDB(String TieuDe, String NoiDung, String Url, String TaiKhoanTao, int MaTheLoai) throws Exception {
        if (TieuDe == null || TieuDe.trim().isEmpty()) {
            throw new Exception("Tiêu đề không được để trống!");
        }
        if (NoiDung == null || NoiDung.trim().isEmpty()) {
            throw new Exception("Nội dung không được để trống!");
        }
        if (TaiKhoanTao == null || TaiKhoanTao.trim().isEmpty()) {
            throw new Exception("Tài khoản tạo không được để trống!");
        }
        if (MaTheLoai < 1) {
            throw new Exception("Thể loại không được để trống!");
        }

        BaiViet bv = new BaiViet((long) -1, TieuDe, NoiDung, Url, TaiKhoanTao, MaTheLoai, null, null, null, null);
        dao.createDB(bv);
    }
    
    // Đánh giá bài viết là trung bình cộng đánh giá của tất cả người dùng theo bảng DanhGiaBaiViet, xử lý thông qua trigger trong CSDL.
    public void updateDB(long MaBaiViet, String TieuDe, String NoiDung, String Url, int MaTheLoai, BigDecimal DanhGia, String TrangThai) throws Exception {
        if (MaBaiViet < 1) {
            throw new Exception("Mã bài viết không được để trống!");
        }
        if (TieuDe == null || TieuDe.trim().isEmpty()) {
            throw new Exception("Tiêu đề không được để trống!");
        }
        if (NoiDung == null || NoiDung.trim().isEmpty()) {
            throw new Exception("Nội dung không được để trống!");
        }
        if (MaTheLoai < 1) {
            throw new Exception("Thể loại không được để trống!");
        }
        if (TrangThai == null || TrangThai.trim().isEmpty()) {
            throw new Exception("Trạng thái không được để trống!");
        }
        if (dao.findByMaBaiViet(MaBaiViet) == null) {
            throw new Exception("Bài viết không tồn tại!");
        }

        BaiViet bv = new BaiViet(MaBaiViet, TieuDe, NoiDung, Url, null, MaTheLoai, DanhGia, TrangThai, null, null);
        dao.updateDB(bv);
    }

    public void deleteDB(long MaBaiViet) throws Exception {
        if (MaBaiViet < 1) {
            throw new Exception("Mã bài viết không được để trống!");
        }
        if (dao.findByMaBaiViet(MaBaiViet) == null) {
            throw new Exception("Bài viết không tồn tại!");
        }

        dao.deleteDB(MaBaiViet);
    }

    // Tìm theo tiêu đề, nội dung, tài khoản tạo, thể loại, đánh giá, thời điểm tạo, thời điểm cập nhật ??
    public ArrayList<BaiViet> findDB_user(ArrayList<BaiViet> dsBaiViet, String key) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>();
        if (key == null || key.trim().isEmpty() || dsBaiViet == null) {
            return dsBaiViet != null ? new ArrayList<>(dsBaiViet) : temp;
        }

        // Lấy ds thể loại
        TheLoaiBO tlbo = new TheLoaiBO();
        ArrayList<TheLoai> dstl = tlbo.readDB();

        for (BaiViet bv : dsBaiViet) {
            String tenTheLoai = "";
            for (TheLoai tl : dstl) {
                if (tl.getMaTheLoai() == bv.getMaTheLoai()) {
                    tenTheLoai = tl.getTenTheLoai();
                    break;
                }
            }
//            if(!bv.getTrangThai().equals("Active")) continue;
            if ((bv.getTieuDe() != null && bv.getTieuDe().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bv.getNoiDung() != null && bv.getNoiDung().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bv.getTaiKhoanTao() != null && bv.getTaiKhoanTao().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (tenTheLoai != null && tenTheLoai.trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bv.getDanhGia() != null && bv.getDanhGia().toString().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bv.getThoiDiemTao() != null && bv.getThoiDiemTao().toString().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bv.getThoiDiemCapNhat() != null && bv.getThoiDiemCapNhat().toString().trim().toLowerCase().contains(key.trim().toLowerCase()))) {
                temp.add(bv);
            }
        }

        return temp;
    }

    // Tìm theo tiêu đề, nội dung, tài khoản tạo, thể loại, đánh giá, ***trạng thái, thời điểm tạo, thời điểm cập nhật
    public ArrayList<BaiViet> findDB_admin(String key) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>();
        if (key == null || key.trim().isEmpty()) {
            return temp;
        }

        // Lấy ds thể loại
        TheLoaiBO tlbo = new TheLoaiBO();
        ArrayList<TheLoai> dstl = tlbo.readDB();

        for (BaiViet bv : readDB()) {
            String tenTheLoai = "";
            for (TheLoai tl : dstl) {
                if (tl.getMaTheLoai() == bv.getMaTheLoai()) {
                    tenTheLoai = tl.getTenTheLoai();
                    break;
                }
            }

            if ((bv.getTieuDe() != null && bv.getTieuDe().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bv.getNoiDung() != null && bv.getNoiDung().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bv.getTaiKhoanTao() != null && bv.getTaiKhoanTao().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (tenTheLoai != null && tenTheLoai.trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bv.getDanhGia() != null && bv.getDanhGia().toString().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bv.getTrangThai() != null && bv.getTrangThai().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bv.getThoiDiemTao() != null && bv.getThoiDiemTao().toString().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bv.getThoiDiemCapNhat() != null && bv.getThoiDiemCapNhat().toString().trim().toLowerCase().contains(key.trim().toLowerCase()))) {
                temp.add(bv);
            }
        }

        return temp;
    }

    // Sắp xếp giảm dần, bài viết đánh giá cao nhất lên đầu (5 sao -> 1 sao -> null)
    // So sánh bv1 với bv2 để quyết định vị trí bv1 so với bv2
    // < 0 -> bv1 trước bv2; = 0 -> giữ nguyên; > 0 -> bv1 đứng sau bv2
    // Muốn đẩy bv1 xuống -> số dương; ngược lại -> số âm.
    public ArrayList<BaiViet> sortDB_danhGia_cao(ArrayList<BaiViet> dsBaiViet) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>(dsBaiViet);
        temp.sort((bv1, bv2) -> {
            if (bv1.getDanhGia() == null && bv2.getDanhGia() == null) return 0;
            if (bv1.getDanhGia() == null) return 1;  // null xuống cuối, bv1 đứng sau bv2, bài chưa có đánh giá bị đẩy xuống cuối
            if (bv2.getDanhGia() == null) return -1; // null xuống cuối, bv1 đứng trước bv2
            return bv2.getDanhGia().compareTo(bv1.getDanhGia()); // Giảm dần. =0 -> Đứng sau; <0 -> Đứng trước. Đảo thứ tự bv2 so với bv1 -> Sắp xếp giảm dần
        });
        return temp;
    }

    // Sắp xếp tăng dần, bài viết đánh giá thấp nhất lên đầu (null -> 1 sao -> 5 sao)
    public ArrayList<BaiViet> sortDB_danhGia_thap(ArrayList<BaiViet> dsBaiViet) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>(dsBaiViet);
        temp.sort((bv1, bv2) -> {
            if (bv1.getDanhGia() == null && bv2.getDanhGia() == null) return 0;
            if (bv1.getDanhGia() == null) return -1;  // null lên đầu
            if (bv2.getDanhGia() == null) return 1;   // null lên đầu
            return bv1.getDanhGia().compareTo(bv2.getDanhGia()); // Tăng dần
        });
        return temp;
    }

	 // Sắp xếp theo thời điểm cập nhật gần đây nhất (muộn nhất lên đầu)
	 // Những bài có ThoiDiemCapNhat null sẽ sắp xếp theo ThoiDiemTao
	 public ArrayList<BaiViet> sortDB_thoiDiemCapNhat_ganNhat(ArrayList<BaiViet> dsBaiViet) throws Exception {
	     ArrayList<BaiViet> temp = new ArrayList<>(dsBaiViet);
	     temp.sort((bv1, bv2) -> {
	         // Nếu cả 2 đều có ThoiDiemCapNhat. Đẩy bv1 xuống dưới do bv1 tạo xa hơn bv2. compare mặc định là thằng nào tạo muộn hơn, thời điểm lớn hơn là lớn hơn
	    	 // thằng còn lại
	         if (bv1.getThoiDiemCapNhat() != null && bv2.getThoiDiemCapNhat() != null) {
	             return bv2.getThoiDiemCapNhat().compareTo(bv1.getThoiDiemCapNhat()); // Giảm dần
	         }
	         
	         // Nếu cả 2 đều null ThoiDiemCapNhat -> sắp xếp theo ThoiDiemTao
	         if (bv1.getThoiDiemCapNhat() == null && bv2.getThoiDiemCapNhat() == null) {
	             if (bv1.getThoiDiemTao() == null && bv2.getThoiDiemTao() == null) return 0;
	             if (bv1.getThoiDiemTao() == null) return 1;
	             if (bv2.getThoiDiemTao() == null) return -1;
	             return bv2.getThoiDiemTao().compareTo(bv1.getThoiDiemTao()); // Giảm dần
	         }
	         
	         // Một bên có ThoiDiemCapNhat, một bên null -> ưu tiên bên có ThoiDiemCapNhat lên đầu
	         if (bv1.getThoiDiemCapNhat() == null) return 1;  // bv1 null -> xuống sau
	         return -1; // bv2 null -> xuống sau
	     });
	     return temp;
	 }
	
	 // Sắp xếp theo thời điểm cập nhật xa nhất (sớm nhất lên đầu)
	 // Những bài có ThoiDiemCapNhat null sẽ sắp xếp theo ThoiDiemTao
	 public ArrayList<BaiViet> sortDB_thoiDiemCapNhat_xaNhat(ArrayList<BaiViet> dsBaiViet) throws Exception {
	     ArrayList<BaiViet> temp = new ArrayList<>(dsBaiViet);
	     temp.sort((bv1, bv2) -> {
	         // Nếu cả 2 đều có ThoiDiemCapNhat
	         if (bv1.getThoiDiemCapNhat() != null && bv2.getThoiDiemCapNhat() != null) {
	             return bv1.getThoiDiemCapNhat().compareTo(bv2.getThoiDiemCapNhat()); // Tăng dần
	         }
	         
	         // Nếu cả 2 đều null ThoiDiemCapNhat -> sắp xếp theo ThoiDiemTao
	         if (bv1.getThoiDiemCapNhat() == null && bv2.getThoiDiemCapNhat() == null) {
	             if (bv1.getThoiDiemTao() == null && bv2.getThoiDiemTao() == null) return 0;
	             if (bv1.getThoiDiemTao() == null) return 1;
	             if (bv2.getThoiDiemTao() == null) return -1;
	             return bv1.getThoiDiemTao().compareTo(bv2.getThoiDiemTao()); // Tăng dần
	         }
	         
	         // Một bên có ThoiDiemCapNhat, một bên null -> ưu tiên bên có ThoiDiemCapNhat lên đầu
	         if (bv1.getThoiDiemCapNhat() == null) return 1;  // bv1 null -> xuống sau
	         return -1; // bv2 null -> xuống sau
	     });
	     return temp;
	 }
	
	 // Sắp xếp theo thời điểm tạo gần đây nhất (muộn nhất lên đầu)
	 public ArrayList<BaiViet> sortDB_thoiDiemTao_ganNhat(ArrayList<BaiViet> dsBaiViet) throws Exception {
	     ArrayList<BaiViet> temp = new ArrayList<>(dsBaiViet);
	     temp.sort((bv1, bv2) -> {
	         if (bv1.getThoiDiemTao() == null && bv2.getThoiDiemTao() == null) return 0;
	         if (bv1.getThoiDiemTao() == null) return 1;  // null xuống cuối
	         if (bv2.getThoiDiemTao() == null) return -1; // null xuống cuối
	         return bv2.getThoiDiemTao().compareTo(bv1.getThoiDiemTao()); // Giảm dần
	     });
	     return temp;
	 }
	
	 // Sắp xếp theo thời điểm tạo xa nhất (sớm nhất lên đầu)
	 public ArrayList<BaiViet> sortDB_thoiDiemTao_xaNhat(ArrayList<BaiViet> dsBaiViet) throws Exception {
	     ArrayList<BaiViet> temp = new ArrayList<>(dsBaiViet);
	     temp.sort((bv1, bv2) -> {
	         if (bv1.getThoiDiemTao() == null && bv2.getThoiDiemTao() == null) return 0;
	         if (bv1.getThoiDiemTao() == null) return 1;  // null xuống cuối
	         if (bv2.getThoiDiemTao() == null) return -1; // null xuống cuối
	         return bv1.getThoiDiemTao().compareTo(bv2.getThoiDiemTao()); // Tăng dần
	     });
	     return temp;
	 }

    // Lọc danh sách bài viết theo tài khoản tạo
    public ArrayList<BaiViet> filterDB_taiKhoanTao(String taiKhoanTao) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>();
        if (taiKhoanTao == null || taiKhoanTao.trim().isEmpty()) {
            return temp;
        }

        for (BaiViet bv : readDB()) {
            if (bv.getTaiKhoanTao().equals(taiKhoanTao)) {
                temp.add(bv);
            }
        }
        return temp;
    }
    
 // Lọc danh sách bài viết theo tài khoản tạo ở trang chủ (Tiếp nối cái trước đó), bài viết Active và của tài khoản tạo mới được thêm vào.
    public ArrayList<BaiViet> filterDB_taiKhoanTao_2(ArrayList<BaiViet> dsBaiViet, String taiKhoanTao) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>();
        if (taiKhoanTao == null || taiKhoanTao.trim().isEmpty()) {
            return temp;
        }

        for (BaiViet bv : dsBaiViet) {
            if (bv.getTaiKhoanTao().equals(taiKhoanTao) && bv.getTrangThai().equals("Active")) {
                temp.add(bv);
            }
        }
        return temp;
    }

    // Lọc danh sách bài viết theo mã thể loại
    public ArrayList<BaiViet> filterDB_maTheLoai(int maTheLoai) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>();
        if (maTheLoai < 1) {
            return temp;
        }

        for (BaiViet bv : readDB()) {
            if (bv.getMaTheLoai() == maTheLoai) {
                temp.add(bv);
            }
        }
        return temp;
    }
    
 // Lọc danh sách bài viết theo mã thể loại ở trang chủ (Tiếp nối cái trước đó)
    public ArrayList<BaiViet> filterDB_maTheLoai_2(ArrayList<BaiViet> dsBaiViet, int maTheLoai) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>();
        if (maTheLoai < 1) {
            return temp;
        }

        for (BaiViet bv : dsBaiViet) {
            if (bv.getMaTheLoai() == maTheLoai && bv.getTrangThai().equals("Active")) {
                temp.add(bv);
            }
        }
        return temp;
    }

    // Lọc danh sách bài viết theo trạng thái
    public ArrayList<BaiViet> filterDB_trangThai(String trangThai) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>();
        if (trangThai == null || trangThai.trim().isEmpty()) {
            return temp;
        }

        for (BaiViet bv : readDB()) {
            if (bv.getTrangThai().equals(trangThai)) {
                temp.add(bv);
            }
        }
        return temp;
    }
}