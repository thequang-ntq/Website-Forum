package Modal.TinNhanChat;

import java.sql.Timestamp;

public class TinNhanChat {
	private long MaTinNhan;
	private long MaDoanChat;
	private String Role;
	private String NoiDung;
	private String Url;
	private Timestamp ThoiDiemTao;
	public TinNhanChat() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TinNhanChat(long maTinNhan, long maDoanChat, String role, String noiDung, String url,
			Timestamp thoiDiemTao) {
		super();
		MaTinNhan = maTinNhan;
		MaDoanChat = maDoanChat;
		Role = role;
		NoiDung = noiDung;
		Url = url;
		ThoiDiemTao = thoiDiemTao;
	}
	public long getMaTinNhan() {
		return MaTinNhan;
	}
	public void setMaTinNhan(long maTinNhan) {
		MaTinNhan = maTinNhan;
	}
	public long getMaDoanChat() {
		return MaDoanChat;
	}
	public void setMaDoanChat(long maDoanChat) {
		MaDoanChat = maDoanChat;
	}
	public String getRole() {
		return Role;
	}
	public void setRole(String role) {
		Role = role;
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
	public Timestamp getThoiDiemTao() {
		return ThoiDiemTao;
	}
	public void setThoiDiemTao(Timestamp thoiDiemTao) {
		ThoiDiemTao = thoiDiemTao;
	}
	
}
