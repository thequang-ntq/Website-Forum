<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="Modal.BaiViet.BaiViet" %>
<%@ page import="Modal.TheLoai.TheLoai" %>
<%@ page import="Modal.BinhLuan.BinhLuan" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.TimeZone" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bài viết của tôi - Website Forum</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/pages/mypost_page/style.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- TinyMCE Editor -->
	<script src="https://cdn.tiny.cloud/1/8iyu7buuyf605fysc52n8rno4r010c26uqkrglbphgmds6pz/tinymce/6/tinymce.min.js" referrerpolicy="origin"></script>
</head>
<body>
    <%
        String quyen = (String) session.getAttribute("quyen");
        String account = (String) session.getAttribute("account");
        boolean isAdmin = "Admin".equals(quyen);
        
        ArrayList<TheLoai> dsTheLoai = (ArrayList<TheLoai>) request.getAttribute("dsTheLoai");
        ArrayList<BaiViet> dsBaiViet = (ArrayList<BaiViet>) request.getAttribute("dsBaiViet");
        int selectedTheLoai = -1;
        String selectedTenTheLoai = "";
        if (request.getAttribute("selectedTheLoai") != null) selectedTheLoai = (int) request.getAttribute("selectedTheLoai");
        for (TheLoai tl : dsTheLoai) {
            if (tl.getMaTheLoai() == selectedTheLoai) {
                selectedTenTheLoai = tl.getTenTheLoai();
                break;
            }
        }
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/BaiVietCuaToiController">
                            <i class="bi bi-journal-text me-1"></i>Bài viết của tôi
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/BinhLuanCuaToiController">
                            <i class="bi bi-chat-square-dots me-1"></i>Bình luận của tôi
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
            <!-- Center Content - Posts -->
            <div class="col-lg-9 col-md-8 col-12">
                <!-- Page Title -->
                <div class="card shadow-sm mb-3 title-card">
                    <div class="card-body">
                        <h4 class="mb-0">
                            <i class="bi bi-journal-text me-2"></i>
                            Danh sách bài viết của <%= account %>
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

                <!-- Add New Post Button -->
                <div class="mb-3">
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addPostModal">
                        <i class="bi bi-plus-circle me-2"></i>Tạo bài viết mới
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
                                    <option value="danhGiaCao" <%= "danhGiaCao".equals(sortBy) ? "selected" : "" %>>Đánh giá cao nhất</option>
                                    <option value="danhGiaThap" <%= "danhGiaThap".equals(sortBy) ? "selected" : "" %>>Đánh giá thấp nhất</option>
                                    <option value="taoGanNhat" <%= "taoGanNhat".equals(sortBy) ? "selected" : "" %>>Tạo gần nhất</option>
                                    <option value="taoXaNhat" <%= "taoXaNhat".equals(sortBy) ? "selected" : "" %>>Tạo xa nhất</option>
                                    <option value="capNhatGanNhat" <%= "capNhatGanNhat".equals(sortBy) ? "selected" : "" %>>Cập nhật gần nhất</option>
                                    <option value="capNhatXaNhat" <%= "capNhatXaNhat".equals(sortBy) ? "selected" : "" %>>Cập nhật xa nhất</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label small fw-bold">Lọc theo thể loại:</label>
                                <select class="form-select form-select-sm" id="filterTheLoaiSelect" onchange="handleFilterTheLoai(this.value)">
                                    <option value="">-- Tất cả thể loại --</option>
                                    <% 
                                        if (dsTheLoai != null && !dsTheLoai.isEmpty()) {
                                            for (TheLoai tl : dsTheLoai) { 
                                    %>
                                        <option value="<%= tl.getMaTheLoai() %>" <%= tl.getMaTheLoai() == selectedTheLoai ? "selected" : "" %>>
                                            <%= tl.getTenTheLoai() %>
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

                <!-- Posts List -->
                <div class="posts-container">
                    <% if (dsBaiViet == null || dsBaiViet.isEmpty()) { %>
                        <div class="card shadow-sm">
                            <div class="card-body text-center py-5">
                                <i class="bi bi-inbox text-muted" style="font-size: 4rem;"></i>
                                <h5 class="mt-3 text-muted">
                                    <% if (selectedTheLoai != -1) { %>
                                        Bạn chưa có bài viết nào thuộc thể loại "<%= selectedTenTheLoai %>"
                                    <% } else if (searchKey != null && !searchKey.trim().isEmpty()) { %>
                                        Không tìm thấy bài viết phù hợp với từ khóa tìm kiếm
                                    <% } else { %>
                                        Bạn chưa có bài viết nào. Hãy tạo bài viết đầu tiên!
                                    <% } %>
                                </h5>
                            </div>
                        </div>
                    <% } else {
                        for (BaiViet bv : dsBaiViet) {
                            String tenTheLoai = "";
                            if (dsTheLoai != null) {
                                for (TheLoai tl : dsTheLoai) {
                                    if (tl.getMaTheLoai() == bv.getMaTheLoai()) {
                                        tenTheLoai = tl.getTenTheLoai();
                                        break;
                                    }
                                }
                            }
                            ArrayList<BinhLuan> dsBinhLuan = (ArrayList<BinhLuan>) request.getAttribute("dsBinhLuan_" + bv.getMaBaiViet());
                            int soBinhLuan = (dsBinhLuan != null) ? dsBinhLuan.size() : 0;
                    %>
                        <div class="card shadow-sm mb-3 post-card" 
                             data-ma-bai-viet="<%= bv.getMaBaiViet() %>"
                             data-tieu-de="<%= bv.getTieuDe().replace("\"", "&quot;") %>"
                             data-noi-dung="<%= bv.getNoiDung().replace("\"", "&quot;").replace("\n", "&#10;") %>"
                             data-url="<%= bv.getUrl() != null ? bv.getUrl() : "" %>"
                             data-ma-the-loai="<%= bv.getMaTheLoai() %>">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-start mb-2">
                                    <div class="d-flex align-items-center flex-grow-1" onclick="window.location.href='${pageContext.request.contextPath}/ChiTietBaiVietController?id=<%= bv.getMaBaiViet() %>'" style="cursor: pointer;">
                                        <i class="bi bi-person-circle post-avatar"></i>
                                        <div class="ms-2">
                                            <div class="fw-bold"><%= bv.getTaiKhoanTao() %></div>
                                            <small class="text-muted">
                                                <i class="bi bi-clock me-1"></i>
                                                <%= bv.getThoiDiemCapNhat() != null ? sdf.format(bv.getThoiDiemCapNhat()) : sdf.format(bv.getThoiDiemTao()) %>
                                            </small>
                                        </div>
                                    </div>
                                    <div class="dropdown">
                                        <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                                            <i class="bi bi-three-dots-vertical"></i>
                                        </button>
                                        <ul class="dropdown-menu dropdown-menu-end">
                                            <li><a class="dropdown-item" href="#" onclick="editPost(<%= bv.getMaBaiViet() %>); return false;">
                                                <i class="bi bi-pencil me-2"></i>Sửa
                                            </a></li>
                                            <li><hr class="dropdown-divider"></li>
                                            <li><a class="dropdown-item text-danger" href="#" onclick="deletePost(<%= bv.getMaBaiViet() %>, '<%= bv.getTieuDe().replace("'", "\\'") %>'); return false;">
                                                <i class="bi bi-trash me-2"></i>Xóa
                                            </a></li>
                                        </ul>
                                    </div>
                                </div>
                                <div onclick="window.location.href='${pageContext.request.contextPath}/ChiTietBaiVietController?id=<%= bv.getMaBaiViet() %>'" style="cursor: pointer;">
                                    <div class="mb-2">
                                        <span class="badge bg-primary"><i class="bi bi-tag me-1"></i><%= tenTheLoai %></span>
                                        <% if (!"Active".equals(bv.getTrangThai())) { %>
                                            <span class="badge bg-danger ms-1"><%= bv.getTrangThai() %></span>
                                        <% } %>
                                    </div>
                                    <h5 class="post-title"><%= bv.getTieuDe() %></h5>
                                    <p class="post-content"><%= bv.getNoiDung().length() > 200 ? bv.getNoiDung().substring(0, 200) + "..." : bv.getNoiDung() %></p>
                                    <% if (bv.getUrl() != null && !bv.getUrl().trim().isEmpty()) { %>
									    <div class="post-media mb-3">
									        <% 
									        String contextPath = request.getContextPath();
									        String fullUrl = contextPath + "/" + bv.getUrl();
									        
									        if (bv.getUrl().matches(".*\\.(jpg|jpeg|png|gif|webp)$")) { %>
									            <img src="<%= fullUrl %>" class="img-fluid rounded" alt="Post image">
									        <% } else if (bv.getUrl().matches(".*\\.(mp4|webm|ogg)$")) { %>
									            <video controls class="w-100 rounded">
									                <source src="<%= fullUrl %>" type="video/mp4">
									            </video>
									        <% } %>
									    </div>
									<% } %>
                                    <div class="d-flex justify-content-between align-items-center pt-2 border-top">
                                        <div class="text-muted">
                                            <i class="bi bi-chat-fill me-1"></i>
                                            <span><%= soBinhLuan %> bình luận</span>
                                        </div>
                                        <div class="rating-display">
                                            <% 
                                                double danhGia = (bv.getDanhGia() != null) ? bv.getDanhGia().doubleValue() : 0;
                                                for (int i = 1; i <= 5; i++) {
                                                    if (i <= danhGia) { %>
                                                        <i class="bi bi-star-fill text-warning"></i>
                                            <%      } else { %>
                                                        <i class="bi bi-star text-warning"></i>
                                            <%      }
                                                }
                                            %>
                                            <span class="ms-1 text-muted"><%= String.format("%.1f", danhGia) %></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    <% }
                    } %>
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
                                (Tổng <%= totalItems %> bài viết)
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
					    <form action="${pageContext.request.contextPath}/BaiVietCuaToiController" method="get">
					        <div class="search-box">
					            <input type="text" class="form-control" name="search" id="searchInput"
					                   placeholder="Tìm kiếm bài viết..." 
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

    <!-- Add Post Modal - UPDATED -->
	<div class="modal fade" id="addPostModal" tabindex="-1">
	    <div class="modal-dialog modal-lg modal-dialog-scrollable">
	        <div class="modal-content">
	            <div class="modal-header bg-gradient text-white">
	                <h5 class="modal-title"><i class="bi bi-plus-circle me-2"></i>Tạo bài viết mới</h5>
	                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
	            </div>
	            <form action="${pageContext.request.contextPath}/XuLyBaiVietCuaToiController" method="post" id="addPostForm">
	                <input type="hidden" name="action" value="create">
	                <input type="hidden" id="addUrlHidden" name="url" value="">
	                <div class="modal-body">
	                    <div class="mb-3">
	                        <label class="form-label">Tiêu đề <span class="text-danger">*</span></label>
	                        <input type="text" class="form-control" name="tieuDe" id="addTieuDe" required maxlength="255">
	                    </div>
	                    <div class="mb-3">
	                        <label class="form-label">Thể loại <span class="text-danger">*</span></label>
	                        <select class="form-select" name="maTheLoai" id="addMaTheLoai" required>
	                            <option value="">-- Chọn thể loại --</option>
	                            <% if (dsTheLoai != null) {
	                                for (TheLoai tl : dsTheLoai) { %>
	                                    <option value="<%= tl.getMaTheLoai() %>"><%= tl.getTenTheLoai() %></option>
	                            <% }
	                            } %>
	                        </select>
	                    </div>
	                    <div class="mb-3">
						    <label class="form-label">Nội dung <span class="text-danger">*</span></label>
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
	                <div class="modal-footer">
	                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
	                    <button type="submit" class="btn btn-primary"><i class="bi bi-check-circle me-2"></i>Tạo bài viết</button>
	                </div>
	            </form>
	        </div>
	    </div>
	</div>
	
	<!-- Edit Post Modal - UPDATED -->
	<div class="modal fade" id="editPostModal" tabindex="-1">
	    <div class="modal-dialog modal-lg modal-dialog-scrollable">
	        <div class="modal-content">
	            <div class="modal-header bg-gradient text-white">
	                <h5 class="modal-title"><i class="bi bi-pencil me-2"></i>Chỉnh sửa bài viết</h5>
	                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
	            </div>
	            <form action="${pageContext.request.contextPath}/XuLyBaiVietCuaToiController" method="post" id="editPostForm">
	                <input type="hidden" name="action" value="update">
	                <input type="hidden" name="maBaiViet" id="editMaBaiViet">
	                <input type="hidden" id="editUrlHidden" name="url" value="">
	                <input type="hidden" id="editKeepOldFile" name="keepOldFile" value="false">
	                <div class="modal-body">
	                    <div class="mb-3">
	                        <label class="form-label">Tiêu đề <span class="text-danger">*</span></label>
	                        <input type="text" class="form-control" name="tieuDe" id="editTieuDe" required maxlength="255">
	                    </div>
	                    <div class="mb-3">
	                        <label class="form-label">Thể loại <span class="text-danger">*</span></label>
	                        <select class="form-select" name="maTheLoai" id="editMaTheLoai" required>
	                            <option value="">-- Chọn thể loại --</option>
	                            <% if (dsTheLoai != null) {
	                                for (TheLoai tl : dsTheLoai) { %>
	                                    <option value="<%= tl.getMaTheLoai() %>"><%= tl.getTenTheLoai() %></option>
	                            <% }
	                            } %>
	                        </select>
	                    </div>
	                    <div class="mb-3">
						    <label class="form-label">Nội dung <span class="text-danger">*</span></label>
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
	                <div class="modal-footer">
	                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
	                    <button type="submit" class="btn btn-primary"><i class="bi bi-check-circle me-2"></i>Cập nhật</button>
	                </div>
	            </form>
	        </div>
	    </div>
	</div>

    <%!
        private String buildPaginationUrl(HttpServletRequest request, int page) {
            String contextPath = request.getContextPath();
            String theloai = request.getParameter("theloai");
            String sort = request.getParameter("sort");
            String search = request.getParameter("search");
            
            StringBuilder url = new StringBuilder(contextPath + "/BaiVietCuaToiController?page=" + page);
            
            if (theloai != null && !theloai.trim().isEmpty()) {
                try {
                    url.append("&theloai=").append(java.net.URLEncoder.encode(theloai, "UTF-8"));
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

    <script src="${pageContext.request.contextPath}/pages/mypost_page/script.js"></script>
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
