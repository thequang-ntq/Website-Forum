package Modal.BinhLuan;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;

import Modal.BaiViet.BaiViet;
import Modal.BaiViet.BaiVietBO;

public class BinhLuanBO {
	private BinhLuanDAO dao = new BinhLuanDAO();
	ArrayList<BinhLuan> ds;
	
	public ArrayList<BinhLuan> readDB() throws Exception {
		ds = dao.readDB();
		return ds;
	}
	
	public void createDB(String NoiDung, String Url, String TaiKhoanTao, long MaBaiViet) throws Exception {
		if(NoiDung == null || NoiDung.trim().isEmpty()) {
			throw new Exception("Nội dung không được để trống!");
		}
		if(TaiKhoanTao == null || TaiKhoanTao.trim().isEmpty()) {
			throw new Exception("Tài khoản tạo không được để trống!");
		}
		if((Object) MaBaiViet == null || MaBaiViet < 1) {
			throw new Exception("Mã bài viết không được để trống!");
		}
		
		BinhLuan bl = new BinhLuan((long) -1, null, NoiDung, Url, TaiKhoanTao, MaBaiViet, 0, null, null, null);
		dao.createDB(bl);
	}
	
	public void updateDB(long MaBinhLuan, String NoiDung, String Url, int SoLuotThich, String TrangThai) throws Exception {
		if((Object) MaBinhLuan == null || MaBinhLuan < 1) {
			throw new Exception("Mã bình luận không được để trống!");
		}
		if(NoiDung == null || NoiDung.trim().isEmpty()) {
			throw new Exception("Nội dung không được để trống!");
		}
		if((Object) SoLuotThich == null || SoLuotThich < 0) {
			throw new Exception("Số lượt thích không hợp lệ!");
		}
		if(TrangThai == null || TrangThai.trim().isEmpty()) {
			throw new Exception("Trạng thái không được để trống!");
		}
		if(dao.findByMaBinhLuan(MaBinhLuan) == null) {
			throw new Exception("Bình luận không tồn tại!");
		}
		
		BinhLuan bl = new BinhLuan(MaBinhLuan, null, NoiDung, Url, null, -1, SoLuotThich, TrangThai, null, null);
		dao.updateDB(bl);
	}
	
	public void deleteDB(long MaBinhLuan) throws Exception {
		if((Object) MaBinhLuan == null || MaBinhLuan < 1) {
			throw new Exception("Mã bình luận không được để trống!");
		}
		if(dao.findByMaBinhLuan(MaBinhLuan) == null) {
			throw new Exception("Bình luận không tồn tại!");
		}
		
		dao.deleteDB(MaBinhLuan);
	}
	
