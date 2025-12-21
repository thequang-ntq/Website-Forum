package Controller.QuenMatKhau;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.TaiKhoan.TaiKhoan;
import Modal.TaiKhoan.TaiKhoanBO;
import Support.EmailService;

@WebServlet("/QuenMatKhauStepController")
public class QuenMatKhauStepController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TaiKhoanBO tkbo = new TaiKhoanBO();
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        String step = request.getParameter("step");
        
        try {
            if("sendPIN".equals(step)) {
                handleSendPIN(request, response, session);
            } else if("verifyPIN".equals(step)) {
                handleVerifyPIN(request, response, session);
            } else if("resetPassword".equals(step)) {
                handleResetPassword(request, response, session);
            }
        } catch(Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
        }
    }
    
    private void handleSendPIN(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws Exception {
        String tenDangNhap = request.getParameter("tenDangNhap");
        
        if(tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tên đăng nhập không được để trống!");
            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
            return;
        }
        
        TaiKhoan tk = tkbo.checkRegisterDB(tenDangNhap.trim());
        if(tk == null || "Deleted".equals(tk.getTrangThai())) {
            session.setAttribute("errorMessage", "Tài khoản không tồn tại!");
            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
            return;
        }
        
        if(tk.getEmail() == null || tk.getEmail().trim().isEmpty()) {
            session.setAttribute("errorMessage", "Tài khoản chưa liên kết email. Vui lòng liên hệ quản trị viên!");
            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
            return;
        }
        
        // Tạo và gửi PIN
        String pin = EmailService.generatePIN();
        boolean sent = EmailService.sendPIN(tk.getEmail(), pin);
        
        if(!sent) {
            session.setAttribute("errorMessage", "Không thể gửi email. Vui lòng thử lại!");
            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
            return;
        }
        
        // Lưu thông tin vào session
        session.setAttribute("resetPIN", pin);
        session.setAttribute("resetUsername", tenDangNhap.trim());
        session.setAttribute("pinExpiry", System.currentTimeMillis() + 5 * 60 * 1000); // 5 phút
        
        response.sendRedirect(request.getContextPath() + "/QuenMatKhauController?step=verify");
    }
    
    private void handleVerifyPIN(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws Exception {
        String inputPIN = request.getParameter("pin");
        String storedPIN = (String) session.getAttribute("resetPIN");
        Long expiry = (Long) session.getAttribute("pinExpiry");
        
        if(storedPIN == null || expiry == null) {
            session.setAttribute("errorMessage", "Phiên xác thực đã hết hạn!");
            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
            return;
        }
        
        if(System.currentTimeMillis() > expiry) {
            session.removeAttribute("resetPIN");
            session.removeAttribute("pinExpiry");
            session.setAttribute("errorMessage", "Mã PIN đã hết hạn!");
            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
            return;
        }
        
        if(!storedPIN.equals(inputPIN)) {
            session.setAttribute("errorVerify", "Mã PIN không chính xác!");
            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController?step=verify");
            return;
        }
        
        // Xác thực thành công
        session.removeAttribute("resetPIN");
        session.removeAttribute("pinExpiry");
        session.setAttribute("verifySuccess", true);
        response.sendRedirect(request.getContextPath() + "/QuenMatKhauController?step=reset");
    }
    
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws Exception {
        Boolean verified = (Boolean) session.getAttribute("verifySuccess");
        if(verified == null || !verified) {
            session.setAttribute("errorMessage", "Vui lòng xác thực mã PIN trước!");
            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController");
            return;
        }
        
        String tenDangNhap = (String) session.getAttribute("resetUsername");
        String matKhauMoi = request.getParameter("matKhauMoi");
        String nhapLaiMatKhauMoi = request.getParameter("nhapLaiMatKhauMoi");
        
        if(matKhauMoi == null || matKhauMoi.trim().isEmpty()) {
            session.setAttribute("errorReset", "Mật khẩu mới không được để trống!");
            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController?step=reset");
            return;
        }
        
        if(!matKhauMoi.equals(nhapLaiMatKhauMoi)) {
            session.setAttribute("errorReset", "Nhập lại mật khẩu không khớp!");
            response.sendRedirect(request.getContextPath() + "/QuenMatKhauController?step=reset");
            return;
        }
        
        // Đổi mật khẩu
        String encryptedPass = Support.md5.ecrypt(matKhauMoi.trim());
        tkbo.forgetPassDB(tenDangNhap, encryptedPass, encryptedPass);
        
        // Xóa session
        session.removeAttribute("verifySuccess");
        session.removeAttribute("resetUsername");
        
        session.setAttribute("successQuenMatKhau", "Đổi mật khẩu thành công! Vui lòng đăng nhập.");
        response.sendRedirect(request.getContextPath() + "/DangNhapController");
    }
}