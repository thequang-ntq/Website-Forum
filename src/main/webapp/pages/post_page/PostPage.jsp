<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="Modal.BaiViet.BaiViet" %>
<%@ page import="Modal.TheLoai.TheLoai" %>
<%@ page import="Modal.BinhLuan.BinhLuan" %>
<%@ page import="Modal.BinhLuan.BinhLuanBO" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Website Forum</title>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/pages/post_page/style.css">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
	<%
		String quyen = (String) session.getAttribute("quyen");
		String account = (String) session.getAttribute("account");
		boolean isAdmin = "Admin".equals(quyen);
		
		ArrayList<BaiViet> dsBaiViet = (ArrayList<BaiViet>) request.getAttribute("dsBaiViet");
		ArrayList<TheLoai> dsTheLoai = (ArrayList<TheLoai>) request.getAttribute("dsTheLoai");
		String searchKey = (String) request.getAttribute("searchKey");
		String message = (String) request.getAttribute("message");
		String messageType = (String) request.getAttribute("messageType");
		
		// Phân trang
		Integer currentPage = (Integer) request.getAttribute("currentPage");
		Integer totalPages = (Integer) request.getAttribute("totalPages");
		Integer totalItems = (Integer) request.getAttribute("totalItems");
		Integer startIndex = (Integer) request.getAttribute("startIndex");
		
		if(currentPage == null) currentPage = 1;
		if(totalPages == null) totalPages = 0;
		if(totalItems == null) totalItems = 0;
		if(startIndex == null) startIndex = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		BinhLuanBO blbo = new BinhLuanBO();
	%>

	<!-- Navbar -->
	<nav class="navbar navbar-expand-lg navbar-dark bg-gradient sticky-top">
		<div class="container-fluid">
			<!-- Logo -->
			<a class="navbar-brand d-none d-lg-flex align-items-center" href="${pageContext.request.contextPath}/TrangChuController">
				<i class="bi bi-chat-dots-fill me-2"></i>
				<span class="fw-bold">Forum</span>
			</a>

			<!-- Mobile Toggle -->
			<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
				<span class="navbar-toggler-icon"></span>
			</button>

			<!-- Nav Links -->
			<div class="collapse navbar-collapse" id="navbarContent">
				<ul class="navbar-nav me-auto">
					<li class="nav-item">
						<a class="nav-link" href="${pageContext.request.contextPath}/TrangChuController">
							<i class="bi bi-house-fill me-1"></i>Trang chủ
						</a>
					</li>
					<% if(isAdmin) { %>
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
						<a class="nav-link active" href="${pageContext.request.contextPath}/BaiVietController">
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
						<a class="nav-link" href="${pageContext.request.contextPath}/BinhLuanCuaToiController">
							<i class="bi bi-chat-square-dots me-1"></i>Bình luận của tôi
						</a>
					</li>
				</ul>

				<!-- User Menu -->
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
	<div class="container mt-5">
		<!-- Page Title -->
		<div class="text-center mb-4">
			<h1 class="page-title">
				<i class="bi bi-file-text-fill me-2"></i>Danh sách Bài viết
			</h1>
		</div>

		<!-- Message Alert -->
		<div id="messageAlert" class="alert-container" style="display: none;">
			<div class="alert alert-dismissible fade" role="alert">
				<i class="bi bi-check-circle-fill me-2"></i>
				<span id="messageText"></span>
			</div>
		</div>

		<!-- Search Bar -->
		<div class="row mb-4">
			<div class="col-lg-4 col-md-6 ms-auto">
				<div class="search-box">
					<input type="text" class="form-control" id="searchInput" 
						   placeholder="Tìm kiếm bài viết..." 
						   value="<%= searchKey != null ? searchKey : "" %>"
						   onkeyup="handleSearch()">
					<i class="bi bi-search search-icon"></i>
				</div>
			</div>
		</div>

		<!-- Post Table or Empty State -->
		<div class="card shadow-sm table-card">
			<div class="card-body">
				<% if(dsBaiViet == null || dsBaiViet.isEmpty()) { %>
					<div class="empty-state text-center py-5">
						<i class="bi bi-inbox text-muted" style="font-size: 4rem;"></i>
						<h5 class="mt-3 text-muted">
							<% if(searchKey != null && !searchKey.trim().isEmpty()) { %>
								Không tìm thấy bài viết nào phù hợp
							<% } else { %>
								Chưa có bài viết nào
							<% } %>
						</h5>
					</div>
				<% } else { %>
					<div class="table-responsive">
						<table class="table table-hover align-middle">
							<thead class="table-header">
								<tr>
									<th width="10%" class="text-center">STT</th>
									<th width="50%">Tiêu đề</th>
									<th width="15%" class="text-center">Trạng thái</th>
									<th width="25%" class="text-center">Thao tác</th>
								</tr>
							</thead>
							<tbody>
								<% 
									int stt = startIndex + 1;
									for(BaiViet bv : dsBaiViet) {
										String trangThaiVN = "";
										if("Active".equals(bv.getTrangThai())) trangThaiVN = "Hoạt động";
										else if("Deleted".equals(bv.getTrangThai())) trangThaiVN = "Đã xóa";
										else if("Hidden".equals(bv.getTrangThai())) trangThaiVN = "Ẩn";
										
										String tieuDeDisplay = bv.getTieuDe();
										if(tieuDeDisplay.length() > 80) {
											tieuDeDisplay = tieuDeDisplay.substring(0, 80) + "...";
										}
								%>
									<tr class="table-row" onclick="showDetailModal(<%= bv.getMaBaiViet() %>)">
										<td class="text-center fw-bold"><%= stt++ %></td>
										<td><%= tieuDeDisplay %></td>
										<td class="text-center">
											<span class="badge <%= "Active".equals(bv.getTrangThai()) ? "bg-success" : ("Deleted".equals(bv.getTrangThai()) ? "bg-danger" : "bg-warning") %>">
												<%= trangThaiVN %>
											</span>
										</td>
										<td class="text-center" onclick="event.stopPropagation()">
											<button class="btn btn-sm btn-edit me-2" 
													onclick="showEditModal(<%= bv.getMaBaiViet() %>)">
												<i class="bi bi-pencil-fill me-1"></i>Sửa
											</button>
											<button class="btn btn-sm btn-delete" 
													onclick="showDeleteModal(<%= bv.getMaBaiViet() %>, '<%= tieuDeDisplay.replace("'", "\\'") %>')">
												<i class="bi bi-trash-fill me-1"></i>Xóa
											</button>
										</td>
									</tr>
								<% } %>
							</tbody>
						</table>
					</div>
					
					<!-- Pagination -->
					<% if(totalPages > 1) { %>
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
									
									if(currentPage <= 3) {
										endPage = Math.min(5, totalPages);
									}
									
									if(currentPage >= totalPages - 2) {
										startPage = Math.max(1, totalPages - 4);
									}
									
									if(startPage > 1) {
								%>
										<li class="page-item">
											<a class="page-link" href="<%= buildPaginationUrl(request, 1) %>">1</a>
										</li>
										<% if(startPage > 2) { %>
											<li class="page-item disabled">
												<span class="page-link">...</span>
											</li>
										<% } %>
								<%
									}
									
									for(int i = startPage; i <= endPage; i++) {
								%>
										<li class="page-item <%= i == currentPage ? "active" : "" %>">
											<a class="page-link" href="<%= buildPaginationUrl(request, i) %>"><%= i %></a>
										</li>
								<%
									}
									
									if(endPage < totalPages) {
										if(endPage < totalPages - 1) {
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
				<% } %>
			</div>
		</div>

		<!-- Add Button -->
		<div class="text-center mt-4 mb-5">
			<button class="btn btn-add" onclick="showAddModal()">
				<i class="bi bi-plus-circle-fill me-2"></i>Thêm bài viết
			</button>
		</div>
	</div>

	<%!
		private String buildPaginationUrl(HttpServletRequest request, int page) {
			String contextPath = request.getContextPath();
			String searchKey = request.getParameter("search");
			
			StringBuilder url = new StringBuilder(contextPath + "/BaiVietController?page=" + page);
			
			if(searchKey != null && !searchKey.trim().isEmpty()) {
				try {
					url.append("&search=").append(java.net.URLEncoder.encode(searchKey, "UTF-8"));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			return url.toString();
		}
	%>

	<!-- Footer -->
	<footer class="footer mt-5">
		<div class="container text-center">
			<p class="mb-0">&copy; 2025 Website Forum. All rights reserved.</p>
		</div>
	</footer>

	<!-- Detail Modal -->
	<div class="modal fade" id="detailModal" tabindex="-1">
		<div class="modal-dialog modal-lg modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title"><i class="bi bi-info-circle-fill me-2"></i>Chi tiết bài viết</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<div class="modal-body" id="detailContent">
					<div class="text-center py-5">
						<div class="spinner-border text-primary" role="status"></div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- THAY THẾ TOÀN BỘ Add Modal -->
	<div class="modal fade" id="addModal" tabindex="-1">
		<div class="modal-dialog modal-lg modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">
						<i class="bi bi-plus-circle-fill me-2"></i>Thêm bài viết
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<form id="addForm" action="${pageContext.request.contextPath}/XuLyBaiVietController" method="post">
					<input type="hidden" name="action" value="create">
					<input type="hidden" id="addUrlHidden" name="url" value="">
					<div class="modal-body">
						<div id="addError" class="alert alert-danger" style="display: none;">
							<i class="bi bi-x-circle-fill me-2"></i>
							<span id="addErrorText"></span>
						</div>
						
						<div class="mb-3">
							<label for="addTieuDe" class="form-label fw-bold">
								Tiêu đề <span class="text-danger">*</span>
							</label>
							<input type="text" class="form-control" id="addTieuDe" 
								   name="tieuDe" maxlength="255" 
								   onkeyup="validateAddForm()" required>
							<div class="invalid-feedback" id="addTieuDeError"></div>
						</div>
	
						<div class="mb-3">
							<label for="addNoiDung" class="form-label fw-bold">
								Nội dung <span class="text-danger">*</span>
							</label>
							<textarea class="form-control" id="addNoiDung" name="noiDung" 
									  rows="6" onkeyup="validateAddForm()" required></textarea>
							<div class="invalid-feedback" id="addNoiDungError"></div>
						</div>
	
						<!-- Upload File Section -->
						<div class="mb-3">
							<label class="form-label fw-bold">Ảnh/Video</label>
							<div class="upload-container">
								<input type="file" id="addFileInput" accept="image/*,video/*" 
									   style="display: none;">
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
	
						<div class="mb-3">
							<label for="addMaTheLoai" class="form-label fw-bold">
								Thể loại <span class="text-danger">*</span>
							</label>
							<select class="form-select" id="addMaTheLoai" name="maTheLoai" 
									onchange="validateAddForm()" required>
								<option value="">-- Chọn thể loại --</option>
								<% if(dsTheLoai != null && !dsTheLoai.isEmpty()) {
									for(TheLoai tl : dsTheLoai) { %>
										<option value="<%= tl.getMaTheLoai() %>"><%= tl.getTenTheLoai() %></option>
								<% }
								} %>
							</select>
							<div class="invalid-feedback" id="addMaTheLoaiError"></div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
							<i class="bi bi-x-circle me-1"></i>Hủy
						</button>
						<button type="submit" class="btn btn-primary" id="addSubmitBtn">
							<i class="bi bi-check-circle me-1"></i>Thêm
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	
	<!-- THAY THẾ TOÀN BỘ Edit Modal -->
	<div class="modal fade" id="editModal" tabindex="-1">
		<div class="modal-dialog modal-lg modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">
						<i class="bi bi-pencil-fill me-2"></i>Sửa bài viết
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<form id="editForm" action="${pageContext.request.contextPath}/XuLyBaiVietController" method="post">
					<input type="hidden" name="action" value="update">
					<input type="hidden" id="editMaBaiViet" name="maBaiViet">
					<input type="hidden" id="editUrlHidden" name="url" value="">
					<input type="hidden" id="editKeepOldFile" name="keepOldFile" value="false">
					<div class="modal-body">
						<div id="editError" class="alert alert-danger" style="display: none;">
							<i class="bi bi-x-circle-fill me-2"></i>
							<span id="editErrorText"></span>
						</div>
						
						<div class="mb-3">
							<label for="editTieuDe" class="form-label fw-bold">
								Tiêu đề <span class="text-danger">*</span>
							</label>
							<input type="text" class="form-control" id="editTieuDe" 
								   name="tieuDe" maxlength="255" 
								   onkeyup="validateEditForm()" required>
							<div class="invalid-feedback" id="editTieuDeError"></div>
						</div>
	
						<div class="mb-3">
							<label for="editNoiDung" class="form-label fw-bold">
								Nội dung <span class="text-danger">*</span>
							</label>
							<textarea class="form-control" id="editNoiDung" name="noiDung" 
									  rows="6" onkeyup="validateEditForm()" required></textarea>
							<div class="invalid-feedback" id="editNoiDungError"></div>
						</div>
	
						<!-- Upload File Section -->
						<div class="mb-3">
							<label class="form-label fw-bold">Ảnh/Video</label>
							<div class="upload-container">
								<input type="file" id="editFileInput" accept="image/*,video/*" 
									   style="display: none;">
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
	
						<div class="mb-3">
							<label for="editMaTheLoai" class="form-label fw-bold">
								Thể loại <span class="text-danger">*</span>
							</label>
							<select class="form-select" id="editMaTheLoai" name="maTheLoai" 
									onchange="validateEditForm()" required>
								<option value="">-- Chọn thể loại --</option>
								<% if(dsTheLoai != null && !dsTheLoai.isEmpty()) {
									for(TheLoai tl : dsTheLoai) { %>
										<option value="<%= tl.getMaTheLoai() %>"><%= tl.getTenTheLoai() %></option>
								<% }
								} %>
							</select>
							<div class="invalid-feedback" id="editMaTheLoaiError"></div>
						</div>
	
						<div class="mb-3">
							<label for="editDanhGia" class="form-label fw-bold">Đánh giá</label>
							<input type="number" class="form-control" id="editDanhGia" 
								   name="danhGia" min="0" max="5" step="0.1" 
								   placeholder="0.0 - 5.0">
							<small class="text-muted">Để trống nếu chưa đánh giá (từ 0.0 đến 5.0)</small>
						</div>
	
						<div class="mb-3">
							<label for="editTrangThai" class="form-label fw-bold">
								Trạng thái <span class="text-danger">*</span>
							</label>
							<select class="form-select" id="editTrangThai" name="trangThai" required>
								<option value="Active">Hoạt động</option>
								<option value="Hidden">Ẩn</option>
								<option value="Deleted">Đã xóa</option>
							</select>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
							<i class="bi bi-x-circle me-1"></i>Hủy
						</button>
						<button type="submit" class="btn btn-primary" id="editSubmitBtn">
							<i class="bi bi-check-circle me-1"></i>Sửa
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Delete Modal -->
	<div class="modal fade" id="deleteModal" tabindex="-1">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header bg-danger text-white">
					<h5 class="modal-title">
						<i class="bi bi-exclamation-triangle-fill me-2"></i>Xác nhận xóa
					</h5>
					<button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
				</div>
				<form action="${pageContext.request.contextPath}/XuLyBaiVietController" method="post">
					<input type="hidden" name="action" value="delete">
					<input type="hidden" id="deleteMaBaiViet" name="maBaiViet">
					<div class="modal-body">
						<p class="mb-0">Bạn có chắc chắn muốn xóa bài viết <strong id="deleteTieuDe"></strong>?</p>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
							<i class="bi bi-x-circle me-1"></i>Không
						</button>
						<button type="submit" class="btn btn-danger">
							<i class="bi bi-check-circle me-1"></i>Có
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Hidden data for detail modal -->
	<div id="postData" style="display: none;">
		<%
			if(dsBaiViet != null && !dsBaiViet.isEmpty()) {
				for(BaiViet bv : dsBaiViet) {
					String tenTheLoai = "";
					if(dsTheLoai != null) {
						for(TheLoai tl : dsTheLoai) {
							if(tl.getMaTheLoai() == bv.getMaTheLoai()) {
								tenTheLoai = tl.getTenTheLoai();
								break;
							}
						}
					}
					
					ArrayList<BinhLuan> dsBinhLuan = blbo.filterDB_maBaiViet(bv.getMaBaiViet(), bv.getTrangThai());
					int soBinhLuan = (dsBinhLuan != null) ? dsBinhLuan.size() : 0;
					
					String trangThaiVN = "";
					if("Active".equals(bv.getTrangThai())) trangThaiVN = "Hoạt động";
					else if("Deleted".equals(bv.getTrangThai())) trangThaiVN = "Đã xóa";
					else if("Hidden".equals(bv.getTrangThai())) trangThaiVN = "Ẩn";
		%>
			<div class="post-data-item" data-id="<%= bv.getMaBaiViet() %>"
				 data-tieude="<%= bv.getTieuDe() %>"
				 data-noidung="<%= bv.getNoiDung().replace("\"", "&quot;") %>"
				 data-url="<%= bv.getUrl() != null ? bv.getUrl() : "" %>"
				 data-taikhoantao="<%= bv.getTaiKhoanTao() %>"
				 data-matheloai="<%= bv.getMaTheLoai() %>"
				 data-tentheloai="<%= tenTheLoai %>"
				 data-danhgia="<%= bv.getDanhGia() != null ? bv.getDanhGia() : "" %>"
				 data-trangthai="<%= bv.getTrangThai() %>"
				 data-trangthaiVN="<%= trangThaiVN %>"
				 data-sobinhluan="<%= soBinhLuan %>"
				 data-thoidiemtao="<%= bv.getThoiDiemTao() != null ? sdf.format(bv.getThoiDiemTao()) : "" %>"
				 data-thoidiemcapnhat="<%= bv.getThoiDiemCapNhat() != null ? sdf.format(bv.getThoiDiemCapNhat()) : "" %>">
			</div>
		<%
				}
			}
		%>
	</div>

	<script src="${pageContext.request.contextPath}/pages/post_page/script.js"></script>
	<script>
		<% if(message != null && messageType != null) { 
			String escapedMessage = message.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r");
		%>
			document.addEventListener('DOMContentLoaded', function() {
				setTimeout(function() {
					showMessage('<%= escapedMessage %>', '<%= messageType %>');
				}, 100);
			});
		<% } %>
	</script>
</body>
</html>