package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class LogService {
    private final List<String> logEntries;
    private final DateTimeFormatter dateFormatter;

    public LogService() {
        this.logEntries = new ArrayList<>();
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Логирует успешную операцию перевода с указанием файла
     */
    public void logSuccess(String sourceFile, String fromAccount, String toAccount, double amount,
                           double newFromBalance, double newToBalance) {
        String timestamp = LocalDateTime.now().format(dateFormatter);
        String logEntry = String.format("%s | %s | перевод с %s на %s в сумме %.2f | успешно обработан | новые балансы: %s=%.2f, %s=%.2f",
                timestamp, sourceFile, fromAccount, toAccount, amount, fromAccount, newFromBalance, toAccount, newToBalance);
        logEntries.add(logEntry);
    }

    /**
     * Логирует ошибку перевода с указанием файла
     */
    public void logError(String sourceFile, String fromAccount, String toAccount, double amount, String errorMessage) {
        String timestamp = LocalDateTime.now().format(dateFormatter);
        String logEntry = String.format("%s | %s | перевод с %s на %s в сумме %.2f | ошибка во время обработки, %s",
                timestamp, sourceFile, fromAccount, toAccount, amount, errorMessage);
        logEntries.add(logEntry);
    }

    /**
     * Логирует ошибку с исходной строкой и указанием файла
     */
    public void logErrorWithRawLine(String sourceFile, String rawLine, String errorMessage) {
        String timestamp = LocalDateTime.now().format(dateFormatter);
        String logEntry = String.format("%s | %s | %s | ошибка во время обработки, %s",
                timestamp, sourceFile, rawLine, errorMessage);
        logEntries.add(logEntry);
    }

    /**
     * Возвращает все записи лога
     */
    public List<String> getLogEntries() {
        return new ArrayList<>(logEntries);
    }

    public List<String> filterLogByDateRange(List<String> logEntries, String startDateStr, String endDateStr) {
        List<String> filteredEntries = new ArrayList<>();

        if (logEntries == null || logEntries.isEmpty()) {
            System.out.println("Лог операций пуст");
            return filteredEntries;
        }

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(startDateStr, dateFormatter);
            LocalDate endDate = LocalDate.parse(endDateStr, dateFormatter);

            if (startDate.isAfter(endDate)) {
                System.out.println("Ошибка: начальная дата не может быть позже конечной");
                return filteredEntries;
            }

            for (String entry : logEntries) {
                if (isEntryInDateRange(entry, startDate, endDate)) {
                    filteredEntries.add(entry);
                }
            }

            System.out.println("Найдено записей за период с " + startDateStr + " по " + endDateStr + ": " + filteredEntries.size());

        } catch (DateTimeParseException e) {
            System.err.println("Ошибка формата даты. Используйте формат: ГГГГ-ММ-ДД (например: 2024-01-15)");
        } catch (Exception e) {
            System.err.println("Ошибка при фильтрации лога: " + e.getMessage());
        }

        return filteredEntries;
    }

    /**
     * Проверяет, находится ли запись в указанном диапазоне дат
     */
    private boolean isEntryInDateRange(String logEntry, LocalDate startDate, LocalDate endDate) {
        try {
            // Дата всегда первая в строке в формате "2024-01-15 14:30:25"
            // Берем первые 10 символов для даты "2024-01-15"
            if (logEntry.length() >= 10) {
                String dateStr = logEntry.substring(0, 10);
                LocalDate entryDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                return !entryDate.isBefore(startDate) && !entryDate.isAfter(endDate);
            }
        } catch (Exception e) {
            // Пропускаем строки с некорректным форматом даты
        }
        return false;
    }

    /**
     * Выводит отфильтрованные записи в консоль
     */
    public void printFilteredLog(List<String> filteredEntries, String startDate, String endDate) {
        if (filteredEntries.isEmpty()) {
            System.out.println("Нет записей за указанный период");
            return;
        }

        System.out.println("\n" + "=".repeat(120));
        System.out.println("ИСТОРИЯ ОПЕРАЦИЙ за период с " + startDate + " по " + endDate);
        System.out.println("=".repeat(120));

        for (int i = 0; i < filteredEntries.size(); i++) {
            System.out.printf("%3d. %s%n", i + 1, filteredEntries.get(i));
        }

        System.out.println("=".repeat(120));
        System.out.println("Всего записей: " + filteredEntries.size());
    }
}
