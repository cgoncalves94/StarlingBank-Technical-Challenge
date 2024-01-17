package com.starlingbank.model;

public class SavingGoal {
    private final String savingsGoalUid;
    private final String name;
    private final Amount target;

    public SavingGoal(String savingsGoalUid, String name, Amount target) {
        this.savingsGoalUid = savingsGoalUid;
        this.name = name;
        this.target = target;
    }

    public String getSavingsGoalUid() {
        return savingsGoalUid;
    }

    public String getName() {
        return name;
    }

    public Amount getTarget() {
        return target;
    }
}
