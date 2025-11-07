package Modal.LuotThichBinhLuan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import Config.DBConfig;

public class LuotThichBinhLuanDAO {
	public ArrayList<LuotThichBinhLuan> readDB() throws Exception {
		ArrayList<LuotThichBinhLuan> ds = new ArrayList<LuotThichBinhLuan>();
		String sql = "SELECT * FROM LuotThichBinhLuan";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		ResultSet rs = pr.executeQuery();
		while(rs.next()) {
			long MaLuotThich = rs.getLong("MaLuotThich");
			long MaBinhLuan = rs.getLong("MaBinhLuan");
			String TenDangNhap = rs.getNString("TenDangNhap");
			Timestamp ThoiDiemThich = rs.getTimestamp("ThoiDiemThich");
			ds.add(new LuotThichBinhLuan(MaLuotThich, MaBinhLuan, TenDangNhap, ThoiDiemThich));
		}
		
		pr.close();
		rs.close();
		return ds;
	}
	
	public void createDB(LuotThichBinhLuan ltbl) throws Exception {
		String sql = "INSERT INTO LuotThichBinhLuan (MaBinhLuan, TenDangNhap) VALUES (?, ?)";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setLong(1, ltbl.getMaBinhLuan());
		pr.setNString(2, ltbl.getTenDangNhap());
		pr.executeUpdate();
		pr.close();
	}
	
	public void updateDB(LuotThichBinhLuan ltbl) throws Exception {
		// Không cần
	}
	
	public void deleteDB(long maLuotThich) throws Exception {
		String sql = "DELETE FROM LuotThichBinhLuan WHERE MaLuotThich = ?;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setLong(1, maLuotThich);
		pr.executeUpdate();
		pr.close();
	}
	
	public void deleteDBByMaBinhLuanVaTenDangNhap(long maBinhLuan, String tenDangNhap) throws Exception {
	    String sql = "DELETE FROM LuotThichBinhLuan WHERE MaBinhLuan = ? AND TenDangNhap = ?";
	    PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
	    pr.setLong(1, maBinhLuan);
	    pr.setNString(2, tenDangNhap);
	    pr.executeUpdate();
	    pr.close();
	}
	
	public LuotThichBinhLuan findByMaLuotThich(long maLuotThich) throws Exception {
		String sql = "SELECT * FROM LuotThichBinhLuan WHERE MaLuotThich = ?";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setLong(1, maLuotThich);
		ResultSet rs = pr.executeQuery();
		LuotThichBinhLuan ltbl = null;
		if(rs.next()) {
			long MaLuotThich = rs.getLong("MaLuotThich");
			long MaBinhLuan = rs.getLong("MaBinhLuan");
			String TenDangNhap = rs.getNString("TenDangNhap");
			Timestamp ThoiDiemThich = rs.getTimestamp("ThoiDiemThich");
			ltbl = new LuotThichBinhLuan(MaLuotThich, MaBinhLuan, TenDangNhap, ThoiDiemThich);
		}
		
		pr.close();
		rs.close();
		return ltbl;
	}
	
	public LuotThichBinhLuan findByMaBinhLuanVaTenDangNhap(long maBinhLuan, String tenDangNhap) throws Exception {
		String sql = "SELECT * FROM LuotThichBinhLuan WHERE MaBinhLuan = ? AND TenDangNhap = ?";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setLong(1, maBinhLuan);
		pr.setNString(2, tenDangNhap);
		ResultSet rs = pr.executeQuery();
		LuotThichBinhLuan ltbl = null;
		if(rs.next()) {
			long MaLuotThich = rs.getLong("MaLuotThich");
			long MaBinhLuan = rs.getLong("MaBinhLuan");
			String TenDangNhap = rs.getNString("TenDangNhap");
			Timestamp ThoiDiemThich = rs.getTimestamp("ThoiDiemThich");
			ltbl = new LuotThichBinhLuan(MaLuotThich, MaBinhLuan, TenDangNhap, ThoiDiemThich);
		}
		
		pr.close();
		rs.close();
		return ltbl;
	}
}
