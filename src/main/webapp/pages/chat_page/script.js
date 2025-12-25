const chatMessages = document.getElementById('chatMessages');
const chatForm = document.getElementById('chatForm');
const messageInput = document.getElementById('messageInput');
const imageInput = document.getElementById('imageInput');
const sendBtn = document.getElementById('sendBtn');
const imagePreview = document.getElementById('imagePreview');
const previewImg = document.getElementById('previewImg');
const maDoanChatInput = document.getElementById('maDoanChatInput');

let selectedImage = null;

// Toggle sidebar on mobile
function toggleSidebar() {
    document.getElementById('chatSidebar').classList.toggle('show');
}

// Start new chat
function startNewChat() {
    window.location.href = getContextPath() + '/ChatPageController';
}

// Load specific chat
function loadChat(maDoanChat) {
    window.location.href = getContextPath() + '/ChatPageController?maDoanChat=' + maDoanChat;
}

// Delete chat
async function deleteChat(maDoanChat) {
    if (!confirm('Bạn có chắc muốn xóa đoạn chat này?')) return;
    
    try {
        const response = await fetch(getContextPath() + '/DeleteChatController', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'maDoanChat=' + maDoanChat
        });
        
        if (response.ok) {
            window.location.reload();
        }
    } catch (error) {
        console.error('Delete error:', error);
        alert('Đã xảy ra lỗi khi xóa đoạn chat');
    }
}

// Handle form submit
chatForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const message = messageInput.value.trim();
    
    if (!message && !selectedImage) return;
    
    // Show user message
    if (message) {
        addMessage(message, 'user');
    }
    if (selectedImage) {
        addImageMessage(previewImg.src, 'user');
    }
    
    messageInput.value = '';
    removeImage();
    sendBtn.disabled = true;
    
    // Show typing indicator
    const typingId = showTypingIndicator();
    
    try {
        const formData = new FormData();
        formData.append('message', message);
        if (selectedImage) {
            formData.append('image', selectedImage);
        }
        
        const currentMaDoanChat = maDoanChatInput.value;
        if (currentMaDoanChat) {
            formData.append('maDoanChat', currentMaDoanChat);
        }
        
        const response = await fetch(getContextPath() + '/ChatAIController', {
            method: 'POST',
            body: formData
        });
        
        // THÊM ĐOẠN NÀY - Kiểm tra response trước khi parse JSON
        const contentType = response.headers.get('content-type');
        if (!contentType || !contentType.includes('application/json')) {
            // Response không phải JSON (có thể là HTML)
            removeTypingIndicator(typingId);
            
            if (response.status === 401) {
                addMessage('Phiên đăng nhập đã hết hạn. Vui lòng tải lại trang.', 'assistant');
                setTimeout(() => {
                    window.location.reload();
                }, 2000);
            } else {
                addMessage('Lỗi server. Vui lòng thử lại sau.', 'assistant');
            }
            sendBtn.disabled = false;
            return;
        }
        
        const data = await response.json();
        removeTypingIndicator(typingId);
        
        if (data.success) {
            addMessage(data.response, 'assistant', data.timestamp);
            
            // Update maDoanChat if new chat
            if (data.maDoanChat && !currentMaDoanChat) {
                maDoanChatInput.value = data.maDoanChat;
                // Reload to show in sidebar
                setTimeout(() => {
                    window.location.href = getContextPath() + '/ChatPageController?maDoanChat=' + data.maDoanChat;
                }, 1000);
            }
        } else {
            addMessage('Lỗi: ' + data.message, 'assistant');
        }
    } catch (error) {
        removeTypingIndicator(typingId);
        console.error('Chat error:', error);
        addMessage('Đã xảy ra lỗi. Vui lòng thử lại.', 'assistant');
    } finally {
        sendBtn.disabled = false;
        messageInput.focus();
    }
});

// Handle image selection
imageInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) {
        if (!file.type.match('image/*')) {
            alert('Chỉ chấp nhận file ảnh');
            return;
        }
        if (file.size > 5 * 1024 * 1024) {
            alert('File quá lớn (tối đa 5MB)');
            return;
        }
        
        selectedImage = file;
        const reader = new FileReader();
        reader.onload = (e) => {
            previewImg.src = e.target.result;
            imagePreview.style.display = 'block';
        };
        reader.readAsDataURL(file);
    }
});

function removeImage() {
    selectedImage = null;
    imageInput.value = '';
    imagePreview.style.display = 'none';
    previewImg.src = '';
}

function addMessage(text, role, timestamp) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${role}`;
    
    const content = document.createElement('div');
    content.textContent = text;
    messageDiv.appendChild(content);
    
    if (timestamp) {
        const time = document.createElement('small');
        time.className = 'message-time';
        time.textContent = timestamp;
        messageDiv.appendChild(time);
    }
    
    chatMessages.appendChild(messageDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;
    
    const welcome = chatMessages.querySelector('.welcome-message');
    if (welcome) welcome.remove();
}

function addImageMessage(src, role) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${role}`;
    
    const img = document.createElement('img');
    img.src = src;
    img.className = 'message-image';
    messageDiv.appendChild(img);
    
    chatMessages.appendChild(messageDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

function showTypingIndicator() {
    const typingDiv = document.createElement('div');
    typingDiv.className = 'message assistant typing-indicator';
    typingDiv.id = 'typing-' + Date.now();
    typingDiv.innerHTML = `
        <div class="typing-dot"></div>
        <div class="typing-dot"></div>
        <div class="typing-dot"></div>
    `;
    chatMessages.appendChild(typingDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;
    return typingDiv.id;
}

function removeTypingIndicator(id) {
    const typing = document.getElementById(id);
    if (typing) typing.remove();
}

function getContextPath() {
    // Lấy context path từ pathname
    // VD: /WebsiteForum/ChatPageController -> return '/WebsiteForum'
    const path = window.location.pathname;
    const parts = path.split('/');
    return '/' + parts[1];
}

// Auto scroll to bottom on load
window.addEventListener('load', () => {
    chatMessages.scrollTop = chatMessages.scrollHeight;
});