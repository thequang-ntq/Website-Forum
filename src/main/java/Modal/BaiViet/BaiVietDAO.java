package Modal.BaiViet;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import Config.DBConfig;
import Modal.BaiViet.BaiViet;

public class BaiVietDAO {
	public ArrayList<BaiViet> readDB() throws Exception {
		ArrayList<BaiViet> ds = new ArrayList<BaiViet>();
		String sql = "SELECT * FROM BaiViet;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		ResultSet rs = pr.executeQuery();
		while(rs.next()) {
			long MaBaiViet = rs.getLong("MaBaiViet");
			String TieuDe = rs.getNString("TieuDe");
			String NoiDung = rs.getNString("NoiDung");
			String Url = rs.getNString("Url");
			String TaiKhoanTao = rs.getNString("TaiKhoanTao");
			int MaTheLoai = rs.getInt("MaTheLoai");
			BigDecimal DanhGia = rs.getBigDecimal("DanhGia");
			String TrangThai = rs.getNString("TrangThai");
			Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
			Timestamp ThoiDiemCapNhat = rs.getTimestamp("ThoiDiemCapNhat");
			ds.add(new BaiViet(MaBaiViet, TieuDe, NoiDung, Url, TaiKhoanTao, MaTheLoai, DanhGia, TrangThai, ThoiDiemTao, ThoiDiemCapNhat));
		}
		
		pr.close();
		rs.close();
		return ds;
	}
	
	// Tạo bài viết của Admin giống của User (BaiVietCuaToi), lúc tạo thì đánh giá NULL (0 sao) và Trạng thái Active.
	public void createDB(BaiViet bv) throws Exception {
		String sql = "INSERT INTO BaiViet (TieuDe, NoiDung, Url, TaiKhoanTao, MaTheLoai) VALUES (?, ?, ?, ?, ?);";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setNString(1, bv.getTieuDe());
		pr.setNString(2, bv.getNoiDung());
		pr.setNString(3, bv.getUrl());
		pr.setNString(4, bv.getTaiKhoanTao());
		pr.setInt(5, bv.getMaTheLoai());
		pr.executeUpdate();
		pr.close();
	}
	
	public void updateDB(BaiViet bv) throws Exception {
		String sql = "UPDATE BaiViet SET TieuDe = ?, NoiDung = ?, Url = ?, MaTheLoai = ?, DanhGia = ?, TrangThai = ? WHERE MaBaiViet = ?;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setNString(1, bv.getTieuDe());
		pr.setNString(2, bv.getNoiDung());
		pr.setNString(3, bv.getUrl());
		pr.setInt(4, bv.getMaTheLoai());
		pr.setBigDecimal(5, bv.getDanhGia());
		pr.setNString(6, bv.getTrangThai());
		pr.setLong(7, bv.getMaBaiViet());
		pr.executeUpdate();
		pr.close();
	}
	
	public void deleteDB(long MaBaiViet) throws Exception {
		String sql = "DELETE FROM BaiViet WHERE MaBaiViet = ?;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setLong(1, MaBaiViet);
		pr.executeUpdate();
		pr.close();
	}
	
	public BaiViet findByMaBaiViet(long maBaiViet) throws Exception {
		String sql = "SELECT * FROM BaiViet WHERE MaBaiViet = ?;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setLong(1, maBaiViet);
		ResultSet rs = pr.executeQuery();
		BaiViet bv = null;
		if(rs.next()) {
			long MaBaiViet = rs.getLong("MaBaiViet");
			String TieuDe = rs.getNString("TieuDe");
			String NoiDung = rs.getNString("NoiDung");
			String Url = rs.getNString("Url");
			String TaiKhoanTao = rs.getNString("TaiKhoanTao");
			int MaTheLoai = rs.getInt("MaTheLoai");
			BigDecimal DanhGia = rs.getBigDecimal("DanhGia");
			String TrangThai = rs.getNString("TrangThai");
			Timestamp ThoiDiemTao = rs.getTimestamp("ThoiDiemTao");
			Timestamp ThoiDiemCapNhat = rs.getTimestamp("ThoiDiemCapNhat");
			bv = new BaiViet(MaBaiViet, TieuDe, NoiDung, Url, TaiKhoanTao, MaTheLoai, DanhGia, TrangThai, ThoiDiemTao, ThoiDiemCapNhat);
		}
		
		pr.close();
		rs.close();
		return bv;
	}
}
