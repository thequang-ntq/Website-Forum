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

// ============================================
// Upload File Functions
// ============================================

let addUploadedFileUrl = '';
let editUploadedFileUrl = '';
let editOriginalFileUrl = '';

// Initialize upload listeners
document.addEventListener('DOMContentLoaded', function() {
    const addFileInput = document.getElementById('addFileInput');
    if(addFileInput) {
        addFileInput.addEventListener('change', handleAddFileSelect);
    }
    
    const editFileInput = document.getElementById('editFileInput');
    if(editFileInput) {
        editFileInput.addEventListener('change', handleEditFileSelect);
    }
});

// Handle Add File Select
function handleAddFileSelect(event) {
    const file = event.target.files[0];
    if (!file) return;
    
    console.log('Add file selected:', file.name, file.type, file.size);
    
    const validImageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    const validVideoTypes = ['video/mp4', 'video/webm', 'video/ogg'];
    const allValidTypes = [...validImageTypes, ...validVideoTypes];
    
    if (!allValidTypes.includes(file.type)) {
        alert('Định dạng file không được hỗ trợ! Vui lòng chọn file ảnh (.jpg, .png, .gif, .webp) hoặc video (.mp4, .webm, .ogg)');
        event.target.value = '';
        return;
    }
    
    if (file.size > 50 * 1024 * 1024) {
        alert('Kích thước file quá lớn! Vui lòng chọn file nhỏ hơn 50MB');
        event.target.value = '';
        return;
    }
    
    document.getElementById('addFileStatus').textContent = 'Đang chuẩn bị tải lên...';
    document.getElementById('addFileStatus').classList.remove('text-muted');
    document.getElementById('addFileStatus').classList.add('text-primary');
    
    showAddFilePreview(file);
    uploadAddFile(file);
}

// Upload Add File
function uploadAddFile(file) {
    const formData = new FormData();
    formData.append('file', file);
    
    const statusElement = document.getElementById('addFileStatus');
    statusElement.innerHTML = '<i class="spinner-border spinner-border-sm me-2"></i>Đang tải lên...';
    statusElement.classList.remove('text-success', 'text-danger', 'text-muted');
    statusElement.classList.add('text-primary');
    
    const contextPath = window.location.pathname.split('/')[1];
    
    fetch('/' + contextPath + '/UploadFileController', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        console.log('Upload response status:', response.status);
        return response.json();
    })
    .then(data => {
        console.log('Upload response data:', data);
        if (data.success) {
            addUploadedFileUrl = data.url;
            document.getElementById('addUrlHidden').value = data.url;
            statusElement.textContent = data.fileName;
            statusElement.classList.remove('text-primary');
            statusElement.classList.add('text-success');
            console.log('Upload success! URL:', data.url);
        } else {
            throw new Error(data.message || 'Upload thất bại');
        }
    })
    .catch(error => {
        console.error('Upload error:', error);
        statusElement.textContent = 'Lỗi: ' + error.message;
        statusElement.classList.remove('text-primary');
        statusElement.classList.add('text-danger');
        document.getElementById('addFileInput').value = '';
        document.getElementById('addFilePreview').style.display = 'none';
        addUploadedFileUrl = '';
    });
}

// Show Add File Preview
function showAddFilePreview(file) {
    const preview = document.getElementById('addFilePreview');
    preview.innerHTML = '';
    preview.style.display = 'block';
    
    const reader = new FileReader();
    
    if (file.type.startsWith('image/')) {
        reader.onload = function(e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            img.style.maxWidth = '100%';
            img.style.maxHeight = '400px';
            img.style.objectFit = 'contain';
            img.style.borderRadius = '12px';
            preview.appendChild(img);
        };
        reader.readAsDataURL(file);
    } else if (file.type.startsWith('video/')) {
        reader.onload = function(e) {
            const video = document.createElement('video');
            video.src = e.target.result;
            video.controls = true;
            video.style.width = '100%';
            video.style.maxHeight = '400px';
            video.style.borderRadius = '12px';
            preview.appendChild(video);
        };
        reader.readAsDataURL(file);
    }
}

