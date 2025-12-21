package Controller.DangKy;

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
import Support.EmailService;
import Support.md5;
import nl.captcha.Captcha;

/**
 * Servlet implementation class XuLyDangKyController
 */
@WebServlet("/XuLyDangKyController")
public class XuLyDangKyController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TaiKhoanBO tkbo = new TaiKhoanBO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public XuLyDangKyController() {
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
		
		// Lấy dữ liệu từ request attribute (đã được forward từ DangKyController)
		String tenDangNhap = (String) request.getAttribute("tenDangNhapReg");
		String matKhau = (String) request.getAttribute("matKhau");
		String nhapLaiMatKhau = (String) request.getAttribute("nhapLaiMatKhau");
		String captchaAnswer = request.getParameter("captchaDangKy");
		String email = (String) request.getAttribute("email");
		
		// Lấy số lần đăng ký sai
	    Integer registerAttempts = (Integer) session.getAttribute("registerAttempts");
	    if (registerAttempts == null) {
	        registerAttempts = 0;
	    }
	    
	    // Nếu đã đăng ký sai >= 3 lần, kiểm tra captcha
	    if (registerAttempts >= 3) {
	        Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
	        
	        // Kiểm tra xem captcha có tồn tại không
	        if (captcha == null) {
	        	registerAttempts++;
			    session.setAttribute("registerAttempts", registerAttempts);
	            session.setAttribute("errorDangKy", "Phiên làm việc hết hạn. Vui lòng thử lại.");
	            response.sendRedirect(request.getContextPath() + "/DangKyController");
	            return;
	        }
	        
	        // Kiểm tra captcha có được nhập không
	        if (captchaAnswer == null || captchaAnswer.trim().isEmpty()) {
	        	registerAttempts++;
			    session.setAttribute("registerAttempts", registerAttempts);
	            session.setAttribute("errorDangKy", "Vui lòng nhập mã Captcha");
	            session.setAttribute("tenDangNhapReg", tenDangNhap);
	            response.sendRedirect(request.getContextPath() + "/DangKyController");
	            return;
	        }
	        
	        // Kiểm tra captcha có đúng không
	        if (!captcha.isCorrect(captchaAnswer)) {
	        	registerAttempts++;
			    session.setAttribute("registerAttempts", registerAttempts);
	            session.setAttribute("errorDangKy", "Mã Captcha không đúng");
	            session.setAttribute("tenDangNhapReg", tenDangNhap);
	            response.sendRedirect(request.getContextPath() + "/DangKyController");
	            return;
	        }
	    }
		
		// Khởi tạo các biến lỗi
		String errorTenDangNhap = null;
		String errorMatKhau = null;
		String errorNhapLaiMatKhau = null;
		String errorDangKy = null;
		boolean hasError = false;
		String errorEmail = null;
		
		// Validate dữ liệu đầu vào
		// Kiểm tra tên đăng nhập
		if(tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
			errorTenDangNhap = "Tên đăng nhập không được để trống!";
			hasError = true;
		}
		
		// Kiểm tra email (nếu có nhập)
		if(email != null && !email.trim().isEmpty()) {
		    if(!EmailService.isValidEmail(email.trim())) {
		        errorEmail = "Email không hợp lệ!";
		        hasError = true;
		    }
		}
		
		// Kiểm tra mật khẩu
		if(matKhau == null || matKhau.trim().isEmpty()) {
			errorMatKhau = "Mật khẩu không được để trống!";
			hasError = true;
		}
		
		// Kiểm tra nhập lại mật khẩu
		if(nhapLaiMatKhau == null || nhapLaiMatKhau.trim().isEmpty()) {
			errorNhapLaiMatKhau = "Vui lòng nhập lại mật khẩu!";
			hasError = true;
		} else if(matKhau != null && !matKhau.trim().isEmpty() && !nhapLaiMatKhau.equals(matKhau)) {
			errorNhapLaiMatKhau = "Nhập lại mật khẩu không khớp với mật khẩu!";
			hasError = true;
		}
		
		// Nếu có lỗi validation, quay lại trang đăng ký
		if(hasError) {
			registerAttempts++;
		    session.setAttribute("registerAttempts", registerAttempts);
			session.setAttribute("errorTenDangNhap", errorTenDangNhap);
			session.setAttribute("errorMatKhau", errorMatKhau);
			session.setAttribute("errorNhapLaiMatKhau", errorNhapLaiMatKhau);
			session.setAttribute("tenDangNhapReg", tenDangNhap);
			session.setAttribute("errorEmail", errorEmail);
			
			response.sendRedirect(request.getContextPath() + "/DangKyController");
			return;
		}
		
		// Xử lý đăng ký
		try {
			
			// MÃ HÓA mật khẩu người dùng nhập vào để so sánh
		    String encryptedPass = md5.ecrypt(matKhau.trim());
		    
			// Gọi hàm checkRegisterDB từ TaiKhoanBO
			TaiKhoan tk = tkbo.checkRegisterDB(tenDangNhap.trim());
			
			// Kiểm tra kết quả đăng ký
			if(tk != null) {
				// Tài khoản đã tồn tại
				errorDangKy = "Tài khoản đã tồn tại!";
				registerAttempts++;
			    session.setAttribute("registerAttempts", registerAttempts);
				session.setAttribute("errorDangKy", errorDangKy);
				session.setAttribute("tenDangNhapReg", tenDangNhap);
				
				response.sendRedirect(request.getContextPath() + "/DangKyController");
			} else {
				// Đăng ký thành công - thêm tài khoản vào database
				tkbo.createDB(tenDangNhap.trim(), encryptedPass, "User", email != null ? email.trim() : null);
				
				// Reset số lần đăng ký sai khi đăng nhập thành công
	            session.removeAttribute("registerAttempts");
	            session.removeAttribute(Captcha.NAME);
	            session.removeAttribute("tenDangNhapReg");
				
				// Lưu thông báo thành công vào session
				session.setAttribute("successDangKy", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
				
				// Chuyển hướng đến trang đăng nhập (dùng sendRedirect vì chuyển sang controller khác)
				response.sendRedirect(request.getContextPath() + "/DangNhapController");
			}
			
		} catch (Exception e) {
			// Xử lý lỗi hệ thống
			e.printStackTrace();
			errorDangKy = "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!";
			session.setAttribute("errorDangKy", errorDangKy);
			session.setAttribute("tenDangNhapReg", tenDangNhap);
			
			response.sendRedirect(request.getContextPath() + "/DangKyController");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}