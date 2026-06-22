/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 07/02/2026
 * Description : Xu ly trang thanh toan: luu dia chi, tao don hang va tru ton kho.
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import DAO.AddressDAO;
import model.AddressDTO;
import model.CartItemDTO;
import DAO.OrderDAO;
import model.OrderResult;
import DAO.ShoppingCartDAO;
import model.UsersDTO;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

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

        List<CartItemDTO> items;
        double total;
        if (acc.getRole_id() == 3) {
            ShoppingCartDAO cartDao = new ShoppingCartDAO();
            items = cartDao.getCartItems(acc.getId());
            total = calculateTotal(items);
        } else {
            Map<Integer, CartItemDTO> cart = getCart(session);
            if (cart == null) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
            items = new ArrayList<>(cart.values());
            total = calculateTotal(cart);
        }

        if (items == null || items.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        List<AddressDTO> addresses = new AddressDAO().listByUser(acc.getId());

        request.setAttribute("cartItems", items);
        request.setAttribute("cartTotal", total);
        request.setAttribute("addresses", addresses);
        request.setAttribute("defaultShippingName", acc.getFull_name());
        request.setAttribute("defaultShippingPhone", acc.getPhone());
        request.setAttribute("defaultShippingStreet", request.getParameter("shippingStreet"));
        request.setAttribute("defaultShippingDistrict", request.getParameter("shippingDistrict"));
        request.setAttribute("defaultShippingCity", request.getParameter("shippingCity"));
        request.setAttribute("success", request.getParameter("success"));
        request.setAttribute("orderId", request.getParameter("orderId"));
        request.getRequestDispatcher("/jsp/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

        String action = request.getParameter("action");
        if ("saveAddress".equalsIgnoreCase(action)) {
            handleSaveAddress(request, response, acc);
            return;
        }

        ShoppingCartDAO cartDao = new ShoppingCartDAO();
        List<CartItemDTO> items = cartDao.getCartItems(acc.getId());
        
        // Filter items if selectedVariants is provided
        String selectedVariantsRaw = request.getParameter("selectedVariants");
        if (selectedVariantsRaw != null && !selectedVariantsRaw.trim().isEmpty()) {
            String[] selectedIds = selectedVariantsRaw.trim().split(",");
            List<Integer> selectedVariantIds = new ArrayList<>();
            for (String id : selectedIds) {
                try {
                    selectedVariantIds.add(Integer.parseInt(id.trim()));
                } catch (Exception e) {
                }
            }
            
            // Filter items to only include selected variants
            items.removeIf(item -> !selectedVariantIds.contains(item.getVariantId()));
            // Store selectedVariants in request attributes to preserve it across form submissions
            request.setAttribute("selectedVariants", selectedVariantsRaw);
        }
        
        if (items == null || items.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        AddressDAO addressDAO = new AddressDAO();
        List<AddressDTO> addresses = addressDAO.listByUser(acc.getId());
        boolean hasSavedAddresses = addresses != null && !addresses.isEmpty();

        String shippingName = request.getParameter("shippingName");
        String shippingPhone = request.getParameter("shippingPhone");
        String selectedAddressIdRaw = request.getParameter("selectedAddressId");
        String useNewAddressRaw = request.getParameter("useNewAddress");
        String shippingStreetInput = request.getParameter("shippingStreet");
        String shippingDistrictInput = request.getParameter("shippingDistrict");
        String shippingCityInput = request.getParameter("shippingCity");
        String newAddressDefaultRaw = request.getParameter("newAddressIsDefault");
        String paymentMethod = request.getParameter("paymentMethod");

        if (isBlank(shippingName)) {
            shippingName = acc.getFull_name();
        }
        if (isBlank(shippingPhone)) {
            shippingPhone = acc.getPhone();
        }

        boolean useNewAddress = "1".equals(useNewAddressRaw);
        boolean newAddressIsDefault = "1".equals(newAddressDefaultRaw)
            || "on".equalsIgnoreCase(newAddressDefaultRaw);

        Integer selectedAddressId = null;
        try {
            if (!isBlank(selectedAddressIdRaw)) {
                selectedAddressId = Integer.valueOf(selectedAddressIdRaw.trim());
            }
        } catch (Exception e) {
            selectedAddressId = null;
        }

        AddressDTO selectedAddress = null;
        if (hasSavedAddresses && !useNewAddress) {
            if (selectedAddressId != null) {
                selectedAddress = addressDAO.getByIdAndUser(selectedAddressId, acc.getId());
            }
        } else {
            if (isBlank(shippingStreetInput) || isBlank(shippingDistrictInput) || isBlank(shippingCityInput)) {
                request.setAttribute("error", "Bạn chưa có địa chỉ. Vui lòng nhập đầy đủ địa chỉ giao hàng để tiếp tục đặt hàng.");
                request.setAttribute("cartItems", items);
                request.setAttribute("cartTotal", calculateTotal(items));
                request.setAttribute("addresses", addresses);
                request.setAttribute("defaultShippingName", shippingName);
                request.setAttribute("defaultShippingPhone", shippingPhone);
                request.setAttribute("defaultShippingStreet", shippingStreetInput);
                request.setAttribute("defaultShippingDistrict", shippingDistrictInput);
                request.setAttribute("defaultShippingCity", shippingCityInput);
                request.setAttribute("useNewAddress", useNewAddress ? "1" : "0");
                request.setAttribute("newAddressIsDefault", newAddressIsDefault ? "1" : "0");
                request.getRequestDispatcher("/jsp/checkout.jsp").forward(request, response);
                return;
            }

            boolean inserted = addressDAO.insertAddress(
                    acc.getId(),
                    shippingStreetInput.trim(),
                    shippingDistrictInput.trim(),
                    shippingCityInput.trim(),
                    !hasSavedAddresses || newAddressIsDefault
            );
            if (!inserted) {
                request.setAttribute("error", "Không thể lưu địa chỉ giao hàng. Vui lòng thử lại.");
                request.setAttribute("cartItems", items);
                request.setAttribute("cartTotal", calculateTotal(items));
                request.setAttribute("addresses", addresses);
                request.setAttribute("defaultShippingName", shippingName);
                request.setAttribute("defaultShippingPhone", shippingPhone);
                request.setAttribute("defaultShippingStreet", shippingStreetInput);
                request.setAttribute("defaultShippingDistrict", shippingDistrictInput);
                request.setAttribute("defaultShippingCity", shippingCityInput);
                request.setAttribute("useNewAddress", useNewAddress ? "1" : "0");
                request.setAttribute("newAddressIsDefault", newAddressIsDefault ? "1" : "0");
                request.getRequestDispatcher("/jsp/checkout.jsp").forward(request, response);
                return;
            }

            addresses = addressDAO.listByUser(acc.getId());
            if (addresses != null && !addresses.isEmpty()) {
                if (!hasSavedAddresses || newAddressIsDefault) {
                    selectedAddress = addresses.get(0);
                } else {
                    String cityTrimmed = shippingCityInput == null ? "" : shippingCityInput.trim();
                    String districtTrimmed = shippingDistrictInput == null ? "" : shippingDistrictInput.trim();
                    String streetTrimmed = shippingStreetInput == null ? "" : shippingStreetInput.trim();
                    for (AddressDTO address : addresses) {
                        if (address == null) {
                            continue;
                        }
                        String city = address.getCity() == null ? "" : address.getCity().trim();
                        String district = address.getDistrict() == null ? "" : address.getDistrict().trim();
                        String street = address.getStreet() == null ? "" : address.getStreet().trim();
                        if (city.equalsIgnoreCase(cityTrimmed)
                                && district.equalsIgnoreCase(districtTrimmed)
                                && street.equalsIgnoreCase(streetTrimmed)) {
                            selectedAddress = address;
                            break;
                        }
                    }
                    if (selectedAddress == null) {
                        selectedAddress = addresses.get(0);
                    }
                }
            }
        }

        if (isBlank(shippingName) || isBlank(shippingPhone) || selectedAddress == null) {
            request.setAttribute("error", "Vui lòng chọn địa chỉ giao hàng và nhập đầy đủ thông tin người nhận.");
            request.setAttribute("cartItems", items);
            request.setAttribute("cartTotal", calculateTotal(items));
            request.setAttribute("addresses", addresses);
            request.setAttribute("defaultShippingName", shippingName);
            request.setAttribute("defaultShippingPhone", shippingPhone);
            request.setAttribute("defaultShippingStreet", shippingStreetInput);
            request.setAttribute("defaultShippingDistrict", shippingDistrictInput);
            request.setAttribute("defaultShippingCity", shippingCityInput);
            request.setAttribute("useNewAddress", useNewAddress ? "1" : "0");
            request.setAttribute("newAddressIsDefault", newAddressIsDefault ? "1" : "0");
            request.getRequestDispatcher("/jsp/checkout.jsp").forward(request, response);
            return;
        }

        String shippingStreet = selectedAddress.getStreet();
        String shippingDistrict = selectedAddress.getDistrict();
        String shippingCity = selectedAddress.getCity();

        OrderDAO dao = new OrderDAO();
        OrderResult result = dao.placeOrder(
            acc.getId(),
            shippingName,
            shippingPhone,
            shippingStreet,
            shippingDistrict,
            shippingCity,
            paymentMethod,
            items
        );

        if (!result.isSuccess()) {
            request.setAttribute("error", result.getMessage());
            request.setAttribute("cartItems", items);
            request.setAttribute("cartTotal", calculateTotal(items));
            request.setAttribute("addresses", addresses);
            request.setAttribute("defaultShippingName", shippingName);
            request.setAttribute("defaultShippingPhone", shippingPhone);
            request.setAttribute("defaultShippingStreet", shippingStreetInput);
            request.setAttribute("defaultShippingDistrict", shippingDistrictInput);
            request.setAttribute("defaultShippingCity", shippingCityInput);
            request.setAttribute("useNewAddress", useNewAddress ? "1" : "0");
            request.setAttribute("newAddressIsDefault", newAddressIsDefault ? "1" : "0");
            request.getRequestDispatcher("/jsp/checkout.jsp").forward(request, response);
            return;
        }

        // Remove only purchased items from cart, keep unpurchased items
        for (CartItemDTO item : items) {
            cartDao.removeItem(acc.getId(), item.getVariantId());
        }
        response.sendRedirect(request.getContextPath() + "/orders");
    }

    private boolean isPurchaseBlocked(UsersDTO acc) {
        if (acc == null) {
            return false;
        }
        int roleId = acc.getRole_id();
        return roleId != 3;
    }

    private Map<Integer, CartItemDTO> getCart(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object data = session.getAttribute("CART");
        if (data instanceof Map) {
            return (Map<Integer, CartItemDTO>) data;
        }
        return null;
    }

    private double calculateTotal(Map<Integer, CartItemDTO> cart) {
        double total = 0;
        for (CartItemDTO item : cart.values()) {
            total += item.getSubtotal();
        }
        return total;
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Double parseDoubleOrNull(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return Double.valueOf(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private void handleSaveAddress(HttpServletRequest request, HttpServletResponse response, UsersDTO acc) throws IOException {
        String street = request.getParameter("shippingStreet");
        String district = request.getParameter("shippingDistrict");
        String city = request.getParameter("shippingCity");
        String newAddressDefaultRaw = request.getParameter("newAddressIsDefault");

        boolean newAddressIsDefault = "1".equals(newAddressDefaultRaw)
                || "on".equalsIgnoreCase(newAddressDefaultRaw);

        response.setContentType("application/json;charset=UTF-8");

        if (isBlank(street) || isBlank(district) || isBlank(city)) {
            response.getWriter().write("{\"success\":false,\"message\":\"Vui lòng nhập đầy đủ địa chỉ mới.\"}");
            return;
        }

        AddressDAO addressDAO = new AddressDAO();
        List<AddressDTO> existing = addressDAO.listByUser(acc.getId());
        boolean hasSavedAddresses = existing != null && !existing.isEmpty();

        boolean inserted = addressDAO.insertAddress(
                acc.getId(),
                street.trim(),
                district.trim(),
                city.trim(),
                !hasSavedAddresses || newAddressIsDefault
        );

        if (!inserted) {
            response.getWriter().write("{\"success\":false,\"message\":\"Không thể lưu địa chỉ mới.\"}");
            return;
        }

        List<AddressDTO> addresses = addressDAO.listByUser(acc.getId());
        AddressDTO savedAddress = null;
        if (addresses != null && !addresses.isEmpty()) {
            for (AddressDTO a : addresses) {
                if (a == null) {
                    continue;
                }
                String aStreet = a.getStreet() == null ? "" : a.getStreet().trim();
                String aDistrict = a.getDistrict() == null ? "" : a.getDistrict().trim();
                String aCity = a.getCity() == null ? "" : a.getCity().trim();
                if (aStreet.equalsIgnoreCase(street.trim())
                        && aDistrict.equalsIgnoreCase(district.trim())
                        && aCity.equalsIgnoreCase(city.trim())) {
                    savedAddress = a;
                    break;
                }
            }
            if (savedAddress == null) {
                savedAddress = addresses.get(0);
            }
        }

        if (savedAddress == null) {
            response.getWriter().write("{\"success\":false,\"message\":\"Không xác định được địa chỉ vừa lưu.\"}");
            return;
        }

        StringBuilder json = new StringBuilder();
        json.append("{\"success\":true");
        json.append(",\"addressId\":").append(savedAddress.getId());
        json.append(",\"street\":\"").append(escapeJson(savedAddress.getStreet())).append("\"");
        json.append(",\"district\":\"").append(escapeJson(savedAddress.getDistrict())).append("\"");
        json.append(",\"city\":\"").append(escapeJson(savedAddress.getCity())).append("\"");
        json.append(",\"isDefault\":").append(savedAddress.isIsDefault() ? "true" : "false");
        json.append("}");
        response.getWriter().write(json.toString());
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", " ");
    }

}