// Handle Edit File Select
function handleEditFileSelect(event) {
    const file = event.target.files[0];
    if (!file) return;
    
    console.log('Edit file selected:', file.name, file.type, file.size);
    
    const validImageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    const validVideoTypes = ['video/mp4', 'video/webm', 'video/ogg'];
    const allValidTypes = [...validImageTypes, ...validVideoTypes];
    
    if (!allValidTypes.includes(file.type)) {
        alert('Định dạng file không được hỗ trợ! Vui lòng chọn file ảnh (.jpg, .png, .gif, .webp) hoặc video (.mp4, .webm, .ogg)');
        event.target.value = '';
        return;
    }
    
    if (file.size > 50 * 1024 * 1024) {
        alert('Kích thước file quá lớn! Vui lòng chọn file nhỏ hơn 50MB');
        event.target.value = '';
        return;
    }
    
    document.getElementById('editFileStatus').textContent = 'Đang chuẩn bị tải lên...';
    document.getElementById('editFileStatus').classList.remove('text-muted');
    document.getElementById('editFileStatus').classList.add('text-primary');
    
    showEditFilePreview(file);
    document.getElementById('editRemoveFileBtn').style.display = 'inline-block';
    document.getElementById('editKeepOldFile').value = 'false';
    
    uploadEditFile(file);
}

// Upload Edit File
function uploadEditFile(file) {
    const formData = new FormData();
    formData.append('file', file);
    
    const statusElement = document.getElementById('editFileStatus');
    statusElement.innerHTML = '<i class="spinner-border spinner-border-sm me-2"></i>Đang tải lên...';
    statusElement.classList.remove('text-success', 'text-danger', 'text-muted');
    statusElement.classList.add('text-primary');
    
    const contextPath = window.location.pathname.split('/')[1];
    
    fetch('/' + contextPath + '/UploadFileController', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        console.log('Edit upload response status:', response.status);
        return response.json();
    })
    .then(data => {
        console.log('Edit upload response data:', data);
        if (data.success) {
            editUploadedFileUrl = data.url;
            document.getElementById('editUrlHidden').value = data.url;
            statusElement.textContent = data.fileName;
            statusElement.classList.remove('text-primary');
            statusElement.classList.add('text-success');
            console.log('Edit upload success! URL:', data.url);
        } else {
            throw new Error(data.message || 'Upload thất bại');
        }
    })
    .catch(error => {
        console.error('Edit upload error:', error);
        statusElement.textContent = 'Lỗi: ' + error.message;
        statusElement.classList.remove('text-primary');
        statusElement.classList.add('text-danger');
        document.getElementById('editFileInput').value = '';
        document.getElementById('editFilePreview').style.display = 'none';
        editUploadedFileUrl = '';
    });
}

// Show Edit File Preview
function showEditFilePreview(file) {
    const preview = document.getElementById('editFilePreview');
    preview.innerHTML = '';
    preview.style.display = 'block';
    
    const reader = new FileReader();
    
    if (file.type.startsWith('image/')) {
        reader.onload = function(e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            img.style.maxWidth = '100%';
            img.style.maxHeight = '400px';
            img.style.objectFit = 'contain';
            img.style.borderRadius = '12px';
            preview.appendChild(img);
        };
        reader.readAsDataURL(file);
    } else if (file.type.startsWith('video/')) {
        reader.onload = function(e) {
            const video = document.createElement('video');
            video.src = e.target.result;
            video.controls = true;
            video.style.width = '100%';
            video.style.maxHeight = '400px';
            video.style.borderRadius = '12px';
            preview.appendChild(video);
        };
        reader.readAsDataURL(file);
    }
}

