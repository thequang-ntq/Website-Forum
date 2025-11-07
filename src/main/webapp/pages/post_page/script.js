// Đợi DOM load xong
document.addEventListener('DOMContentLoaded', function() {
	// Initialize tooltips
	var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
	var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
		return new bootstrap.Tooltip(tooltipTriggerEl);
	});
});

// Handle real-time search
let searchTimeout;
function handleSearch() {
	const searchInput = document.getElementById('searchInput');
	const searchValue = searchInput.value.trim();
	
	// Clear previous timeout
	clearTimeout(searchTimeout);
	
	// Set new timeout for search (debounce)
	searchTimeout = setTimeout(() => {
		const contextPath = window.location.pathname.split('/')[1];
		let url = '/' + contextPath + '/BaiVietController';
		
		if(searchValue) {
			url += '?search=' + encodeURIComponent(searchValue);
		}
		
		window.location.href = url;
	}, 300); // Wait 300ms after user stops typing
}

// Show message alert
function showMessage(message, type) {
	const alertContainer = document.getElementById('messageAlert');
	const messageText = document.getElementById('messageText');
	const alert = alertContainer.querySelector('.alert');
	const icon = alert.querySelector('i');
	
	// Set message text
	messageText.textContent = message;
	
	// Set alert type
	alert.className = 'alert alert-dismissible fade show';
	if(type === 'success') {
		alert.classList.add('alert-success');
		icon.className = 'bi bi-check-circle-fill me-2';
	} else {
		alert.classList.add('alert-danger');
		icon.className = 'bi bi-x-circle-fill me-2';
	}
	
	// Show alert
	alertContainer.style.display = 'block';
	
	// Auto hide after 3 seconds
	setTimeout(() => {
		alertContainer.style.display = 'none';
	}, 3000);
}

// Get post data by ID
function getPostData(maBaiViet) {
	const postItems = document.querySelectorAll('.post-data-item');
	for(let item of postItems) {
		if(item.getAttribute('data-id') == maBaiViet) {
			return {
				maBaiViet: item.getAttribute('data-id'),
				tieuDe: item.getAttribute('data-tieude'),
				noiDung: item.getAttribute('data-noidung'),
				url: item.getAttribute('data-url'),
				taiKhoanTao: item.getAttribute('data-taikhoantao'),
				maTheLoai: item.getAttribute('data-matheloai'),
				tenTheLoai: item.getAttribute('data-tentheloai'),
				danhGia: item.getAttribute('data-danhgia'),
				trangThai: item.getAttribute('data-trangthai'),
				trangThaiVN: item.getAttribute('data-trangthaiVN'),
				soBinhLuan: item.getAttribute('data-sobinhluan'),
				thoiDiemTao: item.getAttribute('data-thoidiemtao'),
				thoiDiemCapNhat: item.getAttribute('data-thoidiemcapnhat')
			};
		}
	}
	return null;
}

// Show Detail Modal
function showDetailModal(maBaiViet) {
	const modal = new bootstrap.Modal(document.getElementById('detailModal'));
	const content = document.getElementById('detailContent');
	
	// Get post data
	const post = getPostData(maBaiViet);
	
	if(!post) {
		content.innerHTML = '<div class="alert alert-danger">Không tìm thấy thông tin bài viết</div>';
		modal.show();
		return;
	}
	
	// Build detail content
	let html = '<div class="detail-container">';
	
	// Tiêu đề
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-card-heading"></i>Tiêu đề</div>';
	html += '<div class="detail-value">' + post.tieuDe + '</div>';
	html += '</div>';
	
	// Nội dung
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-file-text"></i>Nội dung</div>';
	html += '<div class="detail-value">' + post.noiDung.replace(/\n/g, '<br>') + '</div>';
	html += '</div>';
	
	// Ảnh/Video
	if(post.url && post.url.trim() !== '') {
		html += '<div class="detail-item">';
		html += '<div class="detail-label"><i class="bi bi-image"></i>Media</div>';
		html += '<div class="detail-media">';
		
		// Check if image
		if(post.url.match(/\.(jpg|jpeg|png|gif|webp)$/i)) {
			html += '<img src="' + post.url + '" alt="Post image" class="img-fluid">';
		}
		// Check if video
		else if(post.url.match(/\.(mp4|webm|ogg)$/i)) {
			html += '<video controls class="w-100">';
			html += '<source src="' + post.url + '" type="video/mp4">';
			html += 'Trình duyệt không hỗ trợ video.';
			html += '</video>';
		}
		
		html += '</div>';
		html += '</div>';
	}
	
	// Tài khoản tạo
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-person-fill"></i>Tác giả</div>';
	html += '<div class="detail-value">' + post.taiKhoanTao + '</div>';
	html += '</div>';
	
	// Thể loại
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-tag-fill"></i>Thể loại</div>';
	html += '<div class="detail-value">' + post.tenTheLoai + '</div>';
	html += '</div>';
	
	// Đánh giá
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-star-fill"></i>Đánh giá</div>';
	html += '<div class="detail-value">';
	if(post.danhGia && post.danhGia.trim() !== '') {
		let rating = parseFloat(post.danhGia);
		for(let i = 1; i <= 5; i++) {
			if(i <= rating) {
				html += '<i class="bi bi-star-fill text-warning"></i> ';
			} else {
				html += '<i class="bi bi-star text-warning"></i> ';
			}
		}
		html += '<span class="ms-2">(' + rating.toFixed(1) + '/5.0)</span>';
	} else {
		html += '<span class="text-muted">Chưa có đánh giá</span>';
	}
	html += '</div>';
	html += '</div>';
	
	// Trạng thái
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-info-circle-fill"></i>Trạng thái</div>';
	html += '<div class="detail-value">';
	let badgeClass = 'bg-success';
	if(post.trangThai === 'Deleted') badgeClass = 'bg-danger';
	else if(post.trangThai === 'Hidden') badgeClass = 'bg-warning';
	html += '<span class="badge ' + badgeClass + '">' + post.trangThaiVN + '</span>';
	html += '</div>';
	html += '</div>';
	
	// Số bình luận
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-chat-fill"></i>Bình luận</div>';
	html += '<div class="detail-value">' + post.soBinhLuan + ' bình luận</div>';
	html += '</div>';
	
	// Thời điểm tạo
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-calendar-plus"></i>Thời điểm tạo</div>';
	html += '<div class="detail-value">' + (post.thoiDiemTao || 'N/A') + '</div>';
	html += '</div>';
	
	// Thời điểm cập nhật
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-calendar-check"></i>Thời điểm cập nhật</div>';
	html += '<div class="detail-value">' + (post.thoiDiemCapNhat || 'Chưa cập nhật') + '</div>';
	html += '</div>';
	
	html += '</div>';
	
	content.innerHTML = html;
	modal.show();
}

