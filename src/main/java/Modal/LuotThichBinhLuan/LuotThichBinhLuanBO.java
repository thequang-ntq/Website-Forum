package Modal.LuotThichBinhLuan;

import java.util.ArrayList;

public class LuotThichBinhLuanBO {
	private LuotThichBinhLuanDAO dao = new LuotThichBinhLuanDAO();
	ArrayList<LuotThichBinhLuan> ds;
	
	public ArrayList<LuotThichBinhLuan> readDB() throws Exception {
		ds = dao.readDB();
		return ds;
	}
	
	public void createDB(long MaBinhLuan, String TenDangNhap) throws Exception {
		if((Object) MaBinhLuan == null || MaBinhLuan < 1) {
			throw new Exception("Mã bình luận không được để trống");
		}
		if(TenDangNhap == null || TenDangNhap.trim().isEmpty()) {
			throw new Exception("Tên đăng nhập không được để trống");
		}
		LuotThichBinhLuan ltbl = new LuotThichBinhLuan(-1, MaBinhLuan, TenDangNhap, null);
		dao.createDB(ltbl);
	}
	
	public void deleteDB(long MaLuotThich) throws Exception {
		if((Object) MaLuotThich == null || MaLuotThich < 1) {
			throw new Exception("Mã lượt thích không được để trống!");
		}
		if(dao.findByMaLuotThich(MaLuotThich) == null) {
			throw new Exception("Lượt thích không tồn tại!");
		}
		
		dao.deleteDB(MaLuotThich);		
	}
	
	public void deleteDBByMaBinhLuanVaTenDangNhap(long MaBinhLuan, String TenDangNhap) throws Exception {
		if((Object) MaBinhLuan == null || MaBinhLuan < 1) {
			throw new Exception("Mã bình luận không được để trống");
		}
		if(TenDangNhap == null || TenDangNhap.trim().isEmpty()) {
			throw new Exception("Tên đăng nhập không được để trống");
		}
		if(dao.findByMaBinhLuanVaTenDangNhap(MaBinhLuan, TenDangNhap) == null) {
			throw new Exception("Lượt thích không tồn tại");
		}
		
		dao.deleteDBByMaBinhLuanVaTenDangNhap(MaBinhLuan, TenDangNhap);
	}
	
	// Tìm theo tài khoản thích
	public LuotThichBinhLuan findDB(String TenDangNhap) throws Exception {
		if(TenDangNhap == null || TenDangNhap.trim().isEmpty()) return null;
		for(LuotThichBinhLuan ltbl: readDB()) {
			if(ltbl.getTenDangNhap().equals(TenDangNhap)) {
				return ltbl;
			}
		}
		return null;
	}
}
