package Controller.DangNhap;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class DangNhapController
 */
@WebServlet("/DangNhapController")
public class DangNhapController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DangNhapController() {
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
		
		//Xóa DL trang Quên mật khẩu, Đăng ký
		session.removeAttribute("tenDangNhapForget");
		session.removeAttribute("tenDangNhapReg");
		
		// Kiểm tra nếu người dùng nhấn nút đăng nhập
		if (request.getParameter("btnLogin") != null) {
			// Lấy dữ liệu từ form
			String tenDangNhap = request.getParameter("tenDangNhapLogin");
			String matKhau = request.getParameter("matKhau");
			String ghiNhoDangNhap = request.getParameter("ghiNhoDangNhap");			
			// Set vào request attribute để chuyển sang XuLyDangNhapController
			request.setAttribute("tenDangNhapLogin", tenDangNhap);
			request.setAttribute("matKhau", matKhau);
			request.setAttribute("ghiNhoDangNhap", ghiNhoDangNhap);
			
			// Forward sang XuLyDangNhapController để xử lý
			RequestDispatcher rd = request.getRequestDispatcher("/XuLyDangNhapController");
			rd.forward(request, response);
		} else {
			// Hiển thị trang đăng nhập
			request.getRequestDispatcher("/pages/login_page/LoginPage.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}