package Controller.BaiVietCuaToi;

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
import Modal.BaiViet.BaiVietDAO;
import Modal.BinhLuan.BinhLuan;
import Modal.BinhLuan.BinhLuanBO;
import Modal.TinNhanChat.TinNhanChat;
import Modal.TinNhanChat.TinNhanChatBO;
import Modal.BaiVietEmbedding.BaiVietEmbedding;
import Modal.BaiVietEmbedding.BaiVietEmbeddingBO;
import Support.GeminiEmbeddingService;

@WebServlet("/XuLyBaiVietCuaToiController")
@MultipartConfig
public class XuLyBaiVietCuaToiController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BaiVietBO bvbo = new BaiVietBO();
    private BinhLuanBO blbo = new BinhLuanBO();
    private TinNhanChatBO tinNhanBO = new TinNhanChatBO();
	private BaiVietDAO bvdao = new BaiVietDAO();
	private BaiVietEmbeddingBO bveBO = new BaiVietEmbeddingBO();
	private GeminiEmbeddingService embeddingService = new GeminiEmbeddingService();
       
	public XuLyBaiVietCuaToiController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		HttpSession session = request.getSession();
		
		String account = (String) session.getAttribute("account");
		if(account == null || account.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/DangNhapController");
			return;
		}
		
		String action = request.getParameter("action");
		
		try {
			if("create".equals(action)) {
				String tieuDe = request.getParameter("tieuDe");
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				String taiKhoanTao = account;
				String maTheLoaiStr = request.getParameter("maTheLoai");
				
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
					//Tạo Embedding
					try {
					    // Lấy bài viết vừa tạo để có MaBaiViet
					    ArrayList<BaiViet> allPosts = bvbo.filterDB_taiKhoanTao(taiKhoanTao);
					    if (!allPosts.isEmpty()) {
					        BaiViet newPost = allPosts.get(allPosts.size() - 1);
					        
					        // Tạo embedding
					        String contentForEmbedding = tieuDe + " " + noiDung;
					        ArrayList<Double> embedding = embeddingService.createEmbedding(contentForEmbedding);
					        String embeddingJson = embeddingService.embeddingToJson(embedding);
					        
					        // Lưu vào DB
					        bveBO.createDB(newPost.getMaBaiViet(), embeddingJson);
					    }
					} 
					catch (Exception embEx) {
					    System.err.println("Lỗi tạo embedding: " + embEx.getMessage());
					}
					session.setAttribute("message", "Thêm bài viết thành công!");
					session.setAttribute("messageType", "success");
					// Dọn dẹp file orphan sau khi thêm
                    cleanOrphanFiles(request);
				}
				
			} else if("update".equals(action)) {
				long maBaiViet = Long.parseLong(request.getParameter("maBaiViet"));
				
				BaiViet bvCheck = bvdao.findByMaBaiViet(maBaiViet);
				if(bvCheck == null || !bvCheck.getTaiKhoanTao().equals(account)) {
					session.setAttribute("message", "Bạn không có quyền chỉnh sửa bài viết này!");
					session.setAttribute("messageType", "error");
					response.sendRedirect(request.getContextPath() + "/BaiVietCuaToiController");
					return;
				}
				
				String tieuDe = request.getParameter("tieuDe");
				String noiDung = request.getParameter("noiDung");
				String url = request.getParameter("url");
				String keepOldFile = request.getParameter("keepOldFile");
				int maTheLoai = Integer.parseInt(request.getParameter("maTheLoai"));
				String danhGiaStr = request.getParameter("danhGia");
				String trangThai = bvCheck.getTrangThai();
				
				BigDecimal danhGia = bvCheck.getDanhGia();
				if(danhGiaStr != null && !danhGiaStr.trim().isEmpty()) {
					danhGia = new BigDecimal(danhGiaStr);
					if(danhGia.compareTo(BigDecimal.ZERO) < 0 || danhGia.compareTo(new BigDecimal("5")) > 0) {
						session.setAttribute("message", "Đánh giá phải từ 0 đến 5!");
						session.setAttribute("messageType", "error");
						response.sendRedirect(request.getContextPath() + "/BaiVietCuaToiController");
						return;
					}
				}
				
				if(tieuDe == null || tieuDe.trim().isEmpty()) {
					session.setAttribute("message", "Tiêu đề không được để trống!");
					session.setAttribute("messageType", "error");
				} else if(tieuDe.trim().length() > 255) {
					session.setAttribute("message", "Tiêu đề không được quá 255 ký tự!");
					session.setAttribute("messageType", "error");
				} else if(noiDung == null || noiDung.trim().isEmpty()) {
					session.setAttribute("message", "Nội dung không được để trống!");
					session.setAttribute("messageType", "error");
				} else {
					String finalUrl = null;
					
					// Lấy mã ảnh mới
					if(url != null && !url.trim().isEmpty()) {
						finalUrl = url.trim();
					} 
					// Lấy lại url ảnh cũ của bài cũ
					else if("true".equals(keepOldFile)) {
						finalUrl = bvCheck.getUrl();
					}
					
					bvbo.updateDB(maBaiViet, tieuDe.trim(), noiDung.trim(), finalUrl, maTheLoai, danhGia, trangThai);
					//Embedding
					try {
					    // Xóa embedding cũ
					    bveBO.deleteDB(maBaiViet);
					    
					    // Tạo embedding mới
					    String contentForEmbedding = tieuDe + " " + noiDung;
					    ArrayList<Double> embedding = embeddingService.createEmbedding(contentForEmbedding);
					    String embeddingJson = embeddingService.embeddingToJson(embedding);
					    bveBO.createDB(maBaiViet, embeddingJson);
					} 
					catch (Exception embEx) {
					    System.err.println("Lỗi cập nhật embedding: " + embEx.getMessage());
					}
					session.setAttribute("message", "Cập nhật bài viết thành công!");
					session.setAttribute("messageType", "success");
					// Dọn dẹp file orphan sau khi thêm
                    cleanOrphanFiles(request);
				}
				
			} else if("delete".equals(action)) {
				long maBaiViet = Long.parseLong(request.getParameter("maBaiViet"));
				
				//Kiểm tra quyền sở hữu bài viết
				BaiViet bvCheck = bvdao.findByMaBaiViet(maBaiViet);
				if(bvCheck == null || !bvCheck.getTaiKhoanTao().equals(account)) {
					session.setAttribute("message", "Bạn không có quyền xóa bài viết này!");
					session.setAttribute("messageType", "error");
					response.sendRedirect(request.getContextPath() + "/BaiVietCuaToiController");
					return;
				}
				
				bvbo.deleteDB(maBaiViet);
				
				session.setAttribute("message", "Xóa bài viết thành công!");
				session.setAttribute("messageType", "success");
				// Dọn dẹp file orphan sau khi thêm
                cleanOrphanFiles(request);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			session.setAttribute("message", e.getMessage());
			session.setAttribute("messageType", "error");
		}
		
		response.sendRedirect(request.getContextPath() + "/BaiVietCuaToiController");
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