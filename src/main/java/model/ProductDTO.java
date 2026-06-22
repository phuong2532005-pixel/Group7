/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 18/03/2026
 * Description : DTO chi tiet san pham (id, name, description, basePrice, thumbnail).
 */
package model;

public class ProductDTO {

    private int id;
    private String name;
    private String description;
    private int categoryId;
    private long basePrice;
    private String thumbnail;
    private int status;

    public ProductDTO(int id, String name, String description, int categoryId, long basePrice,
            String thumbnail, int status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.basePrice = basePrice;
        this.thumbnail = thumbnail;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public long getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(long basePrice) {
        this.basePrice = basePrice;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
