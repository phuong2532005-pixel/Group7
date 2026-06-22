/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 29/03/2026
 * Description : DTO cho anh cua bien the (variant image metadata).
 */
package model;

public class VariantImageDTO {
    private int id;
    private int variantId;
    private String imageUrl;
    private boolean isThumbnail;

    public VariantImageDTO(int id, int variantId, String imageUrl, boolean isThumbnail) {
        this.id = id;
        this.variantId = variantId;
        this.imageUrl = imageUrl;
        this.isThumbnail = isThumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isThumbnail() {
        return isThumbnail;
    }

    public void setThumbnail(boolean thumbnail) {
        isThumbnail = thumbnail;
    }
}
