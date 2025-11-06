package com.expensetracker.modules;

import com.expensetracker.interfaces.IModule;

public abstract class BaseModule implements IModule {
    protected String currentUser;
    protected double userIncome;

    @Override
    public void setUserData(String username, double income) {
        this.currentUser = username;
        this.userIncome = income;
    }

    @Override
    public void clearUserData() {
        this.currentUser = null;
        this.userIncome = 0.0;
    }

    protected void validateUser() throws Exception {
        if (currentUser == null) {
            throw new Exception("com.expensetracker.model.User not logged in!");
        }
    }
}