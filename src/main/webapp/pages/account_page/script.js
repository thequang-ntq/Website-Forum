// Đợi DOM load xong
document.addEventListener('DOMContentLoaded', function() {
	// Initialize tooltips
	var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
	var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
		return new bootstrap.Tooltip(tooltipTriggerEl);
	});
	
	// Toggle password visibility for Add form
	const addToggleBtn = document.getElementById('addTogglePassword');
	if(addToggleBtn) {
		addToggleBtn.addEventListener('click', function() {
			const passwordInput = document.getElementById('addMatKhau');
			const toggleIcon = document.getElementById('addToggleIcon');
			
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
	}
	
	// Toggle password visibility for Edit form
	const editToggleBtn = document.getElementById('editTogglePassword');
	if(editToggleBtn) {
		editToggleBtn.addEventListener('click', function() {
			const passwordInput = document.getElementById('editMatKhau');
			const toggleIcon = document.getElementById('editToggleIcon');
			
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
	}
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
		let url = '/' + contextPath + '/TaiKhoanController';
		
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

// Get account data by username
function getAccountData(tenDangNhap) {
	const accountItems = document.querySelectorAll('.account-data-item');
	for(let item of accountItems) {
		if(item.getAttribute('data-tendangnhap') === tenDangNhap) {
			return {
				tenDangNhap: item.getAttribute('data-tendangnhap'),
				matKhau: item.getAttribute('data-matkhau'),
				quyen: item.getAttribute('data-quyen'),
				email: item.getAttribute('data-email'),
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
function showDetailModal(tenDangNhap) {
	const modal = new bootstrap.Modal(document.getElementById('detailModal'));
	const content = document.getElementById('detailContent');
	
	// Get account data
	const account = getAccountData(tenDangNhap);
	
	if(!account) {
		content.innerHTML = '<div class="alert alert-danger">Không tìm thấy thông tin tài khoản</div>';
		modal.show();
		return;
	}
	
	// Build detail content
	let html = '<div class="detail-container">';
	
	// Tên đăng nhập
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-person-fill"></i>Tên đăng nhập</div>';
	html += '<div class="detail-value">' + account.tenDangNhap + '</div>';
	html += '</div>';
	
	// Mật khẩu
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-lock-fill"></i>Mật khẩu</div>';
	html += '<div class="detail-value password-masked">' + account.matKhau.replace(/./g, '•') + '</div>';
	html += '</div>';
	
	// Quyền
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-shield-fill"></i>Quyền</div>';
	html += '<div class="detail-value">';
	let badgeClass = account.quyen === 'Admin' ? 'bg-danger' : 'bg-success';
	html += '<span class="badge ' + badgeClass + '">' + account.quyen + '</span>';
	html += '</div>';
	html += '</div>';
	
	// Tìm vị trí hiển thị Quyền trong detail modal, thêm sau đó:
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-envelope-fill"></i>Email</div>';
	html += '<div class="detail-value">' + (data.email || '<span class="text-muted">Chưa liên kết</span>') + '</div>';
	html += '</div>';
	
	// Trạng thái
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-info-circle-fill"></i>Trạng thái</div>';
	html += '<div class="detail-value">';
	badgeClass = 'bg-success';
	if(account.trangThai === 'Deleted') badgeClass = 'bg-danger';
	else if(account.trangThai === 'Hidden') badgeClass = 'bg-warning';
	html += '<span class="badge ' + badgeClass + '">' + account.trangThaiVN + '</span>';
	html += '</div>';
	html += '</div>';
	
	// Thời điểm tạo
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-calendar-plus"></i>Thời điểm tạo</div>';
	html += '<div class="detail-value">' + (account.thoiDiemTao || 'N/A') + '</div>';
	html += '</div>';
	
	// Thời điểm cập nhật
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-calendar-check"></i>Thời điểm cập nhật</div>';
	html += '<div class="detail-value">' + (account.thoiDiemCapNhat || 'Chưa cập nhật') + '</div>';
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
	
	// Reset password visibility
	const passwordInput = document.getElementById('addMatKhau');
	const toggleIcon = document.getElementById('addToggleIcon');
	passwordInput.type = 'password';
	toggleIcon.classList.remove('bi-eye-slash');
	toggleIcon.classList.add('bi-eye');
	
	errorDiv.style.display = 'none';
	submitBtn.disabled = false;
	
	modal.show();
}

// Show Edit Modal
function showEditModal(tenDangNhap) {
	const modal = new bootstrap.Modal(document.getElementById('editModal'));
	const form = document.getElementById('editForm');
	const errorDiv = document.getElementById('editError');
	const submitBtn = document.getElementById('editSubmitBtn');
	
	// Get account data
	const account = getAccountData(tenDangNhap);
	
	if(!account) {
		alert('Không tìm thấy thông tin tài khoản');
		return;
	}
	
	// Set values
	document.getElementById('editTenDangNhapHidden').value = account.tenDangNhap;
	document.getElementById('editTenDangNhap').value = account.tenDangNhap;
	document.getElementById('editMatKhau').value = account.matKhau;
	document.getElementById('editQuyen').value = account.quyen;
	document.getElementById('editTrangThai').value = account.trangThai;
	document.getElementById('editEmail').value = account.email || '';
	
	// Reset validation
	document.querySelectorAll('#editForm .form-control, #editForm .form-select').forEach(el => {
		el.classList.remove('is-invalid');
	});
	document.querySelectorAll('#editForm .invalid-feedback').forEach(el => {
		el.textContent = '';
	});
	
	// Reset password visibility
	const passwordInput = document.getElementById('editMatKhau');
	const toggleIcon = document.getElementById('editToggleIcon');
	passwordInput.type = 'password';
	toggleIcon.classList.remove('bi-eye-slash');
	toggleIcon.classList.add('bi-eye');
	
	errorDiv.style.display = 'none';
	submitBtn.disabled = false;
	
	modal.show();
}

// Show Delete Modal
function showDeleteModal(tenDangNhap) {
	const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
	
	document.getElementById('deleteTenDangNhap').value = tenDangNhap;
	document.getElementById('deleteTenDangNhapText').textContent = tenDangNhap;
	document.getElementById('deleteTenDangNhapText2').textContent = tenDangNhap;
	
	modal.show();
}

// Validate Add Form
function validateAddForm() {
	const tenDangNhap = document.getElementById('addTenDangNhap').value.trim();
	const matKhau = document.getElementById('addMatKhau').value.trim();
	const submitBtn = document.getElementById('addSubmitBtn');
	
	let isValid = true;
	
	// Validate tên đăng nhập
	const tenDangNhapInput = document.getElementById('addTenDangNhap');
	const tenDangNhapError = document.getElementById('addTenDangNhapError');
	if(tenDangNhap === '') {
		tenDangNhapInput.classList.add('is-invalid');
		tenDangNhapError.textContent = 'Tên đăng nhập không được để trống';
		isValid = false;
	} else if(tenDangNhap.length > 150) {
		tenDangNhapInput.classList.add('is-invalid');
		tenDangNhapError.textContent = 'Tên đăng nhập không được quá 150 ký tự';
		isValid = false;
	} else {
		tenDangNhapInput.classList.remove('is-invalid');
		tenDangNhapError.textContent = '';
	}
	
	// Validate mật khẩu
	const matKhauInput = document.getElementById('addMatKhau');
	const matKhauError = document.getElementById('addMatKhauError');
	if(matKhau === '') {
		matKhauInput.classList.add('is-invalid');
		matKhauError.textContent = 'Mật khẩu không được để trống';
		isValid = false;
	} else if(matKhau.length > 255) {
		matKhauInput.classList.add('is-invalid');
		matKhauError.textContent = 'Mật khẩu không được quá 255 ký tự';
		isValid = false;
	} else {
		matKhauInput.classList.remove('is-invalid');
		matKhauError.textContent = '';
	}
	
	// Validate Email (nếu có nhập)
	const email = document.getElementById('addEmail');
	const emailError = document.getElementById('addEmailError');

	if(email.value.trim() !== '') {
	    if(email.value.length > 255) {
	        email.classList.add('is-invalid');
	        emailError.textContent = 'Email không được quá 255 ký tự';
	        isValid = false;
	    } else if(!isValidEmail(email.value)) {
	        email.classList.add('is-invalid');
	        emailError.textContent = 'Email không hợp lệ';
	        isValid = false;
	    } else {
	        email.classList.remove('is-invalid');
	        emailError.textContent = '';
	    }
	} else {
	    email.classList.remove('is-invalid');
	    emailError.textContent = '';
	}
	
	submitBtn.disabled = !isValid;
	return isValid;
}

// Validate Edit Form
function validateEditForm() {
	const matKhau = document.getElementById('editMatKhau').value.trim();
	const submitBtn = document.getElementById('editSubmitBtn');
	
	let isValid = true;
	
	// Validate mật khẩu
	const matKhauInput = document.getElementById('editMatKhau');
	const matKhauError = document.getElementById('editMatKhauError');
	if(matKhau === '') {
		matKhauInput.classList.add('is-invalid');
		matKhauError.textContent = 'Mật khẩu không được để trống';
		isValid = false;
	} else if(matKhau.length > 255) {
		matKhauInput.classList.add('is-invalid');
		matKhauError.textContent = 'Mật khẩu không được quá 255 ký tự';
		isValid = false;
	} else {
		matKhauInput.classList.remove('is-invalid');
		matKhauError.textContent = '';
	}
	
	// Validate Email (nếu có nhập)
	const email = document.getElementById('editEmail');
	const emailError = document.getElementById('editEmailError');

	if(email.value.trim() !== '') {
	    if(email.value.length > 255) {
	        email.classList.add('is-invalid');
	        emailError.textContent = 'Email không được quá 255 ký tự';
	        isValid = false;
	    } else if(!isValidEmail(email.value)) {
	        email.classList.add('is-invalid');
	        emailError.textContent = 'Email không hợp lệ';
	        isValid = false;
	    } else {
	        email.classList.remove('is-invalid');
	        emailError.textContent = '';
	    }
	} else {
	    email.classList.remove('is-invalid');
	    emailError.textContent = '';
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

// Thêm vào cuối file script.js
function isValidEmail(email) {
    const regex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    return regex.test(email);
}