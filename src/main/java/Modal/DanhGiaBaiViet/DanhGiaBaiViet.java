package Modal.DanhGiaBaiViet;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class DanhGiaBaiViet {
    private long MaDanhGia;
    private long MaBaiViet;
    private String TenDangNhap;
    private BigDecimal Diem;
    private Timestamp ThoiDiemDanhGia;

    public DanhGiaBaiViet() {
        super();
    }

    public DanhGiaBaiViet(long maDanhGia, long maBaiViet, String tenDangNhap, BigDecimal diem, Timestamp thoiDiemDanhGia) {
        super();
        this.MaDanhGia = maDanhGia;
        this.MaBaiViet = maBaiViet;
        this.TenDangNhap = tenDangNhap;
        this.Diem = diem;
        this.ThoiDiemDanhGia = thoiDiemDanhGia;
    }

    public long getMaDanhGia() {
        return MaDanhGia;
    }

    public void setMaDanhGia(long maDanhGia) {
        this.MaDanhGia = maDanhGia;
    }

    public long getMaBaiViet() {
        return MaBaiViet;
    }

    public void setMaBaiViet(long maBaiViet) {
        this.MaBaiViet = maBaiViet;
    }

    public String getTenDangNhap() {
        return TenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.TenDangNhap = tenDangNhap;
    }

    public BigDecimal getDiem() {
        return Diem;
    }

    public void setDiem(BigDecimal diem) {
        this.Diem = diem;
    }

    public Timestamp getThoiDiemDanhGia() {
        return ThoiDiemDanhGia;
    }

    public void setThoiDiemDanhGia(Timestamp thoiDiemDanhGia) {
        this.ThoiDiemDanhGia = thoiDiemDanhGia;
    }
}