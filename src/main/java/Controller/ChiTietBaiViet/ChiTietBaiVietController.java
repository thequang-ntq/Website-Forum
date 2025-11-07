package Controller.ChiTietBaiViet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import Modal.BaiViet.BaiViet;
import Modal.BaiViet.BaiVietDAO;
import Modal.BinhLuan.BinhLuan;
import Modal.BinhLuan.BinhLuanBO;
import Modal.TheLoai.TheLoai;
import Modal.TheLoai.TheLoaiDAO;
import Modal.LuotThichBinhLuan.LuotThichBinhLuan;
import Modal.LuotThichBinhLuan.LuotThichBinhLuanBO;
import Modal.DanhGiaBaiViet.DanhGiaBaiViet;
import Modal.DanhGiaBaiViet.DanhGiaBaiVietBO;

@WebServlet("/ChiTietBaiVietController")
public class ChiTietBaiVietController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BaiVietDAO bvdao = new BaiVietDAO();
	private BinhLuanBO blbo = new BinhLuanBO();
	private TheLoaiDAO tldao = new TheLoaiDAO();
	private LuotThichBinhLuanBO ltbo = new LuotThichBinhLuanBO();
	private DanhGiaBaiVietBO dgbo = new DanhGiaBaiVietBO();
       
	public ChiTietBaiVietController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set UTF-8 encoding
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession();
		String account = (String) session.getAttribute("account");
		
		// Lấy action từ URL
		String action = request.getParameter("action");
		
		// Xử lý các action khác nhau
		if ("comment".equals(action)) {
			handleComment(request, response, account);
		} else if ("like".equals(action) || "unlike".equals(action)) {
			handleLike(request, response, account, action);
		} else if ("getLikeList".equals(action)) {
			handleGetLikeList(request, response);
		} else if ("rating".equals(action)) {
			handleRating(request, response, account);
		} else {
			// Mặc định: Hiển thị chi tiết bài viết
			showPostDetail(request, response, account);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	// Hiển thị chi tiết bài viết
	private void showPostDetail(HttpServletRequest request, HttpServletResponse response, String account) 
			throws ServletException, IOException {
		// Set UTF-8 encoding
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		// Kiểm tra đăng nhập
		if(account == null || account.trim().isEmpty()) {
			response.getWriter().write("<div class='alert alert-danger'>Vui lòng đăng nhập để xem chi tiết!</div>");
			return;
		}
		
		try {
			// Lấy mã bài viết từ URL
			String idParam = request.getParameter("id");
			if(idParam == null || idParam.trim().isEmpty()) {
				response.getWriter().write("<div class='alert alert-danger'>Không tìm thấy bài viết!</div>");
				return;
			}
			
			long maBaiViet = Long.parseLong(idParam);
			
			// Lấy thông tin bài viết
			BaiViet bv = bvdao.findByMaBaiViet(maBaiViet);
			if(bv == null || !"Active".equals(bv.getTrangThai())) {
				response.getWriter().write("<div class='alert alert-danger'>Bài viết không tồn tại hoặc đã bị xóa!</div>");
				return;
			}
			
			// Lấy thông tin thể loại
			TheLoai tl = tldao.findByMaTheLoai(bv.getMaTheLoai());
			String tenTheLoai = (tl != null) ? tl.getTenTheLoai() : "Không xác định";
			
			// Lấy danh sách bình luận Active
			ArrayList<BinhLuan> dsBinhLuan = blbo.filterDB_maBaiViet(maBaiViet);
			
			// Sắp xếp bình luận theo thời gian tạo mới nhất
			dsBinhLuan = blbo.sortDB_thoiDiemTao_ganNhat(dsBinhLuan);
			
			// Lấy danh sách lượt thích
			ArrayList<LuotThichBinhLuan> dsLuotThich = ltbo.readDB();
			
			// Lấy danh sách đánh giá
			ArrayList<DanhGiaBaiViet> dsDanhGia = dgbo.readDB();
			
			request.setAttribute("baiViet", bv);
			request.setAttribute("tenTheLoai", tenTheLoai);
			request.setAttribute("dsBinhLuan", dsBinhLuan);
			request.setAttribute("dsLuotThich", dsLuotThich);
			request.setAttribute("dsDanhGia", dsDanhGia);
			
			// Forward đến trang chi tiết
			request.getRequestDispatcher("/pages/post_detail_page/PostDetailPage.jsp").forward(request, response);
			
		} catch(Exception e) {
			e.printStackTrace();
			response.getWriter().write("<div class='alert alert-danger'>Đã xảy ra lỗi khi tải chi tiết bài viết!</div>");
		}
	}
	
	// Xử lý thêm bình luận
	private void handleComment(HttpServletRequest request, HttpServletResponse response, String account) 
	        throws ServletException, IOException {
	    response.setContentType("application/json; charset=UTF-8");
	    PrintWriter out = response.getWriter();
	    JSONObject json = new JSONObject();
	    
	    // Kiểm tra đăng nhập
	    if(account == null || account.trim().isEmpty()) {
	        json.put("success", false);
	        json.put("message", "Vui lòng đăng nhập!");
	        out.print(json.toString());
	        return;
	    }
	    
	    try {
	        String noiDung = request.getParameter("noiDung");
	        String url = request.getParameter("url");
	        String maBaiVietStr = request.getParameter("maBaiViet");
	        
	        // Kiểm tra null
	        if(maBaiVietStr == null || maBaiVietStr.trim().isEmpty()) {
	            json.put("success", false);
	            json.put("message", "Không tìm thấy mã bài viết!");
	            out.print(json.toString());
	            return;
	        }
	        
	        long maBaiViet = Long.parseLong(maBaiVietStr);
	        
	        if(noiDung == null || noiDung.trim().isEmpty()) {
	            json.put("success", false);
	            json.put("message", "Nội dung không được để trống!");
	        } else {
	            blbo.createDB(noiDung.trim(), url, account, maBaiViet);
	            json.put("success", true);
	            json.put("message", "Thêm bình luận thành công!");
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	        json.put("success", false);
	        json.put("message", e.getMessage());
	    }
	    
	    out.print(json.toString());
	}
	
	// Xử lý like/unlike
	private void handleLike(HttpServletRequest request, HttpServletResponse response, String account, String action) 
			throws ServletException, IOException {
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		
		// Kiểm tra đăng nhập
		if(account == null || account.trim().isEmpty()) {
			json.put("success", false);
			json.put("message", "Vui lòng đăng nhập!");
			out.print(json.toString());
			return;
		}
		
		try {
			long maBinhLuan = Long.parseLong(request.getParameter("maBinhLuan"));
			
			if("like".equals(action)) {
				// Thêm lượt thích
				ltbo.createDB(maBinhLuan, account);
				json.put("success", true);
				json.put("message", "Đã thích!");
			} else if("unlike".equals(action)) {
				// Xóa lượt thích
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
	}
	
	// Lấy danh sách lượt thích
	private void handleGetLikeList(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		
		try {
			long maBinhLuan = Long.parseLong(request.getParameter("maBinhLuan"));
			
			// Lấy danh sách tất cả lượt thích
			ArrayList<LuotThichBinhLuan> dsAll = ltbo.readDB();
			
			// Lọc theo mã bình luận
			ArrayList<LuotThichBinhLuan> dsFiltered = new ArrayList<>();
			for(LuotThichBinhLuan lt : dsAll) {
				if(lt.getMaBinhLuan() == maBinhLuan) {
					dsFiltered.add(lt);
				}
			}
			
			// Tạo JSON array
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
	}
	
	// Xử lý đánh giá
	private void handleRating(HttpServletRequest request, HttpServletResponse response, String account) 
			throws ServletException, IOException {
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		
		// Kiểm tra đăng nhập
		if(account == null || account.trim().isEmpty()) {
			json.put("success", false);
			json.put("message", "Vui lòng đăng nhập!");
			out.print(json.toString());
			return;
		}
		
		String ratingAction = request.getParameter("ratingAction");
		
		try {
			long maBaiViet = Long.parseLong(request.getParameter("maBaiViet"));
			
			if("create".equals(ratingAction)) {
				// Thêm đánh giá
				BigDecimal diem = new BigDecimal(request.getParameter("diem"));
				
				if(diem.compareTo(BigDecimal.ZERO) < 0 || diem.compareTo(new BigDecimal("5")) > 0) {
					json.put("success", false);
					json.put("message", "Điểm đánh giá phải từ 0 đến 5!");
				} else {
					dgbo.createDB(maBaiViet, account, diem);
					json.put("success", true);
					json.put("message", "Đánh giá thành công!");
				}
			} else if("delete".equals(ratingAction)) {
				// Xóa đánh giá
				dgbo.deleteDBByMaBaiVietVaTenDangNhap(maBaiViet, account);
				json.put("success", true);
				json.put("message", "Đã xóa đánh giá!");
			}
		} catch(Exception e) {
			e.printStackTrace();
			json.put("success", false);
			json.put("message", e.getMessage());
		}
		
		out.print(json.toString());
	}
}