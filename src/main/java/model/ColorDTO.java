/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 14/03/2026
 * Description : DTO cho thong tin mau sac (color id, name, hex).
 */
package model;

public class ColorDTO {
    private int id;
    private String colorName;

    public ColorDTO() {
    }

    public ColorDTO(int id, String colorName) {
        this.id = id;
        this.colorName = colorName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getName() {
        return colorName;
    }
}
