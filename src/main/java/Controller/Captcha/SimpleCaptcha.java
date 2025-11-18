package Controller.Captcha;

import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nl.captcha.Captcha;

/**
 * Servlet để render ảnh Captcha
 */
@WebServlet("/CaptchaImage.jpg")
public class SimpleCaptcha extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public SimpleCaptcha() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        // Lấy captcha từ session
        Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
        
        if (captcha != null) {
            // Set response headers
            response.setContentType("image/jpg");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // Ghi ảnh captcha vào response
            ImageIO.write(captcha.getImage(), "jpg", response.getOutputStream());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}