package Controller.TrangChu;

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
import Modal.TheLoai.TheLoai;
import Modal.TheLoai.TheLoaiBO;
import Modal.TaiKhoan.TaiKhoan;
import Modal.TaiKhoan.TaiKhoanBO;

@WebServlet("/TrangChuController")
public class TrangChuController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TheLoaiBO tlbo = new TheLoaiBO();
    private BaiVietBO bvbo = new BaiVietBO();
    private BinhLuanBO blbo = new BinhLuanBO();
    private TaiKhoanBO tkbo = new TaiKhoanBO();

    public TrangChuController() {
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
        if (account == null || account.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/DangNhapController");
            return;
        }

        try {
            // Lấy danh sách thể loại
            ArrayList<TheLoai> dsTheLoai = tlbo.readDB2();
            request.setAttribute("dsTheLoai", dsTheLoai);

            // Lấy danh sách tài khoản
            ArrayList<TaiKhoan> dsTaiKhoan = tkbo.readDB();
            request.setAttribute("dsTaiKhoan", dsTaiKhoan);

            // Lấy danh sách bài viết (chỉ Active)
            ArrayList<BaiViet> dsBaiViet = bvbo.readDB();
            ArrayList<BaiViet> dsBaiVietActive = new ArrayList<>();
            for (BaiViet bv : dsBaiViet) {
                if ("Active".equals(bv.getTrangThai())) {
                    dsBaiVietActive.add(bv);
                }
            }
            dsBaiViet = dsBaiVietActive;

            // Lấy tham số từ URL
            String theLoaiParam = request.getParameter("theloai");
            String sortParam = request.getParameter("sort");
            String searchParam = request.getParameter("search");
            String filterTKParam = request.getParameter("filterTK");

            // Lọc theo thể loại (nếu có)
            if (theLoaiParam != null && !theLoaiParam.trim().isEmpty()) {
            	dsBaiViet = bvbo.filterDB_maTheLoai_2(dsBaiViet, Integer.parseInt(theLoaiParam));
                request.setAttribute("selectedTheLoai", Integer.parseInt(theLoaiParam));
            }

            // Lọc theo tài khoản tạo (nếu có)
            if (filterTKParam != null && !filterTKParam.trim().isEmpty()) {
                dsBaiViet = bvbo.filterDB_taiKhoanTao_2(dsBaiViet, filterTKParam);
                request.setAttribute("filterTaiKhoan", filterTKParam);
            }

            // Tìm kiếm (nếu có)
            if (searchParam != null && !searchParam.trim().isEmpty()) {
                dsBaiViet = bvbo.findDB_user(dsBaiViet, searchParam.trim()); // Cập nhật findDB_user để nhận danh sách
                request.setAttribute("searchKey", searchParam);
            }

            // Sắp xếp (nếu có)
            if (sortParam != null && !sortParam.trim().isEmpty()) {
                switch (sortParam) {
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
            int itemsPerPage = 4; // 4 bài viết mỗi trang
            int currentPage = 1;

            // Lấy số trang từ URL
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                    if (currentPage < 1) currentPage = 1;
                } catch (NumberFormatException e) {
                    currentPage = 1;
                }
            }

            // Tính tổng số trang
            int totalItems = dsBaiViet.size();
            int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

            // Đảm bảo currentPage không vượt quá totalPages
            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
            }

            // Lấy danh sách cho trang hiện tại
            int startIndex = (currentPage - 1) * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, totalItems);

            ArrayList<BaiViet> dsBaiVietPhanTrang = new ArrayList<>();
            if (totalItems > 0) {
                dsBaiVietPhanTrang = new ArrayList<>(dsBaiViet.subList(startIndex, endIndex));
            }

            // Truyền dữ liệu phân trang
            request.setAttribute("dsBaiViet", dsBaiVietPhanTrang);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("startIndex", startIndex);

            // Lấy danh sách bình luận cho mỗi bài viết và đếm số lượng
            ArrayList<BinhLuan> dsBinhLuan = blbo.readDB();
            for (BaiViet bv : dsBaiVietPhanTrang) {
                ArrayList<BinhLuan> binhLuanCuaBaiViet = blbo.filterDB_maBaiViet(bv.getMaBaiViet(), bv.getTrangThai());
                request.setAttribute("dsBinhLuan_" + bv.getMaBaiViet(), binhLuanCuaBaiViet);
            }

            // Forward đến trang HomePage
            request.getRequestDispatcher("/pages/home_page/HomePage.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý lỗi
            request.setAttribute("error", "Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại!");
            request.getRequestDispatcher("/pages/home_page/HomePage.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}