package Modal.TaiKhoan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import Config.DBConfig;

public class TaiKhoanDAO {
	// Sửa method readDB()
	public ArrayList<TaiKhoan> readDB() throws Exception {
	    ArrayList<TaiKhoan> ds = new ArrayList<TaiKhoan>();
	    String sql = "SELECT * FROM TaiKhoan;";
	    PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
	    ResultSet rs = pr.executeQuery();
	    while(rs.next()) {
	        String TenDangNhap = rs.getNString("TenDangNhap");
	        String MatKhau = rs.getNString("MatKhau");
	        String Quyen = rs.getNString("Quyen");
	        String TrangThai = rs.getNString("TrangThai");
	        String Email = rs.getNString("Email");
	        Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
	        Timestamp ThoiDiemCapNhat = rs.getTimestamp("ThoiDiemCapNhat");
	        ds.add(new TaiKhoan(TenDangNhap, MatKhau, Quyen, TrangThai, Email, ThoiDiemTao, ThoiDiemCapNhat));
	    }
	    pr.close();
	    rs.close();
	    return ds;
	}

	// Sửa method createDB(), lúc tạo thì trạng thái mặc định Active
	// Đăng ký thì là Quyền là User, còn tạo trong Quản lý tài khoản mới chọn được là User hay Admin.
	public void createDB(TaiKhoan tk) throws Exception {
	    String sql = "INSERT INTO TaiKhoan (TenDangNhap, MatKhau, Quyen, Email) VALUES (?, ?, ?, ?);";
	    PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
	    pr.setNString(1, tk.getTenDangNhap());
	    pr.setNString(2, tk.getMatKhau());
	    pr.setNString(3, tk.getQuyen());
	    pr.setNString(4, tk.getEmail());
	    pr.executeUpdate();
	    pr.close();
	}

	// Sửa method updateDB()
	public void updateDB(TaiKhoan tk) throws Exception {
	    String sql = "UPDATE TaiKhoan SET MatKhau = ?, Quyen = ?, TrangThai = ?, Email = ? WHERE TenDangNhap = ?;";
	    PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
	    pr.setNString(1, tk.getMatKhau());
	    pr.setNString(2, tk.getQuyen());
	    pr.setNString(3, tk.getTrangThai());
	    pr.setNString(4, tk.getEmail());
	    pr.setNString(5, tk.getTenDangNhap());
	    pr.executeUpdate();
	    pr.close();
	}
	
	public void deleteDB(String TenDangNhap) throws Exception {
		String sql = "DELETE FROM TaiKhoan WHERE TenDangNhap = ?;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setNString(1, TenDangNhap);
		pr.executeUpdate();
		pr.close();
	}
	
	// Sửa method findByTenDangNhap()
	public TaiKhoan findByTenDangNhap(String tenDangNhap) throws Exception {
	    String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ?;";
	    PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
	    pr.setNString(1, tenDangNhap);
	    ResultSet rs = pr.executeQuery();
	    TaiKhoan tk = null;
	    if(rs.next()) {
	        String TenDangNhap = rs.getNString("TenDangNhap");
	        String MatKhau = rs.getNString("MatKhau");
	        String Quyen = rs.getNString("Quyen");
	        String TrangThai = rs.getNString("TrangThai");
	        String Email = rs.getNString("Email");
	        Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
	        Timestamp ThoiDiemCapNhat = rs.getTimestamp("ThoiDiemCapNhat");
	        tk = new TaiKhoan(TenDangNhap, MatKhau, Quyen, TrangThai, Email, ThoiDiemTao, ThoiDiemCapNhat);
	    }
	    pr.close();
	    rs.close();
	    return tk;
	}
}