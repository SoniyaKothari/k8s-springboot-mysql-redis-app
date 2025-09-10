package com.pastelplanner.dto;

public class SummaryDTO {
    private int totalHabits;
    private int completedHabits;
    private double totalExpenses;
    private String mood; // placeholder

    public int getTotalHabits() { return totalHabits; }
    public void setTotalHabits(int totalHabits) { this.totalHabits = totalHabits; }

    public int getCompletedHabits() { return completedHabits; }
    public void setCompletedHabits(int completedHabits) { this.completedHabits = completedHabits; }

    public double getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(double totalExpenses) { this.totalExpenses = totalExpenses; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
}
