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
		let url = '/' + contextPath + '/BinhLuanController';
		
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

// Get comment data by ID
function getCommentData(maBinhLuan) {
	const commentItems = document.querySelectorAll('.comment-data-item');
	for(let item of commentItems) {
		if(item.getAttribute('data-id') == maBinhLuan) {
			return {
				maBinhLuan: item.getAttribute('data-id'),
				tieuDe: item.getAttribute('data-tieude'),
				noiDung: item.getAttribute('data-noidung'),
				url: item.getAttribute('data-url'),
				taiKhoanTao: item.getAttribute('data-taikhoantao'),
				maBaiViet: item.getAttribute('data-mabaiviet'),
				tenBaiViet: item.getAttribute('data-tenbaiviet'),
				soLuotThich: item.getAttribute('data-soluotthich'),
				trangThai: item.getAttribute('data-trangthai'),
				trangThaiVN: item.getAttribute('data-trangthaiVN'),
				thoiDiemTao: item.getAttribute('data-thoidiemtao'),
				thoiDiemCapNhat: item.getAttribute('data-thoidiemcapnhat')
			};
		}
	}
	return null;
}

// Show Detail Modal
function showDetailModal(maBinhLuan) {
	const modal = new bootstrap.Modal(document.getElementById('detailModal'));
	const content = document.getElementById('detailContent');
	
	// Get comment data
	const comment = getCommentData(maBinhLuan);
	
	if(!comment) {
		content.innerHTML = '<div class="alert alert-danger">Không tìm thấy thông tin bình luận</div>';
		modal.show();
		return;
	}
	
	// Build detail content
	let html = '<div class="detail-container">';
	
	// Tiêu đề (nếu có)
	if(comment.tieuDe && comment.tieuDe.trim() !== '') {
		html += '<div class="detail-item">';
		html += '<div class="detail-label"><i class="bi bi-card-heading"></i>Tiêu đề</div>';
		html += '<div class="detail-value">' + comment.tieuDe + '</div>';
		html += '</div>';
	}
	
	// Nội dung
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-file-text"></i>Nội dung</div>';
	html += '<div class="detail-value">' + comment.noiDung.replace(/\n/g, '<br>') + '</div>';
	html += '</div>';
	
	// Ảnh/Video
	if(comment.url && comment.url.trim() !== '') {
		html += '<div class="detail-item">';
		html += '<div class="detail-label"><i class="bi bi-image"></i>Media</div>';
		html += '<div class="detail-media">';
		
		// Check if image
		if(comment.url.match(/\.(jpg|jpeg|png|gif|webp)$/i)) {
			html += '<img src="' + comment.url + '" alt="Comment image" class="img-fluid">';
		}
		// Check if video
		else if(comment.url.match(/\.(mp4|webm|ogg)$/i)) {
			html += '<video controls class="w-100">';
			html += '<source src="' + comment.url + '" type="video/mp4">';
			html += 'Trình duyệt không hỗ trợ video.';
			html += '</video>';
		}
		
		html += '</div>';
		html += '</div>';
	}
	
	// Tài khoản tạo
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-person-fill"></i>Tác giả</div>';
	html += '<div class="detail-value">' + comment.taiKhoanTao + '</div>';
	html += '</div>';
	
	// Bài viết
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-file-text-fill"></i>Bài viết</div>';
	html += '<div class="detail-value">' + comment.tenBaiViet + '</div>';
	html += '</div>';
	
	// Số lượt thích
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-heart-fill"></i>Lượt thích</div>';
	html += '<div class="detail-value">';
	html += '<span class="badge bg-danger">' + comment.soLuotThich + ' <i class="bi bi-heart-fill ms-1"></i></span>';
	html += '</div>';
	html += '</div>';
	
	// Trạng thái
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-info-circle-fill"></i>Trạng thái</div>';
	html += '<div class="detail-value">';
	let badgeClass = 'bg-success';
	if(comment.trangThai === 'Deleted') badgeClass = 'bg-danger';
	else if(comment.trangThai === 'Hidden') badgeClass = 'bg-warning';
	html += '<span class="badge ' + badgeClass + '">' + comment.trangThaiVN + '</span>';
	html += '</div>';
	html += '</div>';
	
	// Thời điểm tạo
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-calendar-plus"></i>Thời điểm tạo</div>';
	html += '<div class="detail-value">' + (comment.thoiDiemTao || 'N/A') + '</div>';
	html += '</div>';
	
	// Thời điểm cập nhật
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-calendar-check"></i>Thời điểm cập nhật</div>';
	html += '<div class="detail-value">' + (comment.thoiDiemCapNhat || 'Chưa cập nhật') + '</div>';
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
function showEditModal(maBinhLuan) {
	const modal = new bootstrap.Modal(document.getElementById('editModal'));
	const form = document.getElementById('editForm');
	const errorDiv = document.getElementById('editError');
	const submitBtn = document.getElementById('editSubmitBtn');
	
	// Get comment data
	const comment = getCommentData(maBinhLuan);
	
	if(!comment) {
		alert('Không tìm thấy thông tin bình luận');
		return;
	}
	
	// Set values
	document.getElementById('editMaBinhLuan').value = comment.maBinhLuan;
	document.getElementById('editNoiDung').value = comment.noiDung;
	document.getElementById('editUrl').value = comment.url || '';
	document.getElementById('editSoLuotThich').value = comment.soLuotThich || '';
	document.getElementById('editTrangThai').value = comment.trangThai;
	
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
function showDeleteModal(maBinhLuan, noiDung) {
	const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
	
	document.getElementById('deleteMaBinhLuan').value = maBinhLuan;
	document.getElementById('deleteNoiDung').textContent = noiDung;
	
	modal.show();
}

// Validate Add Form
function validateAddForm() {
	const noiDung = document.getElementById('addNoiDung').value.trim();
	const maBaiViet = document.getElementById('addMaBaiViet').value;
	const submitBtn = document.getElementById('addSubmitBtn');
	
	let isValid = true;
	
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
	
	// Validate bài viết
	const maBaiVietSelect = document.getElementById('addMaBaiViet');
	const maBaiVietError = document.getElementById('addMaBaiVietError');
	if(!maBaiViet || maBaiViet === '') {
		maBaiVietSelect.classList.add('is-invalid');
		maBaiVietError.textContent = 'Vui lòng chọn bài viết';
		isValid = false;
	} else {
		maBaiVietSelect.classList.remove('is-invalid');
		maBaiVietError.textContent = '';
	}
	
	submitBtn.disabled = !isValid;
	return isValid;
}

// Validate Edit Form
function validateEditForm() {
	const noiDung = document.getElementById('editNoiDung').value.trim();
	const submitBtn = document.getElementById('editSubmitBtn');
	
	let isValid = true;
	
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