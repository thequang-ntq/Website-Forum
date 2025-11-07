package Modal.BaiViet;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BaiViet {
	private long MaBaiViet;
	private String TieuDe;
	private String NoiDung;
	private String Url;
	private String TaiKhoanTao;
	private int MaTheLoai;
	private BigDecimal DanhGia;
	private String TrangThai;
	private Timestamp ThoiDiemTao;
	private Timestamp ThoiDiemCapNhat;
	public BaiViet() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BaiViet(long maBaiViet, String tieuDe, String noiDung, String url, String taiKhoanTao, int maTheLoai,
			BigDecimal danhGia, String trangThai, Timestamp thoiDiemTao, Timestamp thoiDiemCapNhat) {
		super();
		MaBaiViet = maBaiViet;
		TieuDe = tieuDe;
		NoiDung = noiDung;
		Url = url;
		TaiKhoanTao = taiKhoanTao;
		MaTheLoai = maTheLoai;
		DanhGia = danhGia;
		TrangThai = trangThai;
		ThoiDiemTao = thoiDiemTao;
		ThoiDiemCapNhat = thoiDiemCapNhat;
	}
	public long getMaBaiViet() {
		return MaBaiViet;
	}
	public void setMaBaiViet(long maBaiViet) {
		MaBaiViet = maBaiViet;
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
	public int getMaTheLoai() {
		return MaTheLoai;
	}
	public void setMaTheLoai(int maTheLoai) {
		MaTheLoai = maTheLoai;
	}
	public BigDecimal getDanhGia() {
		return DanhGia;
	}
	public void setDanhGia(BigDecimal danhGia) {
		DanhGia = danhGia;
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
