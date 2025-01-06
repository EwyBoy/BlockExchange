package com.ewyboy.blockexchange.database.entities;

import com.ewyboy.blockexchange.database.enums.OrderState;
import com.ewyboy.blockexchange.database.enums.OrderTypes;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class DBOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private DBPlayer player;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private DBItem item;

    @Column(name = "order_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderTypes orderType = OrderTypes.BUY;

    @Column(name = "order_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderState orderState = OrderState.OPEN;

    @Column(nullable = false)
    private int amount = 1;

    @Column(name = "price_per_item", nullable = false)
    private double pricePerItem;

    @Column(name = "fulfilled_quantity", nullable = false)
    private int fulfilledQuantity = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBPlayer getPlayer() {
        return player;
    }

    public void setPlayer(DBPlayer DBPlayer) {
        this.player = DBPlayer;
    }

    public DBItem getItem() {
        return item;
    }

    public void setItem(DBItem DBItem) {
        this.item = DBItem;
    }

    public OrderTypes getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderTypes orderType) {
        this.orderType = orderType;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(double pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public int getFulfilledQuantity() {
        return fulfilledQuantity;
    }

    public void setFulfilledQuantity(int fulfilledQuantity) {
        this.fulfilledQuantity = fulfilledQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
