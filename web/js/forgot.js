
            (function () {
                var timerEl = document.getElementById('code-timer');
                var sendBtn = document.getElementById('send-code-btn');
                var sentAtInput = document.getElementById('sentAt');
                var ttlMs = 60000; 

                function setLocalSentAt(value) { try { localStorage.setItem('forgotSentAt', String(value)); } catch (e) {} }
                function getLocalSentAt() { try { return localStorage.getItem('forgotSentAt'); } catch (e) { return null; } }
                function clearLocalSentAt() { try { localStorage.removeItem('forgotSentAt'); } catch (e) {} }

                function startCountdown(startTime) {
                    if (!timerEl) return;
                    if (sendBtn) sendBtn.disabled = true;

                    function tick() {
                        var now = Date.now();
                        var remaining = Math.max(0, ttlMs - (now - startTime));
                        var seconds = Math.ceil(remaining / 1000);
                        
                        timerEl.textContent = remaining > 0 ? ('Mã hiệu lực còn: ' + seconds + ' giây') : '';
                        
                        if (remaining <= 0) {
                            if (sendBtn) sendBtn.disabled = false;
                            clearLocalSentAt();
                            clearInterval(timerId);
                        }
                    }
                    tick();
                    var timerId = setInterval(tick, 1000);
                }

                var sentAt = sentAtInput && sentAtInput.value ? Number(sentAtInput.value) : null;
                if (sentAt && !Number.isNaN(sentAt)) {
                    setLocalSentAt(sentAt);
                    startCountdown(sentAt);
                    return;
                }

                var localSentAt = getLocalSentAt();
                if (localSentAt) {
                    var parsed = Number(localSentAt);
                    if (!Number.isNaN(parsed) && Date.now() - parsed < ttlMs) {
                        startCountdown(parsed);
                    } else {
                        clearLocalSentAt();
                    }
                }
            })();
        