	// Tìm theo tiêu đề, nội dung, tài khoản tạo, tiêu đề bài viết, nội dung bài viết, số lượt thích, thời điểm tạo, thời điểm cập nhật
	public ArrayList<BinhLuan> findDB_user(ArrayList<BinhLuan> dsBinhLuan, String key) throws Exception {
        ArrayList<BinhLuan> temp = new ArrayList<>();
        if (key == null || key.trim().isEmpty() || dsBinhLuan == null) {
            return dsBinhLuan != null ? new ArrayList<>(dsBinhLuan) : temp;
        }

        // Lấy danh sách bài viết
        BaiVietBO bvbo = new BaiVietBO();
        ArrayList<BaiViet> dsbv = bvbo.readDB();

        for (BinhLuan bl : dsBinhLuan) {
            String tieuDeBaiViet = "";
            String noiDungBaiViet = "";
            for (BaiViet bv : dsbv) {
                if (bv.getMaBaiViet() == bl.getMaBaiViet()) {
                    tieuDeBaiViet = bv.getTieuDe();
                    noiDungBaiViet = bv.getNoiDung();
                    break;
                }
            }

            if ((bl.getTieuDe() != null && bl.getTieuDe().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bl.getNoiDung() != null && bl.getNoiDung().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bl.getTaiKhoanTao() != null && bl.getTaiKhoanTao().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (tieuDeBaiViet != null && tieuDeBaiViet.trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (noiDungBaiViet != null && noiDungBaiViet.trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (String.valueOf(bl.getSoLuotThich()).trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bl.getThoiDiemTao() != null && bl.getThoiDiemTao().toString().trim().toLowerCase().contains(key.trim().toLowerCase()))
                    || (bl.getThoiDiemCapNhat() != null && bl.getThoiDiemCapNhat().toString().trim().toLowerCase().contains(key.trim().toLowerCase()))) {
                temp.add(bl);
            }
        }

        return temp;
    }
	
	// Tìm theo tiêu đề, nội dung, tài khoản tạo, tiêu đề bài viết, nội dung bài viết, số lượt thích, trạng thái, thời điểm tạo, thời điểm cập nhật
	public ArrayList<BinhLuan> findDB_admin(String key) throws Exception {
		ArrayList<BinhLuan> temp = new ArrayList<BinhLuan>();
		
		// Lấy danh sách bài viết
		BaiVietBO bvbo = new BaiVietBO();
		ArrayList<BaiViet> dsbv = bvbo.readDB();
		
		for(BinhLuan bl: readDB()) {
			String tieuDeBaiViet = "";
			String noiDungBaiViet = "";
			for(BaiViet bv: dsbv) {
				// Tìm tiêu đề bài viết tương ứng bình luận
				if(bv.getMaBaiViet() == bl.getMaBaiViet()) {
					tieuDeBaiViet = bv.getTieuDe();
					noiDungBaiViet = bv.getNoiDung();
					break;
				}
			}
			
			if(bl.getTieuDe().trim().toLowerCase().contains(key.trim().toLowerCase())
			|| bl.getNoiDung().trim().toLowerCase().contains(key.trim().toLowerCase())
			|| bl.getTaiKhoanTao().toString().trim().toLowerCase().contains(key.trim().toLowerCase())
			|| tieuDeBaiViet.trim().toLowerCase().contains(key.trim().toLowerCase())
			|| noiDungBaiViet.trim().toLowerCase().contains(key.trim().toLowerCase())
			|| String.valueOf(bl.getSoLuotThich()).trim().toLowerCase().contains(key.trim().toLowerCase())
			|| bl.getTrangThai().trim().toLowerCase().contains(key.trim().toLowerCase())
			|| bl.getThoiDiemTao().toString().trim().toLowerCase().contains(key.trim().toLowerCase())
			|| bl.getThoiDiemCapNhat().toString().trim().toLowerCase().contains(key.trim().toLowerCase())
			) {
				temp.add(bl);
			}
		}
		
		return temp;
	}
	
	// Sắp xếp giảm dần, bình luận có số lượt thích cao nhất lên đầu
	public ArrayList<BinhLuan> sortDB_soLuotThich_cao(ArrayList<BinhLuan> dsBinhLuan) throws Exception {
	    ArrayList<BinhLuan> temp = new ArrayList<>(dsBinhLuan);
	    temp.sort(Comparator.comparingInt(BinhLuan::getSoLuotThich).reversed());
	    return temp;
	}

	// Sắp xếp tăng dần, bình luận có số lượt thích thấp nhất lên đầu
	public ArrayList<BinhLuan> sortDB_soLuotThich_thap(ArrayList<BinhLuan> dsBinhLuan) throws Exception {
	    ArrayList<BinhLuan> temp = new ArrayList<>(dsBinhLuan);
	    temp.sort(Comparator.comparingInt(BinhLuan::getSoLuotThich));
	    return temp;
	}

	// Sắp xếp theo thời điểm tạo gần đây nhất (muộn nhất lên đầu)
	public ArrayList<BinhLuan> sortDB_thoiDiemTao_ganNhat(ArrayList<BinhLuan> dsBinhLuan) throws Exception {
	    ArrayList<BinhLuan> temp = new ArrayList<>(dsBinhLuan);
	    temp.sort((bl1, bl2) -> {
	        if (bl1.getThoiDiemTao() == null && bl2.getThoiDiemTao() == null) return 0;
	        if (bl1.getThoiDiemTao() == null) return 1;  // null xuống cuối
	        if (bl2.getThoiDiemTao() == null) return -1; // null xuống cuối
	        return bl2.getThoiDiemTao().compareTo(bl1.getThoiDiemTao()); // Giảm dần
	    });
	    return temp;
	}

	// Sắp xếp theo thời điểm tạo xa nhất (sớm nhất lên đầu)
	public ArrayList<BinhLuan> sortDB_thoiDiemTao_xaNhat(ArrayList<BinhLuan> dsBinhLuan) throws Exception {
	    ArrayList<BinhLuan> temp = new ArrayList<>(dsBinhLuan);
	    temp.sort((bl1, bl2) -> {
	        if (bl1.getThoiDiemTao() == null && bl2.getThoiDiemTao() == null) return 0;
	        if (bl1.getThoiDiemTao() == null) return 1;  // null xuống cuối
	        if (bl2.getThoiDiemTao() == null) return -1; // null xuống cuối
	        return bl1.getThoiDiemTao().compareTo(bl2.getThoiDiemTao()); // Tăng dần
	    });
	    return temp;
	}

	// Sắp xếp theo thời điểm cập nhật gần đây nhất (muộn nhất lên đầu)
	// Những bình luận có ThoiDiemCapNhat null sẽ sắp xếp theo ThoiDiemTao
	public ArrayList<BinhLuan> sortDB_thoiDiemCapNhat_ganNhat(ArrayList<BinhLuan> dsBinhLuan) throws Exception {
	    ArrayList<BinhLuan> temp = new ArrayList<>(dsBinhLuan);
	    temp.sort((bl1, bl2) -> {
	        // Nếu cả 2 đều có ThoiDiemCapNhat
	        if (bl1.getThoiDiemCapNhat() != null && bl2.getThoiDiemCapNhat() != null) {
	            return bl2.getThoiDiemCapNhat().compareTo(bl1.getThoiDiemCapNhat()); // Giảm dần
	        }
	        
	        // Nếu cả 2 đều null ThoiDiemCapNhat -> sắp xếp theo ThoiDiemTao
	        if (bl1.getThoiDiemCapNhat() == null && bl2.getThoiDiemCapNhat() == null) {
	            if (bl1.getThoiDiemTao() == null && bl2.getThoiDiemTao() == null) return 0;
	            if (bl1.getThoiDiemTao() == null) return 1;
	            if (bl2.getThoiDiemTao() == null) return -1;
	            return bl2.getThoiDiemTao().compareTo(bl1.getThoiDiemTao()); // Giảm dần
	        }
	        
	        // Một bên có ThoiDiemCapNhat, một bên null -> ưu tiên bên có ThoiDiemCapNhat lên đầu
	        if (bl1.getThoiDiemCapNhat() == null) return 1;  // bl1 null -> xuống sau
	        return -1; // bl2 null -> xuống sau
	    });
	    return temp;
	}

	// Sắp xếp theo thời điểm cập nhật xa nhất (sớm nhất lên đầu)
	// Những bình luận có ThoiDiemCapNhat null sẽ sắp xếp theo ThoiDiemTao
	public ArrayList<BinhLuan> sortDB_thoiDiemCapNhat_xaNhat(ArrayList<BinhLuan> dsBinhLuan) throws Exception {
	    ArrayList<BinhLuan> temp = new ArrayList<>(dsBinhLuan);
	    temp.sort((bl1, bl2) -> {
	        // Nếu cả 2 đều có ThoiDiemCapNhat
	        if (bl1.getThoiDiemCapNhat() != null && bl2.getThoiDiemCapNhat() != null) {
	            return bl1.getThoiDiemCapNhat().compareTo(bl2.getThoiDiemCapNhat()); // Tăng dần
	        }
	        
	        // Nếu cả 2 đều null ThoiDiemCapNhat -> sắp xếp theo ThoiDiemTao
	        if (bl1.getThoiDiemCapNhat() == null && bl2.getThoiDiemCapNhat() == null) {
	            if (bl1.getThoiDiemTao() == null && bl2.getThoiDiemTao() == null) return 0;
	            if (bl1.getThoiDiemTao() == null) return 1;
	            if (bl2.getThoiDiemTao() == null) return -1;
	            return bl1.getThoiDiemTao().compareTo(bl2.getThoiDiemTao()); // Tăng dần
	        }
	        
	        // Một bên có ThoiDiemCapNhat, một bên null -> ưu tiên bên có ThoiDiemCapNhat lên đầu
	        if (bl1.getThoiDiemCapNhat() == null) return 1;  // bl1 null -> xuống sau
	        return -1; // bl2 null -> xuống sau
	    });
	    return temp;
	}
	
	// Lọc danh sách bình luận theo tài khoản tạo
	public ArrayList<BinhLuan> filterDB_taiKhoanTao(String taiKhoanTao) throws Exception {
		ArrayList<BinhLuan> temp = new ArrayList<BinhLuan>();
		if(taiKhoanTao == null || taiKhoanTao.trim().isEmpty()) {
			return temp;
		}
		
		for(BinhLuan bl: readDB()) {
			if(bl.getTaiKhoanTao().equals(taiKhoanTao)) {
				temp.add(bl);
			}
		}
		return temp;
	}
	
	// Lọc danh sách bình luận theo mã bài viết ??
	public ArrayList<BinhLuan> filterDB_maBaiViet(long maBaiViet, String trangThaiBaiViet) throws Exception {
		ArrayList<BinhLuan> temp = new ArrayList<BinhLuan>();
	
		//Không tìm thấy -> ds rỗng
		if( (Object) maBaiViet == null || maBaiViet == -1) {
			return temp;
		}
		
		//Lọc bình luận theo tiêu đề bài viết
		for(BinhLuan bl: readDB()) {
			if(bl.getMaBaiViet() == maBaiViet) {
				boolean f = false;
				if("Active".equals(trangThaiBaiViet) || "Hidden".equals(trangThaiBaiViet)) {
					if("Active".equals(bl.getTrangThai())) f = true;
				}
				else {
					f = true;
				}
				if(f) temp.add(bl);
			}
		}
		return temp;
	}
	
	// Lọc danh sách bình luận theo trạng thái
	public ArrayList<BinhLuan> filterDB_trangThai(String trangThai) throws Exception {
		ArrayList<BinhLuan> temp = new ArrayList<BinhLuan>();
		if(trangThai == null || trangThai.trim().isEmpty()) {
			return temp;
		}
		
		for(BinhLuan bl: readDB()) {
			if(bl.getTrangThai().equals(trangThai)) {
				temp.add(bl);
			}
		}
		return temp;
	}
}