package model;

public class Order {
    private String orderId;
    private String userId;
    private String proId;
    private String orderTime;

    // Full constructor
    public Order(String orderId, String userId, String proId, String orderTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.proId = proId;
        this.orderTime = orderTime;
    }

   
    public Order() {
        this("o_00000", "u_0000000000", "p_00000", "01-01-2000_00:00:00");
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getProId() {
        return proId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    @Override
    public String toString() {
        return String.format("{\"order_id\":\"%s\", \"user_id\":\"%s\", \"pro_id\":\"%s\", \"order_time\":\"%s\"}",
                orderId, userId, proId, orderTime);
    }
}
