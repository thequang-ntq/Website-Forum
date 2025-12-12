// Đợi DOM load xong
document.addEventListener('DOMContentLoaded', function() {
	// Initialize tooltips
	var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
	var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
		return new bootstrap.Tooltip(tooltipTriggerEl);
	});
	
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
            body: 'query=' + encodeURIComponent(query) + '&context=binhluan'
        });
        
        const data = await response.json();
        
        if (data.success) {
            searchInput.value = data.enhancedQuery;
            setTimeout(() => handleSearch(), 100);
        } else {
            alert('Không thể xử lý với AI. Tìm kiếm thường...');
            handleSearch();
        }
    } catch (error) {
        console.error('AI Search error:', error);
        handleSearch();
    } finally {
        searchInput.disabled = false;
        searchInput.placeholder = originalPlaceholder;
    }
}

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
		let url = '/' + contextPath + '/BinhLuanController';
		
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

// Get comment data by ID
function getCommentData(maBinhLuan) {
	const commentItems = document.querySelectorAll('.comment-data-item');
	for(let item of commentItems) {
		if(item.getAttribute('data-id') == maBinhLuan) {
			return {
				maBinhLuan: item.getAttribute('data-id'),
				tieuDe: item.getAttribute('data-tieude'),
				noiDung: item.getAttribute('data-noidung'),
				url: item.getAttribute('data-url'),
				taiKhoanTao: item.getAttribute('data-taikhoantao'),
				maBaiViet: item.getAttribute('data-mabaiviet'),
				tenBaiViet: item.getAttribute('data-tenbaiviet'),
				soLuotThich: item.getAttribute('data-soluotthich'),
				trangThai: item.getAttribute('data-trangthai'),
				trangThaiVN: item.getAttribute('data-trangthaiVN'),
				thoiDiemTao: item.getAttribute('data-thoidiemtao'),
				thoiDiemCapNhat: item.getAttribute('data-thoidiemcapnhat')
			};
		}
	}
	return null;
}

// Show Detail Modal
function showDetailModal(maBinhLuan) {
	const modal = new bootstrap.Modal(document.getElementById('detailModal'));
	const content = document.getElementById('detailContent');
	
	// Get comment data
	const comment = getCommentData(maBinhLuan);
	
	if(!comment) {
		content.innerHTML = '<div class="alert alert-danger">Không tìm thấy thông tin bình luận</div>';
		modal.show();
		return;
	}
	
	// Build detail content
	let html = '<div class="detail-container">';
	
	// Tiêu đề (nếu có)
	if(comment.tieuDe && comment.tieuDe.trim() !== '') {
		html += '<div class="detail-item">';
		html += '<div class="detail-label"><i class="bi bi-card-heading"></i>Tiêu đề</div>';
		html += '<div class="detail-value">' + comment.tieuDe + '</div>';
		html += '</div>';
	}
	
	// Nội dung
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-file-text"></i>Nội dung</div>';
	html += '<div class="detail-value">' + comment.noiDung.replace(/\n/g, '<br>') + '</div>';
	html += '</div>';
	
	// Ảnh/Video
	if(comment.url && comment.url.trim() !== '') {
		html += '<div class="detail-item">';
		html += '<div class="detail-label"><i class="bi bi-image"></i>Media</div>';
		html += '<div class="detail-media">';
		
		// Check if image
		if(comment.url.match(/\.(jpg|jpeg|png|gif|webp)$/i)) {
			html += '<img src="' + comment.url + '" alt="Comment image" class="img-fluid">';
		}
		// Check if video
		else if(comment.url.match(/\.(mp4|webm|ogg)$/i)) {
			html += '<video controls class="w-100">';
			html += '<source src="' + comment.url + '" type="video/mp4">';
			html += 'Trình duyệt không hỗ trợ video.';
			html += '</video>';
		}
		
		html += '</div>';
		html += '</div>';
	}
	
	// Tài khoản tạo
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-person-fill"></i>Tác giả</div>';
	html += '<div class="detail-value">' + comment.taiKhoanTao + '</div>';
	html += '</div>';
	
	// Bài viết
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-file-text-fill"></i>Bài viết</div>';
	html += '<div class="detail-value">' + comment.tenBaiViet + '</div>';
	html += '</div>';
	
	// Số lượt thích
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-heart-fill"></i>Lượt thích</div>';
	html += '<div class="detail-value">';
	html += '<span class="badge bg-danger">' + comment.soLuotThich + ' <i class="bi bi-heart-fill ms-1"></i></span>';
	html += '</div>';
	html += '</div>';
	
	// Trạng thái
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-info-circle-fill"></i>Trạng thái</div>';
	html += '<div class="detail-value">';
	let badgeClass = 'bg-success';
	if(comment.trangThai === 'Deleted') badgeClass = 'bg-danger';
	else if(comment.trangThai === 'Hidden') badgeClass = 'bg-warning';
	html += '<span class="badge ' + badgeClass + '">' + comment.trangThaiVN + '</span>';
	html += '</div>';
	html += '</div>';
	
	// Thời điểm tạo
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-calendar-plus"></i>Thời điểm tạo</div>';
	html += '<div class="detail-value">' + (comment.thoiDiemTao || 'N/A') + '</div>';
	html += '</div>';
	
	// Thời điểm cập nhật
	html += '<div class="detail-item">';
	html += '<div class="detail-label"><i class="bi bi-calendar-check"></i>Thời điểm cập nhật</div>';
	html += '<div class="detail-value">' + (comment.thoiDiemCapNhat || 'Chưa cập nhật') + '</div>';
	html += '</div>';
	
	html += '</div>';
	
	content.innerHTML = html;
	modal.show();
}