// Show Add Modal
function showAddModal() {
	const modal = new bootstrap.Modal(document.getElementById('addModal'));
	const form = document.getElementById('addForm');
	const errorDiv = document.getElementById('addError');
	const submitBtn = document.getElementById('addSubmitBtn');
	
	// Reset form
	form.reset();
	
	// Reset validation
	document.querySelectorAll('#addForm .form-control, #addForm .form-select').forEach(el => {
		el.classList.remove('is-invalid');
	});
	document.querySelectorAll('#addForm .invalid-feedback').forEach(el => {
		el.textContent = '';
	});
	
	errorDiv.style.display = 'none';
	submitBtn.disabled = false;
	
	modal.show();
}

// Show Edit Modal
function showEditModal(maBaiViet) {
	const modal = new bootstrap.Modal(document.getElementById('editModal'));
	const form = document.getElementById('editForm');
	const errorDiv = document.getElementById('editError');
	const submitBtn = document.getElementById('editSubmitBtn');
	
	// Get post data
	const post = getPostData(maBaiViet);
	
	if(!post) {
		alert('Không tìm thấy thông tin bài viết');
		return;
	}
	
	// Set values
	document.getElementById('editMaBaiViet').value = post.maBaiViet;
	document.getElementById('editTieuDe').value = post.tieuDe;
	document.getElementById('editNoiDung').value = post.noiDung;
	document.getElementById('editUrl').value = post.url || '';
	document.getElementById('editMaTheLoai').value = post.maTheLoai;
	document.getElementById('editDanhGia').value = post.danhGia || '';
	document.getElementById('editTrangThai').value = post.trangThai;
	
	// Reset validation
	document.querySelectorAll('#editForm .form-control, #editForm .form-select').forEach(el => {
		el.classList.remove('is-invalid');
	});
	document.querySelectorAll('#editForm .invalid-feedback').forEach(el => {
		el.textContent = '';
	});
	
	errorDiv.style.display = 'none';
	submitBtn.disabled = false;
	
	modal.show();
}

// Show Delete Modal
function showDeleteModal(maBaiViet, tieuDe) {
	const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
	
	document.getElementById('deleteMaBaiViet').value = maBaiViet;
	document.getElementById('deleteTieuDe').textContent = tieuDe;
	
	modal.show();
}

