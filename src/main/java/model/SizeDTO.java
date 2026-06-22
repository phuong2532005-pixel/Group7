/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 26/03/2026
 * Description : DTO cho thong tin kich co (size) cua san pham.
 */
package model;

public class SizeDTO {
    private int id;
    private String sizeName;

    public SizeDTO() {
    }

    public SizeDTO(int id, String sizeName) {
        this.id = id;
        this.sizeName = sizeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getName() {
        return sizeName;
    }
}
