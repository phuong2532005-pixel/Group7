/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 13/03/2026
 * Description : DTO cho thong tin danh muc san pham.
 */
package model;

public class CategoryDTO {

    private int id;
    private String name;
    private Integer parentId;
    private int level;

    public CategoryDTO(int id, String name, Integer parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public CategoryDTO(int id, String name, Integer parentId, int level) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public int getLevel() {
        return level;
    }
}
