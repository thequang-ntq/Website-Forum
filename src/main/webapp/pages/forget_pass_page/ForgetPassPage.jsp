<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Website Forum</title>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/pages/forget_pass_page/style.css">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="${pageContext.request.contextPath}/pages/forget_pass_page/script.js"></script>
</head>
<body>
	<div class="forget-pass-container">
		<div class="forget-pass-card">
			<!-- Logo/Brand Section -->
			<div class="text-center mb-4">
				<div class="brand-logo">
					<i class="bi bi-shield-lock-fill"></i>
				</div>
				<h2 class="brand-title">Quên Mật Khẩu</h2>
				<p class="text-muted">Đặt lại mật khẩu của bạn</p>
			</div>

			<!-- Success QuenMK -->
			<% String success = (String) session.getAttribute("successQuenMatKhau"); %>
			<% if(success != null && !success.trim().isEmpty()) { %>
				<script>
					setTimeout(function() {
						window.location.href = '${pageContext.request.contextPath}/DangNhapController';
					}, 0);
				</script>
			<% } %>

			<!-- Error Message -->
			<% String error = (String) session.getAttribute("errorQuenMatKhau"); %>
			<% if(error != null && !error.trim().isEmpty()) { %>
				<div class="alert alert-danger alert-dismissible fade show" role="alert">
					<i class="bi bi-exclamation-circle-fill me-2"></i>
					<%= error %>
				</div>
			<% 		session.removeAttribute("errorQuenMatKhau");
				} 
			%>

			<!-- Forget Password Form -->
			<form action="${pageContext.request.contextPath}/QuenMatKhauController" method="post" id="forgetPassForm" novalidate>
				<!-- Username Field -->
				<div class="mb-3">
					<label for="tenDangNhap" class="form-label">
						<i class="bi bi-person-fill me-1"></i>Tên đăng nhập
					</label>
					<div class="input-group">
						<span class="input-group-text">
							<i class="bi bi-person"></i>
						</span>
						<input type="text" 
							   class="form-control" 
							   id="tenDangNhap" 
							   name="tenDangNhapForget" 
							   placeholder="Nhập tên đăng nhập"
							   value="<%= session.getAttribute("tenDangNhapForget") != null ? session.getAttribute("tenDangNhapForget") : "" %>"
							   required>
					</div>
					<% String errorTenDangNhap = (String) session.getAttribute("errorTenDangNhap"); %>
					<% if(errorTenDangNhap != null && !errorTenDangNhap.trim().isEmpty()) { %>
						<div class="error-message">
							<i class="bi bi-exclamation-circle me-1"></i><%= errorTenDangNhap %>
						</div>
					<% 
							session.removeAttribute("errorTenDangNhap");
						}
					%>
				</div>

				<!-- New Password Field -->
				<div class="mb-3">
					<label for="matKhauMoi" class="form-label">
						<i class="bi bi-lock-fill me-1"></i>Mật khẩu mới
					</label>
					<div class="input-group">
						<span class="input-group-text">
							<i class="bi bi-lock"></i>
						</span>
						<input type="password" 
							   class="form-control" 
							   id="matKhauMoi" 
							   name="matKhauMoi" 
							   placeholder="Nhập mật khẩu mới"
							   required>
						<button class="btn btn-outline-secondary" type="button" id="togglePassword1">
							<i class="bi bi-eye" id="toggleIcon1"></i>
						</button>
					</div>
					<% String errorMatKhauMoi = (String) session.getAttribute("errorMatKhauMoi"); %>
					<% if(errorMatKhauMoi != null && !errorMatKhauMoi.trim().isEmpty()) { %>
						<div class="error-message">
							<i class="bi bi-exclamation-circle me-1"></i><%= errorMatKhauMoi %>
						</div>
					<% 
							session.removeAttribute("errorMatKhauMoi");
						} 
					%>
				</div>

				<!-- Confirm New Password Field -->
				<div class="mb-3">
					<label for="nhapLaiMatKhauMoi" class="form-label">
						<i class="bi bi-lock-fill me-1"></i>Nhập lại mật khẩu mới
					</label>
					<div class="input-group">
						<span class="input-group-text">
							<i class="bi bi-shield-check"></i>
						</span>
						<input type="password" 
							   class="form-control" 
							   id="nhapLaiMatKhauMoi" 
							   name="nhapLaiMatKhauMoi" 
							   placeholder="Nhập lại mật khẩu mới"
							   required>
						<button class="btn btn-outline-secondary" type="button" id="togglePassword2">
							<i class="bi bi-eye" id="toggleIcon2"></i>
						</button>
					</div>
					<% String errorNhapLaiMatKhauMoi = (String) session.getAttribute("errorNhapLaiMatKhauMoi"); %>
					<% if(errorNhapLaiMatKhauMoi != null && !errorNhapLaiMatKhauMoi.trim().isEmpty()) { %>
						<div class="error-message">
							<i class="bi bi-exclamation-circle me-1"></i><%= errorNhapLaiMatKhauMoi %>
						</div>
					<% 
							session.removeAttribute("errorNhapLaiMatKhauMoi");
						} 
					%>
				</div>

				<!-- Reset Password Button -->
				<button type="submit" name="btnForgetPass" class="btn btn-primary w-100 btn-reset">
					<i class="bi bi-key-fill me-2"></i>Đổi mật khẩu
				</button>

				<!-- Back to Login Link -->
				<div class="text-center mt-4">
					<p class="mb-0">
						<a href="${pageContext.request.contextPath}/DangNhapController" class="text-decoration-none fw-bold back-link">
							<i class="bi bi-arrow-left me-1"></i>Quay lại đăng nhập
						</a>
					</p>
				</div>
			</form>
		</div>

		<!-- Footer -->
		<div class="text-center mt-4 footer-text">
			<p class="mb-0">&copy; 2025 Website Forum. All rights reserved.</p>
		</div>
	</div>

</body>
</html>