package Modal.BaiVietEmbedding;

import java.sql.Timestamp;

public class BaiVietEmbedding {
    private long ID;
    private long MaBaiViet;
    private String Embedding;
    private Timestamp ThoiDiemTao;
    
    public BaiVietEmbedding() {
        super();
    }
    
    public BaiVietEmbedding(long iD, long maBaiViet, String embedding, Timestamp thoiDiemTao) {
        super();
        ID = iD;
        MaBaiViet = maBaiViet;
        Embedding = embedding;
        ThoiDiemTao = thoiDiemTao;
    }

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getMaBaiViet() {
		return MaBaiViet;
	}

	public void setMaBaiViet(long maBaiViet) {
		MaBaiViet = maBaiViet;
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