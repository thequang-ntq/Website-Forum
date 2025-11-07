package Modal.DanhGiaBaiViet;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import Config.DBConfig;
import Modal.LuotThichBinhLuan.LuotThichBinhLuan;

public class DanhGiaBaiVietDAO {
    public ArrayList<DanhGiaBaiViet> readDB() throws Exception {
        ArrayList<DanhGiaBaiViet> ds = new ArrayList<DanhGiaBaiViet>();
        String sql = "SELECT * FROM DanhGiaBaiViet";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        ResultSet rs = pr.executeQuery();
        while (rs.next()) {
            long MaDanhGia = rs.getLong("MaDanhGia");
            long MaBaiViet = rs.getLong("MaBaiViet");
            String TenDangNhap = rs.getNString("TenDangNhap");
            BigDecimal Diem = rs.getBigDecimal("Diem");
            Timestamp ThoiDiemDanhGia = rs.getTimestamp("ThoiDiemDanhGia");
            ds.add(new DanhGiaBaiViet(MaDanhGia, MaBaiViet, TenDangNhap, Diem, ThoiDiemDanhGia));
        }

        pr.close();
        rs.close();
        return ds;
    }

    public void createDB(DanhGiaBaiViet dgbv) throws Exception {
        String sql = "INSERT INTO DanhGiaBaiViet (MaBaiViet, TenDangNhap, Diem) VALUES (?, ?, ?)";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, dgbv.getMaBaiViet());
        pr.setNString(2, dgbv.getTenDangNhap());
        pr.setBigDecimal(3, dgbv.getDiem());
        pr.executeUpdate();
        pr.close();
    }

    public void updateDB(DanhGiaBaiViet dgbv) throws Exception {
        String sql = "UPDATE DanhGiaBaiViet SET MaBaiViet = ?, TenDangNhap = ?, Diem = ? WHERE MaDanhGia = ?";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, dgbv.getMaBaiViet());
        pr.setNString(2, dgbv.getTenDangNhap());
        pr.setBigDecimal(3, dgbv.getDiem());
        pr.setLong(4, dgbv.getMaDanhGia());
        pr.executeUpdate();
        pr.close();
    }

    public void deleteDB(long maDanhGia) throws Exception {
        String sql = "DELETE FROM DanhGiaBaiViet WHERE MaDanhGia = ?";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, maDanhGia);
        pr.executeUpdate();
        pr.close();
    }
    
	public void deleteDBByMaBaiVietVaTenDangNhap(long maBaiViet, String tenDangNhap) throws Exception {
	    String sql = "DELETE FROM DanhGiaBaiViet WHERE MaBaiViet = ? AND TenDangNhap = ?";
	    PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
	    pr.setLong(1, maBaiViet);
	    pr.setNString(2, tenDangNhap);
	    pr.executeUpdate();
	    pr.close();
	}

    public DanhGiaBaiViet findByMaDanhGia(long maDanhGia) throws Exception {
        String sql = "SELECT * FROM DanhGiaBaiViet WHERE MaDanhGia = ?";
        PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
        pr.setLong(1, maDanhGia);
        ResultSet rs = pr.executeQuery();
        DanhGiaBaiViet dgbv = null;
        if (rs.next()) {
            long MaDanhGia = rs.getLong("MaDanhGia");
            long MaBaiViet = rs.getLong("MaBaiViet");
            String TenDangNhap = rs.getNString("TenDangNhap");
            BigDecimal Diem = rs.getBigDecimal("Diem");
            Timestamp ThoiDiemDanhGia = rs.getTimestamp("ThoiDiemDanhGia");
            dgbv = new DanhGiaBaiViet(MaDanhGia, MaBaiViet, TenDangNhap, Diem, ThoiDiemDanhGia);
        }

        pr.close();
        rs.close();
        return dgbv;
    }
    
	public DanhGiaBaiViet findByMaBaiVietVaTenDangNhap(long maBaiViet, String tenDangNhap) throws Exception {
		String sql = "SELECT * FROM DanhGiaBaiViet WHERE MaBaiViet = ? AND TenDangNhap = ?";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		pr.setLong(1, maBaiViet);
		pr.setNString(2, tenDangNhap);
		ResultSet rs = pr.executeQuery();
		DanhGiaBaiViet dgbv = null;
		if(rs.next()) {
            long MaDanhGia = rs.getLong("MaDanhGia");
            long MaBaiViet = rs.getLong("MaBaiViet");
            String TenDangNhap = rs.getNString("TenDangNhap");
            BigDecimal Diem = rs.getBigDecimal("Diem");
            Timestamp ThoiDiemDanhGia = rs.getTimestamp("ThoiDiemDanhGia");
			dgbv = new DanhGiaBaiViet(MaDanhGia, MaBaiViet, TenDangNhap, Diem, ThoiDiemDanhGia);
		}
		
		pr.close();
		rs.close();
		return dgbv;
	}
}