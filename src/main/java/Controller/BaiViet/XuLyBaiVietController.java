package Controller.BaiViet;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.BaiViet.BaiVietBO;

@WebServlet("/XuLyBaiVietController")
@MultipartConfig
public class XuLyBaiVietController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BaiVietBO bvbo = new BaiVietBO();
       
	public XuLyBaiVietController() {
		super();
	}

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
				// Thêm bài viết
				String tieuDe = request.getParameter("tieuDe");
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url"); // URL từ hidden field sau khi upload
				String taiKhoanTao = account;
				String maTheLoaiStr = request.getParameter("maTheLoai");
				
				// Validate
				if(tieuDe == null || tieuDe.trim().isEmpty()) {
					session.setAttribute("message", "Tiêu đề không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(tieuDe.trim().length() > 255) {
					session.setAttribute("message", "Tiêu đề không được quá 255 ký tự!");
					session.setAttribute("messageType", "error");
				} else if(noiDung == null || noiDung.trim().isEmpty()) {
					session.setAttribute("message", "Nội dung không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(maTheLoaiStr == null || maTheLoaiStr.trim().isEmpty()) {
					session.setAttribute("message", "Thể loại không được để trống!");
					session.setAttribute("messageType", "error");
				} else {
					int maTheLoai = Integer.parseInt(maTheLoaiStr);
					
					// URL có thể null hoặc empty
					String finalUrl = (url != null && !url.trim().isEmpty()) ? url.trim() : null;
					
					bvbo.createDB(tieuDe.trim(), noiDung.trim(), finalUrl, taiKhoanTao, maTheLoai);
					session.setAttribute("message", "Thêm bài viết thành công!");
					session.setAttribute("messageType", "success");
				}
				
			} else if("update".equals(action)) {
				// Sửa bài viết
				long maBaiViet = Long.parseLong(request.getParameter("maBaiViet"));
				String tieuDe = request.getParameter("tieuDe");
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				String keepOldFile = request.getParameter("keepOldFile");
				int maTheLoai = Integer.parseInt(request.getParameter("maTheLoai"));
				String danhGiaStr = request.getParameter("danhGia");
				String trangThai = request.getParameter("trangThai");
				
				BigDecimal danhGia = null;
				if(danhGiaStr != null && !danhGiaStr.trim().isEmpty()) {
					danhGia = new BigDecimal(danhGiaStr);
					if(danhGia.compareTo(BigDecimal.ZERO) < 0 || danhGia.compareTo(new BigDecimal("5")) > 0) {
						session.setAttribute("message", "Đánh giá phải từ 0 đến 5!");
						session.setAttribute("messageType", "error");
						response.sendRedirect(request.getContextPath() + "/BaiVietController");
						return;
					}
				}
				
				// Validate
				if(tieuDe == null || tieuDe.trim().isEmpty()) {
					session.setAttribute("message", "Tiêu đề không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(tieuDe.trim().length() > 255) {
					session.setAttribute("message", "Tiêu đề không được quá 255 ký tự!");
					session.setAttribute("messageType", "error");
				} else if(noiDung == null || noiDung.trim().isEmpty()) {
					session.setAttribute("message", "Nội dung không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(trangThai == null || trangThai.trim().isEmpty()) {
					session.setAttribute("message", "Trạng thái không được để trống!");
					session.setAttribute("messageType", "error");
				} else {
					// Xử lý URL
					String finalUrl = null;
					if(url != null && !url.trim().isEmpty()) {
						finalUrl = url.trim();
					} else if("true".equals(keepOldFile)) {
						// Giữ file cũ - lấy URL cũ từ DB
						finalUrl = bvbo.readDB().stream()
							.filter(bv -> bv.getMaBaiViet() == maBaiViet)
							.findFirst()
							.map(bv -> bv.getUrl())
							.orElse(null);
					}
					
					bvbo.updateDB(maBaiViet, tieuDe.trim(), noiDung.trim(), finalUrl, maTheLoai, danhGia, trangThai);
					session.setAttribute("message", "Cập nhật bài viết thành công!");
					session.setAttribute("messageType", "success");
				}
				
			} else if("delete".equals(action)) {
				// Xóa bài viết
				long maBaiViet = Long.parseLong(request.getParameter("maBaiViet"));
				bvbo.deleteDB(maBaiViet);
				session.setAttribute("message", "Xóa bài viết thành công!");
				session.setAttribute("messageType", "success");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", e.getMessage());
			session.setAttribute("messageType", "error");
		}
		
		// Redirect về trang bài viết
		response.sendRedirect(request.getContextPath() + "/BaiVietController");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}