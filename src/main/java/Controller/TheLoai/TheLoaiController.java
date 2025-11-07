package Controller.TheLoai;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.TheLoai.TheLoai;
import Modal.TheLoai.TheLoaiBO;

/**
 * Servlet implementation class TheLoaiController
 */
@WebServlet("/TheLoaiController")
public class TheLoaiController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TheLoaiBO tlbo = new TheLoaiBO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TheLoaiController() {
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
			
			ArrayList<TheLoai> dsTheLoai;
			
			// Nếu có từ khóa tìm kiếm
			if(searchKey != null && !searchKey.trim().isEmpty()) {
				dsTheLoai = tlbo.findDB(searchKey.trim());
				request.setAttribute("searchKey", searchKey);
			} else {
				// Lấy toàn bộ danh sách
				dsTheLoai = tlbo.readDB();
			}
			
			// Sắp xếp theo MaTheLoai tăng dần
			dsTheLoai.sort((a, b) -> Integer.compare(a.getMaTheLoai(), b.getMaTheLoai()));
			
			// === PHÂN TRANG ===
			int itemsPerPage = 10; // Số mục trên mỗi trang
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
			int totalItems = dsTheLoai.size();
			int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
			
			// Đảm bảo currentPage không vượt quá totalPages
			if(currentPage > totalPages && totalPages > 0) {
				currentPage = totalPages;
			}
			
			// Lấy danh sách cho trang hiện tại
			int startIndex = (currentPage - 1) * itemsPerPage;
			int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
			
			ArrayList<TheLoai> dsTheLoaiPhanTrang = new ArrayList<>();
			if(totalItems > 0) {
				dsTheLoaiPhanTrang = new ArrayList<>(dsTheLoai.subList(startIndex, endIndex));
			}
			
			// Truyền dữ liệu phân trang
			request.setAttribute("dsTheLoai", dsTheLoaiPhanTrang);
			request.setAttribute("currentPage", currentPage);
			request.setAttribute("totalPages", totalPages);
			request.setAttribute("totalItems", totalItems);
			request.setAttribute("startIndex", startIndex); // Để tính số thứ tự
			
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
			
			// Forward đến trang CategoryPage
			request.getRequestDispatcher("/pages/category_page/CategoryPage.jsp").forward(request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", "Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại!");
			request.getRequestDispatcher("/pages/category_page/CategoryPage.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}