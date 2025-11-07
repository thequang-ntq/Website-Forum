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

// Show Add Modal
function showAddModal() {
	const modal = new bootstrap.Modal(document.getElementById('addModal'));
	const form = document.getElementById('addForm');
	const input = document.getElementById('addTenTheLoai');
	const errorDiv = document.getElementById('addError');
	const submitBtn = document.getElementById('addSubmitBtn');
	
	// Reset form
	form.reset();
	input.classList.remove('is-invalid');
	errorDiv.textContent = '';
	submitBtn.disabled = false;
	
	modal.show();
}

// Show Edit Modal
function showEditModal(maTheLoai, tenTheLoai) {
	const modal = new bootstrap.Modal(document.getElementById('editModal'));
	const form = document.getElementById('editForm');
	const maInput = document.getElementById('editMaTheLoai');
	const tenInput = document.getElementById('editTenTheLoai');
	const errorDiv = document.getElementById('editError');
	const submitBtn = document.getElementById('editSubmitBtn');
	
	// Set values
	maInput.value = maTheLoai;
	tenInput.value = tenTheLoai;
	
	// Reset validation
	tenInput.classList.remove('is-invalid');
	errorDiv.textContent = '';
	submitBtn.disabled = false;
	
	modal.show();
}

// Show Delete Modal
function showDeleteModal(maTheLoai, tenTheLoai) {
	const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
	const maInput = document.getElementById('deleteMaTheLoai');
	const tenSpan = document.getElementById('deleteTenTheLoai');
	
	// Set values
	maInput.value = maTheLoai;
	tenSpan.textContent = tenTheLoai;
	
	modal.show();
}

// Validate Add Form
function validateAddForm() {
	const input = document.getElementById('addTenTheLoai');
	const errorDiv = document.getElementById('addError');
	const submitBtn = document.getElementById('addSubmitBtn');
	const value = input.value.trim();
	
	// Check empty
	if(value === '') {
		input.classList.add('is-invalid');
		errorDiv.textContent = 'Tên thể loại không được để trống';
		submitBtn.disabled = true;
		return false;
	}
	
	// Check length
	if(value.length > 200) {
		input.classList.add('is-invalid');
		errorDiv.textContent = 'Tên thể loại không được quá 200 ký tự';
		submitBtn.disabled = true;
		return false;
	}
	
	// Valid
	input.classList.remove('is-invalid');
	errorDiv.textContent = '';
	submitBtn.disabled = false;
	return true;
}

// Validate Edit Form
function validateEditForm() {
	const input = document.getElementById('editTenTheLoai');
	const errorDiv = document.getElementById('editError');
	const submitBtn = document.getElementById('editSubmitBtn');
	const value = input.value.trim();
	
	// Check empty
	if(value === '') {
		input.classList.add('is-invalid');
		errorDiv.textContent = 'Tên thể loại không được để trống';
		submitBtn.disabled = true;
		return false;
	}
	
	// Check length
	if(value.length > 200) {
		input.classList.add('is-invalid');
		errorDiv.textContent = 'Tên thể loại không được quá 200 ký tự';
		submitBtn.disabled = true;
		return false;
	}
	
	// Valid
	input.classList.remove('is-invalid');
	errorDiv.textContent = '';
	submitBtn.disabled = false;
	return true;
}

// Handle Add Form Submit
document.addEventListener('DOMContentLoaded', function() {
	const addForm = document.getElementById('addForm');
	if(addForm) {
		addForm.addEventListener('submit', function(e) {
			if(!validateAddForm()) {
				e.preventDefault();
			}
		});
	}
	
	const editForm = document.getElementById('editForm');
	if(editForm) {
		editForm.addEventListener('submit', function(e) {
			if(!validateEditForm()) {
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