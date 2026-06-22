/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


        const chatMessages = document.getElementById('chatMessages');
        const messageInput = document.getElementById('messageInput');
        const sendBtn = document.getElementById('sendBtn');

        // Auto resize textarea
        messageInput.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = Math.min(this.scrollHeight, 100) + 'px';
        });

        // Send message on Enter (Shift+Enter for new line)
        messageInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });

        sendBtn.addEventListener('click', sendMessage);

        function sendMessage() {
            const message = messageInput.value.trim();
            if (!message) return;

            // Add user message
            addMessage(message, 'user');
            messageInput.value = '';
            messageInput.style.height = 'auto';
            sendBtn.disabled = true;

            // Show loading
            const loadingId = showLoading();

            // Send to server
            fetch(contextPath + '/api/chatbot', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'message=' + encodeURIComponent(message)
            })
            .then(response => response.json())
            .then(data => {
                removeLoading(loadingId);
                if (data.success) {
                    if (data.warning) {
                        addMessage(data.warning, 'warning');
                    }
                    addMessage(data.response, 'ai');
                } else {
                    addMessage('❌ ' + (data.error || 'Có lỗi xảy ra'), 'error');
                }
                sendBtn.disabled = false;
            })
            .catch(error => {
                removeLoading(loadingId);
                addMessage('❌ Lỗi kết nối: ' + error.message, 'error');
                sendBtn.disabled = false;
            });
        }

        function addMessage(text, type) {
            const msgDiv = document.createElement('div');
            msgDiv.className = 'message ' + type;

            const content = document.createElement('div');
            
            if (type === 'warning') {
                content.className = 'warning-message';
                content.style.width = '90%';
                content.innerHTML = text.replace(/\n/g, '<br>');
                msgDiv.appendChild(content);
                msgDiv.style.justifyContent = 'center';
            } else {
                let avatar = '';
                if (type === 'user') {
                    avatar = '<div class="message-avatar user-avatar"><i class="fas fa-user"></i></div>';
                } else if (type === 'ai') {
                    avatar = '<div class="message-avatar ai-avatar"><i class="fas fa-robot"></i></div>';
                }

                content.className = 'message-content';
                content.innerHTML = text.replace(/\n/g, '<br>');

                if (avatar) {
                    msgDiv.innerHTML = avatar;
                    msgDiv.appendChild(content);
                } else {
                    msgDiv.appendChild(content);
                }
            }

            chatMessages.appendChild(msgDiv);
            chatMessages.scrollTop = chatMessages.scrollHeight;

            return msgDiv;
        }

        function showLoading() {
            const msgDiv = document.createElement('div');
            msgDiv.className = 'message ai';
            msgDiv.innerHTML = `
                <div class="message-avatar ai-avatar"><i class="fas fa-robot"></i></div>
                <div class="loading">
                    <span></span>
                    <span></span>
                    <span></span>
                </div>
            `;
            msgDiv.id = 'loading-' + Date.now();
            chatMessages.appendChild(msgDiv);
            chatMessages.scrollTop = chatMessages.scrollHeight;
            return msgDiv.id;
        }

        function removeLoading(id) {
            const elem = document.getElementById(id);
            if (elem) elem.remove();
        }

        // Focus input on load
        messageInput.focus();