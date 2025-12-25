package Controller.BinhLuan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Modal.BaiViet.BaiViet;
import Modal.BaiViet.BaiVietBO;
import Modal.BinhLuan.BinhLuan;
import Modal.BinhLuan.BinhLuanBO;
import Modal.BinhLuan.BinhLuanDAO;
import Modal.BinhLuanEmbedding.BinhLuanEmbedding;
import Modal.BinhLuanEmbedding.BinhLuanEmbeddingBO;
import Modal.TinNhanChat.TinNhanChat;
import Modal.TinNhanChat.TinNhanChatBO;
import Support.GeminiEmbeddingService;

@WebServlet("/XuLyBinhLuanController")
@MultipartConfig
public class XuLyBinhLuanController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BaiVietBO bvbo = new BaiVietBO();
    private BinhLuanBO blbo = new BinhLuanBO();
    private BinhLuanDAO bldao = new BinhLuanDAO();
    private BinhLuanEmbeddingBO bleBO = new BinhLuanEmbeddingBO();
    private TinNhanChatBO tinNhanBO = new TinNhanChatBO();
    private GeminiEmbeddingService embeddingService = new GeminiEmbeddingService();
	
    public XuLyBinhLuanController() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		HttpSession session = request.getSession();
		
		String account = (String) session.getAttribute("account");
		String quyen = (String) session.getAttribute("quyen");
		
		if(account == null || account.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/DangNhapController");
			return;
		}
		
		if(!"Admin".equals(quyen)) {
			response.sendRedirect(request.getContextPath() + "/TrangChuController");
			return;
		}
		
		String action = request.getParameter("action");
		
		try {
			if("create".equals(action)) {
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				String taiKhoanTao = account;
				String maBaiVietStr = request.getParameter("maBaiViet");
				
				if(noiDung == null || noiDung.trim().isEmpty()) {
					session.setAttribute("message", "Nội dung không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(maBaiVietStr == null || maBaiVietStr.trim().isEmpty()) {
					session.setAttribute("message", "Bài viết chứa bình luận không được để trống!");
					session.setAttribute("messageType", "error");
				} else {
					long maBaiViet = Long.parseLong(maBaiVietStr);
					String finalUrl = (url != null && !url.trim().isEmpty()) ? url.trim() : null;
					blbo.createDB(noiDung.trim(), finalUrl, taiKhoanTao, maBaiViet);
					//Embedding
					try {
					    ArrayList<BinhLuan> allComments = blbo.readDB();
					    if (!allComments.isEmpty()) {
					        BinhLuan newComment = allComments.get(allComments.size() - 1);
					        ArrayList<Double> embedding = embeddingService.createEmbedding(noiDung.trim());
					        String embeddingJson = embeddingService.embeddingToJson(embedding);
					        bleBO.createDB(newComment.getMaBinhLuan(), embeddingJson);
					    }
					} catch (Exception embEx) {
					    System.err.println("Lỗi tạo embedding: " + embEx.getMessage());
					}
					session.setAttribute("message", "Thêm bình luận thành công!");
					session.setAttribute("messageType", "success");
					// Dọn dẹp file orphan sau khi thêm
                    cleanOrphanFiles(request);
				}
				
			} else if("update".equals(action)) {
				long maBinhLuan = Long.parseLong(request.getParameter("maBinhLuan"));
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				String keepOldFile = request.getParameter("keepOldFile");
				String soLuotThichStr = request.getParameter("soLuotThich");
				String trangThai = request.getParameter("trangThai");
				
				BinhLuan blCheck = bldao.findByMaBinhLuan(maBinhLuan);
				if(blCheck == null) {
					session.setAttribute("message", "Bình luận không tồn tại!");
					session.setAttribute("messageType", "error");
					response.sendRedirect(request.getContextPath() + "/BinhLuanController");
					return;
				}
				
				int soLuotThich = -1;
				if(soLuotThichStr != null && !soLuotThichStr.trim().isEmpty()) {
					soLuotThich = Integer.parseInt(soLuotThichStr);
					if(soLuotThich < 0) {
						session.setAttribute("message", "Số lượt thích phải lớn hơn hoặc bằng 0");
						session.setAttribute("messageType", "error");
						response.sendRedirect(request.getContextPath() + "/BinhLuanController");
						return;
					}
				}
				
				if(noiDung == null || noiDung.trim().isEmpty()) {
					session.setAttribute("message", "Nội dung không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(trangThai == null || trangThai.trim().isEmpty()) {
					session.setAttribute("message", "Trạng thái không được để trống!");
					session.setAttribute("messageType", "error");
				} else {
					String finalUrl = null;
					
					if(url != null && !url.trim().isEmpty()) {
						finalUrl = url.trim();
					} else if("true".equals(keepOldFile)) {
						finalUrl = blCheck.getUrl();
					}
					
					blbo.updateDB(maBinhLuan, noiDung.trim(), finalUrl, soLuotThich, trangThai);
					//Embedding
					try {
					    bleBO.deleteDB(maBinhLuan);
					    ArrayList<Double> embedding = embeddingService.createEmbedding(noiDung.trim());
					    String embeddingJson = embeddingService.embeddingToJson(embedding);
					    bleBO.createDB(maBinhLuan, embeddingJson);
					} catch (Exception embEx) {
					    System.err.println("Lỗi cập nhật embedding: " + embEx.getMessage());
					}
					session.setAttribute("message", "Cập nhật bình luận thành công!");
					session.setAttribute("messageType", "success");
					// Dọn dẹp file orphan sau khi thêm
                    cleanOrphanFiles(request);
				}
				
			} else if("delete".equals(action)) {
				long maBinhLuan = Long.parseLong(request.getParameter("maBinhLuan"));
				blbo.deleteDB(maBinhLuan);
				session.setAttribute("message", "Xóa bình luận thành công!");
				session.setAttribute("messageType", "success");
				// Dọn dẹp file orphan sau khi thêm
                cleanOrphanFiles(request);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", e.getMessage());
			session.setAttribute("messageType", "error");
		}
		
		response.sendRedirect(request.getContextPath() + "/BinhLuanController");
	}
	
	/**
     * Dọn dẹp file orphan: So sánh file trong storage với URL trong DB, xóa file không dùng.
     */
    private void cleanOrphanFiles(HttpServletRequest request) {
        try {
            // Bước 1: Lấy set tên file từ DB (chỉ phần filename từ URL) từ cả bài viết và bình luận, và tin nhắn chat
            Set<String> usedFileNames = new HashSet<>();
            // Bài viết
            for (BaiViet bv : bvbo.readDB()) {
                String url = bv.getUrl();
                if (url != null && !url.trim().isEmpty()) {
                    // Trích xuất filename từ URL (ví dụ: "storage/abc123.jpg" -> "abc123.jpg")
                    String fileName = url.substring(url.lastIndexOf("/") + 1);
                    if (!fileName.isEmpty()) {
                        usedFileNames.add(fileName);
                    }
                }
            }
            
            // Bình luận
            for (BinhLuan bl : blbo.readDB()) {
                String url = bl.getUrl();
                if (url != null && !url.trim().isEmpty()) {
                    // Trích xuất filename từ URL (ví dụ: "storage/abc123.jpg" -> "abc123.jpg")
                    String fileName = url.substring(url.lastIndexOf("/") + 1);
                    if (!fileName.isEmpty()) {
                        usedFileNames.add(fileName);
                    }
                }
            }
            
            // Tin nhắn chat
            for (TinNhanChat tn : tinNhanBO.readDB2()) {
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
                                System.out.println("Đã xóa file orphan trên server: " + file.getName());
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
                                    System.out.println("Đã xóa file orphan trên local: " + file.getName());
                                }
                            }
                        }
                    }
                }
            }
        } 
        catch (Exception e) {
            System.err.println("Lỗi khi dọn dẹp file orphan: " + e.getMessage());
            e.printStackTrace();
        }
    }
	
	private void deleteOldFile(HttpServletRequest request, String filePath) {
		if(filePath == null || filePath.trim().isEmpty()) {
			return;
		}
		
		try {
			String serverPath = request.getServletContext().getRealPath("") + filePath;
			File serverFile = new File(serverPath);
			if(serverFile.exists()) {
				boolean deleted = serverFile.delete();
				if(deleted) {
					System.out.println("Đã xóa file trên server: " + serverPath);
				}
			}
			
			if(serverPath.contains(".metadata") && System.getProperty("os.name").toLowerCase().contains("win")) {
				String localPath = "D:/Nam4/JavaNangCao" + request.getContextPath() + "/src/main/webapp/" + filePath;
				File localFile = new File(localPath);
				if(localFile.exists()) {
					boolean deleted = localFile.delete();
					if(deleted) {
						System.out.println("Đã xóa file trên local: " + localPath);
					}
				}
			}
		} catch(Exception e) {
			System.err.println("Lỗi khi xóa file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}