package Controller.BinhLuan;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.BinhLuan.BinhLuanBO;

/**
 * Servlet implementation class XuLyBinhLuanController
 */
@WebServlet("/XuLyBinhLuanController")
@MultipartConfig
public class XuLyBinhLuanController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private BinhLuanBO blbo = new BinhLuanBO();  
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public XuLyBinhLuanController() {
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
				// Thêm 
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				String taiKhoanTao = account; // Lấy từ session
				String maBaiVietStr = request.getParameter("maBaiViet");
				
				// Validate
				if(noiDung == null || noiDung.trim().isEmpty()) {
					session.setAttribute("message", "Nội dung không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(maBaiVietStr == null || maBaiVietStr.trim().isEmpty()) {
					session.setAttribute("message", "Bài viết chứa bình luận không được để trống!");
					session.setAttribute("messageType", "error");
				} else {
					long maBaiViet = Long.parseLong(maBaiVietStr);
					blbo.createDB(noiDung.trim(), url, taiKhoanTao, maBaiViet);
					session.setAttribute("message", "Thêm bình luận thành công!");
					session.setAttribute("messageType", "success");
				}
				
			} else if("update".equals(action)) {
				// Sửa bình luận
				long maBinhLuan = Long.parseLong(request.getParameter("maBinhLuan"));
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				String soLuotThichStr = request.getParameter("soLuotThich");
				String trangThai = request.getParameter("trangThai");
				
				int soLuotThich = -1;
				if(soLuotThichStr != null && !soLuotThichStr.trim().isEmpty()) {
					soLuotThich = Integer.parseInt(soLuotThichStr);
					if(soLuotThich < 0) {
						session.setAttribute("message", "Số lượt thích phải lớn hơn hoặc bằng 0");
						session.setAttribute("messageType", "error");
						response.sendRedirect(request.getContextPath() + "/BinhLuanController");
						return;
					}
				}
				
				// Validate
				if(noiDung == null || noiDung.trim().isEmpty()) {
					session.setAttribute("message", "Nội dung không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(trangThai == null || trangThai.trim().isEmpty()) {
					session.setAttribute("message", "Trạng thái không được để trống!");
					session.setAttribute("messageType", "error");
				} else {
					blbo.updateDB(maBinhLuan, noiDung.trim(), url, soLuotThich, trangThai);
					session.setAttribute("message", "Cập nhật bình luận thành công!");
					session.setAttribute("messageType", "success");
				}
				
			} else if("delete".equals(action)) {
				// Xóa bình luận
				long maBinhLuan = Long.parseLong(request.getParameter("maBinhLuan"));
				blbo.deleteDB(maBinhLuan);
				session.setAttribute("message", "Xóa bình luận thành công!");
				session.setAttribute("messageType", "success");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", e.getMessage());
			session.setAttribute("messageType", "error");
		}
		
		// Redirect về trang bài viết
		response.sendRedirect(request.getContextPath() + "/BinhLuanController");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
