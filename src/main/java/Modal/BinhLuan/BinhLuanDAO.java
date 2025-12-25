package Modal.BinhLuan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import Config.DBConfig;

public class BinhLuanDAO {
	public ArrayList<BinhLuan> readDB() throws Exception {
		ArrayList<BinhLuan> ds = new ArrayList<BinhLuan>();
		String sql = "SELECT * FROM BinhLuan;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		ResultSet rs = pr.executeQuery();
		while(rs.next()) {
			long MaBinhLuan = rs.getLong("MaBinhLuan");
			String TieuDe = rs.getNString("TieuDe");
			String NoiDung = rs.getNString("NoiDung");
			String Url = rs.getNString("Url");
			String TaiKhoanTao = rs.getNString("TaiKhoanTao");
			long MaBaiViet = rs.getLong("MaBaiViet");
			int SoLuotThich = rs.getInt("SoLuotThich");
			String TrangThai = rs.getNString("TrangThai");
			Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
			Timestamp ThoiDiemCapNhat = rs.getTimestamp("ThoiDiemCapNhat");
			ds.add(new BinhLuan(MaBinhLuan, TieuDe, NoiDung, Url, TaiKhoanTao, MaBaiViet, SoLuotThich, TrangThai, ThoiDiemTao, ThoiDiemCapNhat));
		}
		
		pr.close();
		rs.close();
		return ds;
	}
	
	public void createDB(BinhLuan bl) throws Exception {
		String sql = "INSERT INTO BinhLuan (NoiDung, Url, TaiKhoanTao, MaBaiViet) VALUES (?, ?, ?, ?);";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setNString(1, bl.getNoiDung());
		pr.setNString(2, bl.getUrl());
		pr.setNString(3, bl.getTaiKhoanTao());
		pr.setLong(4, bl.getMaBaiViet());
		pr.executeUpdate();
		pr.close();
	}
	
	// Tổng số lượt thích lấy từ bảng LuotThichBinhLuan.
	public void updateDB(BinhLuan bl) throws Exception {
		String sql = "UPDATE BinhLuan SET NoiDung = ?, Url = ?, SoLuotThich = ?, TrangThai = ? WHERE MaBinhLuan = ?;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setNString(1, bl.getNoiDung());
		pr.setNString(2, bl.getUrl());
		pr.setInt(3, bl.getSoLuotThich());
		pr.setNString(4, bl.getTrangThai());
		pr.setLong(5, bl.getMaBinhLuan());
		pr.executeUpdate();
		pr.close();
	}
	
	public void deleteDB(long MaBinhLuan) throws Exception {
		String sql = "DELETE FROM BinhLuan WHERE MaBinhLuan = ?;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setLong(1, MaBinhLuan);
		pr.executeUpdate();
		pr.close();
	}
	
	public BinhLuan findByMaBinhLuan(long maBinhLuan) throws Exception {
		String sql = "SELECT * FROM BinhLuan WHERE MaBinhLuan = ?;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setLong(1, maBinhLuan);
		ResultSet rs = pr.executeQuery();
		BinhLuan bl = null;
		if(rs.next()) {
			long MaBinhLuan = rs.getLong("MaBinhLuan");
			String TieuDe = rs.getNString("TieuDe");
			String NoiDung = rs.getNString("NoiDung");
			String Url = rs.getNString("Url");
			String TaiKhoanTao = rs.getNString("TaiKhoanTao");
			long MaBaiViet = rs.getLong("MaBaiViet");
			int SoLuotThich = rs.getInt("SoLuotThich");
			String TrangThai = rs.getNString("TrangThai");
			Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
			Timestamp ThoiDiemCapNhat = rs.getTimestamp("ThoiDiemCapNhat");
			bl = new BinhLuan(MaBinhLuan, TieuDe, NoiDung, Url, TaiKhoanTao, MaBaiViet, SoLuotThich, TrangThai, ThoiDiemTao, ThoiDiemCapNhat);
		}
		
		pr.close();
		rs.close();
		return bl;
	}
	
	public ArrayList<BinhLuan> findByMaBaiViet(long maBaiViet) throws Exception {
		ArrayList<BinhLuan> ds = new ArrayList<BinhLuan>();
		String sql = "SELECT * FROM BinhLuan WHERE MaBaiViet = ?;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setLong(1, maBaiViet);
		ResultSet rs = pr.executeQuery();
		while(rs.next()) {
			long MaBinhLuan = rs.getLong("MaBinhLuan");
			String TieuDe = rs.getNString("TieuDe");
			String NoiDung = rs.getNString("NoiDung");
			String Url = rs.getNString("Url");
			String TaiKhoanTao = rs.getNString("TaiKhoanTao");
			long MaBaiViet = rs.getLong("MaBaiViet");
			int SoLuotThich = rs.getInt("SoLuotThich");
			String TrangThai = rs.getNString("TrangThai");
			Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
			Timestamp ThoiDiemCapNhat = rs.getTimestamp("ThoiDiemCapNhat");
			ds.add(new BinhLuan(MaBinhLuan, TieuDe, NoiDung, Url, TaiKhoanTao, MaBaiViet, SoLuotThich, TrangThai, ThoiDiemTao, ThoiDiemCapNhat));
		}
		
		pr.close();
		rs.close();
		return ds;
	}
}