<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Website Forum</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/pages/chat_page/style.css">
</head>
<body>
    <%
        String quyen = (String) session.getAttribute("quyen");
        String account = (String) session.getAttribute("account");
        boolean isAdmin = "Admin".equals(quyen);
    %>

    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-gradient sticky-top">
        <div class="container-fluid">
            <a class="navbar-brand d-flex align-items-center" href="${pageContext.request.contextPath}/TrangChuController">
                <i class="bi bi-chat-dots-fill me-2"></i>
                <span class="fw-bold">Forum</span>
            </a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarContent">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/TrangChuController">
                            <i class="bi bi-house-fill me-1"></i> Trang chủ
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/ChatPageController">
                            <i class="bi bi-robot me-1"></i>Chat AI
                        </a>
                    </li>
                </ul>

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

    <!-- Chat Container -->
    <div class="chat-container">
        <div class="chat-header">
            <i class="bi bi-robot me-2"></i>
            <span>Chat với AI</span>
            <button class="btn btn-sm btn-outline-light ms-auto" onclick="clearChat()">
                <i class="bi bi-trash"></i> Xóa lịch sử
            </button>
        </div>

        <div class="chat-messages" id="chatMessages">
		    <%
		        ArrayList<Map<String, Object>> chatHistory = (ArrayList<Map<String, Object>>) request.getAttribute("chatHistory");
		        if (chatHistory != null && !chatHistory.isEmpty()) {
		            for (Map<String, Object> msg : chatHistory) {
		                String role = (String) msg.get("role");
		                String content = (String) msg.get("content");
		                String timestamp = (String) msg.get("timestamp");
		                Boolean hasImage = (Boolean) msg.get("hasImage");
		
		                // Tách text và base64 nếu có
		                String textContent = content;
		                String base64Image = null;
		                if (hasImage != null && hasImage && content.contains("|||IMAGE_BASE64|||")) {
		                    String[] parts = content.split("\\|\\|\\|IMAGE_BASE64\\|\\|\\|", 2);
		                    textContent = parts[0];
		                    base64Image = parts.length > 1 ? parts[1] : null;
		                } else if (hasImage != null && hasImage && !content.contains("|||")) {
		                    // Chỉ có ảnh, không có text
		                    textContent = "";
		                    base64Image = content;
		                }
		    %>
		                <div class="message <%= role %>">
		                    <% if (hasImage != null && hasImage && base64Image != null) { %>
		                        <img src="data:image/jpeg;base64,<%= base64Image %>" class="message-image" alt="Ảnh người dùng gửi">
		                    <% } %>
		                    <% if (textContent != null && !textContent.trim().isEmpty()) { %>
		                        <div><%= textContent.replace("\n", "<br>") %></div>
		                    <% } else if (hasImage != null && hasImage) { %>
		                        <div><em>[Đã gửi một ảnh]</em></div>
		                    <% } %>
		                    <small class="message-time"><%= timestamp %></small>
		                </div>
		    <%
		            }
		        } else {
		    %>
		        <div class="welcome-message">
		            <i class="bi bi-stars"></i>
		            <h5>Xin chào! Tôi là trợ lý AI</h5>
		            <p>Hãy hỏi tôi bất cứ điều gì bạn muốn biết!</p>
		        </div>
		    <% } %>
		</div>

        <div class="chat-input-container">
            <form id="chatForm" enctype="multipart/form-data">
                <div class="input-group">
                    <label for="imageInput" class="btn btn-outline-secondary" title="Chọn ảnh">
                        <i class="bi bi-image"></i>
                    </label>
                    <input type="file" id="imageInput" accept=".png,.jpg,.jpeg" style="display: none;">
                    
                    <input type="text" 
                           class="form-control" 
                           id="messageInput" 
                           placeholder="Nhập tin nhắn..."
                           autocomplete="off">
                    
                    <button class="btn btn-primary" type="submit" id="sendBtn">
                        <i class="bi bi-send-fill"></i>
                    </button>
                </div>
                <div id="imagePreview" class="image-preview mt-2" style="display: none;">
                    <img id="previewImg" src="" alt="Preview">
                    <button type="button" class="btn-remove-image" onclick="removeImage()">
                        <i class="bi bi-x"></i>
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/pages/chat_page/script.js"></script>
</body>
</html>