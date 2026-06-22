/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 21/02/2026
 * Description : Hien thi chi tiet san pham, cac bien the, hinh anh va du lieu lien quan.
 */
package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import DAO.CategoryDAO;
import DAO.ProductDAO;
import model.CategoryDTO;
import model.ProductDTO;
import model.ProductVariantDTO;
import model.VariantImageDTO;
import model.SizeDTO;
import model.ColorDTO;

@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/product"})
public class ProductDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer productId = parseInt(request.getParameter("id"));
        if (productId == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ProductDAO dao = new ProductDAO();
        ProductDTO product = dao.getProductById(productId);
        if (product == null || product.getStatus() != 1) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String categoryPath = buildCategoryPath(product.getCategoryId());
        int totalSold = dao.getTotalSoldByProductId(productId);

        List<ProductVariantDTO> variants = dao.getAvailableVariantsByProductId(productId);
        List<VariantImageDTO> variantImages = dao.getVariantImagesByProductId(productId);
        List<SizeDTO> sizes = new ArrayList<>();
        List<ColorDTO> colors = new ArrayList<>();

        Map<Integer, String> sizeMap = new LinkedHashMap<>();
        Map<Integer, String> colorMap = new LinkedHashMap<>();
        Map<Integer, Boolean> availableVariantIds = new HashMap<>();
        for (ProductVariantDTO variant : variants) {
            availableVariantIds.put(variant.getId(), Boolean.TRUE);
            sizeMap.put(variant.getSizeId(), variant.getSizeName());
            colorMap.put(variant.getColorId(), variant.getColorName());
        }
        for (Map.Entry<Integer, String> entry : sizeMap.entrySet()) {
            sizes.add(new SizeDTO(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<Integer, String> entry : colorMap.entrySet()) {
            colors.add(new ColorDTO(entry.getKey(), entry.getValue()));
        }
        
        // Build all images in order: product thumbnail, variant1 image, variant1 images, variant2 image, variant2 images...
        List<String> allImages = new ArrayList<>();
        
        // Add product thumbnail first
        if (product.getThumbnail() != null && !product.getThumbnail().trim().isEmpty()) {
            allImages.add(product.getThumbnail());
        }
        
        // Group variant images by variant_id
        Map<Integer, List<VariantImageDTO>> imagesByVariant = new HashMap<>();
        for (VariantImageDTO img : variantImages) {
            if (!availableVariantIds.containsKey(img.getVariantId())) {
                continue;
            }
            imagesByVariant.computeIfAbsent(img.getVariantId(), k -> new ArrayList<>()).add(img);
        }
        
        // For each variant, add variant main image and then variant images
        for (ProductVariantDTO variant : variants) {
            // Add variant main image (image_url from ProductVariants table)
            if (variant.getImageUrl() != null && !variant.getImageUrl().trim().isEmpty()) {
                allImages.add(variant.getImageUrl());
            }
            
            // Add variant images from VariantImages table
            List<VariantImageDTO> vImages = imagesByVariant.get(variant.getId());
            if (vImages != null) {
                for (VariantImageDTO vImg : vImages) {
                    if (vImg.getImageUrl() != null && !vImg.getImageUrl().trim().isEmpty()) {
                        allImages.add(vImg.getImageUrl());
                    }
                }
            }
        }

        request.setAttribute("product", product);
        request.setAttribute("categoryPath", categoryPath);
        request.setAttribute("totalSold", totalSold);
        request.setAttribute("variants", variants);
        request.setAttribute("allImages", allImages);
        request.setAttribute("sizes", sizes);
        request.setAttribute("colors", colors);
        request.getRequestDispatcher("/jsp/productDetail.jsp").forward(request, response);
    }

    private Integer parseInt(String value) {
        try {
            return value == null || value.trim().isEmpty() ? null : Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String buildCategoryPath(int categoryId) {
        CategoryDAO categoryDAO = new CategoryDAO();
        List<String> nodes = new ArrayList<>();
        CategoryDTO current = categoryDAO.getCategoryById(categoryId);
        while (current != null) {
            nodes.add(0, current.getName());
            Integer parentId = current.getParentId();
            if (parentId == null || parentId <= 0) {
                break;
            }
            current = categoryDAO.getCategoryById(parentId);
        }
        return nodes.isEmpty() ? "Không xác định" : String.join(" > ", nodes);
    }
}
