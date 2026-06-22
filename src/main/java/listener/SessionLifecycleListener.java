package listener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import model.UsersDTO;
import java.util.logging.Logger;

/**
 * JAVA EQUIVALENT của JS: Xử lý đăng nhập / đăng xuất qua session
 * Lắng nghe toàn bộ vòng đời của HTTP Session (tạo mới và hủy).
 *
 * Tương đương các JS event sau:
 *  - profile.js    : document.addEventListener("DOMContentLoaded", ...) → đọc role từ data attribute
 *                     → activateSection() theo role, redirect nếu không có quyền
 *  - main.js       : document.addEventListener("DOMContentLoaded", ...) → kiểm tra trạng thái đăng nhập
 *  - LogoutServlet : session.invalidate() → phản ứng khi session bị hủy
 *
 * Khi session bị hủy (timeout hoặc logout), listener này:
 *   1. Ghi log thông tin user vừa đăng xuất
 *   2. Thực hiện dọn dẹp tài nguyên liên quan đến session đó
 * =====================================================================
 */
@WebListener
public class SessionLifecycleListener implements HttpSessionListener {

    private static final Logger logger = Logger.getLogger(SessionLifecycleListener.class.getName());

    /**
     * Gọi khi một HTTP Session MỚI được tạo.
     * Tương đương: Khi trình duyệt lần đầu mở trang (DOMContentLoaded cho guest).
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // Đặt timeout mặc định 30 phút
        se.getSession().setMaxInactiveInterval(30 * 60);
        logger.info("[SessionLifecycleListener] Session mới được tạo: "
                + se.getSession().getId()
                + " | Timeout: 30 phút");
    }

    /**
     * Gọi khi một HTTP Session bị HỦY (timeout hoặc invalidate sau logout).
     * Tương đương:
     *  - JS: window.addEventListener('beforeunload', ...) — dọn dẹp khi thoát trang
     *  - JS: logoutBtn click → xóa trạng thái đăng nhập
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Object accObj = se.getSession().getAttribute("acc");

        if (accObj instanceof UsersDTO) {
            UsersDTO user = (UsersDTO) accObj;
            logger.info("[SessionLifecycleListener] Session bị HỦY - User đã đăng xuất:"
                    + " | ID: " + user.getId()
                    + " | Tên: " + user.getFull_name()
                    + " | Email: " + user.getEmail()
                    + " | Role: " + getRoleName(user.getRole_id())
                    + " | SessionID: " + se.getSession().getId());
        } else {
            logger.info("[SessionLifecycleListener] Session (guest) bị HỦY: "
                    + se.getSession().getId());
        }
    }

    /** Trả về tên role dễ đọc. */
    private String getRoleName(int roleId) {
        switch (roleId) {
            case 1: return "Admin";
            case 3: return "Customer";
            case 4: return "Director";
            default: return "Unknown (" + roleId + ")";
        }
    }
}
