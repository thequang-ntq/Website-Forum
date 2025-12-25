// Đợi DOM load xong mới chạy script
document.addEventListener('DOMContentLoaded', function() {
	
	// Toggle password visibility
	const togglePasswordBtn = document.getElementById('togglePassword');
	if(togglePasswordBtn) {
		togglePasswordBtn.addEventListener('click', function() {
			const passwordInput = document.getElementById('matKhau');
			const toggleIcon = document.getElementById('toggleIcon');
			
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

	// Toggle confirm password visibility
	const toggleConfirmPasswordBtn = document.getElementById('toggleConfirmPassword');
	if(toggleConfirmPasswordBtn) {
		toggleConfirmPasswordBtn.addEventListener('click', function() {
			const confirmPasswordInput = document.getElementById('nhapLaiMatKhau');
			const toggleConfirmIcon = document.getElementById('toggleConfirmIcon');
			
			if(confirmPasswordInput.type === 'password') {
				confirmPasswordInput.type = 'text';
				toggleConfirmIcon.classList.remove('bi-eye');
				toggleConfirmIcon.classList.add('bi-eye-slash');
			} else {
				confirmPasswordInput.type = 'password';
				toggleConfirmIcon.classList.remove('bi-eye-slash');
				toggleConfirmIcon.classList.add('bi-eye');
			}
		});
	}

	// Form validation on submit, thừa, không cần, đã xử lý trong XuLyDangKyController
	const registerForm = document.getElementById('registerForm');
	if(registerForm) {
		registerForm.addEventListener('submit', function(e) {
			const tenDangNhap = document.getElementById('tenDangNhap').value.trim();
			const matKhau = document.getElementById('matKhau').value.trim();
			const nhapLaiMatKhau = document.getElementById('nhapLaiMatKhau').value.trim();
			
			if(!tenDangNhap || !matKhau || !nhapLaiMatKhau) {
				//
			}
		});
	}

	// Auto dismiss alerts after 3 seconds
	setTimeout(function() {
		const alerts = document.querySelectorAll('.alert');
		alerts.forEach(function(alert) {
			if(typeof bootstrap !== 'undefined') {
				const bsAlert = new bootstrap.Alert(alert);
				bsAlert.close();
			}
		});
	}, 3000);
	
});