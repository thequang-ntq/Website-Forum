// Đợi DOM load xong mới chạy script
document.addEventListener('DOMContentLoaded', function() {
	
	// Tự động đóng sau 3 giây
	setTimeout(function() {
	    var alert = document.getElementById('MyAlert');
	    if (alert) {
	        var bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
	        bsAlert.close();
	    }
	}, 3000);
	
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

	// Form validation on submit, thừa, đã validate trong XuLyDangNhapController
	const loginForm = document.getElementById('loginForm');
	if(loginForm) {
		loginForm.addEventListener('submit', function(e) {
			const tenDangNhap = document.getElementById('tenDangNhap').value.trim();
			const matKhau = document.getElementById('matKhau').value.trim();
			
			if(!tenDangNhap || !matKhau) {
				//
			}
		});
	}

	// Auto dismiss alerts after 3 seconds - tự tắt thông báo sau 3 giây
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