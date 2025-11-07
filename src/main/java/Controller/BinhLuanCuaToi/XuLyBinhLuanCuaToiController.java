package Controller.BinhLuanCuaToi;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import Modal.BinhLuan.BinhLuan;
import Modal.BinhLuan.BinhLuanBO;
import Modal.BinhLuan.BinhLuanDAO;
import Modal.LuotThichBinhLuan.LuotThichBinhLuan;
import Modal.LuotThichBinhLuan.LuotThichBinhLuanBO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

@WebServlet("/XuLyBinhLuanCuaToiController")
public class XuLyBinhLuanCuaToiController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BinhLuanBO blbo = new BinhLuanBO();
	private BinhLuanDAO bldao = new BinhLuanDAO();
	private LuotThichBinhLuanBO ltbo = new LuotThichBinhLuanBO();
       
	public XuLyBinhLuanCuaToiController() {
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
				// Thêm bình luận
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				String taiKhoanTao = account;
				String maBaiVietStr = request.getParameter("maBaiViet");
				
				// Validate
				if(noiDung == null || noiDung.trim().isEmpty()) {
					session.setAttribute("message", "Nội dung không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(maBaiVietStr == null || maBaiVietStr.trim().isEmpty()) {
					session.setAttribute("message", "Mã bài viết không được để trống!");
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
				
				// Kiểm tra quyền sở hữu bình luận
				BinhLuan blCheck = bldao.findByMaBinhLuan(maBinhLuan);
				if(blCheck == null || !blCheck.getTaiKhoanTao().equals(account)) {
					session.setAttribute("message", "Bạn không có quyền chỉnh sửa bình luận này!");
					session.setAttribute("messageType", "error");
					response.sendRedirect(request.getContextPath() + "/BinhLuanCuaToiController");
					return;
				}
				
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				int soLuotThich = blCheck.getSoLuotThich(); // Giữ nguyên số lượt thích
				String trangThai = blCheck.getTrangThai(); // Giữ nguyên trạng thái
				
				// Validate
				if(noiDung == null || noiDung.trim().isEmpty()) {
					session.setAttribute("message", "Nội dung không được để trống!");
					session.setAttribute("messageType", "error");
				} else {
					blbo.updateDB(maBinhLuan, noiDung.trim(), url, soLuotThich, trangThai);
					session.setAttribute("message", "Cập nhật bình luận thành công!");
					session.setAttribute("messageType", "success");
				}
				
			} else if("delete".equals(action)) {
				// Xóa bình luận
				long maBinhLuan = Long.parseLong(request.getParameter("maBinhLuan"));
				
				// Kiểm tra quyền sở hữu bình luận
				BinhLuan blCheck = bldao.findByMaBinhLuan(maBinhLuan);
				if(blCheck == null || !blCheck.getTaiKhoanTao().equals(account)) {
					session.setAttribute("message", "Bạn không có quyền xóa bình luận này!");
					session.setAttribute("messageType", "error");
					response.sendRedirect(request.getContextPath() + "/BinhLuanCuaToiController");
					return;
				}
				
				blbo.deleteDB(maBinhLuan);
				session.setAttribute("message", "Xóa bình luận thành công!");
				session.setAttribute("messageType", "success");
				
			} else if("like".equals(action) || "unlike".equals(action)) {
				// Xử lý like/unlike
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				JSONObject json = new JSONObject();
				
				try {
					long maBinhLuan = Long.parseLong(request.getParameter("maBinhLuan"));
					
					if("like".equals(action)) {
						ltbo.createDB(maBinhLuan, account);
						json.put("success", true);
						json.put("message", "Đã thích!");
					} else {
						ltbo.deleteDBByMaBinhLuanVaTenDangNhap(maBinhLuan, account);
						json.put("success", true);
						json.put("message", "Đã bỏ thích!");
					}
				} catch(Exception e) {
					e.printStackTrace();
					json.put("success", false);
					json.put("message", e.getMessage());
				}
				
				out.print(json.toString());
				return;
				
			} else if("getLikeList".equals(action)) {
				// Lấy danh sách lượt thích
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				JSONObject json = new JSONObject();
				
				try {
					long maBinhLuan = Long.parseLong(request.getParameter("maBinhLuan"));
					
					ArrayList<LuotThichBinhLuan> dsAll = ltbo.readDB();
					ArrayList<LuotThichBinhLuan> dsFiltered = new ArrayList<>();
					for(LuotThichBinhLuan lt : dsAll) {
						if(lt.getMaBinhLuan() == maBinhLuan) {
							dsFiltered.add(lt);
						}
					}
					
					JSONArray arr = new JSONArray();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
					
					for(LuotThichBinhLuan lt : dsFiltered) {
						JSONObject item = new JSONObject();
						item.put("tenDangNhap", lt.getTenDangNhap());
						item.put("thoiDiemThich", sdf.format(lt.getThoiDiemThich()));
						arr.put(item);
					}
					
					json.put("success", true);
					json.put("list", arr);
					
				} catch(Exception e) {
					e.printStackTrace();
					json.put("success", false);
					json.put("message", e.getMessage());
				}
				
				out.print(json.toString());
				return;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			session.setAttribute("message", e.getMessage());
			session.setAttribute("messageType", "error");
		}
		
		// Redirect về trang bình luận của tôi
		response.sendRedirect(request.getContextPath() + "/BinhLuanCuaToiController");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}