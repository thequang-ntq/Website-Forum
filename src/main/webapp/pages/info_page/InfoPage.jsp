<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Modal.TaiKhoan.TaiKhoan" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Thông tin cá nhân - Website Forum</title>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/pages/info_page/style.css">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
	<%
		String quyen = (String) session.getAttribute("quyen");
		String account = (String) session.getAttribute("account");
		boolean isAdmin = "Admin".equals(quyen);
		
		TaiKhoan taiKhoan = (TaiKhoan) request.getAttribute("taiKhoan");
		String message = (String) request.getAttribute("message");
		String messageType = (String) request.getAttribute("messageType");
		
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
						<li><a class="dropdown-item active" href="${pageContext.request.contextPath}/ThongTinCaNhanController">
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
				<i class="bi bi-person-circle me-2"></i>Thông tin cá nhân
			</h1>
		</div>

		<!-- Message Alert -->
		<div id="messageAlert" class="alert-container" style="display: none;">
			<div class="alert alert-dismissible fade" role="alert">
				<i class="bi bi-check-circle-fill me-2"></i>
				<span id="messageText"></span>
			</div>
		</div>

		<!-- Info Card -->
		<div class="row justify-content-center">
			<div class="col-lg-8 col-md-10">
				<div class="card shadow-sm info-card">
					<div class="card-body">
						<% if(taiKhoan == null) { %>
							<div class="empty-state text-center py-5">
								<i class="bi bi-person-x text-muted" style="font-size: 4rem;"></i>
								<h5 class="mt-3 text-muted">Chưa có thông tin cá nhân</h5>
							</div>
						<% } else { %>
							<div class="info-row">
								<div class="info-label">
									<i class="bi bi-person-fill me-2"></i>Tên đăng nhập:
								</div>
								<div class="info-value">
									<%= taiKhoan.getTenDangNhap() %>
								</div>
							</div>

							<div class="info-row">
								<div class="info-label">
									<i class="bi bi-lock-fill me-2"></i>Mật khẩu:
								</div>
								<div class="info-value password-container">
									<input type="password" id="passwordField" class="password-field" 
										   value="<%= taiKhoan.getMatKhau() %>" readonly>
									<button type="button" class="btn-toggle-password" onclick="togglePassword()">
										<i class="bi bi-eye-fill" id="toggleIcon"></i>
									</button>
								</div>
							</div>

							<div class="info-row">
								<div class="info-label">
									<i class="bi bi-shield-fill me-2"></i>Quyền:
								</div>
								<div class="info-value">
									<span class="badge <%= "Admin".equals(taiKhoan.getQuyen()) ? "badge-admin" : "badge-user" %>">
										<%= taiKhoan.getQuyen() %>
									</span>
								</div>
							</div>
							
							<div class="info-row">
							    <div class="info-label">
							        <i class="bi bi-envelope-fill me-2"></i>Email:
							    </div>
							    <div class="info-value d-flex justify-content-between align-items-center">
							        <span>
							            <% if(taiKhoan.getEmail() != null && !taiKhoan.getEmail().trim().isEmpty()) { %>
							                <%= taiKhoan.getEmail() %>
							            <% } else { %>
							                <span class="text-muted">Chưa liên kết</span>
							            <% } %>
							        </span>
							        <% if(taiKhoan.getEmail() != null && !taiKhoan.getEmail().trim().isEmpty()) { %>
							            <button class="btn btn-sm btn-outline-primary" onclick="showChangeEmailModal()">
							                <i class="bi bi-pencil-fill me-1"></i>Đổi Email
							            </button>
							        <% } %>
							    </div>
							</div>

							<div class="info-row">
								<div class="info-label">
									<i class="bi bi-calendar-plus-fill me-2"></i>Thời điểm tạo:
								</div>
								<div class="info-value">
									<%= taiKhoan.getThoiDiemTao() != null ? sdf.format(taiKhoan.getThoiDiemTao()) : "N/A" %>
								</div>
							</div>

							<div class="info-row">
								<div class="info-label">
									<i class="bi bi-calendar-check-fill me-2"></i>Thời điểm cập nhật:
								</div>
								<div class="info-value">
									<%= taiKhoan.getThoiDiemCapNhat() != null ? sdf.format(taiKhoan.getThoiDiemCapNhat()) : "N/A" %>
								</div>
							</div>

							<div class="text-center mt-4">
								<button class="btn btn-change-password me-2" onclick="showChangePasswordModal()">
									<i class="bi bi-key-fill me-2"></i>Đổi mật khẩu
								</button>
								<% if(taiKhoan.getEmail() == null || taiKhoan.getEmail().trim().isEmpty()) { %>
								    <button class="btn btn-success me-2" onclick="showAddEmailModal()">
								        <i class="bi bi-envelope-plus-fill me-2"></i>Thêm Email
								    </button>
								<% } %>
								<button class="btn btn-delete-account" onclick="showDeleteAccountModal()">
									<i class="bi bi-trash-fill me-2"></i>Xóa tài khoản
								</button>
							</div>
						<% } %>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Contact Button -->
	<a href="${pageContext.request.contextPath}/LienHeController" 
	   class="btn-contact-float" 
	   title="Liên hệ">
	    <i class="bi bi-person-lines-fill"></i>
	</a>

	<!-- Footer -->
	<footer class="footer mt-5">
		<div class="container text-center">
			<p class="mb-0">&copy; 2025 Website Forum. All rights reserved.</p>
		</div>
	</footer>

	<!-- Change Password Modal -->
	<div class="modal fade" id="changePasswordModal" tabindex="-1">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">
						<i class="bi bi-key-fill me-2"></i>Đổi mật khẩu
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<form id="changePasswordForm" action="${pageContext.request.contextPath}/ThongTinCaNhanController" method="post">
					<input type="hidden" name="action" value="changePassword">
				
					<div class="modal-body">
						<!-- Error Message -->
						<div id="modalError" class="alert alert-danger" style="display: none;">
							<i class="bi bi-x-circle-fill me-2"></i>
							<span id="modalErrorText"></span>
						</div>

						<!-- Mật khẩu cũ -->
						<div class="mb-3">
							<label for="matKhauCu" class="form-label fw-bold">
								Mật khẩu cũ <span class="text-danger">*</span>
							</label>
							<!-- onkeyup để xác thực kiểm tra khi chỉ cần có thay đổi trong trường nhập -->
							<input type="password" class="form-control" id="matKhauCu" 
								   name="matKhauCu" maxlength="255" 
								   onkeyup="validateChangePasswordForm()" required>
							<div id="matKhauCuError" class="invalid-feedback"></div>
						</div>

						<!-- Mật khẩu mới -->
						<div class="mb-3">
							<label for="matKhauMoi" class="form-label fw-bold">
								Mật khẩu mới <span class="text-danger">*</span>
							</label>
							<input type="password" class="form-control" id="matKhauMoi" 
								   name="matKhauMoi" maxlength="255" 
								   onkeyup="validateChangePasswordForm()" required>
							<div id="matKhauMoiError" class="invalid-feedback"></div>
						</div>

						<!-- Xác nhận mật khẩu mới -->
						<div class="mb-3">
							<label for="xacNhanMatKhauMoi" class="form-label fw-bold">
								Xác nhận mật khẩu mới <span class="text-danger">*</span>
							</label>
							<input type="password" class="form-control" id="xacNhanMatKhauMoi" 
								   name="xacNhanMatKhauMoi" maxlength="255" 
								   onkeyup="validateChangePasswordForm()" required>
							<div id="xacNhanMatKhauMoiError" class="invalid-feedback"></div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
							<i class="bi bi-x-circle me-1"></i>Hủy
						</button>
						<button type="submit" class="btn btn-primary" id="changePasswordSubmitBtn">
							<i class="bi bi-check-circle me-1"></i>Đổi
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	
	<!-- Delete Account Modal, action = deleteAccount -->
	<div class="modal fade" id="deleteAccountModal" tabindex="-1">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header bg-danger text-white">
					<h5 class="modal-title">
						<i class="bi bi-exclamation-triangle-fill me-2"></i>Xác nhận xóa tài khoản
					</h5>
					<button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
				</div>
				<form id="deleteAccountForm" action="${pageContext.request.contextPath}/ThongTinCaNhanController" method="post">
					<!-- Biết được action -->
					<input type="hidden" name="action" value="deleteAccount">
					<div class="modal-body">
						<!-- Warning Alert -->
						<div class="alert alert-danger border-danger">
							<i class="bi bi-exclamation-triangle-fill me-2"></i>
							<strong>Cảnh báo:</strong> Hành động này không thể hoàn tác!<br>
							Tất cả bài viết, bình luận, lượt thích, đánh giá, đoạn chat của bạn sẽ bị xóa vĩnh viễn.
						</div>

						<!-- Error Message -->
						<div id="deleteModalError" class="alert alert-danger" style="display: none;">
							<i class="bi bi-x-circle-fill me-2"></i>
							<span id="deleteModalErrorText"></span>
						</div>

						<!-- Mật khẩu xác nhận -->
						<div class="mb-3">
							<label for="matKhauXacNhan" class="form-label fw-bold">
								Nhập mật khẩu để xác nhận <span class="text-danger">*</span>
							</label>
							<input type="password" class="form-control" id="matKhauXacNhan" 
								   name="matKhauXacNhan" maxlength="255" 
								   onkeyup="validateDeleteAccountForm()" required>
							<div id="matKhauXacNhanError" class="invalid-feedback"></div>
						</div>

						<!-- Xác nhận bằng text -->
						<div class="mb-3">
							<label for="xacNhanXoa" class="form-label fw-bold">
								Nhập "<span class="text-danger">XOA TAI KHOAN</span>" để xác nhận <span class="text-danger">*</span>
							</label>
							<input type="text" class="form-control" id="xacNhanXoa" 
								   name="xacNhanXoa" placeholder="XOA TAI KHOAN"
								   onkeyup="validateDeleteAccountForm()" required>
							<small class="text-muted">Viết hoa, không dấu, có khoảng trắng</small>
							<div id="xacNhanXoaError" class="invalid-feedback"></div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
							<i class="bi bi-x-circle me-1"></i>Hủy
						</button>
						<button type="submit" class="btn btn-danger" id="deleteAccountSubmitBtn" disabled>
							<i class="bi bi-trash-fill me-1"></i>Xóa vĩnh viễn
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	
	<!-- Add Email Modal -->
	<div class="modal fade" id="addEmailModal" tabindex="-1">
	    <div class="modal-dialog modal-dialog-centered">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h5 class="modal-title">
	                    <i class="bi bi-envelope-plus-fill me-2"></i>Thêm Email
	                </h5>
	                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
	            </div>
	            <form id="addEmailForm" action="${pageContext.request.contextPath}/ThongTinCaNhanController" method="post">
	            	<!-- action=addEmail -->
	                <input type="hidden" name="action" value="addEmail">
	                <div class="modal-body">
	                    <div id="addEmailError" class="alert alert-danger" style="display: none;">
	                        <i class="bi bi-x-circle-fill me-2"></i>
	                        <span id="addEmailErrorText"></span>
	                    </div>
	
	                    <div class="mb-3">
	                        <label for="email" class="form-label fw-bold">
	                            Email <span class="text-danger">*</span>
	                        </label>
	                        <input type="email" class="form-control" id="email" 
	                               name="email" maxlength="255" 
	                               placeholder="example@gmail.com"
	                               onkeyup="validateAddEmailForm()" required>
	                        <div id="emailError" class="invalid-feedback"></div>
	                    </div>
	                </div>
	                <div class="modal-footer">
	                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
	                        <i class="bi bi-x-circle me-1"></i>Hủy
	                    </button>
	                    <button type="submit" class="btn btn-success" id="addEmailSubmitBtn" disabled>
	                        <i class="bi bi-check-circle me-1"></i>Thêm Email
	                    </button>
	                </div>
	            </form>
	        </div>
	    </div>
	</div>
	
	<!-- Change Email Modal -->
	<div class="modal fade" id="changeEmailModal" tabindex="-1">
	    <div class="modal-dialog modal-dialog-centered">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h5 class="modal-title">
	                    <i class="bi bi-pencil-fill me-2"></i>Đổi Email
	                </h5>
	                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
	            </div>
	            <form id="changeEmailForm" action="${pageContext.request.contextPath}/ThongTinCaNhanController" method="post">
	            	<!-- action = changeEmail -->
	                <input type="hidden" name="action" value="changeEmail">
	                <div class="modal-body">
	                    <div id="changeEmailError" class="alert alert-danger" style="display: none;">
	                        <i class="bi bi-x-circle-fill me-2"></i>
	                        <span id="changeEmailErrorText"></span>
	                    </div>
	                    
	                    <!-- Email hiện tại -->
	                    <div class="mb-3">
	                        <label class="form-label fw-bold">Email hiện tại</label>
	                        <input type="text" class="form-control" 
	                               value="<%= taiKhoan != null && taiKhoan.getEmail() != null ? taiKhoan.getEmail() : "" %>" 
	                               readonly disabled>
	                    </div>
	
	                    <!-- Email mới -->
	                    <div class="mb-3">
	                        <label for="emailMoi" class="form-label fw-bold">
	                            Email mới <span class="text-danger">*</span>
	                        </label>
	                        <input type="email" class="form-control" id="emailMoi" 
	                               name="emailMoi" maxlength="255" 
	                               placeholder="example@gmail.com"
	                               onkeyup="validateChangeEmailForm()" required>
	                        <div id="emailMoiError" class="invalid-feedback"></div>
	                    </div>
	                    
	                    <!-- Xác nhận mật khẩu -->
	                    <div class="mb-3">
	                        <label for="matKhauXacNhanEmail" class="form-label fw-bold">
	                            Mật khẩu xác nhận <span class="text-danger">*</span>
	                        </label>
	                        <input type="password" class="form-control" id="matKhauXacNhanEmail" 
	                               name="matKhauXacNhan" maxlength="255" 
	                               placeholder="Nhập mật khẩu để xác nhận"
	                               onkeyup="validateChangeEmailForm()" required>
	                        <div id="matKhauXacNhanEmailError" class="invalid-feedback"></div>
	                    </div>
	                </div>
	                <div class="modal-footer">
	                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
	                        <i class="bi bi-x-circle me-1"></i>Hủy
	                    </button>
	                    <button type="submit" class="btn btn-primary" id="changeEmailSubmitBtn" disabled>
	                        <i class="bi bi-check-circle me-1"></i>Đổi Email
	                    </button>
	                </div>
	            </form>
	        </div>
	    </div>
	</div>

	<script src="${pageContext.request.contextPath}/pages/info_page/script.js"></script>
	<script>
		// Message from server
		<% if(message != null && messageType != null) {
			// Có thêm dấu \ ở trước để cho hệ thống biết dấu này được sử dụng
			String escapedMessage = message.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r");
		%>
			document.addEventListener('DOMContentLoaded', function() {
				setTimeout(function() {
					showMessage('<%= escapedMessage %>', '<%= messageType %>');
				}, 100);
			});
		<% } %>
		// Delete success message from URL
		const urlParams = new URLSearchParams(window.location.search);
		if(urlParams.get('deleteSuccess') === 'true') {
			document.addEventListener('DOMContentLoaded', function() {
				setTimeout(function() {
					showMessage('Tài khoản đã được xóa thành công!', 'success');
				}, 100);
			});
		}
	</script>
	
</body>
</html>