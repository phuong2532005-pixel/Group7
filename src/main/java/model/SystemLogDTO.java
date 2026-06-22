/**
 *
 * Author : Nguyen Trung Hieu & Nguyen Thi Phuong Linh
 * Date : 27/03/2026
 * Description : DTO cho ban ghi log he thong (thoi gian, nguoi dung, hanh dong).
 */
package model;

public class SystemLogDTO {
    private int id;
    private Integer userId;
    private String userName;
    private String action;
    private String logTime;

    public SystemLogDTO(int id, Integer userId, String userName, String action, String logTime) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.action = action;
        this.logTime = logTime;
    }

    public int getId() { return id; }
    public Integer getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getAction() { return action; }
    public String getLogTime() { return logTime; }
}
