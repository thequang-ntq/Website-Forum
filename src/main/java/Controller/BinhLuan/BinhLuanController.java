package Controller.BinhLuan;

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
import Modal.BinhLuan.BinhLuan;
import Modal.BinhLuan.BinhLuanBO;
import Modal.TheLoai.TheLoaiBO;

/**
 * Servlet implementation class BinhLuanController
 */
@WebServlet("/BinhLuanController")
public class BinhLuanController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BaiVietBO bvbo = new BaiVietBO();
	private TheLoaiBO tlbo = new TheLoaiBO();
	private BinhLuanBO blbo = new BinhLuanBO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BinhLuanController() {
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
			// Tham số tìm kiếm
			String searchKey = request.getParameter("search");
			
			ArrayList<BinhLuan> dsBinhLuan;
			
			// Nếu có từ khóa tìm kiếm
			if(searchKey != null && !searchKey.trim().isEmpty()) {
				dsBinhLuan = blbo.findDB_admin(searchKey);
				request.setAttribute("searchKey", searchKey);
			} else {
				// Lấy toàn bộ danh sách
				dsBinhLuan = blbo.readDB();
			}
			
			// Xử lý embedding search
			String embeddingSearch = request.getParameter("embeddingSearch");
			if (embeddingSearch != null && !embeddingSearch.trim().isEmpty()) {
			    String[] ids = embeddingSearch.split(",");
			    ArrayList<BinhLuan> filteredList = new ArrayList<>();
			    for (String idStr : ids) {
			        try {
			            long maBinhLuan = Long.parseLong(idStr.trim());
			            for (BinhLuan bl : dsBinhLuan) {
			                if (bl.getMaBinhLuan() == maBinhLuan) {
			                    filteredList.add(bl);
			                    break;
			                }
			            }
			        } catch (NumberFormatException e) {
			            // Ignore invalid IDs
			        }
			    }
			    dsBinhLuan = filteredList;
			    request.setAttribute("embeddingSearch", "true");
			}
			
			//Sắp xếp theo mã bình luận tăng dần
			dsBinhLuan.sort((a, b) -> Long.compare(a.getMaBinhLuan(), b.getMaBinhLuan()));
			
			// Lấy danh sách bài viết cho dropdown
			ArrayList<BaiViet> dsBaiViet = bvbo.readDB();
			request.setAttribute("dsBaiViet", dsBaiViet);
			
			// === PHÂN TRANG ===
			int itemsPerPage = 8; // 8 bình luận mỗi trang
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
			int totalItems = dsBinhLuan.size();
			int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
			
			// Đảm bảo currentPage không vượt quá totalPages
			if(currentPage > totalPages && totalPages > 0) {
				currentPage = totalPages;
			}
			
			// Lấy danh sách cho trang hiện tại
			int startIndex = (currentPage - 1) * itemsPerPage;
			int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
			
			ArrayList<BinhLuan> dsBinhLuanPhanTrang = new ArrayList<>();
			if(totalItems > 0) {
				dsBinhLuanPhanTrang = new ArrayList<>(dsBinhLuan.subList(startIndex, endIndex));
			}
			
			// Truyền dữ liệu phân trang
			request.setAttribute("dsBinhLuan", dsBinhLuanPhanTrang);
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
			
			// Forward đến CommentPage
			request.getRequestDispatcher("/pages/comment_page/CommentPage.jsp").forward(request, response);
		}
		catch(Exception e) {
			e.printStackTrace();
			request.setAttribute("error", "Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại!");
			request.getRequestDispatcher("/pages/comment_page/CommentPage.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
