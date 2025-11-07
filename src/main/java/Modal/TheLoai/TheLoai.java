package Modal.TheLoai;

import java.sql.Timestamp;

public class TheLoai {
	private int MaTheLoai;
	private String TenTheLoai;
	private String TrangThai;
	private Timestamp ThoiDiemTao;
	private Timestamp ThoiDiemCapNhat;
	public TheLoai() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TheLoai(int maTheLoai, String tenTheLoai, String trangThai, Timestamp thoiDiemTao,
			Timestamp thoiDiemCapNhat) {
		super();
		MaTheLoai = maTheLoai;
		TenTheLoai = tenTheLoai;
		TrangThai = trangThai;
		ThoiDiemTao = thoiDiemTao;
		ThoiDiemCapNhat = thoiDiemCapNhat;
	}
	public int getMaTheLoai() {
		return MaTheLoai;
	}
	public void setMaTheLoai(int maTheLoai) {
		MaTheLoai = maTheLoai;
	}
	public String getTenTheLoai() {
		return TenTheLoai;
	}
	public void setTenTheLoai(String tenTheLoai) {
		TenTheLoai = tenTheLoai;
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
