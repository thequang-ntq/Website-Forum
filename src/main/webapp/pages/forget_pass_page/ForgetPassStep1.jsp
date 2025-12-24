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
</head>
<body>
    <div class="forget-pass-container">
        <div class="forget-pass-card">
            <div class="text-center mb-4">
                <div class="brand-logo">
                    <i class="bi bi-shield-lock-fill"></i>
                </div>
                <h2 class="brand-title">Quên Mật Khẩu</h2>
                <p class="text-muted">Nhập tên đăng nhập để nhận mã PIN</p>
            </div>

            <% String error = (String) session.getAttribute("errorMessage"); %>
            <% if(error != null && !error.trim().isEmpty()) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-circle-fill me-2"></i>
                    <%= error %>
                </div>
            <% 
                    session.removeAttribute("errorMessage");
                } 
            %>

            <form action="${pageContext.request.contextPath}/QuenMatKhauStepController" method="post">
                <input type="hidden" name="step" value="sendPIN">
                
                <div class="mb-4">
                    <label for="tenDangNhap" class="form-label">
                        <i class="bi bi-person-fill me-1"></i>Tên đăng nhập
                    </label>
                    <div class="input-group">
                        <span class="input-group-text">
                            <i class="bi bi-person"></i>
                        </span>
                        <input type="text" class="form-control" id="tenDangNhap" 
                               name="tenDangNhap" placeholder="Nhập tên đăng nhập" required>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary w-100 btn-reset">
                    <i class="bi bi-envelope-fill me-2"></i>Gửi mã PIN
                </button>

                <div class="text-center mt-4">
                    <p class="mb-0">
                        <a href="${pageContext.request.contextPath}/DangNhapController" 
                           class="text-decoration-none fw-bold back-link">
                            <i class="bi bi-arrow-left me-1"></i>Quay lại đăng nhập
                        </a>
                    </p>
                </div>
            </form>
        </div>

        <div class="text-center mt-4 footer-text">
            <p class="mb-0">&copy; 2025 Website Forum. All rights reserved.</p>
        </div>
    </div>
    
   	<!-- Contact Button -->
	<a href="${pageContext.request.contextPath}/LienHeController" 
	   class="btn-contact-float" 
	   title="Liên hệ">
	    <i class="bi bi-person-lines-fill"></i>
	</a>
</body>
</html>