// script.js (MyPostPage) - FIXED VERSION

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Initialize TinyMCE
    initTinyMCE();
});

// TinyMCE Initialization
let editorInstances = {};

function initTinyMCE() {
    tinymce.init({
        selector: '.tinymce-editor',
        height: 300,
        menubar: false,
        language: 'vi',
        plugins: [
            'advlist', 'autolink', 'lists', 'link', 'image', 'charmap',
            'anchor', 'searchreplace', 'visualblocks', 'code', 'fullscreen',
            'insertdatetime', 'media', 'table', 'preview', 'help', 'wordcount'
        ],
        toolbar: 'undo redo | blocks | bold italic forecolor | alignleft aligncenter ' +
                 'alignright alignjustify | bullist numlist outdent indent | removeformat | help',
        content_style: 'body { font-family:Helvetica,Arial,sans-serif; font-size:14px }',
        setup: function(editor) {
            editorInstances[editor.id] = editor;
            
            // Remove required attribute when editor initializes
            editor.on('init', function() {
                var textarea = document.getElementById(editor.id);
                if (textarea) {
                    textarea.removeAttribute('required');
                }
            });
        }
    });
}

// AI-Enhanced Search
async function enhanceSearch() {
    const searchInput = document.getElementById('searchInput');
    const query = searchInput.value.trim();
    
    if (!query) {
        alert('Vui lòng nhập từ khóa tìm kiếm!');
        return;
    }
    
    searchInput.disabled = true;
    const originalPlaceholder = searchInput.placeholder;
    searchInput.placeholder = 'Đang xử lý với AI...';
    
    try {
        const contextPath = window.location.pathname.split('/')[1];
        const response = await fetch('/' + contextPath + '/AISearchController', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'query=' + encodeURIComponent(query) + '&context=baiviet'
        });
        
        const data = await response.json();
        
        if (data.success) {
            searchInput.value = data.enhancedQuery;
            const form = searchInput.closest('form');
            if (form) {
                setTimeout(function() { form.submit(); }, 100);
            }
        } else {
            alert('Không thể xử lý với AI. Tìm kiếm thường...');
            searchInput.closest('form').submit();
        }
    } catch (error) {
        console.error('AI Search error:', error);
        searchInput.closest('form').submit();
    } finally {
        searchInput.disabled = false;
        searchInput.placeholder = originalPlaceholder;
    }
}

