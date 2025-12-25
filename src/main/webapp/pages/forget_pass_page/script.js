// File này hiện tại không dùng


// Đợi DOM load xong mới chạy script
document.addEventListener('DOMContentLoaded', function() {
	
	// Toggle password visibility cho mật khẩu mới
	const togglePasswordBtn1 = document.getElementById('togglePassword1');
	if(togglePasswordBtn1) {
		togglePasswordBtn1.addEventListener('click', function() {
			const passwordInput = document.getElementById('matKhauMoi');
			const toggleIcon = document.getElementById('toggleIcon1');
			
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

	// Toggle password visibility cho nhập lại mật khẩu mới
	const togglePasswordBtn2 = document.getElementById('togglePassword2');
	if(togglePasswordBtn2) {
		togglePasswordBtn2.addEventListener('click', function() {
			const passwordInput = document.getElementById('nhapLaiMatKhauMoi');
			const toggleIcon = document.getElementById('toggleIcon2');
			
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

	// Form validation on submit
	const forgetPassForm = document.getElementById('forgetPassForm');
	if(forgetPassForm) {
		forgetPassForm.addEventListener('submit', function(e) {
			const tenDangNhap = document.getElementById('tenDangNhap').value.trim();
			const matKhauMoi = document.getElementById('matKhauMoi').value.trim();
			const nhapLaiMatKhauMoi = document.getElementById('nhapLaiMatKhauMoi').value.trim();
			
			if(!tenDangNhap || !matKhauMoi || !nhapLaiMatKhauMoi) {
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