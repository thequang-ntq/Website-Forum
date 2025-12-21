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
</head>
<body>
    <div class="forget-pass-container">
        <div class="forget-pass-card">
            <div class="text-center mb-4">
                <div class="brand-logo">
                    <i class="bi bi-check-circle-fill"></i>
                </div>
                <h2 class="brand-title">Xác thực thành công!</h2>
                <p class="text-muted">Nhập mật khẩu mới của bạn</p>
            </div>

            <% String error = (String) session.getAttribute("errorReset"); %>
            <% if(error != null && !error.trim().isEmpty()) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-circle-fill me-2"></i>
                    <%= error %>
                </div>
            <% 
                    session.removeAttribute("errorReset");
                } 
            %>

            <form action="${pageContext.request.contextPath}/QuenMatKhauStepController" method="post">
                <input type="hidden" name="step" value="resetPassword">
                
                <div class="mb-3">
                    <label for="matKhauMoi" class="form-label">
                        <i class="bi bi-lock-fill me-1"></i>Mật khẩu mới
                    </label>
                    <div class="input-group">
                        <span class="input-group-text">
                            <i class="bi bi-lock"></i>
                        </span>
                        <input type="password" class="form-control" id="matKhauMoi" 
                               name="matKhauMoi" placeholder="Nhập mật khẩu mới" required>
                        <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                            <i class="bi bi-eye" id="toggleIcon"></i>
                        </button>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="nhapLaiMatKhauMoi" class="form-label">
                        <i class="bi bi-lock-fill me-1"></i>Nhập lại mật khẩu mới
                    </label>
                    <div class="input-group">
                        <span class="input-group-text">
                            <i class="bi bi-shield-check"></i>
                        </span>
                        <input type="password" class="form-control" id="nhapLaiMatKhauMoi" 
                               name="nhapLaiMatKhauMoi" placeholder="Nhập lại mật khẩu mới" required>
                        <button class="btn btn-outline-secondary" type="button" id="togglePassword2">
                            <i class="bi bi-eye" id="toggleIcon2"></i>
                        </button>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary w-100 btn-reset">
                    <i class="bi bi-key-fill me-2"></i>Đổi mật khẩu
                </button>
            </form>
        </div>

        <div class="text-center mt-4 footer-text">
            <p class="mb-0">&copy; 2025 Website Forum. All rights reserved.</p>
        </div>
    </div>

    <script>
        // Toggle password visibility
        document.getElementById('togglePassword').addEventListener('click', function() {
            const passwordInput = document.getElementById('matKhauMoi');
            const toggleIcon = document.getElementById('toggleIcon');
            if(passwordInput.type === 'password') {
                passwordInput.type = 'text';
                toggleIcon.classList.remove('bi-eye');
                toggleIcon.classList.add('bi-eye-slash');
            } else {
                passwordInput.type = 'password';
                toggleIcon.classList.remove('bi-eye-slash');
                toggleIcon.classList.add('bi-eye');
            }
        });

        document.getElementById('togglePassword2').addEventListener('click', function() {
            const passwordInput = document.getElementById('nhapLaiMatKhauMoi');
            const toggleIcon = document.getElementById('toggleIcon2');
            if(passwordInput.type === 'password') {
                passwordInput.type = 'text';
                toggleIcon.classList.remove('bi-eye');
                toggleIcon.classList.add('bi-eye-slash');
            } else {
                passwordInput.type = 'password';
                toggleIcon.classList.remove('bi-eye-slash');
                toggleIcon.classList.add('bi-eye');
            }
        });
    </script>
</body>
</html>