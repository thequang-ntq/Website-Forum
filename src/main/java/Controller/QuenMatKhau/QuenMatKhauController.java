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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        HttpSession session = request.getSession();
        
        // Xóa dữ liệu đăng nhập, đăng ký
        session.removeAttribute("tenDangNhapLogin");
        session.removeAttribute("tenDangNhapReg");
        
        String step = request.getParameter("step");
        
        if("verify".equals(step)) {
            // Bước 2: Xác thực PIN
            if(session.getAttribute("resetPIN") == null) {
                response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
                return;
            }
            request.getRequestDispatcher("/pages/forget_pass_page/ForgetPassStep2.jsp").forward(request, response);
        } else if("reset".equals(step)) {
            // Bước 3: Đổi mật khẩu
            if(session.getAttribute("verifySuccess") == null) {
                response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
                return;
            }
            request.getRequestDispatcher("/pages/forget_pass_page/ForgetPassStep3.jsp").forward(request, response);
        } else {
            // Bước 1: Nhập tên đăng nhập
            request.getRequestDispatcher("/pages/forget_pass_page/ForgetPassStep1.jsp").forward(request, response);
        }
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}