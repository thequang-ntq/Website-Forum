<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="Modal.TaiKhoan.TaiKhoan" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Website Forum</title>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/pages/account_page/style.css">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
	<%
		String quyen = (String) session.getAttribute("quyen");
		String account = (String) session.getAttribute("account");
		boolean isAdmin = "Admin".equals(quyen);
		
		ArrayList<TaiKhoan> dsTaiKhoan = (ArrayList<TaiKhoan>) request.getAttribute("dsTaiKhoan");
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
						<a class="nav-link active" href="${pageContext.request.contextPath}/TaiKhoanController">
							<i class="bi bi-people-fill me-1"></i>Tài khoản
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
						<a class="nav-link" href="${pageContext.request.contextPath}/BinhLuanCuaToiController">
							<i class="bi bi-chat-square-dots me-1"></i>Bình luận của tôi
						</a>
					</li>
					<li class="nav-item">
					    <a class="nav-link" href="${pageContext.request.contextPath}/ChatPageController">
					        <i class="bi bi-robot me-1"></i>Chat AI
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
				<i class="bi bi-people-fill me-2"></i>Danh sách Tài khoản
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
						   placeholder="Tìm kiếm tài khoản..." 
						   value="<%= searchKey != null ? searchKey : "" %>"
						   onkeyup="handleSearch()">
					<i class="bi bi-search search-icon"></i>
				</div>
			</div>
		</div>

		<!-- Account Table or Empty State -->
		<div class="card shadow-sm table-card">
			<div class="card-body">
				<% if(dsTaiKhoan == null || dsTaiKhoan.isEmpty()) { %>
					<div class="empty-state text-center py-5">
						<i class="bi bi-inbox text-muted" style="font-size: 4rem;"></i>
						<h5 class="mt-3 text-muted">
							<% if(searchKey != null && !searchKey.trim().isEmpty()) { %>
								Không tìm thấy tài khoản nào phù hợp
							<% } else { %>
								Chưa có tài khoản nào
							<% } %>
						</h5>
					</div>
				<% } else { %>
					<div class="table-responsive">
						<table class="table table-hover align-middle">
							<thead class="table-header">
								<tr>
									<th width="15%" class="text-center">Số thứ tự</th>
									<th width="40%">Tên đăng nhập</th>
									<th width="20%" class="text-center">Trạng thái</th>
									<th width="25%" class="text-center">Thao tác</th>
								</tr>
							</thead>
							<tbody>
								<% 
									int stt = startIndex + 1;
									for(TaiKhoan tk : dsTaiKhoan) { 
										String trangThaiVN = "";
										if("Active".equals(tk.getTrangThai())) trangThaiVN = "Hoạt động";
										else if("Deleted".equals(tk.getTrangThai())) trangThaiVN = "Đã xóa";
										else if("Hidden".equals(tk.getTrangThai())) trangThaiVN = "Ẩn";
								%>
									<tr class="table-row" onclick="showDetailModal('<%= tk.getTenDangNhap() %>')">
										<td class="text-center fw-bold"><%= stt++ %></td>
										<td><%= tk.getTenDangNhap() %></td>
										<td class="text-center">
											<span class="badge <%= "Active".equals(tk.getTrangThai()) ? "bg-success" : ("Deleted".equals(tk.getTrangThai()) ? "bg-danger" : "bg-warning") %>">
												<%= trangThaiVN %>
											</span>
										</td>
										<% if(!account.equals(tk.getTenDangNhap())) { %>
											<td class="text-center" onclick="event.stopPropagation()">
												<button class="btn btn-sm btn-edit me-2" 
														onclick="showEditModal('<%= tk.getTenDangNhap().replace("'", "\\'") %>')">
													<i class="bi bi-pencil-fill me-1"></i>Sửa
												</button>
												<button class="btn btn-sm btn-delete" 
														onclick="showDeleteModal('<%= tk.getTenDangNhap().replace("'", "\\'") %>')">
													<i class="bi bi-trash-fill me-1"></i>Xóa
												</button>
											</td>
										<% } %>
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
								(Tổng <%= totalItems %> tài khoản)
							</small>
						</div>
					<% } %>
				<% } %>
			</div>
		</div>

		<!-- Add Button -->
		<div class="text-center mt-4 mb-5">
			<button class="btn btn-add" onclick="showAddModal()">
				<i class="bi bi-plus-circle-fill me-2"></i>Thêm tài khoản
			</button>
		</div>
	</div>

	<%!
		private String buildPaginationUrl(HttpServletRequest request, int page) {
			String contextPath = request.getContextPath();
			String searchKey = request.getParameter("search");
			
			StringBuilder url = new StringBuilder(contextPath + "/TaiKhoanController?page=" + page);
			
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
		<div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title"><i class="bi bi-info-circle-fill me-2"></i>Chi tiết tài khoản</h5>
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

	<!-- Add Modal -->
	<div class="modal fade" id="addModal" tabindex="-1">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">
						<i class="bi bi-plus-circle-fill me-2"></i>Thêm tài khoản
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<form id="addForm" action="${pageContext.request.contextPath}/XuLyTaiKhoanController" method="post">
					<input type="hidden" name="action" value="create">
					<div class="modal-body">
						<div id="addError" class="alert alert-danger" style="display: none;">
							<i class="bi bi-x-circle-fill me-2"></i>
							<span id="addErrorText"></span>
						</div>
						<div class="mb-3">
							<label for="addTenDangNhap" class="form-label fw-bold">
								Tên đăng nhập <span class="text-danger">*</span>
							</label>
							<input type="text" class="form-control" id="addTenDangNhap" 
								   name="tenDangNhap" maxlength="150" 
								   onkeyup="validateAddForm()" required>
							<div id="addTenDangNhapError" class="invalid-feedback"></div>
						</div>

						<div class="mb-3">
							<label for="addMatKhau" class="form-label fw-bold">
								Mật khẩu <span class="text-danger">*</span>
							</label>
							<div class="input-group">
								<input type="password" class="form-control" id="addMatKhau" 
									   name="matKhau" maxlength="255" 
									   onkeyup="validateAddForm()" required>
								<button class="btn btn-outline-secondary" type="button" id="addTogglePassword">
									<i class="bi bi-eye" id="addToggleIcon"></i>
								</button>
							</div>
							<div id="addMatKhauError" class="invalid-feedback"></div>
						</div>

						<div class="mb-3">
							<label for="addQuyen" class="form-label fw-bold">
								Quyền <span class="text-danger">*</span>
							</label>
							<select class="form-select" id="addQuyen" name="quyen" required>
								<option value="User" selected>User</option>
								<option value="Admin">Admin</option>
							</select>
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
						<i class="bi bi-pencil-fill me-2"></i>Sửa tài khoản
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<form id="editForm" action="${pageContext.request.contextPath}/XuLyTaiKhoanController" method="post">
					<input type="hidden" name="action" value="update">
					<input type="hidden" id="editTenDangNhapHidden" name="tenDangNhap">
					<div class="modal-body">
						<div id="editError" class="alert alert-danger" style="display: none;">
							<i class="bi bi-x-circle-fill me-2"></i>
							<span id="editErrorText"></span>
						</div>
						<div class="mb-3">
							<label for="editTenDangNhap" class="form-label fw-bold">
								Tên đăng nhập <span class="text-danger">*</span>
							</label>
							<input type="text" class="form-control" id="editTenDangNhap" 
								   maxlength="150" disabled>
							<small class="text-muted">Tên đăng nhập không thể thay đổi</small>
						</div>

						<div class="mb-3">
							<label for="editMatKhau" class="form-label fw-bold">
								Mật khẩu <span class="text-danger">*</span>
							</label>
							<div class="input-group">
								<input type="password" class="form-control" id="editMatKhau" 
									   name="matKhau" maxlength="255" 
									   onkeyup="validateEditForm()" required>
								<button class="btn btn-outline-secondary" type="button" id="editTogglePassword">
									<i class="bi bi-eye" id="editToggleIcon"></i>
								</button>
							</div>
							<div id="editMatKhauError" class="invalid-feedback"></div>
						</div>

						<div class="mb-3">
							<label for="editQuyen" class="form-label fw-bold">
								Quyền <span class="text-danger">*</span>
							</label>
							<select class="form-select" id="editQuyen" name="quyen" required>
								<option value="User">User</option>
								<option value="Admin">Admin</option>
							</select>
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
				<form action="${pageContext.request.contextPath}/XuLyTaiKhoanController" method="post">
					<input type="hidden" name="action" value="delete">
					<input type="hidden" id="deleteTenDangNhap" name="tenDangNhap">
					<div class="modal-body">
						<p class="mb-2">Bạn có chắc chắn muốn xóa tài khoản <strong id="deleteTenDangNhapText"></strong>?</p>
						<p class="text-danger mb-0">
							<small>
								<i class="bi bi-exclamation-triangle-fill me-1"></i>
								<strong>Cảnh báo:</strong> Nếu xóa thì mọi dữ liệu về bài viết, bình luận, lượt thích và lượt đánh giá của tài khoản <strong id="deleteTenDangNhapText2"></strong> cũng sẽ bị xóa theo.
							</small>
						</p>
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
	<div id="accountData" style="display: none;">
		<%
			if(dsTaiKhoan != null && !dsTaiKhoan.isEmpty()) {
				for(TaiKhoan tk : dsTaiKhoan) {
					String trangThaiVN = "";
					if("Active".equals(tk.getTrangThai())) trangThaiVN = "Hoạt động";
					else if("Deleted".equals(tk.getTrangThai())) trangThaiVN = "Đã xóa";
					else if("Hidden".equals(tk.getTrangThai())) trangThaiVN = "Ẩn";
		%>
			<div class="account-data-item" 
				 data-tendangnhap="<%= tk.getTenDangNhap() %>"
				 data-matkhau="<%= tk.getMatKhau() %>"
				 data-quyen="<%= tk.getQuyen() %>"
				 data-trangthai="<%= tk.getTrangThai() %>"
				 data-trangthaiVN="<%= trangThaiVN %>"
				 data-thoidiemtao="<%= tk.getThoiDiemTao() != null ? sdf.format(tk.getThoiDiemTao()) : "" %>"
				 data-thoidiemcapnhat="<%= tk.getThoiDiemCapNhat() != null ? sdf.format(tk.getThoiDiemCapNhat()) : "" %>">
			</div>
		<%
				}
			}
		%>
	</div>

	<script src="${pageContext.request.contextPath}/pages/account_page/script.js"></script>
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