package com.hyy.accountassis.bean;

public class Bill {
    private float incomeAmount;
    private String createDate;
    private float expenseAmount;
    private float balance;

    public float getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(float incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public float getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(float expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "incomeAmount=" + incomeAmount +
                ", createDate='" + createDate + '\'' +
                ", expenseAmount=" + expenseAmount +
                ", balance=" + balance +
                '}';
    }
}
