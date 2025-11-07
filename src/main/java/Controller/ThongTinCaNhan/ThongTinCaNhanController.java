package Controller.ThongTinCaNhan;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.TaiKhoan.TaiKhoan;
import Modal.TaiKhoan.TaiKhoanBO;

@WebServlet("/ThongTinCaNhanController")
public class ThongTinCaNhanController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TaiKhoanBO tkbo = new TaiKhoanBO();

    public ThongTinCaNhanController() {
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
            // Handle POST-like logic for password change
            if ("POST".equalsIgnoreCase(request.getMethod())) {
                // Lấy dữ liệu từ form
                String matKhauCu = request.getParameter("matKhauCu");
                String matKhauMoi = request.getParameter("matKhauMoi");
                String xacNhanMatKhauMoi = request.getParameter("xacNhanMatKhauMoi");

                // Validate
                if (matKhauCu == null || matKhauCu.trim().isEmpty()) {
                    throw new Exception("Mật khẩu cũ không được để trống!");
                }
                if (matKhauCu.length() > 255) {
                    throw new Exception("Mật khẩu cũ không được quá 255 ký tự!");
                }
                if (matKhauMoi == null || matKhauMoi.trim().isEmpty()) {
                    throw new Exception("Mật khẩu mới không được để trống!");
                }
                if (matKhauMoi.length() > 255) {
                    throw new Exception("Mật khẩu mới không được quá 255 ký tự!");
                }
                if (matKhauMoi.equals(matKhauCu)) {
                    throw new Exception("Mật khẩu mới không được trùng mật khẩu cũ!");
                }
                if (xacNhanMatKhauMoi == null || xacNhanMatKhauMoi.trim().isEmpty()) {
                    throw new Exception("Xác nhận mật khẩu mới không được để trống!");
                }
                if (xacNhanMatKhauMoi.length() > 255) {
                    throw new Exception("Xác nhận mật khẩu mới không được quá 255 ký tự!");
                }
                if (!xacNhanMatKhauMoi.equals(matKhauMoi)) {
                    throw new Exception("Xác nhận mật khẩu mới không khớp!");
                }

                // Đổi mật khẩu
                tkbo.changePassDB(account, matKhauCu, matKhauMoi);

                // Thông báo thành công
                request.setAttribute("message", "Đổi mật khẩu thành công!");
                request.setAttribute("messageType", "success");
            }

            // Lấy thông tin tài khoản
            TaiKhoan tk = tkbo.checkRegisterDB(account);
            request.setAttribute("taiKhoan", tk);
            session.setAttribute("user", tk);

            // Forward đến trang InfoPage
            request.getRequestDispatcher("/pages/info_page/InfoPage.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            // Lấy lại thông tin tài khoản
            try {
                TaiKhoan tk = tkbo.checkRegisterDB(account);
                request.setAttribute("taiKhoan", tk);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Thông báo lỗi
            request.setAttribute("message", e.getMessage());
            request.setAttribute("messageType", e instanceof ServletException ? "error" : "error");

            request.getRequestDispatcher("/pages/info_page/InfoPage.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}