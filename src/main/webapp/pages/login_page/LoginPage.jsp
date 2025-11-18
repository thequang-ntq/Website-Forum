<%@page import="nl.captcha.noise.CurvedLineNoiseProducer"%>
<%@page import="nl.captcha.backgrounds.GradiatedBackgroundProducer"%>
<%@page import="java.awt.Color"%>
<%@page import="java.awt.Font"%>
<%@page import="java.util.Arrays"%>
<%@page import="nl.captcha.text.renderer.DefaultWordRenderer"%>
<%@page import="nl.captcha.Captcha"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Website Forum</title>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/pages/login_page/style.css">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="${pageContext.request.contextPath}/pages/login_page/script.js"></script>
</head>
<body>
	<%
		// Kiểm tra số lần đăng nhập sai
		Integer loginAttempts = (Integer) session.getAttribute("loginAttempts");
		if (loginAttempts == null) {
			loginAttempts = 0;
		}
		boolean showCaptcha = loginAttempts >= 3;
		
		// Tạo captcha nếu cần
		if (showCaptcha) {
			Captcha captcha = new Captcha.Builder(200, 60)
				.addText(new DefaultWordRenderer(
					Arrays.asList(new Color(0, 0, 0)),
					Arrays.asList(new Font("Arial", Font.BOLD, 40))
				))
				.addBackground(new GradiatedBackgroundProducer())
				.addNoise(new CurvedLineNoiseProducer())
				.build();
			session.setAttribute(Captcha.NAME, captcha);
		}
	%>
	<div class="login-container">
		<div class="login-card">
			<!-- Logo/Brand Section -->
			<div class="text-center mb-4">
				<div class="brand-logo">
					<i class="bi bi-chat-dots-fill"></i>
				</div>
				<h2 class="brand-title">Website Forum</h2>
				<p class="text-muted">Đăng nhập để tiếp tục</p>
			</div>
			
			<!-- Hiển thị cảnh báo số lần đăng nhập -->
	        <% if(showCaptcha) { %>
	        <div class="text-center">
	        	<span class="warning-badge">
	        		<i class="bi bi-exclamation-triangle-fill"></i> 
	        		Đã đăng nhập sai <%= loginAttempts %> lần!
	        	</span>
	        </div>
	        <% } %>
			
			<!-- Success DangKy -->
			<% String success = (String) session.getAttribute("successDangKy"); %>
			<% if(success != null && !success.trim().isEmpty()) { %>
					<div class="alert alert-success alert-dismissible fade show" role="alert" id="MyAlert">
						<i class="bi bi-check-circle-fill me-2"></i>
						<%= success %>
					</div>
			<% 		
					session.removeAttribute("successDangKy"); 
				}
			%>
			
			<!-- Success QuenMK -->
			<% String successMK = (String) session.getAttribute("successQuenMatKhau"); %>
			<% if(successMK != null && !successMK.trim().isEmpty()) { %>
				<div class="alert alert-success alert-dismissible fade show" role="alert" id="MyAlert">
					<i class="bi bi-check-circle-fill me-2"></i>
					<%= successMK %>
				</div>
			<% 		
					session.removeAttribute("successQuenMatKhau"); 
				}
			%>

			<!-- Error Message -->
			<% 
				String error = (String) session.getAttribute("errorDangNhap"); 
				if(error != null && !error.trim().isEmpty()) { 
				%>
				    <div class="alert alert-danger alert-dismissible fade show" role="alert" id="MyAlert">
				        <i class="bi bi-exclamation-circle-fill me-2"></i>
				        <%= error %>
				    </div>
				<% 
				    session.removeAttribute("errorDangNhap"); 
				} 
			%>

			<!-- Login Form -->
			<form action="${pageContext.request.contextPath}/DangNhapController" method="post" id="loginForm" novalidate>
				<!-- Username Field -->
				<div class="mb-3">
					<label for="tenDangNhapLogin" class="form-label">
						<i class="bi bi-person-fill me-1"></i>Tên đăng nhập
					</label>
					<div class="input-group">
						<span class="input-group-text">
							<i class="bi bi-person"></i>
						</span>
						<input type="text" 
						       class="form-control" 
						       id="tenDangNhapLogin" 
						       name="tenDangNhapLogin" 
						       placeholder="Nhập tên đăng nhập"
						       value="<%= session.getAttribute("tenDangNhapLogin") != null ? session.getAttribute("tenDangNhapLogin") : "" %>"
						       required>
					</div>
					<% 
						String errorTenDangNhap = (String) session.getAttribute("errorTenDangNhap"); 
						if(errorTenDangNhap != null && !errorTenDangNhap.trim().isEmpty()) { 
						%>
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
					<% 
						String errorMatKhau = (String) session.getAttribute("errorMatKhau"); 
						if(errorMatKhau != null && !errorMatKhau.trim().isEmpty()) { 
						%>
						    <div class="error-message">
						        <i class="bi bi-exclamation-circle me-1"></i><%= errorMatKhau %>
						    </div>
						<% 
						    session.removeAttribute("errorMatKhau");
						} 
					%>
				</div>
					
				<!-- Hiển thị Captcha khi đăng nhập sai >= 3 lần -->
	            <% if(showCaptcha) { %>
		            <div class="captcha-container">
		                <label class="form-label fw-bold text-center d-block mb-2">
		                	<i class="bi bi-shield-lock-fill text-danger"></i> 
		                	Xác thực bảo mật
		                </label>
		                <div class="d-flex align-items-center justify-content-center mb-3">
		                    <img src="<%= request.getContextPath() %>/CaptchaImage.jpg" 
		                         alt="Captcha" 
		                         class="captcha-image"
		                         id="captchaImage"/>
		                    <button type="button" 
		                            class="btn btn-link refresh-captcha ms-2" 
		                            onclick="refreshCaptcha()"
		                            title="Tải lại Captcha">
		                        <i class="bi bi-arrow-clockwise"></i>
		                    </button>
		                </div>
		                <input
		                    name="captcha"
		                    type="text"
		                    class="form-control captcha-input"
		                    required
		                    placeholder="Nhập mã xác thực"
		                    autocomplete="off"
		                    maxlength="6"
		                />
		                <small class="text-muted d-block text-center mt-2">
		                	<i class="bi bi-info-circle"></i> 
		                	Nhập đúng mã trong hình để tiếp tục
		                </small>
		            </div>
	            <% } %>	
					
				<!-- Remember Me & Forgot Password -->
				<div class="d-flex justify-content-between align-items-center mb-4">
					<div class="form-check">
						<input class="form-check-input" type="checkbox" name="ghiNhoDangNhap" id="rememberMe">
						<label class="form-check-label" for="rememberMe">
							Ghi nhớ đăng nhập
						</label>
					</div>
					<a href="${pageContext.request.contextPath}/QuenMatKhauController" class="text-decoration-none forgot-password">Quên mật khẩu?</a>
				</div>

				<!-- Login Button -->
				<button type="submit" name="btnLogin" class="btn btn-primary w-100 btn-login">
					<i class="bi bi-box-arrow-in-right me-2"></i>Đăng nhập
				</button>

				<!-- Register Link -->
				<div class="text-center mt-4">
					<p class="mb-0">
						Chưa có tài khoản? 
						<a href="${pageContext.request.contextPath}/DangKyController" class="text-decoration-none fw-bold register-link">
							Đăng ký ngay
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