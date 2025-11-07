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

    // Tìm theo tiêu đề, nội dung, tài khoản tạo, thể loại, đánh giá, thời điểm tạo, thời điểm cập nhật
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

    // Tìm theo tiêu đề, nội dung, tài khoản tạo, thể loại, đánh giá, trạng thái, thời điểm tạo, thời điểm cập nhật
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

    // Sắp xếp giảm dần, bài viết đánh giá cao nhất lên đầu
    public ArrayList<BaiViet> sortDB_danhGia_cao(ArrayList<BaiViet> dsBaiViet) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>(dsBaiViet);
        temp.sort(
            Comparator.comparing(
                BaiViet::getDanhGia,
                Comparator.nullsLast(BigDecimal::compareTo)
            ).reversed()
        );
        return temp;
    }

    // Sắp xếp tăng dần, bài viết đánh giá thấp nhất lên đầu
    public ArrayList<BaiViet> sortDB_danhGia_thap(ArrayList<BaiViet> dsBaiViet) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>(dsBaiViet);
        temp.sort(
            Comparator.comparing(
                BaiViet::getDanhGia,
                Comparator.nullsLast(BigDecimal::compareTo)
            )
        );
        return temp;
    }

    // Sắp xếp theo thời điểm tạo gần đây nhất (muộn nhất lên đầu)
    public ArrayList<BaiViet> sortDB_thoiDiemTao_ganNhat(ArrayList<BaiViet> dsBaiViet) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>(dsBaiViet);
        temp.sort(
            Comparator.comparing(
                BaiViet::getThoiDiemTao,
                Comparator.nullsLast(Timestamp::compareTo)
            ).reversed()
        );
        return temp;
    }

    // Sắp xếp theo thời điểm tạo xa nhất (sớm nhất lên đầu)
    public ArrayList<BaiViet> sortDB_thoiDiemTao_xaNhat(ArrayList<BaiViet> dsBaiViet) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>(dsBaiViet);
        temp.sort(
            Comparator.comparing(
                BaiViet::getThoiDiemTao,
                Comparator.nullsLast(Timestamp::compareTo)
            )
        );
        return temp;
    }

    // Sắp xếp theo thời điểm cập nhật gần đây nhất (muộn nhất lên đầu, null xuống cuối)
    public ArrayList<BaiViet> sortDB_thoiDiemCapNhat_ganNhat(ArrayList<BaiViet> dsBaiViet) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>(dsBaiViet);
        temp.sort(
            Comparator.comparing(
                BaiViet::getThoiDiemCapNhat,
                Comparator.nullsLast(Timestamp::compareTo)
            ).reversed()
        );
        return temp;
    }

    // Sắp xếp theo thời điểm cập nhật xa nhất (sớm nhất lên đầu, null xuống cuối)
    public ArrayList<BaiViet> sortDB_thoiDiemCapNhat_xaNhat(ArrayList<BaiViet> dsBaiViet) throws Exception {
        ArrayList<BaiViet> temp = new ArrayList<>(dsBaiViet);
        temp.sort(
            Comparator.comparing(
                BaiViet::getThoiDiemCapNhat,
                Comparator.nullsLast(Timestamp::compareTo)
            )
        );
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