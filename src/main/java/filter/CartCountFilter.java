/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 03/04/2026
 * Description : Them so luong gio hang vao request de hien thi tren header.
 */
package filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import DAO.CategoryDAO;
import model.CategoryDTO;
import model.CartItemDTO;
import DAO.ShoppingCartDAO;
import model.UsersDTO;

@WebFilter(filterName = "CartCountFilter", urlPatterns = {"/*"})
public class CartCountFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        if (!shouldProcess(req)) {
            chain.doFilter(request, response);
            return;
        }

        int cartCount = 0;
        HttpSession session = req.getSession(false);
        UsersDTO acc = session == null ? null : (UsersDTO) session.getAttribute("acc");
        if (acc != null && acc.getRole_id() == 3) {
            cartCount = new ShoppingCartDAO().getCartQuantitySum(acc.getId());
        } else if (session != null) {
            Object data = session.getAttribute("CART");
            if (data instanceof Map) {
                try {
                    Map<Integer, CartItemDTO> cart = (Map<Integer, CartItemDTO>) data;
                    for (CartItemDTO item : cart.values()) {
                        cartCount += Math.max(0, item.getQuantity());
                    }
                } catch (Exception ex) {
                }
            }
        }

        request.setAttribute("cartCount", cartCount);

        // Provide categories globally for dynamic header menu rendering.
        if (request.getAttribute("categories") == null) {
            try {
                List<CategoryDTO> categories = new CategoryDAO().getAllCategories();
                request.setAttribute("categories", categories);
            } catch (Exception ex) {
                // Keep page rendering even if category loading fails.
            }
        }

        chain.doFilter(request, response);
    }

    private boolean shouldProcess(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if (uri == null) {
            return false;
        }
        String lower = uri.toLowerCase();
        return !(lower.endsWith(".css")
                || lower.endsWith(".js")
                || lower.endsWith(".png")
                || lower.endsWith(".jpg")
                || lower.endsWith(".jpeg")
                || lower.endsWith(".gif")
                || lower.endsWith(".svg")
                || lower.endsWith(".ico")
                || lower.endsWith(".woff")
                || lower.endsWith(".woff2")
                || lower.endsWith(".ttf")
                || lower.endsWith(".eot")
                || lower.endsWith(".map"));
    }

    @Override
    public void init(javax.servlet.FilterConfig filterConfig) throws javax.servlet.ServletException {
        // Init logic
    }

    @Override
    public void destroy() {
        // Destroy logic
    }
}
