// script.js (MyCommentPage)
document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

// Get context path
const contextPath = window.location.pathname.split('/')[1];

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
    const baiviet = urlParams.get('baiviet');
    const search = urlParams.get('search');
    const page = urlParams.get('page');
    
    let url = '/' + contextPath + '/BinhLuanCuaToiController?sort=' + value;
    if (baiviet) url += '&baiviet=' + encodeURIComponent(baiviet);
    if (search) url += '&search=' + encodeURIComponent(search);
    if (page) url += '&page=' + encodeURIComponent(page);
    
    window.location.href = url;
}

// Handle filter by bai viet
function handleFilterBaiViet(value) {
    const urlParams = new URLSearchParams(window.location.search);
    const search = urlParams.get('search');
    const sort = urlParams.get('sort');
    const page = urlParams.get('page');
    
    let url = '/' + contextPath + '/BinhLuanCuaToiController';
    let params = [];
    
    if (search) params.push('search=' + encodeURIComponent(search));
    if (sort) params.push('sort=' + sort);
    if (value) params.push('baiviet=' + encodeURIComponent(value));
    if (page) params.push('page=' + encodeURIComponent(page));
    
    if (params.length > 0) url += '?' + params.join('&');
    
    window.location.href = url;
}

// Edit comment
function editComment(maBinhLuan) {
    // Find the comment card by maBinhLuan
    const commentCard = document.querySelector(`.comment-card[data-ma-binh-luan="${maBinhLuan}"]`);
    if (!commentCard) {
        alert('Không tìm thấy bình luận!');
        return;
    }

    // Get data from data attributes
    const maBinhLuanValue = commentCard.getAttribute('data-ma-binh-luan');
    const noiDung = commentCard.getAttribute('data-noi-dung').replace(/&#10;/g, '\n');
    const url = commentCard.getAttribute('data-url');

    // Populate the edit form
    document.getElementById('editMaBinhLuan').value = maBinhLuanValue;
    document.getElementById('editNoiDung').value = noiDung;
    document.getElementById('editUrl').value = url || '';

    // Show the edit modal
    const modal = new bootstrap.Modal(document.getElementById('editCommentModal'));
    modal.show();
}

// Delete comment
function deleteComment(maBinhLuan, tieuDe) {
    if (confirm('Bạn có chắc chắn muốn xóa bình luận "' + tieuDe + '"?')) {
        window.location.href = '/' + contextPath + '/XuLyBinhLuanCuaToiController?action=delete&maBinhLuan=' + maBinhLuan;
    }
}

// Handle like/unlike
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('like-icon')) {
        const maBinhLuan = e.target.getAttribute('data-ma-binh-luan');
        const daThich = e.target.getAttribute('data-da-thich') === 'true';
        const action = daThich ? 'unlike' : 'like';
        
        fetch('/' + contextPath + '/XuLyBinhLuanCuaToiController?action=' + action + '&maBinhLuan=' + maBinhLuan)
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const commentCard = document.querySelector('.comment-card[data-ma-binh-luan="' + maBinhLuan + '"]');
                    if (!commentCard) return;
                    
                    const likeIcon = commentCard.querySelector('.like-icon[data-ma-binh-luan="' + maBinhLuan + '"]');
                    const likeCount = commentCard.querySelector('.like-count[data-ma-binh-luan="' + maBinhLuan + '"]');
                    
                    if (action === 'like') {
                        likeIcon.classList.remove('bi-hand-thumbs-up');
                        likeIcon.classList.add('bi-hand-thumbs-up-fill');
                        likeIcon.style.color = '#667eea';
                        likeCount.textContent = parseInt(likeCount.textContent) + 1;
                        likeIcon.setAttribute('data-da-thich', 'true');
                    } else {
                        likeIcon.classList.remove('bi-hand-thumbs-up-fill');
                        likeIcon.classList.add('bi-hand-thumbs-up');
                        likeIcon.style.color = '';
                        likeCount.textContent = parseInt(likeCount.textContent) - 1;
                        likeIcon.setAttribute('data-da-thich', 'false');
                    }
                } else {
                    alert(data.message || 'Có lỗi xảy ra!');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Có lỗi xảy ra! Vui lòng thử lại.');
            });
    }
    
    // Handle click on like count
    if (e.target.classList.contains('like-count')) {
        const maBinhLuan = e.target.getAttribute('data-ma-binh-luan');
        showLikeList(maBinhLuan);
    }
});

