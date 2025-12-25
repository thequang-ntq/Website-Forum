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
                    <i class="bi bi-envelope-check-fill"></i>
                </div>
                <h2 class="brand-title">Xác Thực PIN</h2>
                <p class="text-muted">Nhập mã PIN đã gửi về email của bạn</p>
            </div>

            <div class="alert alert-info border-0 mb-3">
                <i class="bi bi-info-circle-fill me-2"></i>
                Mã PIN có hiệu lực trong 5 phút
            </div>

            <% String error = (String) session.getAttribute("errorVerify"); %>
            <% if(error != null && !error.trim().isEmpty()) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-circle-fill me-2"></i>
                    <%= error %>
                </div>
            <% 
                    session.removeAttribute("errorVerify");
                } 
            %>

            <form action="${pageContext.request.contextPath}/QuenMatKhauStepController" method="post">
                <input type="hidden" name="step" value="verifyPIN">
                
                <div class="mb-4">
                    <label for="pin" class="form-label">
                        <i class="bi bi-key-fill me-1"></i>Mã PIN (6 số)
                    </label>
                    <div class="input-group">
                        <span class="input-group-text">
                            <i class="bi bi-shield-lock"></i>
                        </span>
                        <input type="text" class="form-control text-center" id="pin" 
                               name="pin" placeholder="000000" maxlength="6" 
                               pattern="[0-9]{6}" required style="letter-spacing: 8px; font-size: 1.5rem;">
                    </div>
                </div>

                <button type="submit" class="btn btn-primary w-100 btn-reset">
                    <i class="bi bi-check-circle me-2"></i>Xác thực
                </button>
				
				<!-- Quay lại bước 1 -->
                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/QuenMatKhauController" 
                       class="text-decoration-none">
                        <i class="bi bi-arrow-counterclockwise me-1"></i>Gửi lại mã PIN
                    </a>
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