package Modal.TinNhanChat;

import java.util.ArrayList;

public class TinNhanChatBO {
	TinNhanChatDAO dao = new TinNhanChatDAO();
	ArrayList<TinNhanChat> ds;
	
	public ArrayList<TinNhanChat> readDB(long MaDoanChat) throws Exception {
		ds = dao.readDB(MaDoanChat);
		return ds;
	}
	
	public ArrayList<TinNhanChat> readDB2() throws Exception {
		ds = dao.readDB2();
		return ds;
	}
	
	public void createDB(long MaDoanChat, String Role, String NoiDung, String Url) throws Exception {
		if(MaDoanChat < 1) {
			throw new Exception("Mã đoạn chat không được để trống");
		}
        if (Role == null || Role.trim().isEmpty()) {
            throw new Exception("Role không được để trống!");
        }
        // CHO PHÉP NoiDung rỗng nếu có Url (tức là chỉ gửi ảnh)
        // Chỉ bắt buộc có ít nhất một trong hai: text hoặc ảnh
        if ((NoiDung == null || NoiDung.trim().isEmpty()) 
            && (Url == null || Url.trim().isEmpty())) {
            throw new Exception("Phải có nội dung tin nhắn hoặc ảnh!");
        }
        
        TinNhanChat tnc = new TinNhanChat(-1, MaDoanChat, Role, NoiDung, Url, null);
        dao.createDB(tnc);
	}
	
}
