package Controller.Chat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Modal.DoanChat.DoanChatBO;
import Modal.TinNhanChat.TinNhanChat;
import Modal.TinNhanChat.TinNhanChatBO;

@WebServlet("/DeleteChatController")
public class DeleteChatController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DoanChatBO doanChatBO = new DoanChatBO();
    private TinNhanChatBO tinNhanBO = new TinNhanChatBO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String maDoanChatParam = request.getParameter("maDoanChat");
        
        try {
            if (maDoanChatParam != null && !maDoanChatParam.trim().isEmpty()) {
                long maDoanChat = Long.parseLong(maDoanChatParam);
                
                // Xóa đoạn chat (cascade sẽ tự động xóa tin nhắn)
                doanChatBO.deleteDB(maDoanChat);
                
                // Dọn dẹp file ảnh dư thừa sau khi xóa
                cleanOrphanChatImages(request);
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Dọn dẹp file ảnh chat orphan: So sánh file trong storage với URL trong DB TinNhanChat, xóa file không dùng.
     */
    private void cleanOrphanChatImages(HttpServletRequest request) {
        try {
            // Bước 1: Lấy set tên file từ DB TinNhanChat (chỉ phần filename từ URL)
            Set<String> usedFileNames = new HashSet<>();
            ArrayList<TinNhanChat> allTinNhan = tinNhanBO.readDB2();
            
            for (TinNhanChat tn : allTinNhan) {
                String url = tn.getUrl();
                if (url != null && !url.trim().isEmpty()) {
                    // Trích xuất filename từ URL (ví dụ: "storage/abc123.jpg" -> "abc123.jpg")
                    String fileName = url.substring(url.lastIndexOf("/") + 1);
                    if (!fileName.isEmpty()) {
                        usedFileNames.add(fileName);
                    }
                }
            }

            // Bước 2: Lấy đường dẫn storage
            String uploadPath = request.getServletContext().getRealPath("") + "storage"; // Server
            String uploadPath2 = null;
            if (uploadPath.contains(".metadata") && System.getProperty("os.name").toLowerCase().contains("win")) {
                uploadPath2 = "D:/Nam4/JavaNangCao" + request.getContextPath() + "/src/main/webapp/storage"; // Local
            }

            // Bước 3: Duyệt và xóa orphan ở server
            File serverDir = new File(uploadPath);
            if (serverDir.exists() && serverDir.isDirectory()) {
                File[] serverFiles = serverDir.listFiles();
                if (serverFiles != null) {
                    for (File file : serverFiles) {
                        if (file.isFile() && !usedFileNames.contains(file.getName())) {
                            // Xóa orphan
                            if (file.delete()) {
                                System.out.println("Đã xóa file ảnh chat orphan trên server: " + file.getName());
                            }
                        }
                    }
                }
            }

            // Bước 4: Duyệt và xóa orphan ở local (nếu tồn tại)
            if (uploadPath2 != null) {
                File localDir = new File(uploadPath2);
                if (localDir.exists() && localDir.isDirectory()) {
                    File[] localFiles = localDir.listFiles();
                    if (localFiles != null) {
                        for (File file : localFiles) {
                            if (file.isFile() && !usedFileNames.contains(file.getName())) {
                                // Xóa orphan
                                if (file.delete()) {
                                    System.out.println("Đã xóa file ảnh chat orphan trên local: " + file.getName());
                                }
                            }
                        }
                    }
                }
            }
        } 
        catch (Exception e) {
            System.err.println("Lỗi khi dọn dẹp file ảnh chat orphan: " + e.getMessage());
            e.printStackTrace();
        }
    }
}