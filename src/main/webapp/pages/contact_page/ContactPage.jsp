<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Website Forum</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/pages/contact_page/style.css">
</head>
<body>
    <div class="contact-container">
        <div class="contact-card">
            <!-- Header -->
            <div class="text-center mb-4">
                <div class="brand-logo">
                    <i class="bi bi-person-lines-fill"></i>
                </div>
                <h2 class="brand-title">Thông tin liên hệ người tạo</h2>
            </div>

            <!-- Avatar -->
            <div class="text-center mb-4">
                <img src="${pageContext.request.contextPath}/assets/images/avatar.jpg" 
                     alt="Avatar" 
                     class="contact-avatar"
                     onerror="this.src='${pageContext.request.contextPath}/assets/images/default-avatar.png'">
            </div>

            <!-- Contact Information -->
            <div class="info-section">
                <div class="info-row">
                    <div class="info-label">
                        <i class="bi bi-person-badge me-2"></i>Mã sinh viên:
                    </div>
                    <div class="info-value">22T1020362</div>
                </div>

                <div class="info-row">
                    <div class="info-label">
                        <i class="bi bi-person-fill me-2"></i>Họ tên:
                    </div>
                    <div class="info-value">Nguyễn Thế Quang</div>
                </div>

                <div class="info-row">
                    <div class="info-label">
                        <i class="bi bi-bank me-2"></i>Trường:
                    </div>
                    <div class="info-value">Đại học Khoa học Huế</div>
                </div>

                <div class="info-row">
                    <div class="info-label">
                        <i class="bi bi-mortarboard-fill me-2"></i>Chuyên ngành:
                    </div>
                    <div class="info-value">Công nghệ phần mềm</div>
                </div>

                <div class="info-row">
                    <div class="info-label">
                        <i class="bi bi-calendar-event me-2"></i>Khóa:
                    </div>
                    <div class="info-value">K46</div>
                </div>

                <div class="info-row">
                    <div class="info-label">
                        <i class="bi bi-people-fill me-2"></i>Lớp:
                    </div>
                    <div class="info-value">K46F</div>
                </div>

                <div class="info-row">
                    <div class="info-label">
                        <i class="bi bi-telephone-fill me-2"></i>Số điện thoại:
                    </div>
                    <div class="info-value">
                        <a href="tel:0764526367" class="text-decoration-none">0764526367</a>
                    </div>
                </div>

                <div class="info-row">
                    <div class="info-label">
                        <i class="bi bi-envelope-fill me-2"></i>Email:
                    </div>
                    <div class="info-value">
                        <a href="mailto:thequang012004@gmail.com" class="text-decoration-none">
                            thequang012004@gmail.com
                        </a>
                    </div>
                </div>
            </div>

            <!-- Back Button -->
            <div class="text-center mt-4">
                <button onclick="history.back()" class="btn btn-back">
                    <i class="bi bi-arrow-left me-2"></i>Quay lại
                </button>
            </div>
        </div>

        <!-- Footer -->
        <div class="text-center mt-4 footer-text">
            <p class="mb-0">&copy; 2025 Website Forum. All rights reserved.</p>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>