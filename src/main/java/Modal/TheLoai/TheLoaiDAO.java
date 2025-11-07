package Modal.TheLoai;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import Config.DBConfig;
import Modal.TheLoai.TheLoai;

public class TheLoaiDAO {
	public ArrayList<TheLoai> readDB() throws Exception {
		ArrayList<TheLoai> ds = new ArrayList<TheLoai>();
		String sql = "SELECT * FROM TheLoai;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		ResultSet rs = pr.executeQuery();
		while(rs.next()) {
			int MaTheLoai = rs.getInt("MaTheLoai");
			String TenTheLoai = rs.getNString("TenTheLoai");
			ds.add(new TheLoai(MaTheLoai, TenTheLoai));
		}
		
		pr.close();
		rs.close();
		return ds;
	}
	
	public void createDB(TheLoai tl) throws Exception {
		String sql = "INSERT INTO TheLoai (TenTheLoai) VALUES (?);";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setNString(1, tl.getTenTheLoai());
		pr.executeUpdate();
		pr.close();
	}
	
	public void updateDB(TheLoai tl) throws Exception {
		String sql = "UPDATE TheLoai SET TenTheLoai = ? WHERE MaTheLoai = ?;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setNString(1, tl.getTenTheLoai());
		pr.setInt(2, tl.getMaTheLoai());
		pr.executeUpdate();
		pr.close();
	}
	
	public void deleteDB(int MaTheLoai) throws Exception {
		String sql = "DELETE FROM TheLoai WHERE MaTheLoai = ?;";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setInt(1, MaTheLoai);
		pr.executeUpdate();
		pr.close();
	}
	
	public TheLoai findByMaTheLoai(int maTheLoai) throws Exception {
	    String sql = "SELECT * FROM TheLoai WHERE MaTheLoai = ?";
	    PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
	    pr.setInt(1, maTheLoai);
	    ResultSet rs = pr.executeQuery();
	    
	    TheLoai tl = null;
	    if (rs.next()) {
	        int MaTheLoai = rs.getInt("MaTheLoai");
	        String TenTheLoai = rs.getNString("TenTheLoai");
	        tl = new TheLoai(MaTheLoai, TenTheLoai);
	    }

	    rs.close();
	    pr.close();
	    return tl;
	}
	
	public TheLoai findByTenTheLoai(String tenTheLoai) throws Exception {
	    String sql = "SELECT * FROM TheLoai WHERE TenTheLoai = ?";  // bỏ dấu '
	    PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
	    pr.setNString(1, tenTheLoai);
	    ResultSet rs = pr.executeQuery();
	    
	    TheLoai tl = null;
	    if (rs.next()) {
	        int MaTheLoai = rs.getInt("MaTheLoai");
	        String TenTheLoai = rs.getNString("TenTheLoai");
	        tl = new TheLoai(MaTheLoai, TenTheLoai);
	    }

	    rs.close();
	    pr.close();
	    return tl;
	}

}
