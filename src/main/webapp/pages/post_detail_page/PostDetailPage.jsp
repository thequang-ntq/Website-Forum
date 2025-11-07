<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Modal.BaiViet.BaiViet" %>
<%@ page import="Modal.BinhLuan.BinhLuan" %>
<%@ page import="Modal.LuotThichBinhLuan.LuotThichBinhLuan" %>
<%@ page import="Modal.DanhGiaBaiViet.DanhGiaBaiViet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.TimeZone" %>
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
    
    BaiViet bv = (BaiViet) request.getAttribute("baiViet");
    String tenTheLoai = (String) request.getAttribute("tenTheLoai");
    ArrayList<BinhLuan> dsBinhLuan = (ArrayList<BinhLuan>) request.getAttribute("dsBinhLuan");
    ArrayList<LuotThichBinhLuan> dsLuotThich = (ArrayList<LuotThichBinhLuan>) request.getAttribute("dsLuotThich");
    ArrayList<DanhGiaBaiViet> dsDanhGia = (ArrayList<DanhGiaBaiViet>) request.getAttribute("dsDanhGia");
    String currentAccount = account;
    
    // Lấy referer để biết đến từ trang nào
    String referer = request.getHeader("referer");
    String backUrl = request.getContextPath() + "/TrangChuController";
    if (referer != null && referer.contains("BaiVietCuaToi")) {
        backUrl = request.getContextPath() + "/BaiVietCuaToiController";
    }
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    
    boolean daDanhGia = false;
    int diemDaDanhGia = 0;
    if (dsDanhGia != null && account != null && bv != null) {
        for (DanhGiaBaiViet dg : dsDanhGia) {
            if (dg.getTenDangNhap().equals(account) && dg.getMaBaiViet() == bv.getMaBaiViet()) {
                daDanhGia = true;
                diemDaDanhGia = dg.getDiem().intValue();
                break;
            }
        }
    }
