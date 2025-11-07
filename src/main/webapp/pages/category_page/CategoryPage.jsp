<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="Modal.TheLoai.TheLoai" %>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Website Forum</title>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/pages/category_page/style.css">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
	<%
		String quyen = (String) session.getAttribute("quyen");
		String account = (String) session.getAttribute("account");
		boolean isAdmin = "Admin".equals(quyen);
		
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
						<a class="nav-link active" href="${pageContext.request.contextPath}/TheLoaiController">
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
				<i class="bi bi-tags-fill me-2"></i>Danh sách Thể loại
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
						   placeholder="Tìm kiếm theo mã hoặc tên thể loại..." 
						   value="<%= searchKey != null ? searchKey : "" %>"
						   onkeyup="handleSearch()">
					<i class="bi bi-search search-icon"></i>
				</div>
			</div>
		</div>

		<!-- Category Table or Empty State -->
		<div class="card shadow-sm table-card">
			<div class="card-body">
				<% if(dsTheLoai == null || dsTheLoai.isEmpty()) { %>
					<div class="empty-state text-center py-5">
						<i class="bi bi-inbox text-muted" style="font-size: 4rem;"></i>
						<h5 class="mt-3 text-muted">
							<% if(searchKey != null && !searchKey.trim().isEmpty()) { %>
								Không tìm thấy thể loại nào phù hợp
							<% } else { %>
								Chưa có thể loại nào
							<% } %>
						</h5>
					</div>
				<% } else { %>
					<div class="table-responsive">
						<table class="table table-hover align-middle">
							<thead class="table-header">
								<tr>
									<th width="15%" class="text-center">Số thứ tự</th>
									<th width="60%">Tên thể loại</th>
									<th width="25%" class="text-center">Thao tác</th>
								</tr>
							</thead>
							<tbody>
								<% 
									int stt = startIndex + 1;
									for(TheLoai tl : dsTheLoai) { 
								%>
									<tr class="table-row">
										<td class="text-center fw-bold"><%= stt++ %></td>
										<td><%= tl.getTenTheLoai() %></td>
										<td class="text-center">
											<button class="btn btn-sm btn-edit me-2" 
													onclick="showEditModal(<%= tl.getMaTheLoai() %>, '<%= tl.getTenTheLoai().replace("'", "\\'") %>')">
												<i class="bi bi-pencil-fill me-1"></i>Sửa
											</button>
											<button class="btn btn-sm btn-delete" 
													onclick="showDeleteModal(<%= tl.getMaTheLoai() %>, '<%= tl.getTenTheLoai().replace("'", "\\'") %>')">
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
								<!-- Previous Button -->
								<li class="page-item <%= currentPage == 1 ? "disabled" : "" %>">
									<a class="page-link" href="<%= currentPage > 1 ? buildPaginationUrl(request, currentPage - 1) : "#" %>" aria-label="Previous">
										<span aria-hidden="true">&laquo;</span>
									</a>
								</li>
								
								<%
									// Hiển thị các số trang
									int startPage = Math.max(1, currentPage - 2);
									int endPage = Math.min(totalPages, currentPage + 2);
									
									// Nếu ở đầu, hiển thị thêm trang sau
									if(currentPage <= 3) {
										endPage = Math.min(5, totalPages);
									}
									
									// Nếu ở cuối, hiển thị thêm trang trước
									if(currentPage >= totalPages - 2) {
										startPage = Math.max(1, totalPages - 4);
									}
									
									// Trang đầu tiên
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
									
									// Các trang ở giữa
									for(int i = startPage; i <= endPage; i++) {
								%>
										<li class="page-item <%= i == currentPage ? "active" : "" %>">
											<a class="page-link" href="<%= buildPaginationUrl(request, i) %>"><%= i %></a>
										</li>
								<%
									}
									
									// Trang cuối cùng
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
								
								<!-- Next Button -->
								<li class="page-item <%= currentPage == totalPages ? "disabled" : "" %>">
									<a class="page-link" href="<%= currentPage < totalPages ? buildPaginationUrl(request, currentPage + 1) : "#" %>" aria-label="Next">
										<span aria-hidden="true">&raquo;</span>
									</a>
								</li>
							</ul>
						</nav>
						
						<!-- Page Info -->
						<div class="text-center text-muted mt-2">
							<small>
								Trang <%= currentPage %> / <%= totalPages %> 
								(Tổng <%= totalItems %> thể loại)
							</small>
						</div>
					<% } %>
				<% } %>
			</div>
		</div>

		<!-- Add Button -->
		<div class="text-center mt-4 mb-5">
			<button class="btn btn-add" onclick="showAddModal()">
				<i class="bi bi-plus-circle-fill me-2"></i>Thêm thể loại
			</button>
		</div>
	</div>

	<%!
		// Helper method để build URL phân trang
		private String buildPaginationUrl(HttpServletRequest request, int page) {
			String contextPath = request.getContextPath();
			String searchKey = request.getParameter("search");
			
			StringBuilder url = new StringBuilder(contextPath + "/TheLoaiController?page=" + page);
			
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

	<!-- Add Modal -->
	<div class="modal fade" id="addModal" tabindex="-1">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">
						<i class="bi bi-plus-circle-fill me-2"></i>Thêm thể loại
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<form id="addForm" action="${pageContext.request.contextPath}/XuLyTheLoaiController" method="post">
					<input type="hidden" name="action" value="create">
					<div class="modal-body">
						<div class="mb-3">
							<label for="addTenTheLoai" class="form-label fw-bold">
								Tên thể loại <span class="text-danger">*</span>
							</label>
							<input type="text" class="form-control" id="addTenTheLoai" 
								   name="tenTheLoai" maxlength="200" 
								   onkeyup="validateAddForm()" required>
							<div id="addError" class="invalid-feedback"></div>
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

	<!-- Edit Modal -->
	<div class="modal fade" id="editModal" tabindex="-1">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">
						<i class="bi bi-pencil-fill me-2"></i>Sửa thể loại
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<form id="editForm" action="${pageContext.request.contextPath}/XuLyTheLoaiController" method="post">
					<input type="hidden" name="action" value="update">
					<input type="hidden" id="editMaTheLoai" name="maTheLoai">
					<div class="modal-body">
						<div class="mb-3">
							<label for="editTenTheLoai" class="form-label fw-bold">
								Tên thể loại <span class="text-danger">*</span>
							</label>
							<input type="text" class="form-control" id="editTenTheLoai" 
								   name="tenTheLoai" maxlength="200" 
								   onkeyup="validateEditForm()" required>
							<div id="editError" class="invalid-feedback"></div>
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
				<form action="${pageContext.request.contextPath}/XuLyTheLoaiController" method="post">
					<input type="hidden" name="action" value="delete">
					<input type="hidden" id="deleteMaTheLoai" name="maTheLoai">
					<div class="modal-body">
						<p class="mb-0">Bạn có chắc chắn muốn xóa thể loại <strong id="deleteTenTheLoai"></strong>?</p>
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

	<script src="${pageContext.request.contextPath}/pages/category_page/script.js"></script>
	<script>
		// Message from server - Must run after script.js loads
		<% if(message != null && messageType != null) { 
			// Escape single quotes and backslashes for JavaScript
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