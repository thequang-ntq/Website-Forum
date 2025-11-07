package Controller.TheLoai;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.TheLoai.TheLoaiBO;

/**
 * Servlet implementation class XuLyTheLoaiController
 */
@WebServlet("/XuLyTheLoaiController")
public class XuLyTheLoaiController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TheLoaiBO tlbo = new TheLoaiBO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public XuLyTheLoaiController() {
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
		
		// Kiểm tra đăng nhập và quyền Admin
		String account = (String) session.getAttribute("account");
		String quyen = (String) session.getAttribute("quyen");
		
		if(account == null || account.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/DangNhapController");
			return;
		}
		
		if(!"Admin".equals(quyen)) {
			response.sendRedirect(request.getContextPath() + "/TrangChuController");
			return;
		}
		
		// Lấy tham số action
		String action = request.getParameter("action");
		
		try {
			if("create".equals(action)) {
				// Thêm thể loại
				String tenTheLoai = request.getParameter("tenTheLoai");
				
				if(tenTheLoai == null || tenTheLoai.trim().isEmpty()) {
					session.setAttribute("message", "Tên thể loại không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(tenTheLoai.trim().length() > 200) {
					session.setAttribute("message", "Tên thể loại không được quá 200 ký tự!");
					session.setAttribute("messageType", "error");
				} else {
					tlbo.createDB(tenTheLoai.trim());
					session.setAttribute("message", "Thêm thể loại thành công!");
					session.setAttribute("messageType", "success");
				}
				
			} else if("update".equals(action)) {
				// Sửa thể loại
				int maTheLoai = Integer.parseInt(request.getParameter("maTheLoai"));
				String tenTheLoai = request.getParameter("tenTheLoai");
				
				if(tenTheLoai == null || tenTheLoai.trim().isEmpty()) {
					session.setAttribute("message", "Tên thể loại không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(tenTheLoai.trim().length() > 200) {
					session.setAttribute("message", "Tên thể loại không được quá 200 ký tự!");
					session.setAttribute("messageType", "error");
				} else {
					tlbo.updateDB(maTheLoai, tenTheLoai.trim());
					session.setAttribute("message", "Cập nhật thể loại thành công!");
					session.setAttribute("messageType", "success");
				}
				
			} else if("delete".equals(action)) {
				// Xóa thể loại
				int maTheLoai = Integer.parseInt(request.getParameter("maTheLoai"));
				tlbo.deleteDB(maTheLoai);
				session.setAttribute("message", "Xóa thể loại thành công!");
				session.setAttribute("messageType", "success");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", e.getMessage());
			session.setAttribute("messageType", "error");
		}
		
		// Redirect về trang thể loại
		response.sendRedirect(request.getContextPath() + "/TheLoaiController");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}