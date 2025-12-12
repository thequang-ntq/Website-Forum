const chatMessages = document.getElementById('chatMessages');
const chatForm = document.getElementById('chatForm');
const messageInput = document.getElementById('messageInput');
const imageInput = document.getElementById('imageInput');
const sendBtn = document.getElementById('sendBtn');
const imagePreview = document.getElementById('imagePreview');
const previewImg = document.getElementById('previewImg');

let selectedImage = null;

// Load chat history
window.addEventListener('load', loadChatHistory);

// Handle form submit
chatForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const message = messageInput.value.trim();
    
    if (!message && !selectedImage) return;
    
    // Add user message
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
		
		const contextPath = window.location.pathname.split('/')[1];
        const response = await fetch('/' + contextPath + '/ChatAIController', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        removeTypingIndicator(typingId);
        
        if (data.success) {
            addMessage(data.response, 'assistant', data.timestamp);
        } else {
            addMessage('Lỗi: ' + data.message, 'assistant');
        }
    } catch (error) {
        removeTypingIndicator(typingId);
		console.log(error);
        addMessage('Đã xảy ra lỗi khi gửi tin nhắn.', 'assistant');
    } finally {
        sendBtn.disabled = false;
        messageInput.focus();
    }
});

// Handle image selection
imageInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) {
        if (!file.type.match('image/(png|jpeg|jpg)')) {
            alert('Chỉ chấp nhận file PNG, JPG, JPEG');
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
    
    // Remove welcome message
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

async function loadChatHistory() {
    // Chat history is stored in session, will be loaded with messages
}

async function clearChat() {
    if (!confirm('Bạn có chắc muốn xóa lịch sử chat?')) return;
    
    try {
		const contextPath = window.location.pathname.split('/')[1];
        const response = await fetch('/' + contextPath + '/ClearChatController', {
            method: 'POST'
        });
        
        if (response.ok) {
            chatMessages.innerHTML = `
                <div class="welcome-message">
                    <i class="bi bi-stars"></i>
                    <h5>Xin chào! Tôi là trợ lý AI</h5>
                    <p>Hãy hỏi tôi bất cứ điều gì bạn muốn biết!</p>
                </div>
            `;
        }
    } catch (error) {
        alert('Đã xảy ra lỗi khi xóa lịch sử');
    }
}