// Show Add Modal
function showAddModal() {
	const modal = new bootstrap.Modal(document.getElementById('addModal'));
	const form = document.getElementById('addForm');
	const errorDiv = document.getElementById('addError');
	const submitBtn = document.getElementById('addSubmitBtn');
	
	form.reset();
	
	// Reinitialize TinyMCE for add modal
	setTimeout(() => {
	    if (tinymce.get('addNoiDung')) {
	        tinymce.get('addNoiDung').setContent('');
	    }
	}, 100);
	
	document.querySelectorAll('#addForm .form-control, #addForm .form-select').forEach(el => {
		el.classList.remove('is-invalid');
	});
	document.querySelectorAll('#addForm .invalid-feedback').forEach(el => {
		el.textContent = '';
	});
	
	errorDiv.style.display = 'none';
	submitBtn.disabled = false;
	
	modal.show();
}

// Show Edit Modal
function showEditModal(maBinhLuan) {
	const modal = new bootstrap.Modal(document.getElementById('editModal'));
	const form = document.getElementById('editForm');
	const errorDiv = document.getElementById('editError');
	const submitBtn = document.getElementById('editSubmitBtn');
	
	const comment = getCommentData(maBinhLuan);
	
	if(!comment) {
		alert('Không tìm thấy thông tin bình luận');
		return;
	}
	
	document.getElementById('editMaBinhLuan').value = comment.maBinhLuan;
	document.getElementById('editNoiDung').value = comment.noiDung;
	document.getElementById('editUrl').value = comment.url || '';
	document.getElementById('editSoLuotThich').value = comment.soLuotThich || '';
	document.getElementById('editTrangThai').value = comment.trangThai;
	
	// Set TinyMCE content for edit modal
	setTimeout(() => {
	    if (tinymce.get('editNoiDung')) {
	        tinymce.get('editNoiDung').setContent(comment.noiDung);
	    }
	}, 100);
	
	document.querySelectorAll('#editForm .form-control, #editForm .form-select').forEach(el => {
		el.classList.remove('is-invalid');
	});
	document.querySelectorAll('#editForm .invalid-feedback').forEach(el => {
		el.textContent = '';
	});
	
	errorDiv.style.display = 'none';
	submitBtn.disabled = false;
	
	modal.show();
}

// Show Delete Modal
function showDeleteModal(maBinhLuan, noiDung) {
	const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
	
	document.getElementById('deleteMaBinhLuan').value = maBinhLuan;
	document.getElementById('deleteNoiDung').textContent = noiDung;
	
	modal.show();
}

// Validate Add Form
function validateAddForm() {
	const noiDung = document.getElementById('addNoiDung').value.trim();
	const maBaiViet = document.getElementById('addMaBaiViet').value;
	const submitBtn = document.getElementById('addSubmitBtn');
	
	let isValid = true;
	
	// Validate nội dung
	const noiDungInput = document.getElementById('addNoiDung');
	const noiDungError = document.getElementById('addNoiDungError');
	if(noiDung === '') {
		noiDungInput.classList.add('is-invalid');
		noiDungError.textContent = 'Nội dung không được để trống';
		isValid = false;
	} else {
		noiDungInput.classList.remove('is-invalid');
		noiDungError.textContent = '';
	}
	
	// Validate bài viết
	const maBaiVietSelect = document.getElementById('addMaBaiViet');
	const maBaiVietError = document.getElementById('addMaBaiVietError');
	if(!maBaiViet || maBaiViet === '') {
		maBaiVietSelect.classList.add('is-invalid');
		maBaiVietError.textContent = 'Vui lòng chọn bài viết';
		isValid = false;
	} else {
		maBaiVietSelect.classList.remove('is-invalid');
		maBaiVietError.textContent = '';
	}
	
	submitBtn.disabled = !isValid;
	return isValid;
}

// Validate Edit Form
function validateEditForm() {
	const noiDung = document.getElementById('editNoiDung').value.trim();
	const submitBtn = document.getElementById('editSubmitBtn');
	
	let isValid = true;
	
	// Validate nội dung
	const noiDungInput = document.getElementById('editNoiDung');
	const noiDungError = document.getElementById('editNoiDungError');
	if(noiDung === '') {
		noiDungInput.classList.add('is-invalid');
		noiDungError.textContent = 'Nội dung không được để trống';
		isValid = false;
	} else {
		noiDungInput.classList.remove('is-invalid');
		noiDungError.textContent = '';
	}
	
	submitBtn.disabled = !isValid;
	return isValid;
}

