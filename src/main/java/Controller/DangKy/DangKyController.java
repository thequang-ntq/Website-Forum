package Controller.DangKy;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class DangKyController
 */
@WebServlet("/DangKyController")
public class DangKyController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DangKyController() {
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
		
		// Xóa dữ liệu đăng nhập, quên mk
		session.removeAttribute("tenDangNhapLogin");
		session.removeAttribute("tenDangNhapForget");
		
		// Kiểm tra nếu người dùng nhấn nút đăng ký
		if (request.getParameter("btnRegister") != null) {
			// Lấy dữ liệu từ form
			String tenDangNhap = request.getParameter("tenDangNhapReg");
			String matKhau = request.getParameter("matKhau");
			String nhapLaiMatKhau = request.getParameter("nhapLaiMatKhau");
			
			// Set vào request attribute để chuyển sang XuLyDangKyController
			request.setAttribute("tenDangNhapReg", tenDangNhap);
			request.setAttribute("matKhau", matKhau);
			request.setAttribute("nhapLaiMatKhau", nhapLaiMatKhau);
			
			// Forward sang XuLyDangKyController để xử lý
			RequestDispatcher rd = request.getRequestDispatcher("/XuLyDangKyController");
			rd.forward(request, response);
		} else {
			// Hiển thị trang đăng ký
			request.getRequestDispatcher("/pages/register_page/RegisterPage.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}