// Remove Edit File
function removeEditFile() {
    document.getElementById('editFileInput').value = '';
    document.getElementById('editFileStatus').textContent = 'Chưa có ảnh/video';
    document.getElementById('editFileStatus').classList.remove('text-success');
    document.getElementById('editFileStatus').classList.add('text-muted');
    document.getElementById('editFilePreview').style.display = 'none';
    document.getElementById('editRemoveFileBtn').style.display = 'none';
    document.getElementById('editUrlHidden').value = '';
    document.getElementById('editKeepOldFile').value = 'false';
    editUploadedFileUrl = '';
}

// Update editPost function
const originalEditPost = editPost;
editPost = function(maBaiViet) {
    const postCard = document.querySelector(`.post-card[data-ma-bai-viet="${maBaiViet}"]`);
    if (!postCard) {
        alert('Không tìm thấy bài viết!');
        return;
    }

    const maBaiVietValue = postCard.getAttribute('data-ma-bai-viet');
    const tieuDe = postCard.getAttribute('data-tieu-de');
    const noiDung = postCard.getAttribute('data-noi-dung').replace(/&#10;/g, '\n');
    const url = postCard.getAttribute('data-url');
    const maTheLoai = postCard.getAttribute('data-ma-the-loai');

    document.getElementById('editMaBaiViet').value = maBaiVietValue;
    document.getElementById('editTieuDe').value = tieuDe;
    document.getElementById('editNoiDung').value = noiDung;
    document.getElementById('editMaTheLoai').value = maTheLoai;

    // Handle file
    editOriginalFileUrl = url || '';
    editUploadedFileUrl = '';
    document.getElementById('editFileInput').value = '';
    document.getElementById('editUrlHidden').value = editOriginalFileUrl;
    document.getElementById('editKeepOldFile').value = 'true';
    
    const contextPath = window.location.pathname.split('/')[1];
    
    if (editOriginalFileUrl && editOriginalFileUrl.trim() !== '') {
        const fileName = editOriginalFileUrl.split('/').pop();
        document.getElementById('editFileStatus').textContent = fileName;
        document.getElementById('editFileStatus').classList.remove('text-muted');
        document.getElementById('editFileStatus').classList.add('text-success');
        
        const preview = document.getElementById('editFilePreview');
        preview.innerHTML = '';
        preview.style.display = 'block';
        
        const fullUrl = '/' + contextPath + '/' + editOriginalFileUrl;
        
        if (editOriginalFileUrl.match(/\.(jpg|jpeg|png|gif|webp)$/i)) {
            const img = document.createElement('img');
            img.src = fullUrl;
            img.style.maxWidth = '100%';
            img.style.maxHeight = '400px';
            img.style.objectFit = 'contain';
            img.style.borderRadius = '12px';
            preview.appendChild(img);
        } else if (editOriginalFileUrl.match(/\.(mp4|webm|ogg)$/i)) {
            const video = document.createElement('video');
            video.src = fullUrl;
            video.controls = true;
            video.style.width = '100%';
            video.style.maxHeight = '400px';
            video.style.borderRadius = '12px';
            preview.appendChild(video);
        }
        
        document.getElementById('editRemoveFileBtn').style.display = 'inline-block';
    } else {
        document.getElementById('editFileStatus').textContent = 'Chưa có ảnh/video';
        document.getElementById('editFileStatus').classList.remove('text-success');
        document.getElementById('editFileStatus').classList.add('text-muted');
        document.getElementById('editFilePreview').style.display = 'none';
        document.getElementById('editRemoveFileBtn').style.display = 'none';
    }

    const modal = new bootstrap.Modal(document.getElementById('editPostModal'));
    modal.show();
};

// Reset upload state when opening add modal
document.getElementById('addPostModal').addEventListener('show.bs.modal', function () {
    addUploadedFileUrl = '';
    document.getElementById('addFileInput').value = '';
    document.getElementById('addFileStatus').textContent = 'Chưa có ảnh/video';
    document.getElementById('addFileStatus').classList.remove('text-success', 'text-primary');
    document.getElementById('addFileStatus').classList.add('text-muted');
    document.getElementById('addFilePreview').style.display = 'none';
    document.getElementById('addUrlHidden').value = '';
});
