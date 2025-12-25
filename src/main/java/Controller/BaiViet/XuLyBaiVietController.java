package Controller.BaiViet;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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
import Modal.BaiVietEmbedding.BaiVietEmbedding;
import Modal.BaiVietEmbedding.BaiVietEmbeddingBO;
import Modal.BinhLuan.BinhLuan;
import Modal.BinhLuan.BinhLuanBO;
import Modal.TinNhanChat.TinNhanChat;
import Modal.TinNhanChat.TinNhanChatBO;
import Support.GeminiEmbeddingService;

@WebServlet("/XuLyBaiVietController")
@MultipartConfig
public class XuLyBaiVietController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BaiVietBO bvbo = new BaiVietBO();
    private BinhLuanBO blbo = new BinhLuanBO();
    private TinNhanChatBO tinNhanBO = new TinNhanChatBO();
	private BaiVietEmbeddingBO bveBO = new BaiVietEmbeddingBO();
	private GeminiEmbeddingService embeddingService = new GeminiEmbeddingService();
       
	public XuLyBaiVietController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set UTF-8 encoding
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		HttpSession session = request.getSession();
		
		// Kiểm tra đăng nhập và quyền Admin
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
		
		// Lấy tham số action
		String action = request.getParameter("action");
		
		try {
			if("create".equals(action)) {
				// Thêm bài viết
				String tieuDe = request.getParameter("tieuDe");
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				String taiKhoanTao = account;
				String maTheLoaiStr = request.getParameter("maTheLoai");
				
				// Validate
				if(tieuDe == null || tieuDe.trim().isEmpty()) {
					session.setAttribute("message", "Tiêu đề không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(tieuDe.trim().length() > 255) {
					session.setAttribute("message", "Tiêu đề không được quá 255 ký tự!");
					session.setAttribute("messageType", "error");
				} else if(noiDung == null || noiDung.trim().isEmpty()) {
					session.setAttribute("message", "Nội dung không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(maTheLoaiStr == null || maTheLoaiStr.trim().isEmpty()) {
					session.setAttribute("message", "Thể loại không được để trống!");
					session.setAttribute("messageType", "error");
				} else {
					int maTheLoai = Integer.parseInt(maTheLoaiStr);
					String finalUrl = (url != null && !url.trim().isEmpty()) ? url.trim() : null;
					
					bvbo.createDB(tieuDe.trim(), noiDung.trim(), finalUrl, taiKhoanTao, maTheLoai);
					//Embedding
					try {
					    ArrayList<BaiViet> allPosts = bvbo.readDB();
					    if (!allPosts.isEmpty()) {
					        BaiViet newPost = allPosts.get(allPosts.size() - 1);
					        String contentForEmbedding = tieuDe.trim() + " " + noiDung.trim();
					        ArrayList<Double> embedding = embeddingService.createEmbedding(contentForEmbedding);
					        String embeddingJson = embeddingService.embeddingToJson(embedding);
					        bveBO.createDB(newPost.getMaBaiViet(), embeddingJson);
					    }
					} catch (Exception embEx) {
					    System.err.println("Lỗi tạo embedding: " + embEx.getMessage());
					}
					session.setAttribute("message", "Thêm bài viết thành công!");
					session.setAttribute("messageType", "success");
					// Dọn dẹp file dư thừa không có trong csdl sau khi thêm
                    cleanOrphanFiles(request);
				}
				
			} else if("update".equals(action)) {
				// Sửa bài viết
				long maBaiViet = Long.parseLong(request.getParameter("maBaiViet"));
				String tieuDe = request.getParameter("tieuDe");
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				String keepOldFile = request.getParameter("keepOldFile");
				int maTheLoai = Integer.parseInt(request.getParameter("maTheLoai"));
				String danhGiaStr = request.getParameter("danhGia");
				String trangThai = request.getParameter("trangThai");
				
				BigDecimal danhGia = null;
				if(danhGiaStr != null && !danhGiaStr.trim().isEmpty()) {
					danhGia = new BigDecimal(danhGiaStr);
					if(danhGia.compareTo(BigDecimal.ZERO) < 0 || danhGia.compareTo(new BigDecimal("5")) > 0) {
						session.setAttribute("message", "Đánh giá phải từ 0 đến 5!");
						session.setAttribute("messageType", "error");
						response.sendRedirect(request.getContextPath() + "/BaiVietController");
						return;
					}
				}
				
				// Validate
				if(tieuDe == null || tieuDe.trim().isEmpty()) {
					session.setAttribute("message", "Tiêu đề không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(tieuDe.trim().length() > 255) {
					session.setAttribute("message", "Tiêu đề không được quá 255 ký tự!");
					session.setAttribute("messageType", "error");
				} else if(noiDung == null || noiDung.trim().isEmpty()) {
					session.setAttribute("message", "Nội dung không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(trangThai == null || trangThai.trim().isEmpty()) {
					session.setAttribute("message", "Trạng thái không được để trống!");
					session.setAttribute("messageType", "error");
				} else {
					// Lấy thông tin bài viết cũ
					BaiViet bvCu = null;
					for(BaiViet bv : bvbo.readDB()) {
						if(bv.getMaBaiViet() == maBaiViet) {
							bvCu = bv;
							break;
						}
					}
					
					String finalUrl = null;
					
					// Nếu có upload file mới
					if(url != null && !url.trim().isEmpty()) {
						finalUrl = url.trim();
					} 
					// Nếu giữ file cũ
					else if("true".equals(keepOldFile) && bvCu != null) {
						finalUrl = bvCu.getUrl();
					}
					
					bvbo.updateDB(maBaiViet, tieuDe.trim(), noiDung.trim(), finalUrl, maTheLoai, danhGia, trangThai);
					//Embedding
					try {
					    bveBO.deleteDB(maBaiViet);
					    String contentForEmbedding = tieuDe.trim() + " " + noiDung.trim();
					    ArrayList<Double> embedding = embeddingService.createEmbedding(contentForEmbedding);
					    String embeddingJson = embeddingService.embeddingToJson(embedding);
					    bveBO.createDB(maBaiViet, embeddingJson);
					} catch (Exception embEx) {
					    System.err.println("Lỗi cập nhật embedding: " + embEx.getMessage());
					}
					session.setAttribute("message", "Cập nhật bài viết thành công!");
					session.setAttribute("messageType", "success");
					
					// Dọn dẹp file dư thừa không có trong csdl sau khi sửa
                    cleanOrphanFiles(request);
				}
				
			} else if("delete".equals(action)) {
				// Xóa bài viết
				long maBaiViet = Long.parseLong(request.getParameter("maBaiViet"));
				bvbo.deleteDB(maBaiViet);
				
				session.setAttribute("message", "Xóa bài viết thành công!");
				session.setAttribute("messageType", "success");
				// Dọn dẹp file dư thừa không có trong csdl sau khi xóa
                cleanOrphanFiles(request);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", e.getMessage());
			session.setAttribute("messageType", "error");
		}
		
		// Redirect về trang bài viết
		response.sendRedirect(request.getContextPath() + "/BaiVietController");
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
	
	/**
	 * Xóa file cũ ở cả server và local
	 */
	private void deleteOldFile(HttpServletRequest request, String filePath) {
		if(filePath == null || filePath.trim().isEmpty()) {
			return;
		}
		
		try {
			// Xóa file trên server
			String serverPath = request.getServletContext().getRealPath("") + filePath;
			File serverFile = new File(serverPath);
			if(serverFile.exists()) {
				boolean deleted = serverFile.delete();
				if(deleted) {
					System.out.println("Đã xóa file trên server: " + serverPath);
				}
			}
			
			// Xóa file trên local (nếu là Windows)
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