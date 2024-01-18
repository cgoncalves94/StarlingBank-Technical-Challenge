package com.starlingbank.model;

/**
 * Represents a Saving Goal in the Starling Bank system.
 * @author Cesar Goncalves
 */
public class SavingGoal {
    private final String savingsGoalUid;  // Saving Goal Unique Identifier
    private final String name;            // Name of the Saving Goal
    private final Amount target;          // Target Amount of the Saving Goal

    /**
     * Constructs a SavingGoal with the specified unique identifier, name, and target amount.
     *
     * @param savingsGoalUid the unique identifier for the saving goal
     * @param name the name of the saving goal
     * @param target the target amount for the saving goal
     */
    public SavingGoal(String savingsGoalUid, String name, Amount target) {
        this.savingsGoalUid = savingsGoalUid;
        this.name = name;
        this.target = target;
    }

    /**
     * Returns the unique identifier for the saving goal.
     *
     * @return the savingsGoalUid
     */
    public String getSavingsGoalUid() {
        return savingsGoalUid;
    }

    /**
     * Returns the name of the saving goal.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the target amount for the saving goal.
     *
     * @return the target
     */
    public Amount getTarget() {
        return target;
    }
}
