package com.starlingbank.model;

public class Account {
    private final String accountUid;
    private final String categoryUid;


    public Account(String accountUid, String categoryUid) {
        this.accountUid = accountUid;
        this.categoryUid = categoryUid;

    }

    public String getAccountUid() {
        return accountUid;
    }

    public String getCategoryUid() {
        return categoryUid;
    }

}
