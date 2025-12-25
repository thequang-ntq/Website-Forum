package Modal.DoanChat;

import java.sql.*;
import java.util.ArrayList;
import Config.DBConfig;

public class DoanChatDAO {
    
	// Lấy danh sách đoạn chat của tài khoản theo thứ tự thời điểm cập nhật gần nhất rồi tới thời điểm tạo gần nhất
    public ArrayList<DoanChat> readDB(String tenDangNhap) throws Exception {
        ArrayList<DoanChat> ds = new ArrayList<>();
        String sql = "SELECT * FROM DoanChat WHERE TenDangNhap = ? ORDER BY ThoiDiemCapNhat DESC, ThoiDiemTao DESC";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setNString(1, tenDangNhap);
        ResultSet rs = pr.executeQuery();
        while(rs.next()) {
            ds.add(new DoanChat(
                rs.getLong("MaDoanChat"),
                rs.getNString("TenDangNhap"),
                rs.getNString("TieuDe"),
                rs.getTimestamp("ThoiDiemTao"),
                rs.getTimestamp("ThoiDiemCapNhat")
            ));
        }
        pr.close();
        rs.close();
        return ds;
    }
    
    // Tạo đoạn chat với tên đăng nhập tài khoản và tiêu đề, sau đó lấy ra mã đoạn chat vừa mới tạo.
    public long createDB(DoanChat dc) throws Exception {
    	String sql = "INSERT INTO DoanChat (TenDangNhap, TieuDe) VALUES (?, ?); SELECT SCOPE_IDENTITY();";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setNString(1, dc.getTenDangNhap());
        pr.setNString(2, dc.getTieuDe());
        ResultSet rs = pr.executeQuery();
        
        // Nếu chèn thành công thì trả về mã đoạn chat vừa được tạo
        if(rs.next()) {
            return rs.getLong(1);
        }
        
        // Nếu không thì trả về -1
        pr.close();
        rs.close();
        return -1;
    }
    
    // Cập nhật thời điểm cập nhật của đoạn chat, vì cập nhật này là do thêm tin nhắn chat vào nên phải cập nhật thủ công ở đây, không dùng trigger của đoạn chat được.
    public void updateDB(DoanChat dc) throws Exception {
        String sql = "UPDATE DoanChat SET ThoiDiemCapNhat = GETDATE() WHERE MaDoanChat = ?";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, dc.getMaDoanChat());
        pr.executeUpdate();
        pr.close();
    }
    
    // Xóa đoạn chat
    public void deleteDB(long maDoanChat) throws Exception {
        String sql = "DELETE FROM DoanChat WHERE MaDoanChat = ?";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, maDoanChat);
        pr.executeUpdate();
        pr.close();
    }
}