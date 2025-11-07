package Controller.BaiVietCuaToi;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.BaiViet.BaiViet;
import Modal.BaiViet.BaiVietBO;
import Modal.BaiViet.BaiVietDAO;

@WebServlet("/XuLyBaiVietCuaToiController")
public class XuLyBaiVietCuaToiController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BaiVietBO bvbo = new BaiVietBO();
	private BaiVietDAO bvdao = new BaiVietDAO();
       
	public XuLyBaiVietCuaToiController() {
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
		
		// Lấy tham số action
		String action = request.getParameter("action");
		
		try {
			if("create".equals(action)) {
				// Thêm bài viết
				String tieuDe = request.getParameter("tieuDe");
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
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
					bvbo.createDB(tieuDe.trim(), noiDung.trim(), url, taiKhoanTao, maTheLoai);
					session.setAttribute("message", "Thêm bài viết thành công!");
					session.setAttribute("messageType", "success");
				}
				
			} else if("update".equals(action)) {
				// Sửa bài viết
				long maBaiViet = Long.parseLong(request.getParameter("maBaiViet"));
				
				// Kiểm tra quyền sở hữu bài viết
				BaiViet bvCheck = bvdao.findByMaBaiViet(maBaiViet);
				if(bvCheck == null || !bvCheck.getTaiKhoanTao().equals(account)) {
					session.setAttribute("message", "Bạn không có quyền chỉnh sửa bài viết này!");
					session.setAttribute("messageType", "error");
					response.sendRedirect(request.getContextPath() + "/BaiVietCuaToiController");
					return;
				}
				
				String tieuDe = request.getParameter("tieuDe");
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				int maTheLoai = Integer.parseInt(request.getParameter("maTheLoai"));
				String danhGiaStr = request.getParameter("danhGia");
				String trangThai = bvCheck.getTrangThai(); // Giữ nguyên trạng thái
				
				BigDecimal danhGia = bvCheck.getDanhGia(); // Giữ nguyên đánh giá
				if(danhGiaStr != null && !danhGiaStr.trim().isEmpty()) {
					danhGia = new BigDecimal(danhGiaStr);
					if(danhGia.compareTo(BigDecimal.ZERO) < 0 || danhGia.compareTo(new BigDecimal("5")) > 0) {
						session.setAttribute("message", "Đánh giá phải từ 0 đến 5!");
						session.setAttribute("messageType", "error");
						response.sendRedirect(request.getContextPath() + "/BaiVietCuaToiController");
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
				} else {
					bvbo.updateDB(maBaiViet, tieuDe.trim(), noiDung.trim(), url, maTheLoai, danhGia, trangThai);
					session.setAttribute("message", "Cập nhật bài viết thành công!");
					session.setAttribute("messageType", "success");
				}
				
			} else if("delete".equals(action)) {
				// Xóa bài viết
				long maBaiViet = Long.parseLong(request.getParameter("maBaiViet"));
				
				// Kiểm tra quyền sở hữu bài viết
				BaiViet bvCheck = bvdao.findByMaBaiViet(maBaiViet);
				if(bvCheck == null || !bvCheck.getTaiKhoanTao().equals(account)) {
					session.setAttribute("message", "Bạn không có quyền xóa bài viết này!");
					session.setAttribute("messageType", "error");
					response.sendRedirect(request.getContextPath() + "/BaiVietCuaToiController");
					return;
				}
				
				bvbo.deleteDB(maBaiViet);
				session.setAttribute("message", "Xóa bài viết thành công!");
				session.setAttribute("messageType", "success");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			session.setAttribute("message", e.getMessage());
			session.setAttribute("messageType", "error");
		}
		
		// Redirect về trang bài viết của tôi
		response.sendRedirect(request.getContextPath() + "/BaiVietCuaToiController");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}