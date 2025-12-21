package Modal.TaiKhoan;

import java.sql.Timestamp;

public class TaiKhoan {
	private String TenDangNhap;
	private String MatKhau;
	private String Quyen;
	private String TrangThai;
	private Timestamp ThoiDiemTao;
	private Timestamp ThoiDiemCapNhat;
	private String Email;
	
	public TaiKhoan() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TaiKhoan(String tenDangNhap, String matKhau, String quyen, String trangThai, 
            String email, Timestamp thoiDiemTao, Timestamp thoiDiemCapNhat) {
		super();
		TenDangNhap = tenDangNhap;
		MatKhau = matKhau;
		Quyen = quyen;
		TrangThai = trangThai;
		Email = email;
		ThoiDiemTao = thoiDiemTao;
		ThoiDiemCapNhat = thoiDiemCapNhat;
	}
	public String getTenDangNhap() {
		return TenDangNhap;
	}
	public void setTenDangNhap(String tenDangNhap) {
		TenDangNhap = tenDangNhap;
	}
	public String getMatKhau() {
		return MatKhau;
	}
	public void setMatKhau(String matKhau) {
		MatKhau = matKhau;
	}
	public String getQuyen() {
		return Quyen;
	}
	public void setQuyen(String quyen) {
		Quyen = quyen;
	}
	public String getTrangThai() {
		return TrangThai;
	}
	public void setTrangThai(String trangThai) {
		TrangThai = trangThai;
	}
	public String getEmail() {
	    return Email;
	}

	public void setEmail(String email) {
	    Email = email;
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