// Handle Add Form Submit
document.addEventListener('DOMContentLoaded', function() {
	const addForm = document.getElementById('addForm');
	if(addForm) {
		addForm.addEventListener('submit', function(e) {
			if(!validateAddForm()) {
				e.preventDefault();
				const errorDiv = document.getElementById('addError');
				const errorText = document.getElementById('addErrorText');
				errorText.textContent = 'Vui lòng kiểm tra lại các trường đã nhập!';
				errorDiv.style.display = 'block';
				
				setTimeout(() => {
					errorDiv.style.display = 'none';
				}, 3000);
			}
		});
	}
	
	const editForm = document.getElementById('editForm');
	if(editForm) {
		editForm.addEventListener('submit', function(e) {
			if(!validateEditForm()) {
				e.preventDefault();
				const errorDiv = document.getElementById('editError');
				const errorText = document.getElementById('editErrorText');
				errorText.textContent = 'Vui lòng kiểm tra lại các trường đã nhập!';
				errorDiv.style.display = 'block';
				
				setTimeout(() => {
					errorDiv.style.display = 'none';
				}, 3000);
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

// Form submit handlers for TinyMCE
document.addEventListener('DOMContentLoaded', function() {
    const addForm = document.getElementById('addForm');
    if (addForm) {
        addForm.addEventListener('submit', function(e) {
            if (tinymce.get('addNoiDung')) {
                var content = tinymce.get('addNoiDung').getContent();
                document.getElementById('addNoiDung').value = content;
                
                if (!content || content.trim() === '') {
                    e.preventDefault();
                    alert('Vui lòng nhập nội dung bình luận!');
                    return false;
                }
            }
        });
    }

    const editForm = document.getElementById('editForm');
    if (editForm) {
        editForm.addEventListener('submit', function(e) {
            if (tinymce.get('editNoiDung')) {
                var content = tinymce.get('editNoiDung').getContent();
                document.getElementById('editNoiDung').value = content;
                
                if (!content || content.trim() === '') {
                    e.preventDefault();
                    alert('Vui lòng nhập nội dung bình luận!');
                    return false;
                }
            }
        });
    }
});

// Reset TinyMCE when opening add modal
document.getElementById('addModal').addEventListener('show.bs.modal', function () {
    setTimeout(() => {
        if (tinymce.get('addNoiDung')) {
            tinymce.get('addNoiDung').setContent('');
        }
    }, 100);
});

// ============================================
// UPLOAD FILE FUNCTIONALITY
// ============================================

// Global variables
let addUploadedFileUrl = '';
let editUploadedFileUrl = '';
let editOriginalFileUrl = '';

// Initialize upload listeners when DOM loaded
document.addEventListener('DOMContentLoaded', function() {
	// Add file input listener
	const addFileInput = document.getElementById('addFileInput');
	if(addFileInput) {
		addFileInput.addEventListener('change', handleAddFileSelect);
	}
	
	// Edit file input listener
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

// Override showAddModal để reset upload state
const originalShowAddModal = window.showAddModal;
window.showAddModal = function() {
	if(originalShowAddModal) {
		originalShowAddModal();
	}
	
	// Reset upload state
	addUploadedFileUrl = '';
	document.getElementById('addFileInput').value = '';
	document.getElementById('addFileStatus').textContent = 'Chưa có ảnh/video';
	document.getElementById('addFileStatus').classList.remove('text-success', 'text-primary');
	document.getElementById('addFileStatus').classList.add('text-muted');
	document.getElementById('addFilePreview').style.display = 'none';
	document.getElementById('addUrlHidden').value = '';
	console.log('Add modal opened, upload state reset');
};

// Override showEditModal để load file cũ
const originalShowEditModal = window.showEditModal;
window.showEditModal = function(maBinhLuan) {
	const comment = getCommentData(maBinhLuan);
	
	if (!comment) {
		alert('Không tìm thấy thông tin bình luận');
		return;
	}
	
	console.log('Opening edit modal for comment:', maBinhLuan, comment);
	
	// Call original
	const modal = new bootstrap.Modal(document.getElementById('editModal'));
	const form = document.getElementById('editForm');
	
	// Set values
	document.getElementById('editMaBinhLuan').value = comment.maBinhLuan;
	document.getElementById('editNoiDung').value = comment.noiDung;
	document.getElementById('editSoLuotThich').value = comment.soLuotThich || '';
	document.getElementById('editTrangThai').value = comment.trangThai;
	
	// Handle file
	editOriginalFileUrl = comment.url || '';
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
	
	// Reset validation
	document.querySelectorAll('#editForm .form-control, #editForm .form-select').forEach(el => {
		el.classList.remove('is-invalid');
	});
	document.querySelectorAll('#editForm .invalid-feedback').forEach(el => {
		el.textContent = '';
	});
	
	document.getElementById('editError').style.display = 'none';
	document.getElementById('editSubmitBtn').disabled = false;
	
	modal.show();
};