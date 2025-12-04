package Controller.BaiVietCuaToi;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.BaiViet.BaiViet;
import Modal.BaiViet.BaiVietBO;
import Modal.BaiViet.BaiVietDAO;

@WebServlet("/XuLyBaiVietCuaToiController")
@MultipartConfig
public class XuLyBaiVietCuaToiController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BaiVietBO bvbo = new BaiVietBO();
	private BaiVietDAO bvdao = new BaiVietDAO();
       
	public XuLyBaiVietCuaToiController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		HttpSession session = request.getSession();
		
		String account = (String) session.getAttribute("account");
		if(account == null || account.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/DangNhapController");
			return;
		}
		
		String action = request.getParameter("action");
		
		try {
			if("create".equals(action)) {
				String tieuDe = request.getParameter("tieuDe");
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				String taiKhoanTao = account;
				String maTheLoaiStr = request.getParameter("maTheLoai");
				
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
					String finalUrl = (url != null && !url.trim().isEmpty()) ? url.trim() : null;
					bvbo.createDB(tieuDe.trim(), noiDung.trim(), finalUrl, taiKhoanTao, maTheLoai);
					session.setAttribute("message", "Thêm bài viết thành công!");
					session.setAttribute("messageType", "success");
				}
				
			} else if("update".equals(action)) {
				long maBaiViet = Long.parseLong(request.getParameter("maBaiViet"));
				
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
				String keepOldFile = request.getParameter("keepOldFile");
				int maTheLoai = Integer.parseInt(request.getParameter("maTheLoai"));
				String danhGiaStr = request.getParameter("danhGia");
				String trangThai = bvCheck.getTrangThai();
				
				BigDecimal danhGia = bvCheck.getDanhGia();
				if(danhGiaStr != null && !danhGiaStr.trim().isEmpty()) {
					danhGia = new BigDecimal(danhGiaStr);
					if(danhGia.compareTo(BigDecimal.ZERO) < 0 || danhGia.compareTo(new BigDecimal("5")) > 0) {
						session.setAttribute("message", "Đánh giá phải từ 0 đến 5!");
						session.setAttribute("messageType", "error");
						response.sendRedirect(request.getContextPath() + "/BaiVietCuaToiController");
						return;
					}
				}
				
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
					String finalUrl = null;
					
					if(url != null && !url.trim().isEmpty()) {
						finalUrl = url.trim();
						
						if(bvCheck.getUrl() != null && !bvCheck.getUrl().isEmpty()) {
							deleteOldFile(request, bvCheck.getUrl());
						}
					} else if("true".equals(keepOldFile)) {
						finalUrl = bvCheck.getUrl();
					}
					
					bvbo.updateDB(maBaiViet, tieuDe.trim(), noiDung.trim(), finalUrl, maTheLoai, danhGia, trangThai);
					session.setAttribute("message", "Cập nhật bài viết thành công!");
					session.setAttribute("messageType", "success");
				}
				
			} else if("delete".equals(action)) {
				long maBaiViet = Long.parseLong(request.getParameter("maBaiViet"));
				
				//Kiểm tra quyền sở hữu bài viết
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
		
		response.sendRedirect(request.getContextPath() + "/BaiVietCuaToiController");
	}
	
	private void deleteOldFile(HttpServletRequest request, String filePath) {
		if(filePath == null || filePath.trim().isEmpty()) {
			return;
		}
		
		try {
			String serverPath = request.getServletContext().getRealPath("") + filePath;
			File serverFile = new File(serverPath);
			if(serverFile.exists()) {
				boolean deleted = serverFile.delete();
				if(deleted) {
					System.out.println("Đã xóa file trên server: " + serverPath);
				}
			}
			
			if(serverPath.contains(".metadata") && System.getProperty("os.name").toLowerCase().contains("win")) {
				String localPath = "D:/Nam4/JavaNangCao" + request.getContextPath() + "/src/main/webapp/" + filePath;
				File localFile = new File(localPath);
				if(localFile.exists()) {
					boolean deleted = localFile.delete();
					if(deleted) {
						System.out.println("Đã xóa file trên local: " + localPath);
					}
				}
			}
		} catch(Exception e) {
			System.err.println("Lỗi khi xóa file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}