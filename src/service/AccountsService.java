package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountsService {
    private Map<String, Double> accountsMap;

    public AccountsService() {
        this.accountsMap = new HashMap<>();
    }

    public Map<String, Double> createAccountsMap(List<String> validAccounts) {

        if (validAccounts == null || validAccounts.isEmpty()) {
            System.out.println("Загружен пустой лист!");
            return accountsMap;
        }

        for (String line : validAccounts) {
            String[] parts = line.split(" ", 2);
            String account = parts[0].trim();
            double amount = Double.parseDouble(parts[1].trim());

            // Обработка дубликатов - суммируем суммы
            if (accountsMap.containsKey(account)) {
                double existingAmount = accountsMap.get(account);
                accountsMap.put(account, existingAmount + amount);
                System.out.println("Объединен счет " + account +
                        ": " + existingAmount + " + " + amount + " = " +
                        (existingAmount + amount));
            } else {
                accountsMap.put(account, amount);
            }
        }

        return accountsMap;
    }

    public void printAccountsMap() {
        if (accountsMap.isEmpty()) {
            System.out.println("Счетов нет!");
            return;
        }

        System.out.println("=== СПИСОК СЧЕТОВ ===");
        double totalAmount = 0;

        for (Map.Entry<String, Double> entry : accountsMap.entrySet()) {
            System.out.printf("Счет: %-15s Сумма: %10.2f%n",
                    entry.getKey(), entry.getValue());
            totalAmount += entry.getValue();
        }

        System.out.printf("ОБЩАЯ СУММА: %.2f%n", totalAmount);
    }


    public double getAccountAmount(String accountNumber) {
        return accountsMap.getOrDefault(accountNumber, 0.0);
    }
}