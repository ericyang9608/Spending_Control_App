package com.hyy.accountassis.bean;

public class Budget {
    private int id;
    private int account_id;
    private Account account;
    private float amount;
    private String month;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", account_id=" + account_id +
                ", account=" + account +
                ", amount=" + amount +
                ", month='" + month + '\'' +
                '}';
    }
}
