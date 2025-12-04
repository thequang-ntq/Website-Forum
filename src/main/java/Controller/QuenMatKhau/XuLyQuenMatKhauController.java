package Controller.QuenMatKhau;

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
import Modal.TaiKhoan.TaiKhoanDAO;
import Support.md5;
import nl.captcha.Captcha;

/**
 * Servlet implementation class XuLyQuenMatKhauController
 */
@WebServlet("/XuLyQuenMatKhauController")
public class XuLyQuenMatKhauController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TaiKhoanBO tkbo = new TaiKhoanBO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public XuLyQuenMatKhauController() {
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
		
		// Lấy dữ liệu từ request attribute (đã được forward từ QuenMatKhauController)
		String tenDangNhap = (String) request.getAttribute("tenDangNhapForget");
		String matKhauMoi = (String) request.getAttribute("matKhauMoi");
		String nhapLaiMatKhauMoi = (String) request.getAttribute("nhapLaiMatKhauMoi");
		String captchaAnswer = request.getParameter("captchaLayLaiMK");
		
		// Lấy số lần lấy lại mk sai
	    Integer forgetpassAttempts = (Integer) session.getAttribute("forgetpassAttempts");
	    if (forgetpassAttempts == null) {
	        forgetpassAttempts = 0;
	    }
	    
	    // Nếu đã sai >= 3 lần, kiểm tra captcha
	    if (forgetpassAttempts >= 3) {
	        Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
	        
	        // Kiểm tra xem captcha có tồn tại không
	        if (captcha == null) {
	        	forgetpassAttempts++;
			    session.setAttribute("forgetpassAttempts", forgetpassAttempts);
	            session.setAttribute("errorQuenMatKhau", "Phiên làm việc hết hạn. Vui lòng thử lại.");
	            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
	            return;
	        }
	        
	        // Kiểm tra captcha có được nhập không
	        if (captchaAnswer == null || captchaAnswer.trim().isEmpty()) {
	        	forgetpassAttempts++;
			    session.setAttribute("forgetpassAttempts", forgetpassAttempts);
	            session.setAttribute("errorQuenMatKhau", "Vui lòng nhập mã Captcha");
	            session.setAttribute("tenDangNhapForget", tenDangNhap);
	            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
	            return;
	        }
	        
	        // Kiểm tra captcha có đúng không
	        if (!captcha.isCorrect(captchaAnswer)) {
	        	forgetpassAttempts++;
			    session.setAttribute("forgetpassAttempts", forgetpassAttempts);
	            session.setAttribute("errorQuenMatKhau", "Mã Captcha không đúng");
	            session.setAttribute("tenDangNhapForget", tenDangNhap);
	            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
	            return;
	        }
	    }
		
		// Khởi tạo các biến lỗi
		String errorTenDangNhap = null;
		String errorMatKhauMoi = null;
		String errorNhapLaiMatKhauMoi = null;
		String errorQuenMatKhau = null;
		boolean hasError = false;
		
		// Validate dữ liệu đầu vào
		// Kiểm tra tên đăng nhập
		if(tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
			errorTenDangNhap = "Tên đăng nhập không được để trống!";
			hasError = true;
		}
		
		// Kiểm tra mật khẩu mới
		if(matKhauMoi == null || matKhauMoi.trim().isEmpty()) {
			errorMatKhauMoi = "Mật khẩu mới không được để trống!";
			hasError = true;
		}
		
		// Kiểm tra nhập lại mật khẩu mới
		if(nhapLaiMatKhauMoi == null || nhapLaiMatKhauMoi.trim().isEmpty()) {
			errorNhapLaiMatKhauMoi = "Xin hãy nhập lại mật khẩu mới!";
			hasError = true;
		} 
		else if(matKhauMoi != null && !matKhauMoi.trim().isEmpty() && !nhapLaiMatKhauMoi.trim().equals(matKhauMoi.trim())) {
			errorNhapLaiMatKhauMoi = "Nhập lại mật khẩu mới chưa khớp với mật khẩu mới!";
			hasError = true;
		}
		
		// Nếu có lỗi validation, quay lại trang quên mật khẩu
		if(hasError) {
			forgetpassAttempts++;
		    session.setAttribute("forgetpassAttempts", forgetpassAttempts);
			session.setAttribute("errorTenDangNhap", errorTenDangNhap);
			session.setAttribute("errorMatKhauMoi", errorMatKhauMoi);
			session.setAttribute("errorNhapLaiMatKhauMoi", errorNhapLaiMatKhauMoi);
			session.setAttribute("tenDangNhapForget", tenDangNhap);
			
			response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
			return;
		}
		
		// Xử lý quên mật khẩu
		try {
			
			// MÃ HÓA mật khẩu người dùng nhập
		    String encryptedPass1 = md5.ecrypt(matKhauMoi.trim());
		    String encryptedPass2 = md5.ecrypt(nhapLaiMatKhauMoi.trim());
		    
		    // Gọi hàm check tài khoản tồn tại không
		    TaiKhoanDAO tkdao = new TaiKhoanDAO();
 			TaiKhoan tk = tkdao.findByTenDangNhap(tenDangNhap);
		    
 			//Tài khoản có tồn tại
 			if(tk != null) {
 				// Gọi hàm forgetPassDB từ TaiKhoanBO
 				tkbo.forgetPassDB(tenDangNhap.trim(), encryptedPass1, encryptedPass2);
 				
 				// Reset số lần sai khi lấy lại mật khẩu thành công
 	            session.removeAttribute("forgetpassAttempts");
 	            session.removeAttribute(Captcha.NAME);
 	            session.removeAttribute("tenDangNhapForget");
 				
 				// Đổi mật khẩu thành công
 				// Lưu thông báo thành công vào session
 				session.setAttribute("successQuenMatKhau", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
 				
 				// Chuyển hướng đến trang đăng nhập (dùng sendRedirect vì chuyển sang controller khác)
 				response.sendRedirect(request.getContextPath() + "/DangNhapController");
 			}
 			else {
 				// Thất bại do không tìm thấy tài khoản - tăng số lần thử
			    forgetpassAttempts++;
			    session.setAttribute("forgetpassAttempts", forgetpassAttempts);
			    session.setAttribute("tenDangNhapForget", tenDangNhap);
			    
			    if (forgetpassAttempts >= 3) {
			        session.setAttribute("errorQuenMatKhau", "Tài khoản không tồn tại. Vui lòng nhập Captcha để tiếp tục.");
			    } else {
			        session.setAttribute("errorQuenMatKhau", "Tài khoản không tồn tại. (Lần thử: " + forgetpassAttempts + "/3)");
			    }
			    
			    response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
 			}
			
		} 
		catch (Exception e) {
			// Xử lý lỗi từ business logic
			errorQuenMatKhau = e.getMessage();
			session.setAttribute("errorQuenMatKhau", errorQuenMatKhau);
			session.setAttribute("tenDangNhapForget", tenDangNhap);
			
			response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}