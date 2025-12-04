package Controller.DangXuat;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;

/**
 * Servlet implementation class DangXuatController
 */
@WebServlet("/DangXuatController")
public class DangXuatController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DangXuatController() {
        super();
        // TODO Auto-generated constructor stub
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
		
		session.removeAttribute("user");
		session.removeAttribute("account");
		session.removeAttribute("quyen");
		
		//Reset captcha
		session.removeAttribute(Captcha.NAME);
		
		//Reset số lần đăng nhập, tên đn
		session.removeAttribute("loginAttempts");
        session.removeAttribute("tenDangNhapLogin");
        
        // Reset số lần đăng ký, tên đn
        session.removeAttribute("registerAttempts");
        session.removeAttribute("tenDangNhapReg");
        
        // Reset số lần sai quên mk, tên đn
        session.removeAttribute("forgetpassAttempts");
        session.removeAttribute("tenDangNhapForget");
		
		response.sendRedirect(request.getContextPath() + "/DangNhapController");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
