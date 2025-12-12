package Controller.Chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ChatPageController")
public class ChatPageController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ChatPageController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

//        HttpSession session = request.getSession();
//        String account = (String) session.getAttribute("account");
//        
//        if (account == null || account.trim().isEmpty()) {
//            response.sendRedirect(request.getContextPath() + "/DangNhapController");
//            return;
//        }
        
        HttpSession session = request.getSession(false); // false: không tạo mới nếu chưa có
        String account = (String) session.getAttribute("account");
        if (session == null || account == null || account.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/DangNhapController");
            return;
        }

        // Lấy history và forward đến JSP
        ArrayList<Map<String, Object>> chatHistory = (ArrayList<Map<String, Object>>) session.getAttribute("chatHistory");
        if (chatHistory == null) {
            chatHistory = new ArrayList<>();
            session.setAttribute("chatHistory", chatHistory);
        }
        
        request.setAttribute("chatHistory", chatHistory); // forward attribute

        request.getRequestDispatcher("/pages/chat_page/ChatPage.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}