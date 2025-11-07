package Controller.QuenMatKhau;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class QuenMatKhauController
 */
@WebServlet("/QuenMatKhauController")
public class QuenMatKhauController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QuenMatKhauController() {
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
		
		//Xóa các dữ liệu từ trang đăng nhập, đăng ký
		session.removeAttribute("tenDangNhapLogin");
		session.removeAttribute("tenDangNhapReg");
		
		// Kiểm tra nếu người dùng nhấn nút quên mật khẩu
		if (request.getParameter("btnForgetPass") != null) {
			// Lấy dữ liệu từ form
			String tenDangNhap = request.getParameter("tenDangNhapForget");
			String matKhauMoi = request.getParameter("matKhauMoi");
			String nhapLaiMatKhauMoi = request.getParameter("nhapLaiMatKhauMoi");
			
			// Set vào request attribute để chuyển sang XuLyQuenMatKhauController
			request.setAttribute("tenDangNhapForget", tenDangNhap);
			request.setAttribute("matKhauMoi", matKhauMoi);
			request.setAttribute("nhapLaiMatKhauMoi", nhapLaiMatKhauMoi);
			
			// Forward sang XuLyQuenMatKhauController để xử lý
			RequestDispatcher rd = request.getRequestDispatcher("/XuLyQuenMatKhauController");
			rd.forward(request, response);
		} else {
			// Hiển thị trang quên mật khẩu
			request.getRequestDispatcher("/pages/forget_pass_page/ForgetPassPage.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}