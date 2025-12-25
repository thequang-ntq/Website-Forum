package Modal.DoanChat;

import java.util.ArrayList;

public class DoanChatBO {
	DoanChatDAO dao = new DoanChatDAO();
	ArrayList<DoanChat> ds;
	
	public ArrayList<DoanChat> readDB(String TenDangNhap) throws Exception {
		ds = dao.readDB(TenDangNhap);
		return ds;
	}
	
	public long createDB(String TenDangNhap, String TieuDe) throws Exception {
        if (TenDangNhap == null || TenDangNhap.trim().isEmpty()) {
            throw new Exception("Tên đăng nhập không được để trống!");
        }
        if (TieuDe == null || TieuDe.trim().isEmpty()) {
            throw new Exception("Tiêu đề không được để trống!");
        }
        DoanChat dc = new DoanChat(-1, TenDangNhap, TieuDe, null, null);
        return dao.createDB(dc);
	}
	
	public void updateDB(long MaDoanChat) throws Exception {
		if(MaDoanChat < 1) {
			throw new Exception("Mã đoạn chat không được để trống");
		}
		DoanChat dc = new DoanChat(MaDoanChat, null, null, null, null);
        dao.updateDB(dc);
	}
	
	public void deleteDB(long MaDoanChat) throws Exception {
		if(MaDoanChat < 1) {
			throw new Exception("Mã đoạn chat không được để trống");
		}
		dao.deleteDB(MaDoanChat);
	}
}
