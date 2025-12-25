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
        
        // Danh sách đoạn chat và danh sách tin nhắn trong đoạn chat hiện tại
        ArrayList<Modal.DoanChat.DoanChat> dsDoanChat = (ArrayList<Modal.DoanChat.DoanChat>) request.getAttribute("dsDoanChat");
        ArrayList<Modal.TinNhanChat.TinNhanChat> tinNhanList = (ArrayList<Modal.TinNhanChat.TinNhanChat>) request.getAttribute("tinNhanList");
        
        // Đoạn chat hiện tại
        Long currentDoanChat = (Long) request.getAttribute("currentDoanChat");
            
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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

    <!-- Chat Layout -->
    <div class="chat-layout">
        <!-- Sidebar (Drawer) -->
        <div class="chat-sidebar" id="chatSidebar">
            <div class="sidebar-header">
                <h5 class="mb-0"><i class="bi bi-chat-left-dots me-2"></i>Lịch sử chat</h5>
                <button class="btn-new-chat" onclick="startNewChat()">
                    <i class="bi bi-plus-circle"></i> Chat mới
                </button>
            </div>
            
            <div class="sidebar-content">
                <% if(dsDoanChat != null && !dsDoanChat.isEmpty()) {
                    for(Modal.DoanChat.DoanChat dc : dsDoanChat) {
                        boolean isActive = currentDoanChat != null && currentDoanChat == dc.getMaDoanChat();
                %>
                    <div class="chat-item <%= isActive ? "active" : "" %>" 
                         onclick="loadChat(<%= dc.getMaDoanChat() %>)">
                        <div class="chat-item-content">
                            <div class="chat-item-title"><%= dc.getTieuDe() %></div>
                            <div class="chat-item-time">
                                <%= dc.getThoiDiemCapNhat() != null 
                                    ? sdf.format(dc.getThoiDiemCapNhat()) 
                                    : sdf.format(dc.getThoiDiemTao()) %>
                            </div>
                        </div>
                        <button class="btn-delete-chat" 
                                onclick="event.stopPropagation(); deleteChat(<%= dc.getMaDoanChat() %>)">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                <% } } else { %>
                    <div class="text-center text-muted py-5">
                        <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                        <p class="mt-2">Chưa có lịch sử chat</p>
                    </div>
                <% } %>
            </div>
        </div>

        <!-- Main Chat Area -->
        <div class="chat-main">
            <div class="chat-header">
                <button class="btn-toggle-sidebar" onclick="toggleSidebar()">
                    <i class="bi bi-list"></i>
                </button>
                <div class="d-flex align-items-center">
                    <i class="bi bi-robot me-2"></i>
                    <span>Chat với AI</span>
            	</div>
        	</div>

	        <div class="chat-messages" id="chatMessages">
	            <% if(tinNhanList != null && !tinNhanList.isEmpty()) {
	                for(Modal.TinNhanChat.TinNhanChat tn : tinNhanList) {
	                    String role = tn.getRole();
	                    String content = tn.getNoiDung();
	                    String url = tn.getUrl();
	                    String timestamp = sdf.format(tn.getThoiDiemTao());
	            %>
	                <div class="message <%= role %>">
	                    <% if (url != null && !url.trim().isEmpty()) { %>
	                        <img src="${pageContext.request.contextPath}/<%= url %>" 
	                             class="message-image" alt="Ảnh người dùng gửi">
	                    <% } %>
	                    <% if (content != null && !content.trim().isEmpty()) { %>
	                        <div><%= content.replace("\n", "<br>") %></div>
	                    <% } %>
	                    <small class="message-time"><%= timestamp %></small>
	                </div>
	            <% } } else { %>
	                <div class="welcome-message">
	                    <i class="bi bi-stars"></i>
	                    <h5>Xin chào! Tôi là trợ lý AI</h5>
	                    <p>Hãy hỏi tôi bất cứ điều gì bạn muốn biết!</p>
	                </div>
	            <% } %>
	        </div>
	
	        <div class="chat-input-container">
	            <form id="chatForm" enctype="multipart/form-data">
	                <input type="hidden" id="maDoanChatInput" name="maDoanChat" 
	                       value="<%= currentDoanChat != null ? currentDoanChat : "" %>">
	                
	                <div class="input-group">
	                    <label for="imageInput" class="btn btn-outline-secondary" title="Chọn ảnh">
	                        <i class="bi bi-image"></i>
	                    </label>
	                    <input type="file" id="imageInput" name="image" accept="image/*" style="display: none;">
	                    
	                    <input type="text" 
	                           class="form-control" 
	                           id="messageInput" 
	                           name="message"
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
	</div>
	
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="${pageContext.request.contextPath}/pages/chat_page/script.js"></script>
</body>
</html>