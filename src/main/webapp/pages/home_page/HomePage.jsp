<%@page import="Modal.TaiKhoan.TaiKhoan"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="Modal.BaiViet.BaiViet" %>
<%@ page import="Modal.TheLoai.TheLoai" %>
<%@ page import="Modal.BinhLuan.BinhLuan" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Website Forum</title>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/pages/home_page/style.css">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
	<%
		String quyen = (String) session.getAttribute("quyen");
		String account = (String) session.getAttribute("account");
		boolean isAdmin = "Admin".equals(quyen);
		
		ArrayList<TheLoai> dsTheLoai = (ArrayList<TheLoai>) request.getAttribute("dsTheLoai");
		ArrayList<BaiViet> dsBaiViet = (ArrayList<BaiViet>) request.getAttribute("dsBaiViet");
		int selectedTheLoai = -1;
		String selectedTenTheLoai = "";
		if(request.getAttribute("selectedTheLoai") != null) selectedTheLoai = (int) request.getAttribute("selectedTheLoai");
		for(TheLoai tl: dsTheLoai) {
			if(tl.getMaTheLoai() == selectedTheLoai) {
				selectedTenTheLoai = tl.getTenTheLoai();
				break;
			}
		}
		String sortBy = (String) request.getAttribute("sortBy");
		String searchKey = (String) request.getAttribute("searchKey");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		Integer currentPage = (Integer) request.getAttribute("currentPage");
		Integer totalPages = (Integer) request.getAttribute("totalPages");
		Integer totalItems = (Integer) request.getAttribute("totalItems");
		Integer startIndex = (Integer) request.getAttribute("startIndex");
		
		if(currentPage == null) currentPage = 1;
		if(totalPages == null) totalPages = 0;
		if(totalItems == null) totalItems = 0;
		if(startIndex == null) startIndex = 0;
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
						<a class="nav-link active" href="${pageContext.request.contextPath}/TrangChuController">
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

	<!-- Main Content -->
	<div class="container-fluid mt-4">
		<div class="row g-3">
			<!-- Left Sidebar - Categories -->
			<div class="col-lg-3 col-md-4 d-none d-md-block">
				<div class="card shadow-sm category-card">
					<div class="card-header bg-gradient text-white">
						<h5 class="mb-0"><i class="bi bi-tags-fill me-2"></i>Thể loại</h5>
					</div>
					<div class="card-body p-0">
						<div class="list-group list-group-flush category-list">
							<a href="${pageContext.request.contextPath}/TrangChuController" 
							   class="list-group-item list-group-item-action <%= request.getAttribute("selectedTheLoai") == null ? "active" : "" %>">
								<i class="bi bi-grid-fill me-2"></i>Tất cả
							</a>
							<% if(dsTheLoai != null && !dsTheLoai.isEmpty()) {
								for(TheLoai tl : dsTheLoai) { %>
									<a href="${pageContext.request.contextPath}/TrangChuController?theloai=<%= tl.getMaTheLoai() %>" 
									   class="list-group-item list-group-item-action <%= tl.getMaTheLoai() == (selectedTheLoai) ? "active" : "" %>">
										<i class="bi bi-tag-fill me-2"></i><%= tl.getTenTheLoai() %>
									</a>
							<% }
							} %>
						</div>
					</div>
				</div>
			</div>

			<!-- Center Content - Posts -->
			<div class="col-lg-6 col-md-8 col-12">
				<!-- Mobile Category Selector -->
				<div class="card shadow-sm mb-3 d-md-none">
					<div class="card-body">
						<select class="form-select" id="mobileCategorySelect" onchange="window.location.href=this.value">
							<option value="${pageContext.request.contextPath}/TrangChuController" <%= request.getAttribute("selectedTheLoai") == null ? "selected" : "" %>>
								Tất cả thể loại
							</option>
							<% if(dsTheLoai != null && !dsTheLoai.isEmpty()) {
								for(TheLoai tl : dsTheLoai) { %>
									<option value="${pageContext.request.contextPath}/TrangChuController?theloai=<%= tl.getMaTheLoai() %>" 
											<%= tl.getMaTheLoai() == (selectedTheLoai) ? "selected" : "" %>>
										<%= tl.getTenTheLoai() %>
									</option>
							<% }
							} %>
						</select>
					</div>
				</div>

				<!-- Sort & Filter Controls -->
				<div class="card shadow-sm mb-3 controls-card">
					<div class="card-body">
						<div class="row g-2">
							<div class="col-md-6">
								<label class="form-label small fw-bold">Sắp xếp:</label>
								<select class="form-select form-select-sm" id="sortSelect" onchange="handleSort(this.value)">
									<option value="">-- Chọn --</option>
									<option value="danhGiaCao" <%= "danhGiaCao".equals(sortBy) ? "selected" : "" %>>Đánh giá cao nhất</option>
									<option value="danhGiaThap" <%= "danhGiaThap".equals(sortBy) ? "selected" : "" %>>Đánh giá thấp nhất</option>
									<option value="taoGanNhat" <%= "taoGanNhat".equals(sortBy) ? "selected" : "" %>>Tạo gần nhất</option>
									<option value="taoXaNhat" <%= "taoXaNhat".equals(sortBy) ? "selected" : "" %>>Tạo xa nhất</option>
									<option value="capNhatGanNhat" <%= "capNhatGanNhat".equals(sortBy) ? "selected" : "" %>>Cập nhật gần nhất</option>
									<option value="capNhatXaNhat" <%= "capNhatXaNhat".equals(sortBy) ? "selected" : "" %>>Cập nhật xa nhất</option>
								</select>
							</div>
							<div class="col-md-6">
								<label class="form-label small fw-bold">Lọc theo tác giả:</label>
								<select class="form-select form-select-sm" id="filterTaiKhoanSelect" onchange="handleFilterTaiKhoan(this.value)">
									<option value="">-- Tất cả tác giả --</option>
									<% 
										ArrayList<TaiKhoan> dsTaiKhoan = (ArrayList<TaiKhoan>) request.getAttribute("dsTaiKhoan");
										String filterTaiKhoan = (String) request.getAttribute("filterTaiKhoan");
										if(dsTaiKhoan != null && !dsTaiKhoan.isEmpty()) {
											for(TaiKhoan tk : dsTaiKhoan) { 
									%>
										<option value="<%= tk.getTenDangNhap() %>" <%= tk.getTenDangNhap().equals(filterTaiKhoan) ? "selected" : "" %>>
											<%= tk.getTenDangNhap() %>
										</option>
									<% 
											}
										}
									%>
								</select>
							</div>
						</div>
					</div>
				</div>

				<!-- Posts List -->
				<div class="posts-container">
					<% if(dsBaiViet == null || dsBaiViet.isEmpty()) { %>
						<div class="card shadow-sm">
							<div class="card-body text-center py-5">
								<i class="bi bi-inbox text-muted" style="font-size: 4rem;"></i>
								<h5 class="mt-3 text-muted">
									<% if(request.getAttribute("selectedTheLoai") != null) { %>
										Chưa có bài viết nào thỏa điều kiện thể loại "<%= selectedTenTheLoai %>"
									<% } else if(searchKey != null && !searchKey.trim().isEmpty()) { %>
										Chưa có bài viết nào thỏa điều kiện tìm kiếm
									<% } else { %>
										Chưa có bài viết nào.
									<% } %>
								</h5>
							</div>
						</div>
					<% } else {
						for(BaiViet bv : dsBaiViet) {
							// Tìm tên thể loại
							String tenTheLoai = "";
							if(dsTheLoai != null) {
								for(TheLoai tl : dsTheLoai) {
									if(tl.getMaTheLoai() == bv.getMaTheLoai()) {
										tenTheLoai = tl.getTenTheLoai();
										break;
									}
								}
							}
							
							// Lấy số bình luận
							ArrayList<BinhLuan> dsBinhLuan = (ArrayList<BinhLuan>) request.getAttribute("dsBinhLuan_" + bv.getMaBaiViet());
							int soBinhLuan = (dsBinhLuan != null) ? dsBinhLuan.size() : 0;
					%>
						<div class="card shadow-sm mb-3 post-card" onclick="window.location.href='${pageContext.request.contextPath}/ChiTietBaiVietController?id=<%= bv.getMaBaiViet() %>'">
							<div class="card-body">
								<!-- User Info -->
								<div class="d-flex align-items-center mb-2">
									<i class="bi bi-person-circle post-avatar"></i>
									<div class="ms-2">
										<div class="fw-bold"><%= bv.getTaiKhoanTao() %></div>
										<small class="text-muted">
											<i class="bi bi-clock me-1"></i>
											<%= bv.getThoiDiemCapNhat() != null ? sdf.format(bv.getThoiDiemCapNhat()) : sdf.format(bv.getThoiDiemTao()) %>
										</small>
									</div>
								</div>

								<!-- Category Badge -->
								<div class="mb-2">
									<span class="badge bg-primary"><i class="bi bi-tag me-1"></i><%= tenTheLoai %></span>
								</div>

								<!-- Post Content -->
								<h5 class="post-title"><%= bv.getTieuDe() %></h5>
								<p class="post-content"><%= bv.getNoiDung().length() > 200 ? bv.getNoiDung().substring(0, 200) + "..." : bv.getNoiDung() %></p>

								<!-- Media -->
								<% if(bv.getUrl() != null && !bv.getUrl().trim().isEmpty()) { %>
									<div class="post-media mb-3">
										<% if(bv.getUrl().matches(".*\\.(jpg|jpeg|png|gif|webp)$")) { %>
											<img src="<%= bv.getUrl() %>" class="img-fluid rounded" alt="Post image">
										<% } else if(bv.getUrl().matches(".*\\.(mp4|webm|ogg)$")) { %>
											<video controls class="w-100 rounded">
												<source src="<%= bv.getUrl() %>" type="video/mp4">
											</video>
										<% } %>
									</div>
								<% } %>

								<!-- Post Stats -->
								<div class="d-flex justify-content-between align-items-center pt-2 border-top">
									<div class="text-muted">
										<i class="bi bi-chat-fill me-1"></i>
										<span><%= soBinhLuan %> bình luận</span>
									</div>
									<div class="rating-display">
										<% 
											double danhGia = (bv.getDanhGia() != null) ? bv.getDanhGia().doubleValue() : 0;
											for(int i = 1; i <= 5; i++) {
												if(i <= danhGia) { %>
													<i class="bi bi-star-fill text-warning"></i>
										<%		} else { %>
													<i class="bi bi-star text-warning"></i>
										<%		}
											}
										%>
										<span class="ms-1 text-muted"><%= String.format("%.1f", danhGia) %></span>
									</div>
								</div>
							</div>
						</div>
					<% }
					} %>
					
					<% if(totalPages > 1) { %>
						<nav aria-label="Page navigation" class="mt-4">
							<ul class="pagination justify-content-center">
								<li class="page-item <%= currentPage == 1 ? "disabled" : "" %>">
									<a class="page-link" href="<%= currentPage > 1 ? buildPaginationUrl(request, currentPage - 1) : "#" %>" aria-label="Previous">
										<span aria-hidden="true">&laquo;</span>
									</a>
								</li>
								
								<%
									int startPage = Math.max(1, currentPage - 2);
									int endPage = Math.min(totalPages, currentPage + 2);
									
									if(currentPage <= 3) {
										endPage = Math.min(5, totalPages);
									}
									
									if(currentPage >= totalPages - 2) {
										startPage = Math.max(1, totalPages - 4);
									}
									
									if(startPage > 1) {
								%>
										<li class="page-item">
											<a class="page-link" href="<%= buildPaginationUrl(request, 1) %>">1</a>
										</li>
										<% if(startPage > 2) { %>
											<li class="page-item disabled">
												<span class="page-link">...</span>
											</li>
										<% } %>
								<%
									}
									
									for(int i = startPage; i <= endPage; i++) {
								%>
										<li class="page-item <%= i == currentPage ? "active" : "" %>">
											<a class="page-link" href="<%= buildPaginationUrl(request, i) %>"><%= i %></a>
										</li>
								<%
									}
									
									if(endPage < totalPages) {
										if(endPage < totalPages - 1) {
								%>
											<li class="page-item disabled">
												<span class="page-link">...</span>
											</li>
								<%
										}
								%>
										<li class="page-item">
											<a class="page-link" href="<%= buildPaginationUrl(request, totalPages) %>"><%= totalPages %></a>
										</li>
								<%
									}
								%>
								
								<li class="page-item <%= currentPage == totalPages ? "disabled" : "" %>">
									<a class="page-link" href="<%= currentPage < totalPages ? buildPaginationUrl(request, currentPage + 1) : "#" %>" aria-label="Next">
										<span aria-hidden="true">&raquo;</span>
									</a>
								</li>
							</ul>
						</nav>
						
						<div class="text-center text-muted mt-2">
							<small>
								Trang <%= currentPage %> / <%= totalPages %> 
								(Tổng <%= totalItems %> bài viết)
							</small>
						</div>
					<% } %>
				</div>
			</div>

			<!-- Right Sidebar - Search -->
			<div class="col-lg-3 d-none d-lg-block">
				<div class="card shadow-sm search-card sticky-top" style="top: 80px;">
					<div class="card-header bg-gradient text-white">
						<h5 class="mb-0"><i class="bi bi-search me-2"></i>Tìm kiếm</h5>
					</div>
					<div class="card-body position-relative">
					    <form id="searchForm" action="${pageContext.request.contextPath}/TrangChuController" method="get">
					        <div class="input-group search-box">
					            <input type="text" class="form-control" id="searchInput" name="search"
					                   placeholder="Tìm kiếm bài viết..."
					                   value="<%= searchKey != null ? searchKey : "" %>">
					            <button class="btn btn-primary" type="submit">
					                <i class="bi bi-search"></i>
					            </button>
					            <button type="button" class="btn btn-ai-search" onclick="enhanceSearch()" title="Tìm kiếm thông minh với AI">
					                <i class="bi bi-stars"></i>
					            </button>
					        </div>
					    </form>
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
	
	<%!
		private String buildPaginationUrl(HttpServletRequest request, int page) {
			String contextPath = request.getContextPath();
			String theloai = request.getParameter("theloai");
			String sort = request.getParameter("sort");
			String search = request.getParameter("search");
			String filterTK = request.getParameter("filterTK");
			
			StringBuilder url = new StringBuilder(contextPath + "/TrangChuController?page=" + page);
			
			if(theloai != null && !theloai.trim().isEmpty()) {
				try {
					url.append("&theloai=").append(java.net.URLEncoder.encode(theloai, "UTF-8"));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(sort != null && !sort.trim().isEmpty()) {
				try {
					url.append("&sort=").append(java.net.URLEncoder.encode(sort, "UTF-8"));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(search != null && !search.trim().isEmpty()) {
				try {
					url.append("&search=").append(java.net.URLEncoder.encode(search, "UTF-8"));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(filterTK != null && !filterTK.trim().isEmpty()) {
				try {
					url.append("&filterTK=").append(java.net.URLEncoder.encode(filterTK, "UTF-8"));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			return url.toString();
		}
	%>

	<script src="${pageContext.request.contextPath}/pages/home_page/script.js"></script>
	
	<!-- Full-screen AI Search Loading Overlay -->
	<div id="fullPageLoading" 
	     class="position-fixed top-0 start-0 w-100 h-100 d-none 
	            bg-white bg-opacity-95 d-flex flex-column align-items-center justify-content-center"
	     style="z-index: 9999; backdrop-filter: blur(8px);">
	    <div class="text-center">
	        <div class="spinner-border text-primary mb-4" 
	             style="width: 4rem; height: 4rem;" role="status">
	            <span class="visually-hidden">Loading...</span>
	        </div>
	        <h4 class="text-primary mb-2 fw-bold">
	            <i class="bi bi-stars me-2"></i>AI đang tìm kiếm thông minh...
	        </h4>
	        <p class="text-muted">Đang phân tích và tối ưu từ khóa cho bạn</p>
	        <div class="d-flex justify-content-center gap-2 mt-3">
	            <div class="bg-primary opacity-25 rounded" 
	                 style="width: 8px; height: 8px; animation: pulse 1.5s infinite;"></div>
	            <div class="bg-primary opacity-50 rounded" 
	                 style="width: 8px; height: 8px; animation: pulse 1.5s infinite 0.3s;"></div>
	            <div class="bg-primary opacity-75 rounded" 
	                 style="width: 8px; height: 8px; animation: pulse 1.5s infinite 0.6s;"></div>
	        </div>
	    </div>
	</div>
	
	<!-- Thêm animation pulse -->
	<style>
	@keyframes pulse {
	    0%, 100% { transform: scale(1); opacity: 0.5; }
	    50% { transform: scale(1.5); opacity: 1; }
	}
	</style>
</body>
</html>
