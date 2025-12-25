package Controller.BinhLuanCuaToi;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import Modal.BaiViet.BaiViet;
import Modal.BaiViet.BaiVietBO;
import Modal.BinhLuan.BinhLuan;
import Modal.BinhLuan.BinhLuanBO;
import Modal.BinhLuan.BinhLuanDAO;
import Modal.LuotThichBinhLuan.LuotThichBinhLuan;
import Modal.LuotThichBinhLuan.LuotThichBinhLuanBO;
import Modal.TinNhanChat.TinNhanChat;
import Modal.TinNhanChat.TinNhanChatBO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import Modal.BinhLuanEmbedding.BinhLuanEmbedding;
import Modal.BinhLuanEmbedding.BinhLuanEmbeddingBO;
import Support.GeminiEmbeddingService;

@WebServlet("/XuLyBinhLuanCuaToiController")
@MultipartConfig
public class XuLyBinhLuanCuaToiController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BaiVietBO bvbo = new BaiVietBO();
    private BinhLuanBO blbo = new BinhLuanBO();
    private TinNhanChatBO tinNhanBO = new TinNhanChatBO();
	private BinhLuanDAO bldao = new BinhLuanDAO();
	private LuotThichBinhLuanBO ltbo = new LuotThichBinhLuanBO();
	private BinhLuanEmbeddingBO bleBO = new BinhLuanEmbeddingBO();
	private GeminiEmbeddingService embeddingService = new GeminiEmbeddingService();
       
	public XuLyBinhLuanCuaToiController() {
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
			    String noiDung = request.getParameter("noiDung");
			    String url = request.getParameter("url");
			    String taiKhoanTao = account;
			    String maBaiVietStr = request.getParameter("maBaiViet");
			    
			    if(noiDung == null || noiDung.trim().isEmpty()) {
			        session.setAttribute("message", "Nội dung không được để trống!");
			        session.setAttribute("messageType", "error");
			    } else if(maBaiVietStr == null || maBaiVietStr.trim().isEmpty()) {
			        session.setAttribute("message", "Mã bài viết không được để trống!");
			        session.setAttribute("messageType", "error");
			    } else {
			        long maBaiViet = Long.parseLong(maBaiVietStr);
			        String finalUrl = (url != null && !url.trim().isEmpty()) ? url.trim() : null;
			        
			        blbo.createDB(noiDung.trim(), finalUrl, taiKhoanTao, maBaiViet);
			        // Tạo embedding cho bình luận
			        try {
			            ArrayList<BinhLuan> allComments = blbo.filterDB_taiKhoanTao(taiKhoanTao);
			            if (!allComments.isEmpty()) {
			                BinhLuan newComment = allComments.get(allComments.size() - 1);
			                
			                ArrayList<Double> embedding = embeddingService.createEmbedding(noiDung.trim());
			                String embeddingJson = embeddingService.embeddingToJson(embedding);
			                bleBO.createDB(newComment.getMaBinhLuan(), embeddingJson);
			            }
			        } 
			        catch (Exception embEx) {
			            System.err.println("Lỗi tạo embedding: " + embEx.getMessage());
			        }
			        session.setAttribute("message", "Thêm bình luận thành công!");
			        session.setAttribute("messageType", "success");
			        // Dọn dẹp file orphan sau khi thêm
                    cleanOrphanFiles(request);
			    }
			    
			} else if("update".equals(action)) {
			    long maBinhLuan = Long.parseLong(request.getParameter("maBinhLuan"));
			    
			    BinhLuan blCheck = bldao.findByMaBinhLuan(maBinhLuan);
			    if(blCheck == null || !blCheck.getTaiKhoanTao().equals(account)) {
			        session.setAttribute("message", "Bạn không có quyền chỉnh sửa bình luận này!");
			        session.setAttribute("messageType", "error");
			        response.sendRedirect(request.getContextPath() + "/BinhLuanCuaToiController");
			        return;
			    }
			    
			    String noiDung = request.getParameter("noiDung");
			    String url = request.getParameter("url");
			    String keepOldFile = request.getParameter("keepOldFile");
			    int soLuotThich = blCheck.getSoLuotThich();
			    String trangThai = blCheck.getTrangThai();
			    
			    if(noiDung == null || noiDung.trim().isEmpty()) {
			        session.setAttribute("message", "Nội dung không được để trống!");
			        session.setAttribute("messageType", "error");
			    } else {
			        String finalUrl = null;
			        
			        if(url != null && !url.trim().isEmpty()) {
			            finalUrl = url.trim();
			        } else if("true".equals(keepOldFile)) {
			            finalUrl = blCheck.getUrl();
			        }
			        
			        blbo.updateDB(maBinhLuan, noiDung.trim(), finalUrl, soLuotThich, trangThai);
			        // Cập nhật embedding
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
				
				BinhLuan blCheck = bldao.findByMaBinhLuan(maBinhLuan);
				if(blCheck == null || !blCheck.getTaiKhoanTao().equals(account)) {
					session.setAttribute("message", "Bạn không có quyền xóa bình luận này!");
					session.setAttribute("messageType", "error");
					response.sendRedirect(request.getContextPath() + "/BinhLuanCuaToiController");
					return;
				}
				
				blbo.deleteDB(maBinhLuan);
				session.setAttribute("message", "Xóa bình luận thành công!");
				session.setAttribute("messageType", "success");
				// Dọn dẹp file orphan sau khi thêm
                cleanOrphanFiles(request);
				
			} else if("like".equals(action) || "unlike".equals(action)) {
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				JSONObject json = new JSONObject();
				
				try {
					long maBinhLuan = Long.parseLong(request.getParameter("maBinhLuan"));
					
					if("like".equals(action)) {
						ltbo.createDB(maBinhLuan, account);
						json.put("success", true);
						json.put("message", "Đã thích!");
					} else {
						ltbo.deleteDBByMaBinhLuanVaTenDangNhap(maBinhLuan, account);
						json.put("success", true);
						json.put("message", "Đã bỏ thích!");
					}
				} catch(Exception e) {
					e.printStackTrace();
					json.put("success", false);
					json.put("message", e.getMessage());
				}
				
				out.print(json.toString());
				return;
				
			} else if("getLikeList".equals(action)) {
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				JSONObject json = new JSONObject();
				
				try {
					long maBinhLuan = Long.parseLong(request.getParameter("maBinhLuan"));
					
					ArrayList<LuotThichBinhLuan> dsAll = ltbo.readDB();
					ArrayList<LuotThichBinhLuan> dsFiltered = new ArrayList<>();
					for(LuotThichBinhLuan lt : dsAll) {
						if(lt.getMaBinhLuan() == maBinhLuan) {
							dsFiltered.add(lt);
						}
					}
					
					JSONArray arr = new JSONArray();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
					
					for(LuotThichBinhLuan lt : dsFiltered) {
						JSONObject item = new JSONObject();
						item.put("tenDangNhap", lt.getTenDangNhap());
						item.put("thoiDiemThich", sdf.format(lt.getThoiDiemThich()));
						arr.put(item);
					}
					
					json.put("success", true);
					json.put("list", arr);
					
				} catch(Exception e) {
					e.printStackTrace();
					json.put("success", false);
					json.put("message", e.getMessage());
				}
				
				out.print(json.toString());
				return;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			session.setAttribute("message", e.getMessage());
			session.setAttribute("messageType", "error");
		}
		
		response.sendRedirect(request.getContextPath() + "/BinhLuanCuaToiController");
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
        } catch (Exception e) {
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