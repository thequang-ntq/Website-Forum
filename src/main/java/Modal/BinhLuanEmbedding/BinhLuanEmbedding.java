package Modal.BinhLuanEmbedding;

import java.sql.Timestamp;

public class BinhLuanEmbedding {
    private long ID;
    private long MaBinhLuan;
    private String Embedding;
    private Timestamp ThoiDiemTao;
    
    public BinhLuanEmbedding() {
        super();
    }

	public BinhLuanEmbedding(long iD, long maBinhLuan, String embedding, Timestamp thoiDiemTao) {
		super();
		ID = iD;
		MaBinhLuan = maBinhLuan;
		Embedding = embedding;
		ThoiDiemTao = thoiDiemTao;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getMaBinhLuan() {
		return MaBinhLuan;
	}

	public void setMaBinhLuan(long maBinhLuan) {
		MaBinhLuan = maBinhLuan;
	}

	public String getEmbedding() {
		return Embedding;
	}

	public void setEmbedding(String embedding) {
		Embedding = embedding;
	}

	public Timestamp getThoiDiemTao() {
		return ThoiDiemTao;
	}

	public void setThoiDiemTao(Timestamp thoiDiemTao) {
		ThoiDiemTao = thoiDiemTao;
	}
}