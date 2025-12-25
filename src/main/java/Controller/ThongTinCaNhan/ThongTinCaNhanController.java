package Controller.ThongTinCaNhan;
// Bên Server
// Validate lần cuối trước khi cập nhật CSDL
// Kiểm tra logic nghiệp vụ chính
// Mã hóa dữ liệu, ghi vào CSDL
// Mục đích là xử lý
// Khi bấm Submit mới bắt đầu xử lý
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.TaiKhoan.TaiKhoan;
import Modal.TaiKhoan.TaiKhoanBO;
import Support.md5;

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
                String action = request.getParameter("action");
                
                // Đổi mật khẩu, mặc định action null là đổi mật khẩu
                if ("changePassword".equals(action)) {
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

                    // Mã hóa
                    String encryptedPass1 = md5.ecrypt(matKhauCu.trim());
                    String encryptedPass2 = md5.ecrypt(matKhauMoi.trim());
                    
                    // Đổi mật khẩu
                    tkbo.changePassDB(account, encryptedPass1, encryptedPass2);

                    // Thông báo thành công
                    request.setAttribute("message", "Đổi mật khẩu thành công!");
                    request.setAttribute("messageType", "success");
                }
                // Xóa tài khoản
                else if ("deleteAccount".equals(action)) {
                    String matKhauXacNhan = request.getParameter("matKhauXacNhan");
                    String xacNhanXoa = request.getParameter("xacNhanXoa");
                    
                    // Validate
                    if (matKhauXacNhan == null || matKhauXacNhan.trim().isEmpty()) {
                        throw new Exception("Mật khẩu xác nhận không được để trống!");
                    }
                    if (matKhauXacNhan.length() > 255) {
                        throw new Exception("Mật khẩu xác nhận không được quá 255 ký tự!");
                    }
                    if (xacNhanXoa == null || !xacNhanXoa.equals("XOA TAI KHOAN")) {
                        throw new Exception("Vui lòng nhập đúng cụm từ xác nhận: XOA TAI KHOAN");
                    }
                    
                    // Mã hóa
                    String encryptedPass1 = md5.ecrypt(matKhauXacNhan.trim());
                    
                    // Kiểm tra mật khẩu
                    TaiKhoan tkCheck = tkbo.checkLoginDB(account, encryptedPass1);
                    if (tkCheck == null) {
                        throw new Exception("Mật khẩu không chính xác!");
                    }
                    
                    // Xóa tài khoản
                    tkbo.deleteDB(account);
                    
                    // Xóa session và chuyển về trang đăng nhập
                    session.invalidate();
                    response.sendRedirect(request.getContextPath() + "/DangNhapController?deleteSuccess=true");
                    return;
                }
                
                // Thêm Email
                else if ("addEmail".equals(action)) {
                    String email = request.getParameter("email");
                    
                    // Validate
                    if (email == null || email.trim().isEmpty()) {
                        throw new Exception("Email không được để trống!");
                    }
                    if (email.length() > 255) {
                        throw new Exception("Email không được quá 255 ký tự!");
                    }
                    if (!Support.EmailService.isValidEmail(email.trim())) {
                        throw new Exception("Email không hợp lệ!");
                    }
                    
                    // Cập nhật email
                    tkbo.updateEmail(account, email.trim());
                    
                    // Thông báo thành công
                    request.setAttribute("message", "Thêm email thành công!");
                    request.setAttribute("messageType", "success");
                }
                
                // Đổi Email
                else if ("changeEmail".equals(action)) {
                    String emailMoi = request.getParameter("emailMoi");
                    String matKhauXacNhan = request.getParameter("matKhauXacNhan");
                    
                    // Validate
                    if (emailMoi == null || emailMoi.trim().isEmpty()) {
                        throw new Exception("Email mới không được để trống!");
                    }
                    if (emailMoi.length() > 255) {
                        throw new Exception("Email mới không được quá 255 ký tự!");
                    }
                    if (!Support.EmailService.isValidEmail(emailMoi.trim())) {
                        throw new Exception("Email mới không hợp lệ!");
                    }
                    if (matKhauXacNhan == null || matKhauXacNhan.trim().isEmpty()) {
                        throw new Exception("Mật khẩu xác nhận không được để trống!");
                    }
                    if (matKhauXacNhan.length() > 255) {
                        throw new Exception("Mật khẩu xác nhận không được quá 255 ký tự!");
                    }
                    
                    // Mã hóa
                    String encryptedPass1 = md5.ecrypt(matKhauXacNhan.trim());
                    
                    // Kiểm tra mật khẩu
                    TaiKhoan tkCheck = tkbo.checkLoginDB(account, encryptedPass1);
                    if (tkCheck == null) {
                        throw new Exception("Mật khẩu không chính xác!");
                    }
                    
                    // Kiểm tra email mới có trùng với email cũ không
                    TaiKhoan tkCurrent = tkbo.checkRegisterDB(account);
                    if (tkCurrent.getEmail() != null && tkCurrent.getEmail().equals(emailMoi.trim())) {
                        throw new Exception("Email mới không được trùng với email hiện tại!");
                    }
                    
                    // Cập nhật email
                    tkbo.updateEmail(account, emailMoi.trim());
                    
                    // Thông báo thành công
                    request.setAttribute("message", "Đổi email thành công!");
                    request.setAttribute("messageType", "success");
                }
                else {
                    //Không xác định được action
                    throw new Exception("Action không hợp lệ!");
                }
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