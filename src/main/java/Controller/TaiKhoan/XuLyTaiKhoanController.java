package Controller.TaiKhoan;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.TaiKhoan.TaiKhoan;
import Modal.TaiKhoan.TaiKhoanBO;
import Modal.TaiKhoan.TaiKhoanDAO;
import Support.md5;

/**
 * Servlet implementation class XuLyTaiKhoanController
 */
@WebServlet("/XuLyTaiKhoanController")
public class XuLyTaiKhoanController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TaiKhoanBO tkbo = new TaiKhoanBO();
	private TaiKhoanDAO tkdao = new TaiKhoanDAO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public XuLyTaiKhoanController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set UTF-8 encoding
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		HttpSession session = request.getSession();
		
		// Kiểm tra đăng nhập và quyền Admin
		String account = (String) session.getAttribute("account");
		String quyen = (String) session.getAttribute("quyen");
		
		if(account == null || account.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/DangNhapController");
			return;
		}
		
		if(!"Admin".equals(quyen)) {
			response.sendRedirect(request.getContextPath() + "/TrangChuController");
			return;
		}
		
		// Lấy tham số action
		String action = request.getParameter("action");
		
		try {
			if("create".equals(action)) {
			    // Thêm tài khoản
			    String tenDangNhap = request.getParameter("tenDangNhap");
			    String matKhau = request.getParameter("matKhau");
			    String quyenTK = request.getParameter("quyen");
			    String email = request.getParameter("email"); // Lấy thêm email
			    
			    if(tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
			        session.setAttribute("message", "Tên đăng nhập không được để trống!");
			        session.setAttribute("messageType", "error");
			    } else if(tenDangNhap.trim().length() > 150) {
			        session.setAttribute("message", "Tên đăng nhập không được quá 150 ký tự!");
			        session.setAttribute("messageType", "error");
			    } else if(matKhau == null || matKhau.trim().isEmpty()) {
			        session.setAttribute("message", "Mật khẩu không được để trống!");
			        session.setAttribute("messageType", "error");
			    } else if(matKhau.trim().length() > 255) {
			        session.setAttribute("message", "Mật khẩu không được quá 255 ký tự!");
			        session.setAttribute("messageType", "error");
			    } else if(quyenTK == null || quyenTK.trim().isEmpty()) {
			        session.setAttribute("message", "Quyền không được để trống!");
			        session.setAttribute("messageType", "error");
			    } else if(tkdao.findByTenDangNhap(tenDangNhap) != null) {
			        session.setAttribute("message", "Tên đăng nhập đã tồn tại!");
			        session.setAttribute("messageType", "error");  
			    } else {
			        // Truyền 4 tham số: TenDangNhap, MatKhau, Quyen, Email
			    	String encryptedPass = md5.ecrypt(matKhau.trim());
			        tkbo.createDB(tenDangNhap.trim(), encryptedPass, quyenTK.trim(), 
			                      email != null && !email.trim().isEmpty() ? email.trim() : null);
			        session.setAttribute("message", "Thêm tài khoản thành công!");
			        session.setAttribute("messageType", "success");
			    }
			} else if("update".equals(action)) {
			    // Sửa tài khoản
			    String tenDangNhap = request.getParameter("tenDangNhap");
			    String matKhau = request.getParameter("matKhau");
			    String quyenTK = request.getParameter("quyen");
			    String trangThai = request.getParameter("trangThai");
			    String email = request.getParameter("email");
			    
			    if(tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
			        session.setAttribute("message", "Tên đăng nhập không được để trống!");
			        session.setAttribute("messageType", "error");
			    } else if(matKhau == null || matKhau.trim().isEmpty()) {
			        session.setAttribute("message", "Mật khẩu không được để trống!");
			        session.setAttribute("messageType", "error");
			    } else if(matKhau.trim().length() > 255) {
			        session.setAttribute("message", "Mật khẩu không được quá 255 ký tự!");
			        session.setAttribute("messageType", "error");
			    } else if(quyenTK == null || quyenTK.trim().isEmpty()) {
			        session.setAttribute("message", "Quyền không được để trống!");
			        session.setAttribute("messageType", "error");
			    } else if(trangThai == null || trangThai.trim().isEmpty()) {
			        session.setAttribute("message", "Trạng thái không được để trống!");
			        session.setAttribute("messageType", "error");
			    } else {  	
			    	// Không cho sửa chính mình
					if(tenDangNhap.equals(account)) {
						session.setAttribute("message", "Không thể sửa tài khoản đang đăng nhập!");
						session.setAttribute("messageType", "error");
					}
					else {
						// Lấy thông tin tài khoản hiện tại từ DB
				        TaiKhoan tkHienTai = tkdao.findByTenDangNhap(tenDangNhap);
				        
				        String matKhauCanLuu;
				        // Kiểm tra: nếu mật khẩu nhập vào trùng với mật khẩu đã mã hóa trong DB
				        // thì giữ nguyên, còn không thì mã hóa mật khẩu mới
				        // nếu mật khẩu không đụng vào thì không cần mã hóa, mật khẩu hiện tại chưa đổi
				        if(tkHienTai != null && matKhau.trim().equals(tkHienTai.getMatKhau())) {
				            // Mật khẩu không thay đổi, giữ nguyên
				            matKhauCanLuu = matKhau.trim();
				        } else {
				            // Mật khẩu mới, cần mã hóa
				            matKhauCanLuu = md5.ecrypt(matKhau.trim());
				        }
				        
				        // Cập nhật với mật khẩu đã xử lý
				        tkbo.updateDB(tenDangNhap.trim(), matKhauCanLuu, quyenTK.trim(), trangThai, 
				                      email != null && !email.trim().isEmpty() ? email.trim() : null);
				        session.setAttribute("message", "Cập nhật tài khoản thành công!");
				        session.setAttribute("messageType", "success");
					}
			    }
			} else if("delete".equals(action)) {
				// Xóa tài khoản
				String tenDangNhap = request.getParameter("tenDangNhap");
				
				// Không cho xóa chính mình
				if(tenDangNhap.equals(account)) {
					session.setAttribute("message", "Không thể xóa tài khoản đang đăng nhập!");
					session.setAttribute("messageType", "error");
				} else {
					tkbo.deleteDB(tenDangNhap);
					session.setAttribute("message", "Xóa tài khoản thành công!");
					session.setAttribute("messageType", "success");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", e.getMessage());
			session.setAttribute("messageType", "error");
		}
		
		// Redirect về trang tài khoản
		response.sendRedirect(request.getContextPath() + "/TaiKhoanController");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}