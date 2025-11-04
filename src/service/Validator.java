package service;

import exceptions.IncorrectAmountException;
import exceptions.WrongAccountException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    private final String accRegex = "^\\d{5}-\\d{5}$";
    private final Pattern accPattern = Pattern.compile(accRegex);

    public List<String> accValidation(List<String> accounts) {
        List<String> validAccounts = new ArrayList<>();
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("Нет данных счетов для проверки");
            return validAccounts;
        }

        for (int i = 0; i < accounts.size(); i++) {
            String line = accounts.get(i).trim().replaceAll("\\s+", " ");

            try {
                if (isValidAccount(line)) {
                    validAccounts.add(line);
                }
            } catch (WrongAccountException | IncorrectAmountException e) {
                System.out.println(e.getMessage() + " " + line);
            }

        }
        return validAccounts;
    }

    private boolean isValidAccount(String line) throws WrongAccountException, IncorrectAmountException {
        //if (line == null || line.trim().isEmpty()) {
        //    throw new WrongAccountException("Пустая строка!");
        //}

        String[] parts = line.split("\\s+");
        // Убедимся, что строка содержит счет и значение
        if (parts.length == 2) {
            String acc = parts[0].trim();
            String valueString = parts[1].trim();
            // Создаем Matcher для проверки счета
            Matcher keyMatcher = accPattern.matcher(acc);
            // Проверяем, соответствует ли счет шаблону
            if (!keyMatcher.matches()) {
                throw new WrongAccountException("Неверный счет!");
            }

            try {
                if (valueString.matches(".*[a-zA-Zа-яА-Я].*")) {
                    throw new IncorrectAmountException("Сумма не должна содержать буквы!");
                }
                // Пробуем преобразовать значение в число
                double value = Double.parseDouble(valueString);
                if (value < 0) {
                    throw new IncorrectAmountException("Отрицательная сумма!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Неверно указана сумма на счете! " + line);
                return false;
            }
            return true;

        } else if (parts.length == 3) {
            String acc = parts[0].trim();
            String acc2 = parts[2].trim();
            String valueString = parts[1].trim();
            // Создаем Matcher для проверки счета
            Matcher account = accPattern.matcher(acc);
            Matcher account2 = accPattern.matcher(acc2);
            // Проверяем, соответствует ли счет шаблону
            if (!account.matches()) {
                throw new WrongAccountException("Неверный счет!");
            }
            if (!account2.matches()) {
                throw new WrongAccountException("Неверный счет!");
            }

            try {
                if (valueString.matches(".*[a-zA-Zа-яА-Я].*")) {
                    throw new IncorrectAmountException("Отказ в переводе! Сумма не должна содержать буквы!");
                }
                // Пробуем преобразовать значение в число
                double value = Double.parseDouble(valueString);
                if (value <= 0) {
                    throw new IncorrectAmountException("Отказ в переводе, сумма должна быть положительной!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Неверно указана сумма перевода! " + line);
                return false;
            }
            return true;
        } else {
            throw new WrongAccountException("Неверный формат!");
        }

    }
}
