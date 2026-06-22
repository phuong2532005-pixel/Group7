/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

        function confirmDelete(userId) {
            var modal = document.getElementById('confirm-modal');
            var input = document.getElementById('delete-user-id');
            if (input) {
                input.value = userId;
            }
            if (modal) {
                modal.style.display = 'block';
            }
        }

        function closeModal() {
            var modal = document.getElementById('confirm-modal');
            if (modal) {
                modal.style.display = 'none';
            }
        }

        function confirmRestore(userId) {
            var modal = document.getElementById('restore-modal');
            var input = document.getElementById('restore-user-id');
            if (input) {
                input.value = userId;
            }
            if (modal) {
                modal.style.display = 'block';
            }
        }

        function closeRestoreModal() {
            var modal = document.getElementById('restore-modal');
            if (modal) {
                modal.style.display = 'none';
            }
        }

        function openEditModal(id, username, fullName, email, phone, roleId, status) {
            var modal = document.getElementById('edit-modal');
            var idInput = document.getElementById('edit-user-id');
            var usernameInput = document.getElementById('edit-username');
            var fullNameInput = document.getElementById('edit-fullname');
            var emailInput = document.getElementById('edit-email');
            var phoneInput = document.getElementById('edit-phone');
            var roleInput = document.getElementById('edit-role');
            var statusInput = document.getElementById('edit-status');

            if (idInput) {
                idInput.value = id;
            }
            if (usernameInput) {
                usernameInput.value = username;
            }
            if (fullNameInput) {
                fullNameInput.value = fullName;
            }
            if (emailInput) {
                emailInput.value = email;
            }
            if (phoneInput) {
                phoneInput.value = phone;
            }
            if (roleInput) {
                roleInput.value = roleId;
            }
            if (statusInput) {
                statusInput.value = status;
            }
            if (modal) {
                modal.style.display = 'block';
            }
        }

        function closeEditModal() {
            var modal = document.getElementById('edit-modal');
            if (modal) {
                modal.style.display = 'none';
            }
        }

