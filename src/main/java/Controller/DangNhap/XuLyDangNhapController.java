package Controller.DangNhap;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.TaiKhoan.TaiKhoan;
import Modal.TaiKhoan.TaiKhoanBO;
import Support.md5;
import nl.captcha.Captcha;

/**
 * Servlet implementation class XuLyDangNhapController
 */
@WebServlet("/XuLyDangNhapController")
public class XuLyDangNhapController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TaiKhoanBO tkbo = new TaiKhoanBO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public XuLyDangNhapController() {
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
		
		// Lấy dữ liệu từ request attribute (đã được forward từ DangNhapController)
		String tenDangNhap = (String) request.getAttribute("tenDangNhapLogin");
		String matKhau = (String) request.getAttribute("matKhau");
		String ghiNhoDangNhap = (String) request.getAttribute("ghiNhoDangNhap");
		String captchaAnswer = request.getParameter("captchaDangNhap");
		
		// Lấy số lần đăng nhập sai
	    Integer loginAttempts = (Integer) session.getAttribute("loginAttempts");
	    if (loginAttempts == null) {
	        loginAttempts = 0;
	    }
	    
	    // Nếu đã đăng nhập sai >= 3 lần, kiểm tra captcha
	    if (loginAttempts >= 3) {
	        Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
	        
	        // Kiểm tra xem captcha có tồn tại không
	        if (captcha == null) {
	        	loginAttempts++;
			    session.setAttribute("loginAttempts", loginAttempts);
	            session.setAttribute("errorDangNhap", "Phiên làm việc hết hạn. Vui lòng thử lại.");
	            response.sendRedirect(request.getContextPath() + "/DangNhapController");
	            return;
	        }
	        
	        // Kiểm tra captcha có được nhập không
	        if (captchaAnswer == null || captchaAnswer.trim().isEmpty()) {
	        	loginAttempts++;
			    session.setAttribute("loginAttempts", loginAttempts);
	            session.setAttribute("errorDangNhap", "Vui lòng nhập mã Captcha");
	            session.setAttribute("tenDangNhapLogin", tenDangNhap);
	            response.sendRedirect(request.getContextPath() + "/DangNhapController");
	            return;
	        }
	        
	        // Kiểm tra captcha có đúng không
	        if (!captcha.isCorrect(captchaAnswer)) {
	        	loginAttempts++;
			    session.setAttribute("loginAttempts", loginAttempts);
	            session.setAttribute("errorDangNhap", "Mã Captcha không đúng");
	            session.setAttribute("tenDangNhapLogin", tenDangNhap);
	            response.sendRedirect(request.getContextPath() + "/DangNhapController");
	            return;
	        }
	    }
		
		// Khởi tạo các biến lỗi
		String errorTenDangNhap = null;
		String errorMatKhau = null;
		String errorDangNhap = null;
		boolean hasError = false;
		
		// Validate dữ liệu đầu vào
		// Kiểm tra tên đăng nhập
		if(tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
			errorTenDangNhap = "Tên đăng nhập không được để trống!";
			hasError = true;
		}
		
		// Kiểm tra mật khẩu
		if(matKhau == null || matKhau.trim().isEmpty()) {
			errorMatKhau = "Mật khẩu không được để trống!";
			hasError = true;
		}
		
		// Nếu có lỗi validation, quay lại trang đăng nhập
		if(hasError) {
		    // Lưu lỗi vào session thay vì request
			loginAttempts++;
		    session.setAttribute("loginAttempts", loginAttempts);
		    session.setAttribute("errorTenDangNhap", errorTenDangNhap);
		    session.setAttribute("errorMatKhau", errorMatKhau);
		    session.setAttribute("tenDangNhapLogin", tenDangNhap);
		    
		    // Dùng redirect thay vì forward
		    response.sendRedirect(request.getContextPath() + "/DangNhapController");
		    return;
		}
		

		
		// Xử lý đăng nhập
		try {
			
		    // MÃ HÓA mật khẩu người dùng nhập vào để so sánh
		    String encryptedPass = md5.ecrypt(matKhau.trim());
			
			// Gọi hàm checkLoginDB từ TaiKhoanBO
			TaiKhoan tk = tkbo.checkLoginDB(tenDangNhap.trim(), encryptedPass);
			
			// Kiểm tra kết quả đăng nhập
			if(tk != null) {
				// Đăng nhập thành công
				// Tạo session và lưu thông tin người dùng
				session.setAttribute("user", tk);
				session.setAttribute("account", tk.getTenDangNhap());
				session.setAttribute("quyen", tk.getQuyen());
				
	            // Reset số lần đăng nhập sai khi đăng nhập thành công
	            session.removeAttribute("loginAttempts");
	            session.removeAttribute(Captcha.NAME);
	            session.removeAttribute("tenDangNhapLogin");
	            
				// Thiết lập thời gian timeout cho session, không ghi nhớ ĐN thì 1 giờ, có thì 1 tuần
				if(ghiNhoDangNhap == null) session.setMaxInactiveInterval(60 * 60);
				else session.setMaxInactiveInterval(60 * 60 * 24 * 7);
				
				// Chuyển hướng đến trang chủ (dùng sendRedirect vì chuyển sang controller khác)
				response.sendRedirect(request.getContextPath() + "/TrangChuController");
			} else {
				
				// Đăng nhập thất bại - tăng số lần thử
			    loginAttempts++;
			    session.setAttribute("loginAttempts", loginAttempts);
			    session.setAttribute("tenDangNhapLogin", tenDangNhap);
			    
			    if (loginAttempts >= 3) {
			        session.setAttribute("errorDangNhap", "Sai tên đăng nhập hoặc mật khẩu. Vui lòng nhập Captcha để tiếp tục.");
			    } else {
			        session.setAttribute("errorDangNhap", "Sai tên đăng nhập hoặc mật khẩu. (Lần thử: " + loginAttempts + "/3)");
			    }
			    
			    // Redirect thay vì forward
			    response.sendRedirect(request.getContextPath() + "/DangNhapController");
			}
			
		} catch (Exception e) {
			// Xử lý lỗi hệ thống
			e.printStackTrace();
			errorDangNhap = "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!";
			session.setAttribute("errorDangNhap", errorDangNhap);
			session.setAttribute("tenDangNhapLogin", tenDangNhap);
			
			response.sendRedirect(request.getContextPath() + "/DangNhapController");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}