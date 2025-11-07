package Controller.BaiVietCuaToi;

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

@WebServlet("/BaiVietCuaToiController")
public class BaiVietCuaToiController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BaiVietBO bvbo = new BaiVietBO();
	private TheLoaiBO tlbo = new TheLoaiBO();
	private BinhLuanBO blbo = new BinhLuanBO();
       
	public BaiVietCuaToiController() {
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
		if(account == null || account.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/DangNhapController");
			return;
		}
		
		try {
			// Lấy danh sách thể loại cho dropdown và filter
			ArrayList<TheLoai> dsTheLoai = tlbo.readDB();
			request.setAttribute("dsTheLoai", dsTheLoai);
			
			// Lấy danh sách bài viết của tài khoản hiện tại
			ArrayList<BaiViet> dsBaiViet = bvbo.filterDB_taiKhoanTao(account);
			
			// Lấy tham số từ URL
			String maTheLoaiParam = request.getParameter("theloai");
			String sortParam = request.getParameter("sort");
			String searchParam = request.getParameter("search");
			
			// Lọc theo thể loại (nếu có)
			if(maTheLoaiParam != null && !maTheLoaiParam.trim().isEmpty()) {
				int maTheLoai = Integer.parseInt(maTheLoaiParam);
				ArrayList<BaiViet> dsBaiVietTheLoai = new ArrayList<>();
				for(BaiViet bv : dsBaiViet) {
					if(bv.getMaTheLoai() == maTheLoai) {
						dsBaiVietTheLoai.add(bv);
					}
				}
				dsBaiViet = dsBaiVietTheLoai;
				request.setAttribute("selectedTheLoai", maTheLoai);
			}
			
			// Tìm kiếm (nếu có)
			if(searchParam != null && !searchParam.trim().isEmpty()) {
				dsBaiViet = bvbo.findDB_user(dsBaiViet, searchParam.trim());
				request.setAttribute("searchKey", searchParam);
			}
			
			// Sắp xếp (nếu có)
			if(sortParam != null && !sortParam.trim().isEmpty()) {
				switch(sortParam) {
					case "danhGiaCao":
						dsBaiViet = bvbo.sortDB_danhGia_cao(dsBaiViet);
						break;
					case "danhGiaThap":
						dsBaiViet = bvbo.sortDB_danhGia_thap(dsBaiViet);
						break;
					case "taoGanNhat":
						dsBaiViet = bvbo.sortDB_thoiDiemTao_ganNhat(dsBaiViet);
						break;
					case "taoXaNhat":
						dsBaiViet = bvbo.sortDB_thoiDiemTao_xaNhat(dsBaiViet);
						break;
					case "capNhatGanNhat":
						dsBaiViet = bvbo.sortDB_thoiDiemCapNhat_ganNhat(dsBaiViet);
						break;
					case "capNhatXaNhat":
						dsBaiViet = bvbo.sortDB_thoiDiemCapNhat_xaNhat(dsBaiViet);
						break;
				}
				request.setAttribute("sortBy", sortParam);
			}
			
			// === PHÂN TRANG ===
			int itemsPerPage = 4;
			int currentPage = 1;
			
			String pageParam = request.getParameter("page");
			if(pageParam != null && !pageParam.trim().isEmpty()) {
				try {
					currentPage = Integer.parseInt(pageParam);
					if(currentPage < 1) currentPage = 1;
				} catch(NumberFormatException e) {
					currentPage = 1;
				}
			}
			
			int totalItems = dsBaiViet.size();
			int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
			
			if(currentPage > totalPages && totalPages > 0) {
				currentPage = totalPages;
			}
			
			int startIndex = (currentPage - 1) * itemsPerPage;
			int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
			
			ArrayList<BaiViet> dsBaiVietPhanTrang = new ArrayList<>();
			if(totalItems > 0) {
				dsBaiVietPhanTrang = new ArrayList<>(dsBaiViet.subList(startIndex, endIndex));
			}
			
			request.setAttribute("dsBaiViet", dsBaiVietPhanTrang);
			request.setAttribute("currentPage", currentPage);
			request.setAttribute("totalPages", totalPages);
			request.setAttribute("totalItems", totalItems);
			request.setAttribute("startIndex", startIndex);
			
			// Lấy danh sách bình luận cho mỗi bài viết
			for(BaiViet bv : dsBaiVietPhanTrang) {
				ArrayList<BinhLuan> binhLuanCuaBaiViet = blbo.filterDB_maBaiViet(bv.getMaBaiViet());
				request.setAttribute("dsBinhLuan_" + bv.getMaBaiViet(), binhLuanCuaBaiViet);
			}
			
			// Lấy thông báo từ session (nếu có)
			String message = (String) session.getAttribute("message");
			String messageType = (String) session.getAttribute("messageType");
			
			if(message != null) {
				request.setAttribute("message", message);
				request.setAttribute("messageType", messageType);
				session.removeAttribute("message");
				session.removeAttribute("messageType");
			}
			
			// Forward đến trang MyPostPage
			request.getRequestDispatcher("/pages/mypost_page/MyPostPage.jsp").forward(request, response);
			
		} catch(Exception e) {
			e.printStackTrace();
			request.setAttribute("error", "Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại!");
			request.getRequestDispatcher("/pages/mypost_page/MyPostPage.jsp").forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}