%>

	<!-- Navbar -->
	<nav class="navbar navbar-expand-lg navbar-dark bg-gradient sticky-top">
	    <div class="container-fluid">
	        <a class="navbar-brand d-none d-lg-flex align-items-center" href="${pageContext.request.contextPath}/TrangChuController">
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
	                <% if (isAdmin) { %>
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
	<!-- Main Content -->
	<div class="container mt-4">
	    <div class="row">
	        <div class="col-lg-10 mx-auto">
	            <!-- Back Button -->
	            <div class="mb-3">
	                <a href="<%= backUrl %>" class="btn btn-outline-secondary">
	                    <i class="bi bi-arrow-left me-2"></i>Quay lại
	                </a>
	            </div>
	
	            <!-- Post Detail Card -->
	            <div class="card shadow-sm">
	                <div class="card-body p-4">
	                    <% if (bv != null) { %>

							<div class="post-detail-container">
								<!-- Post Header -->
								<div class="d-flex align-items-center mb-3">
									<i class="bi bi-person-circle post-detail-avatar"></i>
									<div class="ms-3">
										<div class="fw-bold fs-5"><%= bv.getTaiKhoanTao() %></div>
										<small class="text-muted">
											<i class="bi bi-clock me-1"></i>
											<%= bv.getThoiDiemCapNhat() != null ? sdf.format(bv.getThoiDiemCapNhat()) : sdf.format(bv.getThoiDiemTao()) %>
										</small>
									</div>
								</div>
							
								<!-- Category Badge -->
								<div class="mb-3">
									<span class="badge bg-primary"><i class="bi bi-tag me-1"></i><%= tenTheLoai %></span>
								</div>
							
								<!-- Post Title -->
								<h4 class="post-detail-title mb-3"><%= bv.getTieuDe() %></h4>
							
								<!-- Post Content -->
								<div class="post-detail-content mb-3">
									<%= bv.getNoiDung().replace("\n", "<br>") %>
								</div>
							
								<!-- Media -->
								<% if (bv.getUrl() != null && !bv.getUrl().trim().isEmpty()) { %>
									<div class="post-detail-media mb-4">
										<% if (bv.getUrl().matches(".*\\.(jpg|jpeg|png|gif|webp)$")) { %>
											<img src="<%= bv.getUrl() %>" class="img-fluid rounded" alt="Post image">
										<% } else if (bv.getUrl().matches(".*\\.(mp4|webm|ogg)$")) { %>
											<video controls class="w-100 rounded">
												<source src="<%= bv.getUrl() %>" type="video/mp4">
											</video>
										<% } %>
									</div>
								<% } %>
							
								<!-- Post Stats & Rating -->
								<div class="d-flex justify-content-between align-items-center py-3 border-top border-bottom mb-4">
									<div class="text-muted">
										<i class="bi bi-chat-fill me-2"></i>
										<span><%= dsBinhLuan != null ? dsBinhLuan.size() : 0 %> bình luận</span>
									</div>
									<div class="rating-interactive" data-ma-bai-viet="<%= bv.getMaBaiViet() %>" data-da-danh-gia="<%= daDanhGia %>">
									    <% 
									        double danhGia = (bv.getDanhGia() != null) ? bv.getDanhGia().doubleValue() : 0;
									        // Hiển thị sao theo đánh giá trung bình
									        for (int i = 1; i <= 5; i++) {
									            // Sao đầy nếu i <= đánh giá trung bình
									            // Sao nửa nếu i - 0.5 <= đánh giá trung bình < i
									            String starClass = "bi-star text-warning";
									            if (i <= Math.floor(danhGia)) {
									                starClass = "bi-star-fill text-warning";
									            } else if (i - 0.5 <= danhGia && danhGia < i) {
									                starClass = "bi-star-half text-warning";
									            }
									            
									            // Highlight sao user đã đánh giá (viền màu khác)
									            String highlightStyle = (daDanhGia && i <= diemDaDanhGia) ? "text-shadow: 0 0 3px #667eea; filter: drop-shadow(0 0 2px #667eea);" : "";
									    %>
									        <i class="bi <%= starClass %> rating-star" 
									           data-rating="<%= i %>" 
									           data-ma-bai-viet="<%= bv.getMaBaiViet() %>"
									           data-da-danh-gia="<%= daDanhGia %>"
									           style="cursor: pointer; font-size: 1.3rem; <%= highlightStyle %>"></i>
									    <% } %>
									    <span class="ms-2 fw-bold"><%= String.format("%.1f", danhGia) %></span>
									    <% if (daDanhGia) { %>
									        <small class="text-muted ms-2">(Bạn đã đánh giá: <%= diemDaDanhGia %> <i class="bi bi-star-fill" style="font-size: 0.8rem;"></i>)</small>
									    <% } %>
									</div>
								</div>
							
								<div class="divider"></div>
							
								<!-- Comment Input -->
								<div class="comment-input-section mb-4">
									<h5 class="mb-3"><i class="bi bi-pencil-square me-2"></i>Viết bình luận</h5>
									<form id="commentForm">
										<input type="hidden" id="maBaiVietInput" value="<%= bv.getMaBaiViet() %>">
										<div class="mb-3">
											<textarea class="form-control" id="commentContent" rows="3" 
													  placeholder="Nhập bình luận của bạn..." required></textarea>
										</div>
										<div class="mb-3">
											<input type="url" class="form-control" id="commentUrl" 
												   placeholder="URL ảnh/video (không bắt buộc)">
											<small class="text-muted">Hỗ trợ: jpg, jpeg, png, gif, webp, mp4, webm, ogg</small>
										</div>
										<button type="submit" class="btn btn-primary">
											<i class="bi bi-send-fill me-2"></i>Gửi bình luận
										</button>
									</form>
								</div>
							
								<div class="divider"></div>
							
								<!-- Comments Section -->
								<div class="comments-section">
									<h5 class="mb-4"><i class="bi bi-chat-dots-fill me-2"></i>Bình luận (<%= dsBinhLuan != null ? dsBinhLuan.size() : 0 %>)</h5>
									
									<div id="commentsList">
										<% if (dsBinhLuan == null || dsBinhLuan.isEmpty()) { %>
											<div class="text-center py-5" id="emptyComments">
												<i class="bi bi-chat-left text-muted" style="font-size: 3rem;"></i>
												<p class="text-muted mt-3">Chưa có bình luận nào. Hãy là người đầu tiên bình luận!</p>
											</div>
										<% } else {
											for (BinhLuan bl : dsBinhLuan) {
												// Đếm số lượt thích của bình luận này
												int soLuotThich = 0;
												boolean daThich = false;
												if (dsLuotThich != null) {
													for (LuotThichBinhLuan lt : dsLuotThich) {
														if (lt.getMaBinhLuan() == bl.getMaBinhLuan()) {
															soLuotThich++;
															if (currentAccount != null && lt.getTenDangNhap().equals(currentAccount)) {
																daThich = true;
															}
														}
													}
												}
										%>
											<div class="comment-item mb-3" data-ma-binh-luan="<%= bl.getMaBinhLuan() %>">
												<div class="d-flex align-items-start">
													<i class="bi bi-person-circle comment-avatar me-2"></i>
													<div class="flex-grow-1">
														<div class="comment-author fw-bold mb-1"><%= bl.getTaiKhoanTao() %></div>
														<div class="comment-bubble">
															<div class="comment-content mb-2">
																<%= bl.getNoiDung().replace("\n", "<br>") %>
															</div>
															
															<% if (bl.getUrl() != null && !bl.getUrl().trim().isEmpty()) { %>
																<div class="comment-media">
																	<% if (bl.getUrl().matches(".*\\.(jpg|jpeg|png|gif|webp)$")) { %>
																		<img src="<%= bl.getUrl() %>" class="img-fluid rounded" alt="Comment image">
																	<% } else if (bl.getUrl().matches(".*\\.(mp4|webm|ogg)$")) { %>
																		<video controls class="w-100 rounded">
																			<source src="<%= bl.getUrl() %>" type="video/mp4">
																		</video>
																	<% } %>
																</div>
															<% } %>
														</div>
														<div class="comment-meta mt-2 d-flex align-items-center">
															<small class="text-muted me-3">
																<i class="bi bi-clock me-1"></i>
																<%= bl.getThoiDiemCapNhat() != null ? sdf.format(bl.getThoiDiemCapNhat()) : sdf.format(bl.getThoiDiemTao()) %>
															</small>
															<small class="like-section" style="cursor: pointer;">
																<i class="bi <%= daThich ? "bi-hand-thumbs-up-fill" : "bi-hand-thumbs-up" %> me-1 like-icon" 
																   data-ma-binh-luan="<%= bl.getMaBinhLuan() %>"
																   data-da-thich="<%= daThich %>"
																   style="color: <%= daThich ? "#667eea" : "" %>;"></i>
																<span class="like-count" data-ma-binh-luan="<%= bl.getMaBinhLuan() %>"><%= soLuotThich %></span>
																<span class="text-muted"> lượt thích</span>
															</small>
														</div>
													</div>
												</div>
											</div>
										<% 
											}
										} 
										%>
									</div>
								</div>
							</div>
                    <% } else { %>
	                    <div class="alert alert-danger">
	                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
	                        Không tìm thấy bài viết!
	                    </div>
	                    <% } %>
	                </div>
	            </div>
	        </div>
	    </div>
	</div>
	
	<!-- Footer -->
	<footer class="footer mt-5">
	    <div class="container text-center">
	        <p class="mb-0">&copy; 2025 Website Forum. All rights reserved.</p>
	    </div>
	</footer>
	<!-- Like List Modal -->
	<div class="modal fade" id="likeListModal" tabindex="-1">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title"><i class="bi bi-hand-thumbs-up-fill me-2"></i>Danh sách lượt thích</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<div class="modal-body" id="likeListContent">
					<div class="text-center py-3">
						<div class="spinner-border text-primary" role="status"></div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<style>
		.post-detail-avatar {
			font-size: 3rem;
			color: #667eea;
		}
		
		.post-detail-title {
			color: #2d3748;
			font-weight: 700;
			line-height: 1.4;
		}
		
		.post-detail-content {
			color: #4a5568;
			line-height: 1.8;
			font-size: 1.05rem;
		}
		
		.post-detail-media img,
		.post-detail-media video {
			max-height: 500px;
			object-fit: cover;
			box-shadow: 0 4px 12px rgba(0,0,0,0.1);
		}
		
		.rating-interactive .rating-star {
			transition: all 0.2s ease;
		}
		
		.rating-interactive .rating-star:hover {
			transform: scale(1.2);
		}
		
		.rating-interactive .rating-star.user-rated {
		    filter: drop-shadow(0 0 3px #667eea);
		}
		
		.comment-input-section {
			background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
			padding: 20px;
			border-radius: 12px;
		}
		
		.comments-section {
			margin-top: 2rem;
		}
		
		.comment-item {
			animation: fadeIn 0.5s ease;
		}
		
		@keyframes fadeIn {
			from { opacity: 0; transform: translateY(10px); }
			to { opacity: 1; transform: translateY(0); }
		}
		
		.comment-avatar {
			font-size: 2.5rem;
			color: #764ba2;
			min-width: 40px;
		}
		
		.comment-author {
			color: #2d3748;
			font-size: 0.95rem;
		}
		
		.comment-bubble {
			background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
			padding: 12px 16px;
			border-radius: 18px;
			box-shadow: 0 2px 8px rgba(0,0,0,0.08);
		}
		
		.comment-content {
			color: #4a5568;
			line-height: 1.6;
			word-wrap: break-word;
		}
		
		.comment-media img,
		.comment-media video {
			max-height: 300px;
			object-fit: cover;
			margin-top: 8px;
			border-radius: 12px;
		}
		
		.comment-meta {
			font-size: 0.85rem;
		}
		
		.like-icon {
			transition: all 0.2s ease;
			font-size: 1.1rem;
		}
		
		.like-icon:hover {
			transform: scale(1.2);
		}
		
		.like-count {
			font-weight: 600;
			color: #667eea;
		}
		
		.like-count:hover {
			text-decoration: underline;
		}
		
		.divider {
			height: 2px;
			background: linear-gradient(90deg, transparent, #667eea, transparent);
			margin: 2rem 0;
		}
	</style>
	
	<script>
		// Lấy context path
		const contextPath = window.location.pathname.split('/')[1];
		
		// Xử lý submit bình luận
		document.getElementById('commentForm').addEventListener('submit', function(e) {
		    e.preventDefault();
		    
		    const content = document.getElementById('commentContent').value.trim();
		    const url = document.getElementById('commentUrl').value.trim();
		    const maBaiViet = document.getElementById('maBaiVietInput').value;
		    
		    if (!content) {
		        alert('Vui lòng nhập nội dung bình luận!');
		        return;
		    }
		    
		    // Gửi qua URL params
		    const urlParams = new URLSearchParams();
		    urlParams.append('action', 'comment');
		    urlParams.append('maBaiViet', maBaiViet);
		    urlParams.append('noiDung', content);
		    if (url) urlParams.append('url', url);
		    
		    fetch('/' + contextPath + '/ChiTietBaiVietController?' + urlParams.toString(), {
		        method: 'POST'
		    })
		    .then(response => response.json())
		    .then(data => {
		        if (data.success) {
		            // Reload trang
		            window.location.reload();
		        } else {
		            alert(data.message || 'Có lỗi xảy ra khi thêm bình luận!');
		        }
		    })
		    .catch(error => {
		        console.error('Error:', error);
		        alert('Có lỗi xảy ra! Vui lòng thử lại.');
		    });
		});
		
		// Xử lý like/unlike bình luận
		document.addEventListener('click', function(e) {
		    if (e.target.classList.contains('like-icon')) {
		        const maBinhLuan = e.target.getAttribute('data-ma-binh-luan');
		        const daThich = e.target.getAttribute('data-da-thich') === 'true';
		        const action = daThich ? 'unlike' : 'like';
		        
		        fetch('/' + contextPath + '/ChiTietBaiVietController?action=' + action + '&maBinhLuan=' + maBinhLuan)
		            .then(response => response.json())
		            .then(data => {
		                if (data.success) {
		                    const commentItem = document.querySelector('.comment-item[data-ma-binh-luan="' + maBinhLuan + '"]');
		                    if (!commentItem) return;
		                    
		                    const likeIcon = commentItem.querySelector('.like-icon[data-ma-binh-luan="' + maBinhLuan + '"]');
		                    const likeCount = commentItem.querySelector('.like-count[data-ma-binh-luan="' + maBinhLuan + '"]');
		                    
		                    if (action === 'like') {
		                        likeIcon.classList.remove('bi-hand-thumbs-up');
		                        likeIcon.classList.add('bi-hand-thumbs-up-fill');
		                        likeIcon.style.color = '#667eea';
		                        likeCount.textContent = parseInt(likeCount.textContent) + 1;
		                        likeIcon.setAttribute('data-da-thich', 'true');
		                    } else {
		                        likeIcon.classList.remove('bi-hand-thumbs-up-fill');
		                        likeIcon.classList.add('bi-hand-thumbs-up');
		                        likeIcon.style.color = '';
		                        likeCount.textContent = parseInt(likeCount.textContent) - 1;
		                        likeIcon.setAttribute('data-da-thich', 'false');
		                    }
		                } else {
		                    alert(data.message || 'Có lỗi xảy ra!');
		                }
		            })
		            .catch(error => {
		                console.error('Error:', error);
		                alert('Có lỗi xảy ra! Vui lòng thử lại.');
		            });
		    }
		    
		    // Xử lý click vào số lượt thích
		    if (e.target.classList.contains('like-count')) {
		        const maBinhLuan = e.target.getAttribute('data-ma-binh-luan');
		        showLikeList(maBinhLuan);
		    }
		});
		
		// Hiển thị danh sách lượt thích
		function showLikeList(maBinhLuan) {
		    let modal = bootstrap.Modal.getInstance(document.getElementById('likeListModal'));
		    if (!modal) {
		        modal = new bootstrap.Modal(document.getElementById('likeListModal'));
		    }
		    
		    const content = document.getElementById('likeListContent');
		    content.innerHTML = '<div class="text-center py-3"><div class="spinner-border text-primary" role="status"></div></div>';
		    modal.show();
		    
		    fetch('/' + contextPath + '/ChiTietBaiVietController?action=getLikeList&maBinhLuan=' + maBinhLuan)
		        .then(response => response.json())
		        .then(data => {
		            if (data.success && data.list && data.list.length > 0) {
		                let html = '<div class="list-group">';
		                for (let i = 0; i < data.list.length; i++) {
		                    const item = data.list[i];
		                    html += '<div class="list-group-item d-flex align-items-center">';
		                    html += '<i class="bi bi-person-circle me-3" style="font-size: 2rem; color: #667eea;"></i>';
		                    html += '<div><div class="fw-bold">' + item.tenDangNhap + '</div>';
		                    html += '<small class="text-muted">' + item.thoiDiemThich + '</small></div></div>';
		                }
		                html += '</div>';
		                content.innerHTML = html;
		            } else {
		                content.innerHTML = '<div class="text-center py-3 text-muted">Chưa có ai thích bình luận này</div>';
		            }
		        })
		        .catch(error => {
		            console.error('Error:', error);
		            content.innerHTML = '<div class="alert alert-danger">Không thể tải danh sách!</div>';
		        });
		}
		
		// Xử lý đánh giá
		document.querySelectorAll('.rating-star').forEach(star => {
		    star.addEventListener('click', function() {
		        const rating = parseInt(this.getAttribute('data-rating'));
		        const maBaiViet = this.getAttribute('data-ma-bai-viet');
		        const daDanhGia = this.getAttribute('data-da-danh-gia') === 'true';
		        const ratingAction = daDanhGia ? 'delete' : 'create';
		        
		        fetch('/' + contextPath + '/ChiTietBaiVietController?action=rating&ratingAction=' + ratingAction + '&maBaiViet=' + maBaiViet + '&diem=' + rating)
		            .then(response => response.json())
		            .then(data => {
		                if (data.success) {
		                    // Reload trang
		                    window.location.reload();
		                } else {
		                    alert(data.message || 'Có lỗi xảy ra!');
		                }
		            })
		            .catch(error => {
		                console.error('Error:', error);
		                alert('Có lỗi xảy ra! Vui lòng thử lại.');
		            });
		    });
		});
		
		// Xử lý Enter để submit bình luận
		document.getElementById('commentContent').addEventListener('keydown', function(e) {
		    if (e.key === 'Enter' && !e.shiftKey) {
		        e.preventDefault();
		        document.getElementById('commentForm').dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));
		    }
		});
	</script>
</body>
</html>