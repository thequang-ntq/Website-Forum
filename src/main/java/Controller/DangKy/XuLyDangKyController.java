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
		
		// Khởi tạo các biến lỗi
		String errorTenDangNhap = null;
		String errorMatKhau = null;
		String errorNhapLaiMatKhau = null;
		String errorDangKy = null;
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
			session.setAttribute("errorTenDangNhap", errorTenDangNhap);
			session.setAttribute("errorMatKhau", errorMatKhau);
			session.setAttribute("errorNhapLaiMatKhau", errorNhapLaiMatKhau);
			session.setAttribute("tenDangNhapReg", tenDangNhap);
			
			response.sendRedirect(request.getContextPath() + "/DangKyController");
			return;
		}
		
		// Xử lý đăng ký
		try {
			// Gọi hàm checkRegisterDB từ TaiKhoanBO
			TaiKhoan tk = tkbo.checkRegisterDB(tenDangNhap.trim());
			
			// Kiểm tra kết quả đăng ký
			if(tk != null) {
				// Tài khoản đã tồn tại
				errorDangKy = "Tên đăng nhập đã tồn tại!";
				session.setAttribute("errorDangKy", errorDangKy);
				session.setAttribute("tenDangNhapReg", tenDangNhap);
				
				response.sendRedirect(request.getContextPath() + "/DangKyController");
			} else {
				// Đăng ký thành công - thêm tài khoản vào database
				tkbo.createDB(tenDangNhap.trim(), matKhau.trim(), "User");
				
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