package service;

import exceptions.IncorrectAmountException;
import exceptions.WrongAccountException;

import java.util.List;
import java.util.Map;

public class TransferService {
    private final Validator accountValidator;
    private Map<String, Double> accountsMap;
    private final LogService transferLogger;

    public TransferService(Validator accountValidator, Map<String, Double> accountsMap, LogService transferLogger) {
        this.accountValidator = accountValidator;
        this.accountsMap = accountsMap;
        this.transferLogger = transferLogger;
    }

    //Обрабатывает переводы из одного файла
    public void transferFile(String sourceFile, List<String> transfers) {
        if (transfers == null || transfers.isEmpty()) {
            System.out.println("Нет данных для перевода в файле: " + sourceFile);
            return;
        }

        List<String> verifiedTransfers = accountValidator.accValidation(transfers);

        for (int i = 0; i < verifiedTransfers.size(); i++) {
            String verifiedLine = verifiedTransfers.get(i);

            try {
                transfer(sourceFile, verifiedLine);
            } catch (WrongAccountException | IncorrectAmountException e) {
                // Логируем ошибку с указанием файла
                logTransferError(sourceFile, verifiedLine, e.getMessage());
            }
        }
    }

    //Обрабатывает переводы из нескольких файлов
    public void transferMultipleFiles(Map<String, List<String>> filesWithTransfers) {
        if (filesWithTransfers == null || filesWithTransfers.isEmpty()) {
            System.out.println("Нет файлов для обработки!");
            return;
        }
        for (Map.Entry<String, List<String>> entry : filesWithTransfers.entrySet()) {
            String fileName = entry.getKey();
            List<String> transfers = entry.getValue();
            transferFile(fileName, transfers);
        }
    }


    //Выполняет перевод с указанием файла-источника
    public void transfer(String sourceFile, String line) {
        String[] parts = line.split(" ");
        String from = parts[0].trim();
        Double value = Double.parseDouble(parts[1].trim());
        String to = parts[2].trim();

        // Проверяем существование счета отправителя
        if (!accountsMap.containsKey(from)) {
            throw new WrongAccountException("Счет отправителя не найден: " + from);
        }
        if (from.equals(to)) {
            throw new WrongAccountException("Нельзя переводить на тот же счет: " + from);
        }
        // Сохраняем текущие балансы для логирования
        double fromAccountBalance = accountsMap.get(from);
        double toAccountBalance = accountsMap.getOrDefault(to, 0.0);

        // Проверяем достаточно ли средств
        if (fromAccountBalance < value) {
            throw new IncorrectAmountException("Недостаточно средств на счете " + from +
                    ". Доступно: " + fromAccountBalance + ", требуется: " + value);
        }

        // Выполняем перевод
        double newFromBalance = fromAccountBalance - value;
        accountsMap.put(from, newFromBalance);

        double newToBalance = toAccountBalance + value;
        accountsMap.put(to, newToBalance);

        // Логируем успешную операцию с указанием файла
        transferLogger.logSuccess(sourceFile, from, to, value, newFromBalance, newToBalance);
    }

    //Логирует ошибку перевода с указанием файла
    private void logTransferError(String sourceFile, String line, String errorMessage) {
        try {
            String[] parts = line.split(" ");
            if (parts.length == 3) {
                String from = parts[0].trim();
                String to = parts[2].trim();
                double value = Double.parseDouble(parts[1].trim());
                transferLogger.logError(sourceFile, from, to, value, errorMessage);
            } else {
                transferLogger.logErrorWithRawLine(sourceFile, line, errorMessage);
            }
        } catch (Exception e) {
            // Если не удалось распарсить строку, логируем как есть
            transferLogger.logErrorWithRawLine(sourceFile, line, errorMessage);
        }
    }
}
