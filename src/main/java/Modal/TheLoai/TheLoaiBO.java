package Modal.TheLoai;

import java.util.ArrayList;

import Modal.TheLoai.TheLoai;
import Modal.TheLoai.TheLoaiDAO;

public class TheLoaiBO {
	private TheLoaiDAO dao = new TheLoaiDAO();
	ArrayList<TheLoai> ds;
	
	public ArrayList<TheLoai> readDB() throws Exception {
		ds = dao.readDB();
		return ds;
	}
	
	// Đọc các thể loại đang Active
	public ArrayList<TheLoai> readDB2() throws Exception {
		ds = dao.readDB();
		ArrayList<TheLoai> temp = new ArrayList<TheLoai>();
		for(TheLoai tl: ds) {
			if("Active".equals(tl.getTrangThai())) temp.add(tl);
		}
		return temp;
	}
	
	public void createDB(String TenTheLoai) throws Exception {
		if(TenTheLoai == null || TenTheLoai.trim().isEmpty()) {
			throw new Exception("Tên thể loại không được để trống!");
		}
		if(dao.findByTenTheLoai(TenTheLoai) != null) {
			throw new Exception("Thể loại đã tồn tại!");
		}
		
		TheLoai tl = new TheLoai(-1, TenTheLoai, null, null, null);
		dao.createDB(tl);
	}
	
	public void updateDB(int MaTheLoai, String TenTheLoai, String TrangThai) throws Exception {
		if(TenTheLoai == null || TenTheLoai.trim().isEmpty()) {
			throw new Exception("Tên thể loại không được để trống!");
		}
		if(TrangThai == null || TrangThai.trim().isEmpty()) {
			throw new Exception("Trạng thái không được để trống!");
		}
		if(dao.findByMaTheLoai(MaTheLoai) == null) {
			throw new Exception("Thể loại không tồn tại!");
		}
		
		TheLoai tl = new TheLoai(MaTheLoai, TenTheLoai, TrangThai, null, null);
		dao.updateDB(tl);
	}
	
	public void deleteDB(int MaTheLoai) throws Exception {
		if(dao.findByMaTheLoai(MaTheLoai) == null) {
			throw new Exception("Thể loại không tồn tại!");
		}
		
		dao.deleteDB(MaTheLoai);
	}
	
	// Tìm theo mã / tên thể loại / trạng thái / thời điểm tạo / thời điểm cập nhật
	public ArrayList<TheLoai> findDB(String key) throws Exception {
		ArrayList<TheLoai> temp = new ArrayList<TheLoai>();
		for(TheLoai tl: ds) {
			if( tl.getTenTheLoai().trim().toLowerCase().contains(key.trim().toLowerCase())
			|| String.valueOf(tl.getMaTheLoai()).trim().toLowerCase().contains(key.trim().toLowerCase())
			|| tl.getTrangThai().trim().toLowerCase().contains(key.trim().toLowerCase())
			|| tl.getThoiDiemTao().toString().trim().toLowerCase().contains(key.trim().toLowerCase())
			|| (tl.getThoiDiemCapNhat() != null && tl.getThoiDiemCapNhat().toString().trim().toLowerCase().contains(key.trim().toLowerCase()))
			) {
				temp.add(tl);
			}
		}
		
		return temp;
	}
	
}
