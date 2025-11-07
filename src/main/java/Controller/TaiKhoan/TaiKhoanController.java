package Controller.TaiKhoan;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.TaiKhoan.TaiKhoan;
import Modal.TaiKhoan.TaiKhoanBO;

/**
 * Servlet implementation class TaiKhoanController
 */
@WebServlet("/TaiKhoanController")
public class TaiKhoanController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TaiKhoanBO tkbo = new TaiKhoanBO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TaiKhoanController() {
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
			
			ArrayList<TaiKhoan> dsTaiKhoan;
			
			// Nếu có từ khóa tìm kiếm
			if(searchKey != null && !searchKey.trim().isEmpty()) {
				dsTaiKhoan = tkbo.findDB(searchKey.trim());
				request.setAttribute("searchKey", searchKey);
			} else {
				// Lấy toàn bộ danh sách
				dsTaiKhoan = tkbo.readDB();
			}
			
			// Sắp xếp theo TenDangNhap
			dsTaiKhoan.sort((a, b) -> a.getTenDangNhap().compareTo(b.getTenDangNhap()));
			
			// === PHÂN TRANG ===
			int itemsPerPage = 8; // 8 tài khoản mỗi trang
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
			int totalItems = dsTaiKhoan.size();
			int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
			
			// Đảm bảo currentPage không vượt quá totalPages
			if(currentPage > totalPages && totalPages > 0) {
				currentPage = totalPages;
			}
			
			// Lấy danh sách cho trang hiện tại
			int startIndex = (currentPage - 1) * itemsPerPage;
			int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
			
			ArrayList<TaiKhoan> dsTaiKhoanPhanTrang = new ArrayList<>();
			if(totalItems > 0) {
				dsTaiKhoanPhanTrang = new ArrayList<>(dsTaiKhoan.subList(startIndex, endIndex));
			}
			
			// Truyền dữ liệu phân trang
			request.setAttribute("dsTaiKhoan", dsTaiKhoanPhanTrang);
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
			
			// Forward đến trang AccountPage
			request.getRequestDispatcher("/pages/account_page/AccountPage.jsp").forward(request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", "Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại!");
			request.getRequestDispatcher("/pages/account_page/AccountPage.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}