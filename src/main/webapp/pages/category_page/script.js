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
		let url = '/' + contextPath + '/TheLoaiController';
		
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

// Get category data by id
function getCategoryData(maTheLoai) {
	const categoryItems = document.querySelectorAll('.category-data-item');
	for(let item of categoryItems) {
		if(item.getAttribute('data-id') == maTheLoai) {
			return {
				maTheLoai: item.getAttribute('data-matheloai'),
				tenTheLoai: item.getAttribute('data-tentheloai'),
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
function showDetailModal(maTheLoai) {
	const modal = new bootstrap.Modal(document.getElementById('detailModal'));
	const content = document.getElementById('detailContent');
	
	// Get category data
	const category = getCategoryData(maTheLoai);
	
	if(!category) {
		content.innerHTML = '<div class="alert alert-danger">Không tìm thấy thông tin thể loại</div>';
		modal.show();
		return;
	}
	
	// Build detail content
	let html = '<div class="detail-content-wrapper">';
	
	// Mã thể loại
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-hash"></i>Mã thể loại</div>';
	html += '<div class="detail-value">' + category.maTheLoai + '</div>';
	html += '</div>';
	
	// Tên thể loại
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-tags-fill"></i>Tên thể loại</div>';
	html += '<div class="detail-value">' + category.tenTheLoai + '</div>';
	html += '</div>';
	
	// Trạng thái
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-info-circle-fill"></i>Trạng thái</div>';
	html += '<div class="detail-value">';
	let badgeClass = 'bg-success';
	if(category.trangThai === 'Deleted') badgeClass = 'bg-danger';
	else if(category.trangThai === 'Hidden') badgeClass = 'bg-warning';
	html += '<span class="badge ' + badgeClass + '">' + category.trangThaiVN + '</span>';
	html += '</div>';
	html += '</div>';
	
	// Thời điểm tạo
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-calendar-plus"></i>Thời điểm tạo</div>';
	html += '<div class="detail-value">' + (category.thoiDiemTao || 'N/A') + '</div>';
	html += '</div>';
	
	// Thời điểm cập nhật
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-calendar-check"></i>Thời điểm cập nhật</div>';
	html += '<div class="detail-value">' + (category.thoiDiemCapNhat || 'Chưa cập nhật') + '</div>';
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
	document.querySelectorAll('#addForm .form-control').forEach(el => {
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
function showEditModal(maTheLoai) {
	const modal = new bootstrap.Modal(document.getElementById('editModal'));
	const form = document.getElementById('editForm');
	const errorDiv = document.getElementById('editError');
	const submitBtn = document.getElementById('editSubmitBtn');
	
	// Get category data
	const category = getCategoryData(maTheLoai);
	
	if(!category) {
		alert('Không tìm thấy thông tin thể loại');
		return;
	}
	
	// Set values
	document.getElementById('editMaTheLoai').value = category.maTheLoai;
	document.getElementById('editTenTheLoai').value = category.tenTheLoai;
	document.getElementById('editTrangThai').value = category.trangThai;
	
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
function showDeleteModal(maTheLoai, tenTheLoai) {
	const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
	
	document.getElementById('deleteMaTheLoai').value = maTheLoai;
	document.getElementById('deleteTenTheLoai').textContent = tenTheLoai;
	
	modal.show();
}

// Validate Add Form
function validateAddForm() {
	const tenTheLoai = document.getElementById('addTenTheLoai').value.trim();
	const submitBtn = document.getElementById('addSubmitBtn');
	
	let isValid = true;
	
	// Validate tên thể loại
	const tenTheLoaiInput = document.getElementById('addTenTheLoai');
	const tenTheLoaiError = document.getElementById('addTenTheLoaiError');
	if(tenTheLoai === '') {
		tenTheLoaiInput.classList.add('is-invalid');
		tenTheLoaiError.textContent = 'Tên thể loại không được để trống';
		isValid = false;
	} else if(tenTheLoai.length > 200) {
		tenTheLoaiInput.classList.add('is-invalid');
		tenTheLoaiError.textContent = 'Tên thể loại không được quá 200 ký tự';
		isValid = false;
	} else {
		tenTheLoaiInput.classList.remove('is-invalid');
		tenTheLoaiError.textContent = '';
	}
	
	submitBtn.disabled = !isValid;
	return isValid;
}

// Validate Edit Form
function validateEditForm() {
	const tenTheLoai = document.getElementById('editTenTheLoai').value.trim();
	const submitBtn = document.getElementById('editSubmitBtn');
	
	let isValid = true;
	
	// Validate tên thể loại
	const tenTheLoaiInput = document.getElementById('editTenTheLoai');
	const tenTheLoaiError = document.getElementById('editTenTheLoaiError');
	if(tenTheLoai === '') {
		tenTheLoaiInput.classList.add('is-invalid');
		tenTheLoaiError.textContent = 'Tên thể loại không được để trống';
		isValid = false;
	} else if(tenTheLoai.length > 200) {
		tenTheLoaiInput.classList.add('is-invalid');
		tenTheLoaiError.textContent = 'Tên thể loại không được quá 200 ký tự';
		isValid = false;
	} else {
		tenTheLoaiInput.classList.remove('is-invalid');
		tenTheLoaiError.textContent = '';
	}
	
	submitBtn.disabled = !isValid;
	return isValid;
}

// Handle Add Form, Edit Form Submit
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