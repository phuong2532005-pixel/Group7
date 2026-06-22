/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 11/03/2026
 * Description : DTO chua thong tin dia chi (street, district, city, isDefault).
 */
package model;

public class AddressDTO {
    private int id;
    private int userId;
    private String street;
    private String district;
    private String city;
    private boolean isDefault;

    public AddressDTO(int id, int userId, String street, String district, String city, boolean isDefault) {
        this.id = id;
        this.userId = userId;
        this.street = street;
        this.district = district;
        this.city = city;
        this.isDefault = isDefault;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getStreet() {
        return street;
    }

    public String getDistrict() {
        return district;
    }

    public String getCity() {
        return city;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public String getFullAddress() {
        return street + ", " + district + ", " + city;
    }
}
