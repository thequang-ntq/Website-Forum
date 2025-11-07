package Modal.LuotThichBinhLuan;

import java.sql.Timestamp;

public class LuotThichBinhLuan {
	private long MaLuotThich;
	private long MaBinhLuan;
	private String TenDangNhap;
	private Timestamp ThoiDiemThich;
	public LuotThichBinhLuan() {
		super();
		// TODO Auto-generated constructor stub
	}
	public LuotThichBinhLuan(long maLuotThich, long maBinhLuan, String tenDangNhap, Timestamp thoiDiemThich) {
		super();
		MaLuotThich = maLuotThich;
		MaBinhLuan = maBinhLuan;
		TenDangNhap = tenDangNhap;
		ThoiDiemThich = thoiDiemThich;
	}
	public long getMaLuotThich() {
		return MaLuotThich;
	}
	public void setMaLuotThich(long maLuotThich) {
		MaLuotThich = maLuotThich;
	}
	public long getMaBinhLuan() {
		return MaBinhLuan;
	}
	public void setMaBinhLuan(long maBinhLuan) {
		MaBinhLuan = maBinhLuan;
	}
	public String getTenDangNhap() {
		return TenDangNhap;
	}
	public void setTenDangNhap(String tenDangNhap) {
		TenDangNhap = tenDangNhap;
	}
	public Timestamp getThoiDiemThich() {
		return ThoiDiemThich;
	}
	public void setThoiDiemThich(Timestamp thoiDiemThich) {
		ThoiDiemThich = thoiDiemThich;
	}
	
}
