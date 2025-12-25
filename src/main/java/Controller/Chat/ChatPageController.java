package Controller.Chat;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.DoanChat.DoanChat;
import Modal.DoanChat.DoanChatBO;
import Modal.TinNhanChat.TinNhanChat;
import Modal.TinNhanChat.TinNhanChatBO;

@WebServlet("/ChatPageController")
public class ChatPageController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DoanChatBO doanChatBO = new DoanChatBO();
    private TinNhanChatBO tinNhanBO = new TinNhanChatBO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        // Khác biệt so với không có tham số là nếu chưa có session thì trả về null, không tạo session mới
        HttpSession session = request.getSession(false);
        
        String account = (String) session.getAttribute("account");
        
        if (session == null || account == null || account.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/DangNhapController");
            return;
        }

        try {
            // Lấy danh sách đoạn chat
            ArrayList<DoanChat> dsDoanChat = doanChatBO.readDB(account);
            request.setAttribute("dsDoanChat", dsDoanChat);
            
            // Lấy maDoanChat từ URL (nếu có)
            String maDoanChatParam = request.getParameter("maDoanChat");
            
            // Lấy danh sách tin nhắn chat của đoạn chat có mã đoạn chat trên
            ArrayList<TinNhanChat> tinNhanList = new ArrayList<>();
            Long currentDoanChat = null;
            
            if (maDoanChatParam != null && !maDoanChatParam.trim().isEmpty()) {
                try {
                    currentDoanChat = Long.parseLong(maDoanChatParam);
                    tinNhanList = tinNhanBO.readDB(currentDoanChat);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            
            request.setAttribute("tinNhanList", tinNhanList);
            request.setAttribute("currentDoanChat", currentDoanChat);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.getRequestDispatcher("/pages/chat_page/ChatPage.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}