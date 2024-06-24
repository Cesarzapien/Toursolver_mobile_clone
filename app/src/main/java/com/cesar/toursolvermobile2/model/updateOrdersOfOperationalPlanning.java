package com.cesar.toursolvermobile2.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class updateOrdersOfOperationalPlanning {

    @SerializedName("orders")
    private List<OperationalOrderAchievement> orders;

    public List<OperationalOrderAchievement> getOrders() {
        return orders;
    }

    public void setOrders(List<OperationalOrderAchievement> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "updateOrdersOfOperationalPlanning{" +
                "orders=" + orders +
                '}';
    }

    public updateOrdersOfOperationalPlanning(List<OperationalOrderAchievement> orders) {
        this.orders = orders;
    }
}
