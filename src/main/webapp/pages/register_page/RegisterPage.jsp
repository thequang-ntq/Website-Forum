<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Website Forum</title>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/pages/register_page/style.css">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="${pageContext.request.contextPath}/pages/register_page/script.js"></script>
</head>
<body>
	<div class="register-container">
		<div class="register-card">
			<!-- Logo/Brand Section -->
			<div class="text-center mb-4">
				<div class="brand-logo">
					<i class="bi bi-person-plus-fill"></i>
				</div>
				<h2 class="brand-title">Đăng ký tài khoản</h2>
				<p class="text-muted">Tạo tài khoản mới để tham gia cộng đồng</p>
			</div>

			<!-- Error Message -->
			<% String error = (String) session.getAttribute("errorDangKy"); %>
			<% if(error != null && !error.trim().isEmpty()) { %>
				<div class="alert alert-danger alert-dismissible fade show" role="alert">
					<i class="bi bi-exclamation-circle-fill me-2"></i>
					<%= error %>
				</div>
			<% 
					session.removeAttribute("errorDangKy");
				} 
			%>

			<!-- Success Message -->
			<% String success = (String) session.getAttribute("successDangKy"); %>
			<% if(success != null && !success.trim().isEmpty()) { %>
				<script>
					setTimeout(function() {
						window.location.href = '${pageContext.request.contextPath}/DangNhapController';
					}, 0);
				</script>
			<% } %>

			<!-- Register Form -->
			<form action="${pageContext.request.contextPath}/DangKyController" method="post" id="registerForm" novalidate>
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
							   name="tenDangNhapReg" 
							   placeholder="Nhập tên đăng nhập"
							   value="<%= session.getAttribute("tenDangNhapReg") != null ? session.getAttribute("tenDangNhapReg") : "" %>"
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

				<!-- Password Field -->
				<div class="mb-3">
					<label for="matKhau" class="form-label">
						<i class="bi bi-lock-fill me-1"></i>Mật khẩu
					</label>
					<div class="input-group">
						<span class="input-group-text">
							<i class="bi bi-lock"></i>
						</span>
						<input type="password" 
							   class="form-control" 
							   id="matKhau" 
							   name="matKhau" 
							   placeholder="Nhập mật khẩu"
							   required>
						<button class="btn btn-outline-secondary" type="button" id="togglePassword">
							<i class="bi bi-eye" id="toggleIcon"></i>
						</button>
					</div>
					<% String errorMatKhau = (String) session.getAttribute("errorMatKhau"); %>
					<% if(errorMatKhau != null && !errorMatKhau.trim().isEmpty()) { %>
						<div class="error-message">
							<i class="bi bi-exclamation-circle me-1"></i><%= errorMatKhau %>
						</div>
					<% 
							session.removeAttribute("errorMatKhau");
						} 
					%>
				</div>

				<!-- Confirm Password Field -->
				<div class="mb-4">
					<label for="nhapLaiMatKhau" class="form-label">
						<i class="bi bi-lock-fill me-1"></i>Nhập lại mật khẩu
					</label>
					<div class="input-group">
						<span class="input-group-text">
							<i class="bi bi-lock"></i>
						</span>
						<input type="password" 
							   class="form-control" 
							   id="nhapLaiMatKhau" 
							   name="nhapLaiMatKhau" 
							   placeholder="Nhập lại mật khẩu"
							   required>
						<button class="btn btn-outline-secondary" type="button" id="toggleConfirmPassword">
							<i class="bi bi-eye" id="toggleConfirmIcon"></i>
						</button>
					</div>
					<% String errorNhapLaiMatKhau = (String) session.getAttribute("errorNhapLaiMatKhau"); %>
					<% if(errorNhapLaiMatKhau != null && !errorNhapLaiMatKhau.trim().isEmpty()) { %>
						<div class="error-message">
							<i class="bi bi-exclamation-circle me-1"></i><%= errorNhapLaiMatKhau %>
						</div>
					<% 
							session.removeAttribute("errorNhapLaiMatKhau");
						} 
					%>
				</div>

				<!-- Register Button -->
				<button type="submit" name="btnRegister" class="btn btn-primary w-100 btn-register">
					<i class="bi bi-person-plus me-2"></i>Đăng ký
				</button>

				<!-- Login Link -->
				<div class="text-center mt-4">
					<p class="mb-0">
						Đã có tài khoản? 
						<a href="${pageContext.request.contextPath}/DangNhapController" class="text-decoration-none fw-bold login-link">
							Đăng nhập
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