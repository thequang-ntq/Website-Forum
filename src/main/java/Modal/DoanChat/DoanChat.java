package Modal.DoanChat;

import java.sql.Timestamp;

public class DoanChat {
    private long MaDoanChat;
    private String TenDangNhap;
    private String TieuDe;
    private Timestamp ThoiDiemTao;
    private Timestamp ThoiDiemCapNhat;
	public DoanChat() {
		super();
		// TODO Auto-generated constructor stub
	}
	public DoanChat(long maDoanChat, String tenDangNhap, String tieuDe, Timestamp thoiDiemTao,
			Timestamp thoiDiemCapNhat) {
		super();
		MaDoanChat = maDoanChat;
		TenDangNhap = tenDangNhap;
		TieuDe = tieuDe;
		ThoiDiemTao = thoiDiemTao;
		ThoiDiemCapNhat = thoiDiemCapNhat;
	}
	public long getMaDoanChat() {
		return MaDoanChat;
	}
	public void setMaDoanChat(long maDoanChat) {
		MaDoanChat = maDoanChat;
	}
	public String getTenDangNhap() {
		return TenDangNhap;
	}
	public void setTenDangNhap(String tenDangNhap) {
		TenDangNhap = tenDangNhap;
	}
	public String getTieuDe() {
		return TieuDe;
	}
	public void setTieuDe(String tieuDe) {
		TieuDe = tieuDe;
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
