// Đợi DOM load xong
document.addEventListener('DOMContentLoaded', function() {
	// Initialize tooltips
	var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
	var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
		return new bootstrap.Tooltip(tooltipTriggerEl);
	});
});

// Toggle password visibility
function togglePassword() {
	const passwordField = document.getElementById('passwordField');
	const toggleIcon = document.getElementById('toggleIcon');
	
	if(passwordField.type === 'password') {
		passwordField.type = 'text';
		toggleIcon.classList.remove('bi-eye-fill');
		toggleIcon.classList.add('bi-eye-slash-fill');
	} else {
		passwordField.type = 'password';
		toggleIcon.classList.remove('bi-eye-slash-fill');
		toggleIcon.classList.add('bi-eye-fill');
	}
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

// Show Change Password Modal
function showChangePasswordModal() {
	const modal = new bootstrap.Modal(document.getElementById('changePasswordModal'));
	const form = document.getElementById('changePasswordForm');
	const modalError = document.getElementById('modalError');
	
	// Reset form
	form.reset();
	
	// Clear all errors
	clearAllErrors();
	
	// Hide modal error
	modalError.style.display = 'none';
	
	modal.show();
}

// Clear all errors
function clearAllErrors() {
	const inputs = ['matKhauCu', 'matKhauMoi', 'xacNhanMatKhauMoi'];
	
	inputs.forEach(id => {
		const input = document.getElementById(id);
		const errorDiv = document.getElementById(id + 'Error');
		
		if(input) {
			input.classList.remove('is-invalid');
		}
		if(errorDiv) {
			errorDiv.textContent = '';
		}
	});
	
	// Enable submit button
	const submitBtn = document.getElementById('changePasswordSubmitBtn');
	if(submitBtn) {
		submitBtn.disabled = false;
	}
}

// Validate Change Password Form
function validateChangePasswordForm() {
	const matKhauCu = document.getElementById('matKhauCu');
	const matKhauMoi = document.getElementById('matKhauMoi');
	const xacNhanMatKhauMoi = document.getElementById('xacNhanMatKhauMoi');
	
	const matKhauCuError = document.getElementById('matKhauCuError');
	const matKhauMoiError = document.getElementById('matKhauMoiError');
	const xacNhanMatKhauMoiError = document.getElementById('xacNhanMatKhauMoiError');
	
	const submitBtn = document.getElementById('changePasswordSubmitBtn');
	const modalError = document.getElementById('modalError');
	
	let isValid = true;
	
	// Hide modal error
	modalError.style.display = 'none';
	
	// Validate Mật khẩu cũ
	if(matKhauCu.value.trim() === '') {
		matKhauCu.classList.add('is-invalid');
		matKhauCuError.textContent = 'Mật khẩu cũ không được để trống';
		isValid = false;
	} else if(matKhauCu.value.length > 255) {
		matKhauCu.classList.add('is-invalid');
		matKhauCuError.textContent = 'Mật khẩu cũ không được quá 255 ký tự';
		isValid = false;
	} else {
		matKhauCu.classList.remove('is-invalid');
		matKhauCuError.textContent = '';
	}
	
	// Validate Mật khẩu mới
	if(matKhauMoi.value.trim() === '') {
		matKhauMoi.classList.add('is-invalid');
		matKhauMoiError.textContent = 'Mật khẩu mới không được để trống';
		isValid = false;
	} else if(matKhauMoi.value.length > 255) {
		matKhauMoi.classList.add('is-invalid');
		matKhauMoiError.textContent = 'Mật khẩu mới không được quá 255 ký tự';
		isValid = false;
	} else if(matKhauMoi.value === matKhauCu.value) {
		matKhauMoi.classList.add('is-invalid');
		matKhauMoiError.textContent = 'Mật khẩu mới không được trùng mật khẩu cũ';
		isValid = false;
	} else {
		matKhauMoi.classList.remove('is-invalid');
		matKhauMoiError.textContent = '';
	}
	
	// Validate Xác nhận mật khẩu mới
	if(xacNhanMatKhauMoi.value.trim() === '') {
		xacNhanMatKhauMoi.classList.add('is-invalid');
		xacNhanMatKhauMoiError.textContent = 'Xác nhận mật khẩu mới không được để trống';
		isValid = false;
	} else if(xacNhanMatKhauMoi.value.length > 255) {
		xacNhanMatKhauMoi.classList.add('is-invalid');
		xacNhanMatKhauMoiError.textContent = 'Xác nhận mật khẩu mới không được quá 255 ký tự';
		isValid = false;
	} else if(xacNhanMatKhauMoi.value !== matKhauMoi.value) {
		xacNhanMatKhauMoi.classList.add('is-invalid');
		xacNhanMatKhauMoiError.textContent = 'Xác nhận mật khẩu mới không khớp';
		isValid = false;
	} else {
		xacNhanMatKhauMoi.classList.remove('is-invalid');
		xacNhanMatKhauMoiError.textContent = '';
	}
	
	// Enable/disable submit button
	submitBtn.disabled = !isValid;
	
	return isValid;
}

// Handle Change Password Form Submit
document.addEventListener('DOMContentLoaded', function() {
	const changePasswordForm = document.getElementById('changePasswordForm');
	if(changePasswordForm) {
		changePasswordForm.addEventListener('submit', function(e) {
			if(!validateChangePasswordForm()) {
				e.preventDefault();
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

// Show Delete Account Modal
function showDeleteAccountModal() {
	const modal = new bootstrap.Modal(document.getElementById('deleteAccountModal'));
	const form = document.getElementById('deleteAccountForm');
	const deleteModalError = document.getElementById('deleteModalError');
	
	// Reset form
	form.reset();
	
	// Clear all errors
	clearDeleteAccountErrors();
	
	// Hide modal error
	deleteModalError.style.display = 'none';
	
	modal.show();
}

// Clear Delete Account Errors
function clearDeleteAccountErrors() {
	const inputs = ['matKhauXacNhan', 'xacNhanXoa'];
	
	inputs.forEach(id => {
		const input = document.getElementById(id);
		const errorDiv = document.getElementById(id + 'Error');
		
		if(input) {
			input.classList.remove('is-invalid');
		}
		if(errorDiv) {
			errorDiv.textContent = '';
		}
	});
	
	// Disable submit button
	const submitBtn = document.getElementById('deleteAccountSubmitBtn');
	if(submitBtn) {
		submitBtn.disabled = true;
	}
}

// Validate Delete Account Form
function validateDeleteAccountForm() {
	const matKhauXacNhan = document.getElementById('matKhauXacNhan');
	const xacNhanXoa = document.getElementById('xacNhanXoa');
	
	const matKhauXacNhanError = document.getElementById('matKhauXacNhanError');
	const xacNhanXoaError = document.getElementById('xacNhanXoaError');
	
	const submitBtn = document.getElementById('deleteAccountSubmitBtn');
	const deleteModalError = document.getElementById('deleteModalError');
	
	let isValid = true;
	
	// Hide modal error
	deleteModalError.style.display = 'none';
	
	// Validate Mật khẩu xác nhận
	if(matKhauXacNhan.value.trim() === '') {
		matKhauXacNhan.classList.add('is-invalid');
		matKhauXacNhanError.textContent = 'Mật khẩu xác nhận không được để trống';
		isValid = false;
	} else if(matKhauXacNhan.value.length > 255) {
		matKhauXacNhan.classList.add('is-invalid');
		matKhauXacNhanError.textContent = 'Mật khẩu xác nhận không được quá 255 ký tự';
		isValid = false;
	} else {
		matKhauXacNhan.classList.remove('is-invalid');
		matKhauXacNhanError.textContent = '';
	}
	
	// Validate Xác nhận xóa
	if(xacNhanXoa.value.trim() === '') {
		xacNhanXoa.classList.add('is-invalid');
		xacNhanXoaError.textContent = 'Vui lòng nhập cụm từ xác nhận';
		isValid = false;
	} else if(xacNhanXoa.value !== 'XOA TAI KHOAN') {
		xacNhanXoa.classList.add('is-invalid');
		xacNhanXoaError.textContent = 'Cụm từ xác nhận không chính xác';
		isValid = false;
	} else {
		xacNhanXoa.classList.remove('is-invalid');
		xacNhanXoaError.textContent = '';
	}
	
	// Enable/disable submit button
	submitBtn.disabled = !isValid;
	
	return isValid;
}

// Handle Delete Account Form Submit
document.addEventListener('DOMContentLoaded', function() {
	const deleteAccountForm = document.getElementById('deleteAccountForm');
	if(deleteAccountForm) {
		deleteAccountForm.addEventListener('submit', function(e) {
			if(!validateDeleteAccountForm()) {
				e.preventDefault();
				
				const deleteModalError = document.getElementById('deleteModalError');
				const deleteModalErrorText = document.getElementById('deleteModalErrorText');
				deleteModalErrorText.textContent = 'Vui lòng kiểm tra lại các trường đã nhập!';
				deleteModalError.style.display = 'block';
				
				setTimeout(() => {
					deleteModalError.style.display = 'none';
				}, 3000);
			} else {
				// Show confirmation before submit
				if(!confirm('Bạn có chắc chắn muốn xóa tài khoản? Hành động này không thể hoàn tác!')) {
					e.preventDefault();
				}
			}
		});
	}
});

// Show Add Email Modal
function showAddEmailModal() {
    const modal = new bootstrap.Modal(document.getElementById('addEmailModal'));
    const form = document.getElementById('addEmailForm');
    const addEmailError = document.getElementById('addEmailError');
    
    // Reset form
    form.reset();
    
    // Clear errors
    clearAddEmailErrors();
    
    // Hide modal error
    addEmailError.style.display = 'none';
    
    modal.show();
}

// Clear Add Email Errors
function clearAddEmailErrors() {
    const email = document.getElementById('email');
    const emailError = document.getElementById('emailError');
    
    if(email) email.classList.remove('is-invalid');
    if(emailError) emailError.textContent = '';
    
    const submitBtn = document.getElementById('addEmailSubmitBtn');
    if(submitBtn) submitBtn.disabled = true;
}

// Validate Add Email Form
function validateAddEmailForm() {
    const email = document.getElementById('email');
    const emailError = document.getElementById('emailError');
    const submitBtn = document.getElementById('addEmailSubmitBtn');
    const addEmailError = document.getElementById('addEmailError');
    
    let isValid = true;
    
    // Hide modal error
    addEmailError.style.display = 'none';
    
    // Validate Email
    if(email.value.trim() === '') {
        email.classList.add('is-invalid');
        emailError.textContent = 'Email không được để trống';
        isValid = false;
    } else if(email.value.length > 255) {
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
    
    submitBtn.disabled = !isValid;
    return isValid;
}

// Email validation function
function isValidEmail(email) {
    const regex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    return regex.test(email);
}

// Handle Add Email Form Submit
document.addEventListener('DOMContentLoaded', function() {
    const addEmailForm = document.getElementById('addEmailForm');
    if(addEmailForm) {
        addEmailForm.addEventListener('submit', function(e) {
            if(!validateAddEmailForm()) {
                e.preventDefault();
            }
        });
    }
});