<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="Modal.BaiViet.BaiViet" %>
<%@ page import="Modal.BinhLuan.BinhLuan" %>
<%@ page import="Modal.LuotThichBinhLuan.LuotThichBinhLuan" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="Support.TinyMCEConfig" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Website Forum</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/pages/mycomment_page/style.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- TinyMCE Editor -->
	<script src="https://cdn.tiny.cloud/1/<%= TinyMCEConfig.getApiKey() %>/tinymce/6/tinymce.min.js" referrerpolicy="origin"></script>
</head>
<body>
    <%
        String quyen = (String) session.getAttribute("quyen");
        String account = (String) session.getAttribute("account");
        boolean isAdmin = "Admin".equals(quyen);
        
        ArrayList<BaiViet> dsBaiVietAll = (ArrayList<BaiViet>) request.getAttribute("dsBaiViet");
        ArrayList<BinhLuan> dsBinhLuan = (ArrayList<BinhLuan>) request.getAttribute("dsBinhLuan");
        ArrayList<LuotThichBinhLuan> dsLuotThich = (ArrayList<LuotThichBinhLuan>) request.getAttribute("dsLuotThich");
        
        long selectedBaiViet = -1;
        if (request.getAttribute("selectedBaiViet") != null) selectedBaiViet = (long) request.getAttribute("selectedBaiViet");
        
        String sortBy = (String) request.getAttribute("sortBy");
        String searchKey = (String) request.getAttribute("searchKey");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        
        Integer currentPage = (Integer) request.getAttribute("currentPage");
        Integer totalPages = (Integer) request.getAttribute("totalPages");
        Integer totalItems = (Integer) request.getAttribute("totalItems");
        Integer startIndex = (Integer) request.getAttribute("startIndex");
        
        if (currentPage == null) currentPage = 1;
        if (totalPages == null) totalPages = 0;
        if (totalItems == null) totalItems = 0;
        if (startIndex == null) startIndex = 0;
        
        String message = (String) request.getAttribute("message");
        String messageType = (String) request.getAttribute("messageType");
    %>

    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-gradient sticky-top">
        <div class="container-fluid">
            <a class="navbar-brand d-none d-lg-flex align-items-center" href="${pageContext.request.contextPath}/TrangChuController">
                <i class="bi bi-chat-dots-fill me-2"></i>
                <span class="fw-bold">Forum</span>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarContent">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/TrangChuController">
                            <i class="bi bi-house-fill me-1"></i> Trang chủ
                        </a>
                    </li>
                    <% if (isAdmin) { %>
                    <li class="nav-item">
						<a class="nav-link" href="${pageContext.request.contextPath}/TaiKhoanController">
							<i class="bi bi-tags-fill me-1"></i>Tài khoản
						</a>
					</li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/TheLoaiController">
                            <i class="bi bi-tags-fill me-1"></i>Thể loại
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/BaiVietController">
                            <i class="bi bi-file-text-fill me-1"></i>Bài viết
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/BinhLuanController">
                            <i class="bi bi-chat-left-text-fill me-1"></i>Bình luận
                        </a>
                    </li>
                    <% } %>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/BaiVietCuaToiController">
                            <i class="bi bi-journal-text me-1"></i>Bài viết của tôi
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/BinhLuanCuaToiController">
                            <i class="bi bi-chat-square-dots me-1"></i>Bình luận của tôi
                        </a>
                    </li>
                    <li class="nav-item">
					    <a class="nav-link" href="${pageContext.request.contextPath}/ChatPageController">
					        <i class="bi bi-robot me-1"></i>Chat AI
					    </a>
					</li>
                </ul>
                <div class="dropdown">
                    <button class="btn btn-user dropdown-toggle" type="button" data-bs-toggle="dropdown">
                        <i class="bi bi-person-circle"></i>
                        <span class="ms-2 d-none d-lg-inline"><%= account %></span>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/ThongTinCaNhanController">
                            <i class="bi bi-person-fill me-2"></i>Thông tin cá nhân
                        </a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/DangXuatController">
                            <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
                        </a></li>
                    </ul>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container-fluid mt-4">
        <div class="row g-3">
            <!-- Center Content - Comments -->
            <div class="col-lg-9 col-md-8 col-12">
                <!-- Page Title -->
                <div class="card shadow-sm mb-3 title-card">
                    <div class="card-body">
                        <h4 class="mb-0">
                            <i class="bi bi-chat-square-dots me-2"></i>
                            Danh sách bình luận của <%= account %>
                        </h4>
                    </div>
                </div>

                <!-- Alert Container (Auto dismiss) -->
                <div id="messageAlert" class="alert-container" style="display: none;">
                    <div class="alert fade" role="alert">
                        <i class="bi me-2"></i>
                        <span id="messageText"></span>
                    </div>
                </div>

                <!-- Add New Comment Button -->
                <div class="mb-3">
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCommentModal">
                        <i class="bi bi-plus-circle me-2"></i>Tạo bình luận mới
                    </button>
                </div>

                <!-- Sort & Filter Controls -->
                <div class="card shadow-sm mb-3 controls-card">
                    <div class="card-body">
                        <div class="row g-2">
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Sắp xếp:</label>
                                <select class="form-select form-select-sm" id="sortSelect" onchange="handleSort(this.value)">
                                    <option value="">-- Chọn --</option>
                                    <option value="luotThichCao" <%= "luotThichCao".equals(sortBy) ? "selected" : "" %>>Lượt thích cao nhất</option>
                                    <option value="luotThichThap" <%= "luotThichThap".equals(sortBy) ? "selected" : "" %>>Lượt thích thấp nhất</option>
                                    <option value="taoGanNhat" <%= "taoGanNhat".equals(sortBy) ? "selected" : "" %>>Tạo gần nhất</option>
                                    <option value="taoXaNhat" <%= "taoXaNhat".equals(sortBy) ? "selected" : "" %>>Tạo xa nhất</option>
                                    <option value="capNhatGanNhat" <%= "capNhatGanNhat".equals(sortBy) ? "selected" : "" %>>Cập nhật gần nhất</option>
                                    <option value="capNhatXaNhat" <%= "capNhatXaNhat".equals(sortBy) ? "selected" : "" %>>Cập nhật xa nhất</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Lọc theo bài viết:</label>
                                <select class="form-select form-select-sm" id="filterBaiVietSelect" onchange="handleFilterBaiViet(this.value)">
                                    <option value="">-- Tất cả bài viết --</option>
                                    <% 
                                        if (dsBaiVietAll != null && !dsBaiVietAll.isEmpty()) {
                                            for (BaiViet bv : dsBaiVietAll) { 
                                    %>
                                        <option value="<%= bv.getMaBaiViet() %>" <%= bv.getMaBaiViet() == selectedBaiViet ? "selected" : "" %>>
                                            <%= bv.getTieuDe() %>
                                        </option>
                                    <% 
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Comments List -->
                <div class="comments-container">
                    <% if (dsBinhLuan == null || dsBinhLuan.isEmpty()) { %>
                        <div class="card shadow-sm">
                            <div class="card-body text-center py-5">
                                <i class="bi bi-inbox text-muted" style="font-size: 4rem;"></i>
                                <h5 class="mt-3 text-muted">
                                    <% if (selectedBaiViet != -1) { %>
                                        Bạn chưa có bình luận nào trong bài viết này
                                    <% } else if (searchKey != null && !searchKey.trim().isEmpty()) { %>
                                        Không tìm thấy bình luận phù hợp với từ khóa tìm kiếm
                                    <% } else { %>
                                        Bạn chưa có bình luận nào. Hãy tạo bình luận đầu tiên!
                                    <% } %>
                                </h5>
                            </div>
                        </div>
                    <% } else {
                        for (BinhLuan bl : dsBinhLuan) {
                            // Tìm tên bài viết
                            String tenBaiViet = "";
                            if (dsBaiVietAll != null) {
                                for (BaiViet bv : dsBaiVietAll) {
                                    if (bv.getMaBaiViet() == bl.getMaBaiViet()) {
                                        tenBaiViet = bv.getTieuDe();
                                        break;
                                    }
                                }
                            }
                            
                            // Đếm số lượt thích
                            int soLuotThich = 0;
                            boolean daThich = false;
                            if (dsLuotThich != null) {
                                for (LuotThichBinhLuan lt : dsLuotThich) {
                                    if (lt.getMaBinhLuan() == bl.getMaBinhLuan()) {
                                        soLuotThich++;
                                        if (account != null && lt.getTenDangNhap().equals(account)) {
                                            daThich = true;
                                        }
                                    }
                                }
                            }
                    %>
                        <div class="card shadow-sm mb-3 comment-card" 
                             data-ma-binh-luan="<%= bl.getMaBinhLuan() %>"
                             data-noi-dung="<%= bl.getNoiDung().replace("\"", "&quot;").replace("\n", "&#10;") %>"
                             data-url="<%= bl.getUrl() != null ? bl.getUrl() : "" %>"
                             data-ma-bai-viet="<%= bl.getMaBaiViet() %>">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-start mb-2">
                                    <div class="d-flex align-items-center flex-grow-1">
                                        <i class="bi bi-person-circle comment-avatar"></i>
                                        <div class="ms-2">
                                            <div class="fw-bold"><%= bl.getTaiKhoanTao() %></div>
                                            <small class="text-muted">
                                                <i class="bi bi-clock me-1"></i>
                                                <%= bl.getThoiDiemCapNhat() != null ? sdf.format(bl.getThoiDiemCapNhat()) : sdf.format(bl.getThoiDiemTao()) %>
                                            </small>
                                        </div>
                                    </div>
                                    <div class="dropdown">
                                        <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                                            <i class="bi bi-three-dots-vertical"></i>
                                        </button>
                                        <ul class="dropdown-menu dropdown-menu-end">
                                            <li><a class="dropdown-item" href="#" onclick="editComment(<%= bl.getMaBinhLuan() %>); return false;">
                                                <i class="bi bi-pencil me-2"></i>Sửa
                                            </a></li>
                                            <li><hr class="dropdown-divider"></li>
                                            <li><a class="dropdown-item text-danger" href="#" onclick="deleteComment(<%= bl.getMaBinhLuan() %>, '<%= bl.getTieuDe() != null ? bl.getTieuDe().replace("'", "\\'") : "bình luận này" %>'); return false;">
                                                <i class="bi bi-trash me-2"></i>Xóa
                                            </a></li>
                                        </ul>
                                    </div>
                                </div>
                                
                                <div class="mb-2">
                                    <% if (!"Active".equals(bl.getTrangThai())) { %>
                                        <span class="badge bg-danger ms-1"><%= bl.getTrangThai() %></span>
                                    <% } %>
                                </div>
                                
                                <% if (bl.getTieuDe() != null && !bl.getTieuDe().trim().isEmpty()) { %>
                                    <h6 class="comment-title"><%= bl.getTieuDe() %></h6>
                                <% } %>
                                
                                <div class="comment-content mb-3">
                                    <%= bl.getNoiDung().replace("\n", "<br>") %>
                                </div>
                                
                                <% if (bl.getUrl() != null && !bl.getUrl().trim().isEmpty()) { %>
                                    <div class="comment-media mb-3">
                                        <% if (bl.getUrl().matches(".*\\.(jpg|jpeg|png|gif|webp)$")) { %>
                                            <img src="<%= bl.getUrl() %>" class="img-fluid rounded" alt="Comment image">
                                        <% } else if (bl.getUrl().matches(".*\\.(mp4|webm|ogg)$")) { %>
                                            <video controls class="w-100 rounded">
                                                <source src="<%= bl.getUrl() %>" type="video/mp4">
                                            </video>
                                        <% } %>
                                    </div>
                                <% } %>
                                
                                <div class="d-flex justify-content-between align-items-center pt-2 border-top">
                                    <div class="text-muted">
                                        <i class="bi bi-file-text-fill me-1"></i>
                                        <a href="${pageContext.request.contextPath}/ChiTietBaiVietController?id=<%= bl.getMaBaiViet() %>" 
                                           class="text-decoration-none">
                                            <span><%= tenBaiViet %></span>
                                        </a>
                                    </div>
                                    <div class="like-section" style="cursor: pointer;">
                                        <i class="bi <%= daThich ? "bi-hand-thumbs-up-fill" : "bi-hand-thumbs-up" %> me-1 like-icon" 
                                           data-ma-binh-luan="<%= bl.getMaBinhLuan() %>"
                                           data-da-thich="<%= daThich %>"
                                           style="color: <%= daThich ? "#667eea" : "" %>; font-size: 1.1rem;"></i>
                                        <span class="like-count" data-ma-binh-luan="<%= bl.getMaBinhLuan() %>"><%= soLuotThich %></span>
                                        <span class="text-muted"> lượt thích</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    <% 
                        }
                    } 
                    %>
                    
                    <% if (totalPages > 1) { %>
                        <nav aria-label="Page navigation" class="mt-4">
                            <ul class="pagination justify-content-center">
                                <li class="page-item <%= currentPage == 1 ? "disabled" : "" %>">
                                    <a class="page-link" href="<%= currentPage > 1 ? buildPaginationUrl(request, currentPage - 1) : "#" %>" aria-label="Previous">
                                        <span aria-hidden="true">&laquo;</span>
                                    </a>
                                </li>
                                <%
                                    int startPage = Math.max(1, currentPage - 2);
                                    int endPage = Math.min(totalPages, currentPage + 2);
                                    if (currentPage <= 3) {
                                        endPage = Math.min(5, totalPages);
                                    }
                                    if (currentPage >= totalPages - 2) {
                                        startPage = Math.max(1, totalPages - 4);
                                    }
                                    if (startPage > 1) {
                                %>
                                        <li class="page-item">
                                            <a class="page-link" href="<%= buildPaginationUrl(request, 1) %>">1</a>
                                        </li>
                                        <% if (startPage > 2) { %>
                                            <li class="page-item disabled">
                                                <span class="page-link">...</span>
                                            </li>
                                        <% } %>
                                <%
                                    }
                                    for (int i = startPage; i <= endPage; i++) {
                                %>
                                        <li class="page-item <%= i == currentPage ? "active" : "" %>">
                                            <a class="page-link" href="<%= buildPaginationUrl(request, i) %>"><%= i %></a>
                                        </li>
                                <%
                                    }
                                    if (endPage < totalPages) {
                                        if (endPage < totalPages - 1) {
                                %>
                                            <li class="page-item disabled">
                                                <span class="page-link">...</span>
                                            </li>
                                <%
                                        }
                                %>
                                        <li class="page-item">
                                            <a class="page-link" href="<%= buildPaginationUrl(request, totalPages) %>"><%= totalPages %></a>
                                        </li>
                                <%
                                    }
                                %>
                                <li class="page-item <%= currentPage == totalPages ? "disabled" : "" %>">
                                    <a class="page-link" href="<%= currentPage < totalPages ? buildPaginationUrl(request, currentPage + 1) : "#" %>" aria-label="Next">
                                        <span aria-hidden="true">&raquo;</span>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                        
                        <div class="text-center text-muted mt-2">
                            <small>
                                Trang <%= currentPage %> / <%= totalPages %> 
                                (Tổng <%= totalItems %> bình luận)
                            </small>
                        </div>
                    <% } %>
                </div>
            </div>

            <!-- Right Sidebar - Search -->
            <div class="col-lg-3 col-md-4 d-none d-md-block">
                <div class="card shadow-sm search-card sticky-top" style="top: 80px;">
                    <div class="card-header bg-gradient text-white">
                        <h5 class="mb-0"><i class="bi bi-search me-2"></i>Tìm kiếm</h5>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/BinhLuanCuaToiController" method="get">
                            <div class="search-box">
                                <input type="text" class="form-control" name="search" id="searchInput"
                                       placeholder="Tìm kiếm bình luận..."
                                       value="<%= searchKey != null ? searchKey : "" %>">
                                <i class="bi bi-search search-icon"></i>
                                <button type="button" class="btn btn-ai-search" onclick="enhanceSearch()" title="Tìm kiếm thông minh với AI">
                                    <i class="bi bi-stars"></i>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="footer mt-5">
        <div class="container text-center">
            <p class="mb-0">&copy; 2025 Website Forum. All rights reserved.</p>
        </div>
    </footer>

    <!-- Add Comment Modal -->
	<div class="modal fade" id="addCommentModal" tabindex="-1">
	    <div class="modal-dialog modal-lg modal-dialog-scrollable">
	        <div class="modal-content">
	            <div class="modal-header bg-gradient text-white">
	                <h5 class="modal-title"><i class="bi bi-plus-circle me-2"></i>Tạo bình luận mới</h5>
	                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
	            </div>
	            <form action="${pageContext.request.contextPath}/XuLyBinhLuanCuaToiController" method="post" enctype="multipart/form-data">
	                <input type="hidden" name="action" value="create">
	                <input type="hidden" id="addUrlHidden" name="url" value="">
	                <div class="modal-body">
	                    <div class="mb-3">
	                        <label class="form-label fw-bold">Bài viết <span class="text-danger">*</span></label>
	                        <select class="form-select" name="maBaiViet" required>
	                            <option value="">-- Chọn bài viết --</option>
	                            <% if (dsBaiVietAll != null) {
	                                for (BaiViet bv : dsBaiVietAll) { %>
	                                    <option value="<%= bv.getMaBaiViet() %>"><%= bv.getTieuDe() %></option>
	                            <% }
	                            } %>
	                        </select>
	                    </div>
	                    <div class="mb-3">
						    <label class="form-label fw-bold">Nội dung <span class="text-danger">*</span></label>
						    <textarea class="form-control tinymce-editor" name="noiDung" id="addNoiDung" 
						              rows="6"></textarea>
						</div>
	                    
	                    <!-- Upload File Section -->
	                    <div class="mb-3">
	                        <label class="form-label fw-bold">Ảnh/Video</label>
	                        <div class="upload-container">
	                            <input type="file" id="addFileInput" accept="image/*,video/*" style="display: none;">
	                            <button type="button" class="btn btn-upload" onclick="document.getElementById('addFileInput').click()">
	                                <i class="bi bi-cloud-upload me-2"></i>Tải ảnh/video lên
	                            </button>
	                            <div class="file-status mt-2">
	                                <i class="bi bi-file-earmark"></i>
	                                <span id="addFileStatus" class="text-muted">Chưa có ảnh/video</span>
	                            </div>
	                            <div id="addFilePreview" class="file-preview mt-3" style="display: none;"></div>
	                        </div>
	                        <small class="text-muted">
	                            Hỗ trợ: .jpg, .jpeg, .png, .gif, .webp (ảnh), .mp4, .webm, .ogg (video) - Tối đa 50MB
	                        </small>
	                    </div>
	                </div>
	                <div class="modal-footer sticky-footer">
	                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
	                        <i class="bi bi-x-circle me-1"></i>Hủy
	                    </button>
	                    <button type="submit" class="btn btn-primary">
	                        <i class="bi bi-check-circle me-1"></i>Tạo bình luận
	                    </button>
	                </div>
	            </form>
	        </div>
	    </div>
	</div>

    <!-- Edit Comment Modal -->
	<div class="modal fade" id="editCommentModal" tabindex="-1">
	    <div class="modal-dialog modal-lg modal-dialog-scrollable">
	        <div class="modal-content">
	            <div class="modal-header bg-gradient text-white">
	                <h5 class="modal-title"><i class="bi bi-pencil me-2"></i>Chỉnh sửa bình luận</h5>
	                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
	            </div>
	            <form action="${pageContext.request.contextPath}/XuLyBinhLuanCuaToiController" method="post" enctype="multipart/form-data" id="editCommentForm">
	                <input type="hidden" name="action" value="update">
	                <input type="hidden" name="maBinhLuan" id="editMaBinhLuan">
	                <input type="hidden" id="editUrlHidden" name="url" value="">
	                <input type="hidden" id="editKeepOldFile" name="keepOldFile" value="false">
	                <div class="modal-body">
	                    <div class="mb-3">
						    <label class="form-label fw-bold">Nội dung <span class="text-danger">*</span></label>
						    <textarea class="form-control tinymce-editor" name="noiDung" id="editNoiDung" 
						              rows="6"></textarea>
						</div>
	                    
	                    <!-- Upload File Section -->
	                    <div class="mb-3">
	                        <label class="form-label fw-bold">Ảnh/Video</label>
	                        <div class="upload-container">
	                            <input type="file" id="editFileInput" accept="image/*,video/*" style="display: none;">
	                            <button type="button" class="btn btn-upload" onclick="document.getElementById('editFileInput').click()">
	                                <i class="bi bi-cloud-upload me-2"></i>Tải ảnh/video lên
	                            </button>
	                            <div class="file-status mt-2">
	                                <i class="bi bi-file-earmark"></i>
	                                <span id="editFileStatus" class="text-muted">Chưa có ảnh/video</span>
	                            </div>
	                            <div id="editFilePreview" class="file-preview mt-3" style="display: none;"></div>
	                            <button type="button" class="btn btn-sm btn-danger mt-2" id="editRemoveFileBtn" 
	                                    style="display: none;" onclick="removeEditFile()">
	                                <i class="bi bi-trash me-1"></i>Xóa file
	                            </button>
	                        </div>
	                        <small class="text-muted">
	                            Hỗ trợ: .jpg, .jpeg, .png, .gif, .webp (ảnh), .mp4, .webm, .ogg (video) - Tối đa 50MB
	                        </small>
	                    </div>
	                </div>
	                <div class="modal-footer sticky-footer">
	                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
	                        <i class="bi bi-x-circle me-1"></i>Hủy
	                    </button>
	                    <button type="submit" class="btn btn-primary">
	                        <i class="bi bi-check-circle me-1"></i>Cập nhật
	                    </button>
	                </div>
	            </form>
	        </div>
	    </div>
	</div>

    <!-- Like List Modal -->
    <div class="modal fade" id="likeListModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="bi bi-hand-thumbs-up-fill me-2"></i>Danh sách lượt thích</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body" id="likeListContent">
                    <div class="text-center py-3">
                        <div class="spinner-border text-primary" role="status"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%!
        private String buildPaginationUrl(HttpServletRequest request, int page) {
            String contextPath = request.getContextPath();
            String baiviet = request.getParameter("baiviet");
            String sort = request.getParameter("sort");
            String search = request.getParameter("search");
            
            StringBuilder url = new StringBuilder(contextPath + "/BinhLuanCuaToiController?page=" + page);
            
            if (baiviet != null && !baiviet.trim().isEmpty()) {
                try {
                    url.append("&baiviet=").append(java.net.URLEncoder.encode(baiviet, "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (sort != null && !sort.trim().isEmpty()) {
                try {
                    url.append("&sort=").append(java.net.URLEncoder.encode(sort, "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (search != null && !search.trim().isEmpty()) {
                try {
                    url.append("&search=").append(java.net.URLEncoder.encode(search, "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return url.toString();
        }
    %>

    <script src="${pageContext.request.contextPath}/pages/mycomment_page/script.js"></script>
    <script>
        // Show message from server - Auto dismiss after 3 seconds
        <% if (message != null && messageType != null) { 
            String escapedMessage = message.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r");
        %>
            document.addEventListener('DOMContentLoaded', function() {
                setTimeout(function() {
                    showMessage('<%= escapedMessage %>', '<%= messageType %>');
                }, 100);
            });
        <% } %>
    </script>
    
    <!-- Full-screen AI Search Loading Overlay -->
	<div id="fullPageLoading" 
	     class="position-fixed top-0 start-0 w-100 h-100 d-none 
	            bg-white bg-opacity-95 d-flex flex-column align-items-center justify-content-center"
	     style="z-index: 9999; backdrop-filter: blur(8px);">
	    <div class="text-center">
	        <div class="spinner-border text-primary mb-4" 
	             style="width: 4rem; height: 4rem;" role="status">
	            <span class="visually-hidden">Loading...</span>
	        </div>
	        <h4 class="text-primary mb-2 fw-bold">
	            <i class="bi bi-stars me-2"></i>AI đang tìm kiếm thông minh...
	        </h4>
	        <p class="text-muted">Đang phân tích và tối ưu từ khóa cho bạn</p>
	        <div class="d-flex justify-content-center gap-2 mt-3">
	            <div class="bg-primary opacity-25 rounded" 
	                 style="width: 8px; height: 8px; animation: pulse 1.5s infinite;"></div>
	            <div class="bg-primary opacity-50 rounded" 
	                 style="width: 8px; height: 8px; animation: pulse 1.5s infinite 0.3s;"></div>
	            <div class="bg-primary opacity-75 rounded" 
	                 style="width: 8px; height: 8px; animation: pulse 1.5s infinite 0.6s;"></div>
	        </div>
	    </div>
	</div>
	
	<!-- Thêm animation pulse -->
	<style>
	@keyframes pulse {
	    0%, 100% { transform: scale(1); opacity: 0.5; }
	    50% { transform: scale(1.5); opacity: 1; }
	}
	</style>
</body>
</html>