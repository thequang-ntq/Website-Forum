package Controller.QuenMatKhau;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.TaiKhoan.TaiKhoanBO;
import Support.md5;

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
		} else if(matKhauMoi != null && !matKhauMoi.trim().isEmpty() && !nhapLaiMatKhauMoi.trim().equals(matKhauMoi.trim())) {
			errorNhapLaiMatKhauMoi = "Nhập lại mật khẩu mới chưa khớp với mật khẩu mới!";
			hasError = true;
		}
		
		// Nếu có lỗi validation, quay lại trang quên mật khẩu
		if(hasError) {
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
		    
			// Gọi hàm forgetPassDB từ TaiKhoanBO
			tkbo.forgetPassDB(tenDangNhap.trim(), encryptedPass1, encryptedPass2);
			
			// Đổi mật khẩu thành công
			// Lưu thông báo thành công vào session
			session.setAttribute("successQuenMatKhau", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
			
			// Chuyển hướng đến trang đăng nhập (dùng sendRedirect vì chuyển sang controller khác)
			response.sendRedirect(request.getContextPath() + "/DangNhapController");
			
		} catch (Exception e) {
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