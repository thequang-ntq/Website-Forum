package Modal.TaiKhoan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import Config.DBConfig;

public class TaiKhoanDAO {
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
			Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
			Timestamp ThoiDiemCapNhat = rs.getTimestamp("ThoiDiemCapNhat");
			ds.add(new TaiKhoan(TenDangNhap, MatKhau, Quyen, TrangThai, ThoiDiemTao, ThoiDiemCapNhat));
		}
		
		pr.close();
		rs.close();
		return ds;
	}
	
	public void createDB(TaiKhoan tk) throws Exception {
		String sql = "INSERT INTO TaiKhoan (TenDangNhap, MatKhau, Quyen) VALUES (?, ?, ?);";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setNString(1, tk.getTenDangNhap());
		pr.setNString(2, tk.getMatKhau());
		pr.setNString(3, tk.getQuyen());
		pr.executeUpdate();
		pr.close();
	}
	
	public void updateDB(TaiKhoan tk) throws Exception {
		String sql = "UPDATE TaiKhoan SET MatKhau = ?, Quyen = ?, TrangThai = ? WHERE TenDangNhap = ?;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setNString(1, tk.getMatKhau());
		pr.setNString(2, tk.getQuyen());
		pr.setNString(3, tk.getTrangThai());
		pr.setNString(4, tk.getTenDangNhap());
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
			Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
			Timestamp ThoiDiemCapNhat = rs.getTimestamp("ThoiDiemCapNhat");
			tk = new TaiKhoan(TenDangNhap, MatKhau, Quyen, TrangThai, ThoiDiemTao, ThoiDiemCapNhat);
		}
		
		pr.close();
		rs.close();
		return tk;
	}
}