document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

// Show message alert with auto dismiss
function showMessage(message, type) {
	const alertContainer = document.getElementById('messageAlert');
	const messageText = document.getElementById('messageText');
	const alert = alertContainer.querySelector('.alert');
	const icon = alert.querySelector('i');
	
	// Set message text
	messageText.textContent = message;
	
	// Set alert type
	alert.className = 'alert fade show';
	if (type === 'success') {
		alert.classList.add('alert-success');
		icon.className = 'bi bi-check-circle-fill me-2';
	} else {
		alert.classList.add('alert-danger');
		icon.className = 'bi bi-exclamation-circle-fill me-2';
	}
	
	// Show alert
	alertContainer.style.display = 'block';
	
	// Auto hide after 3 seconds
	setTimeout(() => {
		alertContainer.style.display = 'none';
	}, 3000);
}

// Handle sort selection
function handleSort(value) {
    if (!value) return;
    
    const urlParams = new URLSearchParams(window.location.search);
    const contextPath = window.location.pathname.split('/')[1];
    const theloai = urlParams.get('theloai');
    const search = urlParams.get('search');
    const page = urlParams.get('page');
    
    let url = '/' + contextPath + '/BaiVietCuaToiController?sort=' + value;
    if (theloai) url += '&theloai=' + encodeURIComponent(theloai);
    if (search) url += '&search=' + encodeURIComponent(search);
    if (page) url += '&page=' + encodeURIComponent(page);
    
    window.location.href = url;
}

// Handle filter by the loai
function handleFilterTheLoai(value) {
    const urlParams = new URLSearchParams(window.location.search);
    const contextPath = window.location.pathname.split('/')[1];
    const search = urlParams.get('search');
    const sort = urlParams.get('sort');
    const page = urlParams.get('page');
    
    let url = '/' + contextPath + '/BaiVietCuaToiController';
    let params = [];
    
    if (search) params.push('search=' + encodeURIComponent(search));
    if (sort) params.push('sort=' + sort);
    if (value) params.push('theloai=' + encodeURIComponent(value));
    if (page) params.push('page=' + encodeURIComponent(page));
    
    if (params.length > 0) url += '?' + params.join('&');
    
    window.location.href = url;
}

// Edit post
function editPost(maBaiViet) {
    // Find the post card by maBaiViet
    const postCard = document.querySelector(`.post-card[data-ma-bai-viet="${maBaiViet}"]`);
    if (!postCard) {
        alert('Không tìm thấy bài viết!');
        return;
    }

    // Get data from data attributes
    const maBaiVietValue = postCard.getAttribute('data-ma-bai-viet');
    const tieuDe = postCard.getAttribute('data-tieu-de');
    const noiDung = postCard.getAttribute('data-noi-dung').replace(/&#10;/g, '\n');
    const url = postCard.getAttribute('data-url');
    const maTheLoai = postCard.getAttribute('data-ma-the-loai');

    // Populate the edit form
    document.getElementById('editMaBaiViet').value = maBaiVietValue;
    document.getElementById('editTieuDe').value = tieuDe;
    document.getElementById('editNoiDung').value = noiDung;
    document.getElementById('editUrl').value = url || '';
    document.getElementById('editMaTheLoai').value = maTheLoai;

    // Show the edit modal
    const modal = new bootstrap.Modal(document.getElementById('editPostModal'));
    modal.show();
}

// Delete post
function deletePost(maBaiViet, tieuDe) {
    if (confirm('Bạn có chắc chắn muốn xóa bài viết "' + tieuDe + '"?\n\nLưu ý: Tất cả bình luận của bài viết này cũng sẽ bị xóa!')) {
        const contextPath = window.location.pathname.split('/')[1];
        window.location.href = '/' + contextPath + '/XuLyBaiVietCuaToiController?action=delete&maBaiViet=' + maBaiViet;
    }
}

// Close modal on escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        const modals = ['postDetailModal', 'addPostModal', 'editPostModal'];
        modals.forEach(modalId => {
            const modalElement = document.getElementById(modalId);
            if (modalElement) {
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
            }
        });
    }
});

// Form validation
document.addEventListener('DOMContentLoaded', function() {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const tieuDeInput = form.querySelector('input[name="tieuDe"]');
            const noiDungInput = form.querySelector('textarea[name="noiDung"]');
            
            if (tieuDeInput && tieuDeInput.value.trim().length > 255) {
                e.preventDefault();
                alert('Tiêu đề không được quá 255 ký tự!');
                tieuDeInput.focus();
                return false;
            }
            
            if (noiDungInput && noiDungInput.value.trim() === '') {
                e.preventDefault();
                alert('Nội dung không được để trống!');
                noiDungInput.focus();
                return false;
            }
        });
    });
});
