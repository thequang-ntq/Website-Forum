package Modal.TaiKhoan;

import java.util.ArrayList;

import Support.EmailService;

public class TaiKhoanBO {
	private TaiKhoanDAO dao = new TaiKhoanDAO();
	ArrayList<TaiKhoan> ds;
	
	public ArrayList<TaiKhoan> readDB() throws Exception {
		ds = dao.readDB();
		return ds;
	}
	
	public void createDB(String TenDangNhap, String MatKhau, String Quyen, String Email) throws Exception {
	    if(TenDangNhap == null || TenDangNhap.trim().isEmpty()) {
	        throw new Exception("Tên đăng nhập không được để trống!");
	    }
	    if(MatKhau == null || MatKhau.trim().isEmpty()) {
	        throw new Exception("Mật khẩu không được để trống!");
	    }
	    if(dao.findByTenDangNhap(TenDangNhap) != null) {
	        throw new Exception("Tên đăng nhập đã tồn tại!");
	    }
	    
	    // Validate email nếu có
	    if(Email != null && !Email.trim().isEmpty()) {
	        if(!EmailService.isValidEmail(Email.trim())) {
	            throw new Exception("Email không hợp lệ!");
	        }
	    }
	    
	    TaiKhoan tk = new TaiKhoan(TenDangNhap, MatKhau, Quyen, null, Email, null, null);
	    dao.createDB(tk);
	}
	
	// Thêm method updateEmail
	public void updateEmail(String TenDangNhap, String Email) throws Exception {
	    TaiKhoan tk = dao.findByTenDangNhap(TenDangNhap);
	    if(tk == null || "Deleted".equals(tk.getTrangThai())) {
	        throw new Exception("Tài khoản không tồn tại!");
	    }
	    
	    if(Email == null || Email.trim().isEmpty()) {
	        throw new Exception("Email không được để trống!");
	    }
	    
	    if(!EmailService.isValidEmail(Email.trim())) {
	        throw new Exception("Email không hợp lệ!");
	    }
	    
	    tk.setEmail(Email.trim());
	    dao.updateDB(tk);
	}
	
	public void updateDB(String TenDangNhap, String MatKhau, String Quyen, String TrangThai, String Email) throws Exception {
	    if(TenDangNhap == null || TenDangNhap.trim().isEmpty()) {
	        throw new Exception("Tên đăng nhập không được để trống!");
	    }
	    if(MatKhau == null || MatKhau.trim().isEmpty()) {
	        throw new Exception("Mật khẩu không được để trống!");
	    }
	    if(TrangThai == null || TrangThai.trim().isEmpty()) {
	        throw new Exception("Trạng thái không được để trống!");
	    }
	    if(dao.findByTenDangNhap(TenDangNhap) == null) {
	        throw new Exception("Tài khoản không tồn tại!");
	    }
	    
	    // Validate email nếu có
	    if(Email != null && !Email.trim().isEmpty()) {
	        if(!EmailService.isValidEmail(Email.trim())) {
	            throw new Exception("Email không hợp lệ!");
	        }
	    }
	    
	    TaiKhoan tk = new TaiKhoan(TenDangNhap, MatKhau, Quyen, TrangThai, Email, null, null);
	    dao.updateDB(tk);
	}
	
	public void deleteDB(String TenDangNhap) throws Exception {
		if(TenDangNhap == null || TenDangNhap.trim().isEmpty()) {
			throw new Exception("Tên đăng nhập không được để trống!");
		}
		if(dao.findByTenDangNhap(TenDangNhap) == null) {
			throw new Exception("Tài khoản không tồn tại!");
		}
		
		dao.deleteDB(TenDangNhap);
	}
	
	// Tìm theo tên tài khoản / quyền / trạng thái / Email / thời điểm tạo / thời điểm cập nhật
	public ArrayList<TaiKhoan> findDB(String key) throws Exception {
		ArrayList<TaiKhoan> temp = new ArrayList<TaiKhoan>();
		for(TaiKhoan tk: readDB()) {
			if(tk.getTenDangNhap().trim().toLowerCase().contains(key.trim().toLowerCase())
			|| tk.getQuyen().trim().toLowerCase().contains(key.trim().toLowerCase())
			|| tk.getTrangThai().trim().toLowerCase().contains(key.trim().toLowerCase())
			|| (tk.getEmail() != null && tk.getEmail().trim().toLowerCase().contains(key.trim().toLowerCase()))
			|| tk.getThoiDiemTao().toString().trim().toLowerCase().contains(key.trim().toLowerCase())
			|| (tk.getThoiDiemCapNhat() != null && tk.getThoiDiemCapNhat().toString().trim().toLowerCase().contains(key.trim().toLowerCase()))
			) {
				temp.add(tk);
			}
		}
		
		return temp;
	}
	
	// Nếu tài khoản chưa tồn tại == null: thì đăng ký thành công 
	public TaiKhoan checkRegisterDB(String TenDangNhap) throws Exception {
		TaiKhoan tk = dao.findByTenDangNhap(TenDangNhap);
		return tk;
	}
	
	// Nếu tài khoản tồn tại != null và đang hoạt động: thì trả về tk và đăng nhập thành công
	public TaiKhoan checkLoginDB(String TenDangNhap, String MatKhau) throws Exception {
		TaiKhoan tk = dao.findByTenDangNhap(TenDangNhap);
		if(tk != null && tk.getMatKhau().equals(MatKhau) && tk.getTrangThai().equals("Active")) {
			return tk;
		}
		
		return null;
	}
	
	//Tài khoản tồn tại, trạng thái không phải Deleted, có thể Active hoặc Hidden
	public void forgetPassDB(String TenDangNhap, String MatKhauMoi, String NhapLaiMatKhauMoi) throws Exception {
		TaiKhoan tk = dao.findByTenDangNhap(TenDangNhap);
		if(tk == null || tk.getTrangThai().equals("Deleted")) {
			throw new Exception("Tài khoản không tồn tại!");
		}
		if(MatKhauMoi == null || MatKhauMoi.trim().isEmpty()) {
			throw new Exception("Mật khẩu mới không được để trống!");
		}
		if(NhapLaiMatKhauMoi == null || NhapLaiMatKhauMoi.trim().isEmpty()) {
			throw new Exception("Xin hãy nhập lại mật khẩu mới!");
		}
		if(!NhapLaiMatKhauMoi.equals(MatKhauMoi)) {
			throw new Exception("Nhập lại mật khẩu mới chưa khớp với mật khẩu mới!");
		}
		
		tk.setMatKhau(MatKhauMoi);
		dao.updateDB(tk);
	}
	
	public void changePassDB(String TenDangNhap, String MatKhauCu, String MatKhauMoi) throws Exception {
		TaiKhoan tk = dao.findByTenDangNhap(TenDangNhap);
		if(tk == null || tk.getTrangThai().equals("Deleted")) {
			throw new Exception("Tài khoản không tồn tại!");
		}
		if(!tk.getMatKhau().equals(MatKhauCu)) {
			throw new Exception("Mật khẩu cũ không đúng!");
		}
		if(MatKhauMoi == null || MatKhauMoi.trim().isEmpty()) {
			throw new Exception("Mật khẩu mới không được để trống!");
		}
		if(MatKhauMoi.equals(MatKhauCu)) {
			throw new Exception("Mật khẩu mới không được trùng mật khẩu cũ!");
		}
		
		tk.setMatKhau(MatKhauMoi);
		dao.updateDB(tk);
	}
}