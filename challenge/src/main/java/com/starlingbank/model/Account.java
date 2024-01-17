package com.starlingbank.model;

/**
 * Represents an Account in the Starling Bank system.
 */
public class Account {
    // Unique identifier for the account
    private final String accountUid;
    // Unique identifier for the category associated with the account
    private final String categoryUid;
    

    /**
     * Constructs a new Account with the specified accountUid and categoryUid.
     *
     * @param accountUid  the unique identifier for the account
     * @param categoryUid the unique identifier for the category associated with the account
     */
    public Account(String accountUid, String categoryUid) {
        this.accountUid = accountUid;
        this.categoryUid = categoryUid;
    }

    /**
     * Returns the unique identifier for the account.
     *
     * @return the accountUid
     */
    public String getAccountUid() {
        return accountUid;
    }

    /**
     * Returns the unique identifier for the category associated with the account.
     *
     * @return the categoryUid
     */
    public String getCategoryUid() {
        return categoryUid;
    }
}
