package Controller.BaiViet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.BaiViet.BaiViet;
import Modal.BaiViet.BaiVietBO;
import Modal.TheLoai.TheLoai;
import Modal.TheLoai.TheLoaiBO;
import Modal.BinhLuan.BinhLuan;
import Modal.BinhLuan.BinhLuanBO;

@WebServlet("/BaiVietController")
public class BaiVietController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BaiVietBO bvbo = new BaiVietBO();
	private TheLoaiBO tlbo = new TheLoaiBO();
	private BinhLuanBO blbo = new BinhLuanBO();
       
	public BaiVietController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set UTF-8 encoding
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		HttpSession session = request.getSession();
		
		// Kiểm tra đăng nhập
		String account = (String) session.getAttribute("account");
		String quyen = (String) session.getAttribute("quyen");
		
		if(account == null || account.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/DangNhapController");
			return;
		}
		
		// Kiểm tra quyền Admin
		if(!"Admin".equals(quyen)) {
			response.sendRedirect(request.getContextPath() + "/TrangChuController");
			return;
		}
		
		try {
			// Lấy tham số tìm kiếm
			String searchKey = request.getParameter("search");
			
			ArrayList<BaiViet> dsBaiViet;
			
			// Nếu có từ khóa tìm kiếm
			if(searchKey != null && !searchKey.trim().isEmpty()) {
				dsBaiViet = bvbo.findDB_admin(searchKey.trim());
				request.setAttribute("searchKey", searchKey);
			} else {
				// Lấy toàn bộ danh sách
				dsBaiViet = bvbo.readDB();
			}
			
			// Sắp xếp theo MaBaiViet tăng dần
			dsBaiViet.sort((a, b) -> Long.compare(a.getMaBaiViet(), b.getMaBaiViet()));
			
			// Lấy danh sách thể loại cho dropdown
			ArrayList<TheLoai> dsTheLoai = tlbo.readDB();
			request.setAttribute("dsTheLoai", dsTheLoai);
			
			// === PHÂN TRANG ===
			int itemsPerPage = 4; // 4 bài viết mỗi trang
			int currentPage = 1;
			
			// Lấy số trang từ URL
			String pageParam = request.getParameter("page");
			if(pageParam != null && !pageParam.trim().isEmpty()) {
				try {
					currentPage = Integer.parseInt(pageParam);
					if(currentPage < 1) currentPage = 1;
				} catch(NumberFormatException e) {
					currentPage = 1;
				}
			}
			
			// Tính tổng số trang
			int totalItems = dsBaiViet.size();
			int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
			
			// Đảm bảo currentPage không vượt quá totalPages
			if(currentPage > totalPages && totalPages > 0) {
				currentPage = totalPages;
			}
			
			// Lấy danh sách cho trang hiện tại
			int startIndex = (currentPage - 1) * itemsPerPage;
			int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
			
			ArrayList<BaiViet> dsBaiVietPhanTrang = new ArrayList<>();
			if(totalItems > 0) {
				dsBaiVietPhanTrang = new ArrayList<>(dsBaiViet.subList(startIndex, endIndex));
			}
			
			// Truyền dữ liệu phân trang
			request.setAttribute("dsBaiViet", dsBaiVietPhanTrang);
			request.setAttribute("currentPage", currentPage);
			request.setAttribute("totalPages", totalPages);
			request.setAttribute("totalItems", totalItems);
			request.setAttribute("startIndex", startIndex);
			
			// Lấy thông báo từ session (nếu có)
			String message = (String) session.getAttribute("message");
			String messageType = (String) session.getAttribute("messageType");
			
			if(message != null) {
				request.setAttribute("message", message);
				request.setAttribute("messageType", messageType);
				// Xóa thông báo khỏi session
				session.removeAttribute("message");
				session.removeAttribute("messageType");
			}
			
			// Forward đến trang PostPage
			request.getRequestDispatcher("/pages/post_page/PostPage.jsp").forward(request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", "Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại!");
			request.getRequestDispatcher("/pages/post_page/PostPage.jsp").forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}