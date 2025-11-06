package com.expensetracker.interfaces;

import java.util.function.BiConsumer;

public interface IAuthOperations {
    void showLoginScreen(BiConsumer<String, Double> onSuccess);
    void showCreateAccountScreen(BiConsumer<String, Double> onSuccess);
}