// Show like list
function showLikeList(maBinhLuan) {
    let modal = bootstrap.Modal.getInstance(document.getElementById('likeListModal'));
    if (!modal) {
        modal = new bootstrap.Modal(document.getElementById('likeListModal'));
    }
    
    const content = document.getElementById('likeListContent');
    content.innerHTML = '<div class="text-center py-3"><div class="spinner-border text-primary" role="status"></div></div>';
    modal.show();
    
    fetch('/' + contextPath + '/XuLyBinhLuanCuaToiController?action=getLikeList&maBinhLuan=' + maBinhLuan)
        .then(response => response.json())
        .then(data => {
            if (data.success && data.list && data.list.length > 0) {
                let html = '<div class="list-group">';
                for (let i = 0; i < data.list.length; i++) {
                    const item = data.list[i];
                    html += '<div class="list-group-item d-flex align-items-center">';
                    html += '<i class="bi bi-person-circle me-3" style="font-size: 2rem; color: #667eea;"></i>';
                    html += '<div><div class="fw-bold">' + item.tenDangNhap + '</div>';
                    html += '<small class="text-muted">' + item.thoiDiemThich + '</small></div></div>';
                }
                html += '</div>';
                content.innerHTML = html;
            } else {
                content.innerHTML = '<div class="text-center py-3 text-muted">Chưa có ai thích bình luận này</div>';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            content.innerHTML = '<div class="alert alert-danger">Không thể tải danh sách!</div>';
        });
}

// Close modal on escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        const modals = ['addCommentModal', 'editCommentModal', 'likeListModal'];
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
            const noiDungInput = form.querySelector('textarea[name="noiDung"]');
            
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
// UPLOAD FILE FUNCTIONALITY
// ============================================

// Global variables
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
    
    console.log('File selected:', file.name, file.type, file.size);
    
    // Validate file type
    const validImageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    const validVideoTypes = ['video/mp4', 'video/webm', 'video/ogg'];
    const allValidTypes = [...validImageTypes, ...validVideoTypes];
    
    if (!allValidTypes.includes(file.type)) {
        alert('Định dạng file không được hỗ trợ! Vui lòng chọn file ảnh (.jpg, .png, .gif, .webp) hoặc video (.mp4, .webm, .ogg)');
        event.target.value = '';
        return;
    }
    
    // Validate file size (50MB)
    if (file.size > 50 * 1024 * 1024) {
        alert('Kích thước file quá lớn! Vui lòng chọn file nhỏ hơn 50MB');
        event.target.value = '';
        return;
    }
    
    // Update file status
    document.getElementById('addFileStatus').textContent = 'Đang chuẩn bị tải lên...';
    document.getElementById('addFileStatus').classList.remove('text-muted');
    document.getElementById('addFileStatus').classList.add('text-primary');
    
    // Show preview
    showAddFilePreview(file);
    
    // Upload file
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
    
    fetch('/' + contextPath + '/UploadFileController', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
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
            img.className = 'preview-image';
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
            video.className = 'preview-video';
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
    
    // Validate file type
    const validImageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    const validVideoTypes = ['video/mp4', 'video/webm', 'video/ogg'];
    const allValidTypes = [...validImageTypes, ...validVideoTypes];
    
    if (!allValidTypes.includes(file.type)) {
        alert('Định dạng file không được hỗ trợ! Vui lòng chọn file ảnh (.jpg, .png, .gif, .webp) hoặc video (.mp4, .webm, .ogg)');
        event.target.value = '';
        return;
    }
    
    // Validate file size (50MB)
    if (file.size > 50 * 1024 * 1024) {
        alert('Kích thước file quá lớn! Vui lòng chọn file nhỏ hơn 50MB');
        event.target.value = '';
        return;
    }
    
    // Update file status
    document.getElementById('editFileStatus').textContent = 'Đang chuẩn bị tải lên...';
    document.getElementById('editFileStatus').classList.remove('text-muted');
    document.getElementById('editFileStatus').classList.add('text-primary');
    
    // Show preview
    showEditFilePreview(file);
    
    // Show remove button
    document.getElementById('editRemoveFileBtn').style.display = 'inline-block';
    
    // Mark that we're uploading a new file
    document.getElementById('editKeepOldFile').value = 'false';
    
    // Upload file
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
    
    fetch('/' + contextPath + '/UploadFileController', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
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
            img.className = 'preview-image';
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
            video.className = 'preview-video';
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

// Override editComment function
const originalEditComment = window.editComment;
window.editComment = function(maBinhLuan) {
    // Find the comment card by maBinhLuan
    const commentCard = document.querySelector(`.comment-card[data-ma-binh-luan="${maBinhLuan}"]`);
    if (!commentCard) {
        alert('Không tìm thấy bình luận!');
        return;
    }

    // Get data from data attributes
    const maBinhLuanValue = commentCard.getAttribute('data-ma-binh-luan');
    const noiDung = commentCard.getAttribute('data-noi-dung').replace(/&#10;/g, '\n');
    const url = commentCard.getAttribute('data-url');

    // Populate the edit form
    document.getElementById('editMaBinhLuan').value = maBinhLuanValue;
    document.getElementById('editNoiDung').value = noiDung;
    
    // Handle file
    editOriginalFileUrl = url || '';
    editUploadedFileUrl = '';
    document.getElementById('editFileInput').value = '';
    document.getElementById('editUrlHidden').value = editOriginalFileUrl;
    document.getElementById('editKeepOldFile').value = 'true';
    
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
            img.className = 'preview-image';
            img.style.maxWidth = '100%';
            img.style.maxHeight = '400px';
            img.style.objectFit = 'contain';
            img.style.borderRadius = '12px';
            preview.appendChild(img);
        } else if (editOriginalFileUrl.match(/\.(mp4|webm|ogg)$/i)) {
            const video = document.createElement('video');
            video.src = fullUrl;
            video.controls = true;
            video.className = 'preview-video';
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

    // Show the edit modal
    const modal = new bootstrap.Modal(document.getElementById('editCommentModal'));
    modal.show();
};

// Reset add modal when opened
document.getElementById('addCommentModal').addEventListener('show.bs.modal', function() {
    addUploadedFileUrl = '';
    document.getElementById('addFileInput').value = '';
    document.getElementById('addFileStatus').textContent = 'Chưa có ảnh/video';
    document.getElementById('addFileStatus').classList.remove('text-success', 'text-primary');
    document.getElementById('addFileStatus').classList.add('text-muted');
    document.getElementById('addFilePreview').style.display = 'none';
    document.getElementById('addUrlHidden').value = '';
    console.log('Add modal opened, upload state reset');
});