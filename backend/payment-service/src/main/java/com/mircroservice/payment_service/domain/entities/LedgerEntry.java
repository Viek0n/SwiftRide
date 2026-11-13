package com.mircroservice.payment_service.domain.entities;

public class LedgerEntry {
    private String account;
    private Direction direction;
    private long amount;

    public enum Direction {
        DEBIT,
        CREDIT
    }

    public LedgerEntry() { }
    public LedgerEntry(String account, Direction direction, long amount) {
        this.account = account;
        this.direction = direction;
        this.amount = amount;
    }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
}
