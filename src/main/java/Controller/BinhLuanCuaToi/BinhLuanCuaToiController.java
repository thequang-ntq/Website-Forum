package Controller.BinhLuanCuaToi;

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
import Modal.LuotThichBinhLuan.LuotThichBinhLuan;
import Modal.LuotThichBinhLuan.LuotThichBinhLuanBO;

@WebServlet("/BinhLuanCuaToiController")
public class BinhLuanCuaToiController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BinhLuanBO blbo = new BinhLuanBO();
	private BaiVietBO bvbo = new BaiVietBO();
	private LuotThichBinhLuanBO ltbo = new LuotThichBinhLuanBO();
       
	public BinhLuanCuaToiController() {
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
			// Lấy danh sách bài viết cho dropdown filter
			ArrayList<BaiViet> dsBaiVietAll = bvbo.readDB();
			request.setAttribute("dsBaiViet", dsBaiVietAll);
			
			// Lấy danh sách bình luận của tài khoản hiện tại, chỉ lấy các bình luận đang active
			ArrayList<BinhLuan> dsTong = blbo.filterDB_taiKhoanTao(account);
			ArrayList<BinhLuan> dsBinhLuan = new ArrayList<BinhLuan>();
			for(BinhLuan bl: dsTong) {
				if("Active".equals(bl.getTrangThai())) {
					dsBinhLuan.add(bl);
				}
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
			
			// Lấy tham số từ URL
			String maBaiVietParam = request.getParameter("baiviet");
			String sortParam = request.getParameter("sort");
			String searchParam = request.getParameter("search");
			
			// Lọc theo bài viết (nếu có)
			if(maBaiVietParam != null && !maBaiVietParam.trim().isEmpty()) {
				long maBaiViet = Long.parseLong(maBaiVietParam);
				ArrayList<BinhLuan> dsBinhLuanBaiViet = new ArrayList<>();
				for(BinhLuan bl : dsBinhLuan) {
					if(bl.getMaBaiViet() == maBaiViet) {
						dsBinhLuanBaiViet.add(bl);
					}
				}
				dsBinhLuan = dsBinhLuanBaiViet;
				request.setAttribute("selectedBaiViet", maBaiViet);
			}
			
			// Tìm kiếm (nếu có)
			if(searchParam != null && !searchParam.trim().isEmpty()) {
				dsBinhLuan = blbo.findDB_user(dsBinhLuan, searchParam.trim());
				request.setAttribute("searchKey", searchParam);
			}
			
			// Sắp xếp (nếu có)
			if(sortParam != null && !sortParam.trim().isEmpty()) {
				switch(sortParam) {
					case "luotThichCao":
						dsBinhLuan = blbo.sortDB_soLuotThich_cao(dsBinhLuan);
						break;
					case "luotThichThap":
						dsBinhLuan = blbo.sortDB_soLuotThich_thap(dsBinhLuan);
						break;
					case "taoGanNhat":
						dsBinhLuan = blbo.sortDB_thoiDiemTao_ganNhat(dsBinhLuan);
						break;
					case "taoXaNhat":
						dsBinhLuan = blbo.sortDB_thoiDiemTao_xaNhat(dsBinhLuan);
						break;
					case "capNhatGanNhat":
						dsBinhLuan = blbo.sortDB_thoiDiemCapNhat_ganNhat(dsBinhLuan);
						break;
					case "capNhatXaNhat":
						dsBinhLuan = blbo.sortDB_thoiDiemCapNhat_xaNhat(dsBinhLuan);
						break;
				}
				request.setAttribute("sortBy", sortParam);
			}
			
			// === PHÂN TRANG ===
			int itemsPerPage = 8;
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
			
			int totalItems = dsBinhLuan.size();
			int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
			
			if(currentPage > totalPages && totalPages > 0) {
				currentPage = totalPages;
			}
			
			int startIndex = (currentPage - 1) * itemsPerPage;
			int endIndex = Math.min(startIndex + itemsPerPage, totalItems);
			
			ArrayList<BinhLuan> dsBinhLuanPhanTrang = new ArrayList<>();
			if(totalItems > 0) {
				dsBinhLuanPhanTrang = new ArrayList<>(dsBinhLuan.subList(startIndex, endIndex));
			}
			
			request.setAttribute("dsBinhLuan", dsBinhLuanPhanTrang);
			request.setAttribute("currentPage", currentPage);
			request.setAttribute("totalPages", totalPages);
			request.setAttribute("totalItems", totalItems);
			request.setAttribute("startIndex", startIndex);
			
			// Lấy danh sách lượt thích
			ArrayList<LuotThichBinhLuan> dsLuotThich = ltbo.readDB();
			request.setAttribute("dsLuotThich", dsLuotThich);
			
			// Lấy thông báo từ session (nếu có)
			String message = (String) session.getAttribute("message");
			String messageType = (String) session.getAttribute("messageType");
			
			if(message != null) {
				request.setAttribute("message", message);
				request.setAttribute("messageType", messageType);
				session.removeAttribute("message");
				session.removeAttribute("messageType");
			}
			
			// Forward đến trang MyCommentPage
			request.getRequestDispatcher("/pages/mycomment_page/MyCommentPage.jsp").forward(request, response);
			
		} catch(Exception e) {
			e.printStackTrace();
			request.setAttribute("error", "Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại!");
			request.getRequestDispatcher("/pages/mycomment_page/MyCommentPage.jsp").forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}