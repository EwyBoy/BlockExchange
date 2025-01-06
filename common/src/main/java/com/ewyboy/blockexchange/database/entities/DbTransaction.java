package com.ewyboy.blockexchange.database.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class DbTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buy_order_id", nullable = false)
    private DbOrder buyOrder;

    @ManyToOne
    @JoinColumn(name = "sell_order_id", nullable = false)
    private DbOrder sellOrder;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private DbItem DBItem;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "price_per_item", nullable = false)
    private double pricePerItem;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DbOrder getBuyOrder() {
        return buyOrder;
    }

    public void setBuyOrder(DbOrder buyDbOrder) {
        this.buyOrder = buyDbOrder;
    }

    public DbOrder getSellOrder() {
        return sellOrder;
    }

    public void setSellOrder(DbOrder sellDbOrder) {
        this.sellOrder = sellDbOrder;
    }

    public DbItem getItem() {
        return DBItem;
    }

    public void setItem(DbItem DBItem) {
        this.DBItem = DBItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(double pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
