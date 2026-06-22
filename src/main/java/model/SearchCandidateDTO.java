/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 21/03/2026
 * Description : DTO cho tu dong goi y tim kiem (search candidate) tren UI.
 */
package model;

public class SearchCandidateDTO {

    private int id;
    private String name;
    private String description;
    private String categoryName;
    private int totalSold;

    public SearchCandidateDTO(int id, String name, String description, String categoryName, int totalSold) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryName = categoryName;
        this.totalSold = totalSold;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getTotalSold() {
        return totalSold;
    }
}