// Validate Add Form
function validateAddForm() {
	const tieuDe = document.getElementById('addTieuDe').value.trim();
	const noiDung = document.getElementById('addNoiDung').value.trim();
	const maTheLoai = document.getElementById('addMaTheLoai').value;
	const submitBtn = document.getElementById('addSubmitBtn');
	
	let isValid = true;
	
	// Validate tiêu đề
	const tieuDeInput = document.getElementById('addTieuDe');
	const tieuDeError = document.getElementById('addTieuDeError');
	if(tieuDe === '') {
		tieuDeInput.classList.add('is-invalid');
		tieuDeError.textContent = 'Tiêu đề không được để trống';
		isValid = false;
	} else if(tieuDe.length > 255) {
		tieuDeInput.classList.add('is-invalid');
		tieuDeError.textContent = 'Tiêu đề không được quá 255 ký tự';
		isValid = false;
	} else {
		tieuDeInput.classList.remove('is-invalid');
		tieuDeError.textContent = '';
	}
	
	// Validate nội dung
	const noiDungInput = document.getElementById('addNoiDung');
	const noiDungError = document.getElementById('addNoiDungError');
	if(noiDung === '') {
		noiDungInput.classList.add('is-invalid');
		noiDungError.textContent = 'Nội dung không được để trống';
		isValid = false;
	} else {
		noiDungInput.classList.remove('is-invalid');
		noiDungError.textContent = '';
	}
	
	// Validate thể loại
	const maTheLoaiSelect = document.getElementById('addMaTheLoai');
	const maTheLoaiError = document.getElementById('addMaTheLoaiError');
	if(!maTheLoai || maTheLoai === '') {
		maTheLoaiSelect.classList.add('is-invalid');
		maTheLoaiError.textContent = 'Vui lòng chọn thể loại';
		isValid = false;
	} else {
		maTheLoaiSelect.classList.remove('is-invalid');
		maTheLoaiError.textContent = '';
	}
	
	submitBtn.disabled = !isValid;
	return isValid;
}

// Validate Edit Form
function validateEditForm() {
	const tieuDe = document.getElementById('editTieuDe').value.trim();
	const noiDung = document.getElementById('editNoiDung').value.trim();
	const maTheLoai = document.getElementById('editMaTheLoai').value;
	const submitBtn = document.getElementById('editSubmitBtn');
	
	let isValid = true;
	
	// Validate tiêu đề
	const tieuDeInput = document.getElementById('editTieuDe');
	const tieuDeError = document.getElementById('editTieuDeError');
	if(tieuDe === '') {
		tieuDeInput.classList.add('is-invalid');
		tieuDeError.textContent = 'Tiêu đề không được để trống';
		isValid = false;
	} else if(tieuDe.length > 255) {
		tieuDeInput.classList.add('is-invalid');
		tieuDeError.textContent = 'Tiêu đề không được quá 255 ký tự';
		isValid = false;
	} else {
		tieuDeInput.classList.remove('is-invalid');
		tieuDeError.textContent = '';
	}
	
	// Validate nội dung
	const noiDungInput = document.getElementById('editNoiDung');
	const noiDungError = document.getElementById('editNoiDungError');
	if(noiDung === '') {
		noiDungInput.classList.add('is-invalid');
		noiDungError.textContent = 'Nội dung không được để trống';
		isValid = false;
	} else {
		noiDungInput.classList.remove('is-invalid');
		noiDungError.textContent = '';
	}
	
	// Validate thể loại
	const maTheLoaiSelect = document.getElementById('editMaTheLoai');
	const maTheLoaiError = document.getElementById('editMaTheLoaiError');
	if(!maTheLoai || maTheLoai === '') {
		maTheLoaiSelect.classList.add('is-invalid');
		maTheLoaiError.textContent = 'Vui lòng chọn thể loại';
		isValid = false;
	} else {
		maTheLoaiSelect.classList.remove('is-invalid');
		maTheLoaiError.textContent = '';
	}
	
	submitBtn.disabled = !isValid;
	return isValid;
}

// Handle Add Form Submit
document.addEventListener('DOMContentLoaded', function() {
	const addForm = document.getElementById('addForm');
	if(addForm) {
		addForm.addEventListener('submit', function(e) {
			if(!validateAddForm()) {
				e.preventDefault();
				const errorDiv = document.getElementById('addError');
				const errorText = document.getElementById('addErrorText');
				errorText.textContent = 'Vui lòng kiểm tra lại các trường đã nhập!';
				errorDiv.style.display = 'block';
				
				setTimeout(() => {
					errorDiv.style.display = 'none';
				}, 3000);
			}
		});
	}
	
	const editForm = document.getElementById('editForm');
	if(editForm) {
		editForm.addEventListener('submit', function(e) {
			if(!validateEditForm()) {
				e.preventDefault();
				const errorDiv = document.getElementById('editError');
				const errorText = document.getElementById('editErrorText');
				errorText.textContent = 'Vui lòng kiểm tra lại các trường đã nhập!';
				errorDiv.style.display = 'block';
				
				setTimeout(() => {
					errorDiv.style.display = 'none';
				}, 3000);
			}
		});
	}
});

// Close modal on escape key
document.addEventListener('keydown', function(e) {
	if(e.key === 'Escape') {
		const modals = document.querySelectorAll('.modal.show');
		modals.forEach(modal => {
			const modalInstance = bootstrap.Modal.getInstance(modal);
			if(modalInstance) {
				modalInstance.hide();
			}
		});
	}
});

// Prevent duplicate form submission
let isSubmitting = false;
document.addEventListener('DOMContentLoaded', function() {
	const forms = document.querySelectorAll('form');
	forms.forEach(form => {
		form.addEventListener('submit', function(e) {
			if(isSubmitting) {
				e.preventDefault();
				return false;
			}
			isSubmitting = true;
			
			// Re-enable after 3 seconds (in case of error)
			setTimeout(() => {
				isSubmitting = false;
			}, 3000);
		});
	});
});