// Show message alert with auto dismiss
function showMessage(message, type) {
    const alertContainer = document.getElementById('messageAlert');
    const messageText = document.getElementById('messageText');
    const alert = alertContainer.querySelector('.alert');
    const icon = alert.querySelector('i');
    
    messageText.textContent = message;
    
    alert.className = 'alert fade show';
    if (type === 'success') {
        alert.classList.add('alert-success');
        icon.className = 'bi bi-check-circle-fill me-2';
    } else {
        alert.classList.add('alert-danger');
        icon.className = 'bi bi-exclamation-circle-fill me-2';
    }
    
    alertContainer.style.display = 'block';
    
    setTimeout(function() {
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
    const postCard = document.querySelector('.post-card[data-ma-bai-viet="' + maBaiViet + '"]');
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
    
    // Set TinyMCE content
    setTimeout(function() {
        if (tinymce.get('editNoiDung')) {
            tinymce.get('editNoiDung').setContent(noiDung);
        }
    }, 100);
    
    // Handle file upload state
    handleEditFileState(url);

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

// Form submit handlers for TinyMCE
document.addEventListener('DOMContentLoaded', function() {
    const addPostForm = document.getElementById('addPostForm');
    if (addPostForm) {
        addPostForm.addEventListener('submit', function(e) {
            // Get content from TinyMCE
            if (tinymce.get('addNoiDung')) {
                var content = tinymce.get('addNoiDung').getContent();
                document.getElementById('addNoiDung').value = content;
                
                // Custom validation
                if (!content || content.trim() === '') {
                    e.preventDefault();
                    alert('Vui lòng nhập nội dung bài viết!');
                    return false;
                }
            }
        });
    }

    const editPostForm = document.getElementById('editPostForm');
    if (editPostForm) {
        editPostForm.addEventListener('submit', function(e) {
            // Get content from TinyMCE
            if (tinymce.get('editNoiDung')) {
                var content = tinymce.get('editNoiDung').getContent();
                document.getElementById('editNoiDung').value = content;
                
                // Custom validation
                if (!content || content.trim() === '') {
                    e.preventDefault();
                    alert('Vui lòng nhập nội dung bài viết!');
                    return false;
                }
            }
        });
    }
});

// Reset TinyMCE when opening add modal
document.getElementById('addPostModal').addEventListener('show.bs.modal', function () {
    setTimeout(function() {
        if (tinymce.get('addNoiDung')) {
            tinymce.get('addNoiDung').setContent('');
        }
    }, 100);
});

// ============================================
// Upload File Functions
// ============================================

let addUploadedFileUrl = '';
let editUploadedFileUrl = '';
let editOriginalFileUrl = '';

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

function handleAddFileSelect(event) {
    const file = event.target.files[0];
    if (!file) return;
    
    const validImageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    const validVideoTypes = ['video/mp4', 'video/webm', 'video/ogg'];
    const allValidTypes = validImageTypes.concat(validVideoTypes);
    
    if (allValidTypes.indexOf(file.type) === -1) {
        alert('Định dạng file không được hỗ trợ!');
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

function uploadAddFile(file) {
    const formData = new FormData();
    formData.append('file', file);
    
    const statusElement = document.getElementById('addFileStatus');
    statusElement.innerHTML = '<i class="spinner-border spinner-border-sm me-2"></i>Đang tải lên...';
    
    const contextPath = window.location.pathname.split('/')[1];
    
    fetch('/' + contextPath + '/UploadFileController', {
        method: 'POST',
        body: formData
    })
    .then(function(response) {
        return response.json();
    })
    .then(function(data) {
        if (data.success) {
            addUploadedFileUrl = data.url;
            document.getElementById('addUrlHidden').value = data.url;
            statusElement.textContent = data.fileName;
            statusElement.classList.remove('text-primary');
            statusElement.classList.add('text-success');
        } else {
            throw new Error(data.message || 'Upload thất bại');
        }
    })
    .catch(function(error) {
        console.error('Upload error:', error);
        statusElement.textContent = 'Lỗi: ' + error.message;
        statusElement.classList.remove('text-primary');
        statusElement.classList.add('text-danger');
        document.getElementById('addFileInput').value = '';
        document.getElementById('addFilePreview').style.display = 'none';
        addUploadedFileUrl = '';
    });
}

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

function handleEditFileSelect(event) {
    const file = event.target.files[0];
    if (!file) return;
    
    const validImageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    const validVideoTypes = ['video/mp4', 'video/webm', 'video/ogg'];
    const allValidTypes = validImageTypes.concat(validVideoTypes);
    
    if (allValidTypes.indexOf(file.type) === -1) {
        alert('Định dạng file không được hỗ trợ!');
        event.target.value = '';
        return;
    }
    
    if (file.size > 50 * 1024 * 1024) {
        alert('Kích thước file quá lớn!');
        event.target.value = '';
        return;
    }
    
    document.getElementById('editFileStatus').textContent = 'Đang chuẩn bị tải lên...';
    showEditFilePreview(file);
    document.getElementById('editRemoveFileBtn').style.display = 'inline-block';
    document.getElementById('editKeepOldFile').value = 'false';
    
    uploadEditFile(file);
}

function uploadEditFile(file) {
    const formData = new FormData();
    formData.append('file', file);
    
    const statusElement = document.getElementById('editFileStatus');
    statusElement.innerHTML = '<i class="spinner-border spinner-border-sm me-2"></i>Đang tải lên...';
    
    const contextPath = window.location.pathname.split('/')[1];
    
    fetch('/' + contextPath + '/UploadFileController', {
        method: 'POST',
        body: formData
    })
    .then(function(response) {
        return response.json();
    })
    .then(function(data) {
        if (data.success) {
            editUploadedFileUrl = data.url;
            document.getElementById('editUrlHidden').value = data.url;
            statusElement.textContent = data.fileName;
            statusElement.classList.remove('text-primary');
            statusElement.classList.add('text-success');
        } else {
            throw new Error(data.message || 'Upload thất bại');
        }
    })
    .catch(function(error) {
        console.error('Edit upload error:', error);
        statusElement.textContent = 'Lỗi: ' + error.message;
        document.getElementById('editFileInput').value = '';
        document.getElementById('editFilePreview').style.display = 'none';
        editUploadedFileUrl = '';
    });
}

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

function handleEditFileState(url) {
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
}

document.getElementById('addPostModal').addEventListener('show.bs.modal', function () {
    addUploadedFileUrl = '';
    document.getElementById('addFileInput').value = '';
    document.getElementById('addFileStatus').textContent = 'Chưa có ảnh/video';
    document.getElementById('addFileStatus').classList.remove('text-success', 'text-primary');
    document.getElementById('addFileStatus').classList.add('text-muted');
    document.getElementById('addFilePreview').style.display = 'none';
    document.getElementById('addUrlHidden').value = '';
});