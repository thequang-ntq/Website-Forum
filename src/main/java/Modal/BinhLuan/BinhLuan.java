package Modal.BinhLuan;

import java.sql.Timestamp;

public class BinhLuan {
	private long MaBinhLuan;
	private String TieuDe;
	private String NoiDung;
	private String Url;
	private String TaiKhoanTao;
	private long MaBaiViet;
	private int SoLuotThich;
	private String TrangThai;
	private Timestamp ThoiDiemTao;
	private Timestamp ThoiDiemCapNhat;
	public BinhLuan() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BinhLuan(long maBinhLuan, String tieuDe, String noiDung, String url, String taiKhoanTao, long maBaiViet,
			int soLuotThich, String trangThai, Timestamp thoiDiemTao, Timestamp thoiDiemCapNhat) {
		super();
		MaBinhLuan = maBinhLuan;
		TieuDe = tieuDe;
		NoiDung = noiDung;
		Url = url;
		TaiKhoanTao = taiKhoanTao;
		MaBaiViet = maBaiViet;
		SoLuotThich = soLuotThich;
		TrangThai = trangThai;
		ThoiDiemTao = thoiDiemTao;
		ThoiDiemCapNhat = thoiDiemCapNhat;
	}
	public long getMaBinhLuan() {
		return MaBinhLuan;
	}
	public void setMaBinhLuan(long maBinhLuan) {
		MaBinhLuan = maBinhLuan;
	}
	public String getTieuDe() {
		return TieuDe;
	}
	public void setTieuDe(String tieuDe) {
		TieuDe = tieuDe;
	}
	public String getNoiDung() {
		return NoiDung;
	}
	public void setNoiDung(String noiDung) {
		NoiDung = noiDung;
	}
	public String getUrl() {
		return Url;
	}
	public void setUrl(String url) {
		Url = url;
	}
	public String getTaiKhoanTao() {
		return TaiKhoanTao;
	}
	public void setTaiKhoanTao(String taiKhoanTao) {
		TaiKhoanTao = taiKhoanTao;
	}
	public long getMaBaiViet() {
		return MaBaiViet;
	}
	public void setMaBaiViet(long maBaiViet) {
		MaBaiViet = maBaiViet;
	}
	public int getSoLuotThich() {
		return SoLuotThich;
	}
	public void setSoLuotThich(int soLuotThich) {
		SoLuotThich = soLuotThich;
	}
	public String getTrangThai() {
		return TrangThai;
	}
	public void setTrangThai(String trangThai) {
		TrangThai = trangThai;
	}
	public Timestamp getThoiDiemTao() {
		return ThoiDiemTao;
	}
	public void setThoiDiemTao(Timestamp thoiDiemTao) {
		ThoiDiemTao = thoiDiemTao;
	}
	public Timestamp getThoiDiemCapNhat() {
		return ThoiDiemCapNhat;
	}
	public void setThoiDiemCapNhat(Timestamp thoiDiemCapNhat) {
		ThoiDiemCapNhat = thoiDiemCapNhat;
	}
	
}
