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