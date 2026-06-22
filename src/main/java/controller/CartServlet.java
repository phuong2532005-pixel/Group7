/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 04/02/2026
 * Description : Xu ly hanh dong gio hang (them, cap nhat, thay doi bien the, xoa).
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.CartItemDTO;
import DAO.ProductDAO;
import model.ProductVariantDTO;
import DAO.ShoppingCartDAO;
import model.UsersDTO;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UsersDTO acc = session == null ? null : (UsersDTO) session.getAttribute("acc");
        if (acc == null) {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=LoginPage");
            return;
        }
        if (isPurchaseBlocked(acc)) {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=ProductList");
            return;
        }

        if (acc.getRole_id() == 3) {
            ShoppingCartDAO cartDao = new ShoppingCartDAO();
            List<CartItemDTO> items = cartDao.getCartItems(acc.getId());
            Map<Integer, List<ProductVariantDTO>> variantOptionsByProduct = new LinkedHashMap<>();
            ProductDAO productDAO = new ProductDAO();
            for (CartItemDTO item : items) {
                if (!variantOptionsByProduct.containsKey(item.getProductId())) {
                    variantOptionsByProduct.put(item.getProductId(), productDAO.getVariantsByProductId(item.getProductId()));
                }
            }
            request.setAttribute("cartItems", items);
            request.setAttribute("variantOptionsByProduct", variantOptionsByProduct);
            request.setAttribute("cartTotal", calculateTotal(items));
        } else {
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=ProductList");
            return;
        }
        request.getRequestDispatcher("/jsp/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        HttpSession session = request.getSession(false);
        UsersDTO acc = session == null ? null : (UsersDTO) session.getAttribute("acc");
        if (acc == null) {
            if ("add".equals(action)) {
                response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=LoginPage");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writeJson(response, false, "Unauthorized", 0, 0, 0);
            }
            return;
        }
        if (isPurchaseBlocked(acc)) {
            if ("update".equals(action) || "remove".equals(action)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                writeJson(response, false, "Forbidden", 0, 0, 0);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=ProductList");
            return;
        }

        if (acc.getRole_id() == 3) {
            handleDbCart(request, response, action, acc);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/dispatchcontroller?button=ProductList");
    }

    private void handleDbCart(HttpServletRequest request, HttpServletResponse response, String action, UsersDTO acc)
            throws IOException {
        ShoppingCartDAO cartDao = new ShoppingCartDAO();
        Integer variantId = parseInt(request.getParameter("variantId"));
        Integer productId = parseInt(request.getParameter("productId"));
        int quantity = parseQuantity(request.getParameter("quantity"));

        switch (action) {
            case "add":
                if (variantId == null) {
                    String isAjax = request.getHeader("X-Requested-With");
                    if ("XMLHttpRequest".equals(isAjax)) {
                        writeJson(response, false, "Vui lòng chọn size và màu trước khi thêm vào giỏ.", 0, 0, 0);
                    } else if (productId != null) {
                        response.sendRedirect(request.getContextPath() + "/product?id=" + productId + "&cartError=variant");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/cart?error=invalid_variant");
                    }
                    return;
                }
                boolean added = cartDao.addItem(acc.getId(), variantId, quantity);
                if (!added) {
                    String isAjax = request.getHeader("X-Requested-With");
                    if ("XMLHttpRequest".equals(isAjax)) {
                        writeJson(response, false, "Không thể thêm vào giỏ hàng. Vui lòng thử lại.", 0, 0, cartDao.getCartQuantitySum(acc.getId()));
                    } else if (productId != null) {
                        response.sendRedirect(request.getContextPath() + "/product?id=" + productId + "&cartError=add_failed");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/cart?error=add_failed");
                    }
                    return;
                }
                String isAjax = request.getHeader("X-Requested-With");
                if ("XMLHttpRequest".equals(isAjax)) {
                    writeJson(response, true, "Thêm vào giỏ hàng thành công!", 0, 0, cartDao.getCartQuantitySum(acc.getId()));
                } else {
                    response.sendRedirect(request.getContextPath() + "/cart?added=1");
                }
                return;
            case "update":
                if (variantId == null) {
                    writeJson(response, false, "Invalid item", 0, 0, cartDao.getCartQuantitySum(acc.getId()));
                    return;
                }
                if (quantity <= 0) {
                    cartDao.removeItem(acc.getId(), variantId);
                } else {
                    cartDao.setItemQuantity(acc.getId(), variantId, quantity);
                }
                List<CartItemDTO> itemsAfterUpdate = cartDao.getCartItems(acc.getId());
                double cartTotalAfterUpdate = calculateTotal(itemsAfterUpdate);
                double itemSubtotal = 0;
                for (CartItemDTO it : itemsAfterUpdate) {
                    if (it.getVariantId() == variantId) {
                        itemSubtotal = it.getSubtotal();
                        break;
                    }
                }
                writeJson(response, true, "Updated", itemSubtotal, cartTotalAfterUpdate, cartDao.getCartQuantitySum(acc.getId()));
                return;
            case "remove":
                if (variantId == null) {
                    writeJson(response, false, "Invalid item", 0, 0, cartDao.getCartQuantitySum(acc.getId()));
                    return;
                }
                cartDao.removeItem(acc.getId(), variantId);
                List<CartItemDTO> itemsAfterRemove = cartDao.getCartItems(acc.getId());
                writeJson(response, true, "Removed", 0, calculateTotal(itemsAfterRemove), cartDao.getCartQuantitySum(acc.getId()));
                return;
            case "removeSelected":
                List<Integer> selectedVariantIds = parseIntList(request.getParameter("variantIds"));
                if (selectedVariantIds.isEmpty()) {
                    writeJson(response, false, "No selected items", 0, 0, cartDao.getCartQuantitySum(acc.getId()));
                    return;
                }
                if (!cartDao.removeItems(acc.getId(), selectedVariantIds)) {
                    writeJson(response, false, "Cannot remove selected items", 0, 0, cartDao.getCartQuantitySum(acc.getId()));
                    return;
                }
                List<CartItemDTO> itemsAfterBulkRemove = cartDao.getCartItems(acc.getId());
                writeJson(response, true, "Removed selected", 0, calculateTotal(itemsAfterBulkRemove), cartDao.getCartQuantitySum(acc.getId()));
                return;
            case "changeVariant":
                Integer newVariantId = parseInt(request.getParameter("newVariantId"));
                if (variantId == null || newVariantId == null) {
                    writeJson(response, false, "Invalid variant", 0, 0, cartDao.getCartQuantitySum(acc.getId()));
                    return;
                }
                if (!cartDao.changeItemVariant(acc.getId(), variantId, newVariantId)) {
                    writeJson(response, false, "Không thể đổi biến thể", 0, 0, cartDao.getCartQuantitySum(acc.getId()));
                    return;
                }
                List<CartItemDTO> itemsAfterVariantChange = cartDao.getCartItems(acc.getId());
                CartItemDTO changedItem = null;
                for (CartItemDTO item : itemsAfterVariantChange) {
                    if (item.getVariantId() == newVariantId) {
                        changedItem = item;
                        break;
                    }
                }
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                if (changedItem == null) {
                    out.print("{\"ok\":false,\"message\":\"Không tìm thấy biến thể sau cập nhật\"}");
                } else {
                    String safeProductName = changedItem.getProductName() == null ? "" : changedItem.getProductName().replace("\"", "\\\"");
                    String safeSize = changedItem.getSize() == null ? "" : changedItem.getSize().replace("\"", "\\\"");
                    String safeColor = changedItem.getColor() == null ? "" : changedItem.getColor().replace("\"", "\\\"");
                    String safeImage = changedItem.getImageUrl() == null ? "" : changedItem.getImageUrl().replace("\"", "\\\"");
                    out.print("{\"ok\":true,\"success\":true,\"oldVariantId\":" + variantId
                            + ",\"newVariantId\":" + changedItem.getVariantId()
                            + ",\"productId\":" + changedItem.getProductId()
                            + ",\"sizeId\":" + changedItem.getSizeId()
                            + ",\"colorId\":" + changedItem.getColorId()
                            + ",\"productName\":\"" + safeProductName + "\""
                            + ",\"size\":\"" + safeSize + "\""
                            + ",\"color\":\"" + safeColor + "\""
                            + ",\"price\":" + changedItem.getPrice()
                            + ",\"quantity\":" + changedItem.getQuantity()
                            + ",\"itemSubtotal\":" + changedItem.getSubtotal()
                            + ",\"imageUrl\":\"" + safeImage + "\""
                            + ",\"cartCount\":" + cartDao.getCartQuantitySum(acc.getId())
                            + ",\"cartTotal\":" + calculateTotal(itemsAfterVariantChange)
                            + "}");
                }
                out.flush();
                return;
            case "clear":
                cartDao.clearCart(acc.getId());
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            default:
                response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private double calculateTotal(List<CartItemDTO> items) {
        double total = 0;
        if (items == null) {
            return 0;
        }
        for (CartItemDTO item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    private boolean isPurchaseBlocked(UsersDTO acc) {
        if (acc == null) {
            return false;
        }
        int roleId = acc.getRole_id();
        return roleId != 3;
    }

    private Integer parseInt(String value) {
        try {
            return value == null || value.trim().isEmpty() ? null : Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private List<Integer> parseIntList(String csv) {
        List<Integer> values = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) {
            return values;
        }
        String[] parts = csv.split(",");
        for (String part : parts) {
            Integer value = parseInt(part);
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }

    private int parseQuantity(String value) {
        try {
            int qty = Integer.parseInt(value);
            return qty <= 0 ? 1 : qty;
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private void writeJson(HttpServletResponse response, boolean ok, String message,
            double itemSubtotal, double cartTotal, int cartCount) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String safeMessage = message == null ? "" : message.replace("\"", "\\\"");
        PrintWriter out = response.getWriter();
        out.print("{\"ok\":" + ok + ",\"success\":" + ok + ",\"message\":\"" + safeMessage + "\"," 
            + "\"itemSubtotal\":" + itemSubtotal + ",\"cartTotal\":" + cartTotal + ",\"cartCount\":" + cartCount + "}");
        out.flush();
    }
}
