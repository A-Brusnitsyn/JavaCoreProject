package service;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BankService {
    private final FileProcessing fileProcessing;
    private final Validator validator;
    private final AccountsService accountsService;
    private final LogService logService;
    private TransferService transferService;
    private final Scanner scanner;

    private Map<String, Double> currentAccountsMap;

    public BankService() {
        this.fileProcessing = new FileProcessing();
        this.validator = new Validator();
        this.accountsService = new AccountsService();
        this.logService = new LogService();
        this.scanner = new Scanner(System.in);
        this.transferService = null;
        this.currentAccountsMap = null;
    }

    public void run() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    processTransfers();
                    break;
                case "2":
                    viewOperationHistory();
                    break;
                case "3":
                    printAccounts();
                    break;
                case "4":
                    System.out.println("Выход из программы...");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("БАНКОВСКАЯ СИСТЕМА - ГЛАВНОЕ МЕНЮ");
        System.out.println("=".repeat(50));
        System.out.println("1. Обработать переводы");
        System.out.println("2. Просмотреть историю операций по датам");
        System.out.println("3. Показать текущие счета");
        System.out.println("4. Выход");
        System.out.print("Выберите действие: ");
    }

    //Обрабатывает все переводы
    public void processTransfers() {
        System.out.println("\n=== ОБРАБОТКА ПЕРЕВОДОВ ===");

        if (currentAccountsMap == null || currentAccountsMap.isEmpty()) {
            List<String> bankAccounts = fileProcessing.readBankAccounts();
            System.out.println("Прочитано счетов из файла: " + bankAccounts.size());

            if (bankAccounts.isEmpty()) {
                System.out.println("Файл счетов пуст. Не могу продолжить обработку.");
                return;
            }

            List<String> validAccounts = validator.accValidation(bankAccounts);
            System.out.println("Валидных счетов: " + validAccounts.size());

            if (validAccounts.isEmpty()) {
                System.out.println("Нет валидных счетов для обработки.");
                return;
            }

            currentAccountsMap = accountsService.createAccountsMap(validAccounts);

            if (currentAccountsMap == null || currentAccountsMap.isEmpty()) {
                System.out.println("Не удалось создать карту счетов.");
                return;
            }
        }

        transferService = new TransferService(validator, currentAccountsMap, logService);

        System.out.println("\n=== НАЧАЛЬНОЕ СОСТОЯНИЕ СЧЕТОВ ===");
        accountsService.printAccountsMap();

        Map<String, List<String>> filesWithTransfers = fileProcessing.moneyTransferRead();
        System.out.println("Найдено файлов для переводов: " + filesWithTransfers.size());

        if (!filesWithTransfers.isEmpty()) {
            // ДОПОЛНИТЕЛЬНАЯ ПРОВЕРКА ПЕРЕД ВЫЗОВОМ
            if (transferService != null) {
                transferService.transferMultipleFiles(filesWithTransfers);

                fileProcessing.writeHashMapToFile(currentAccountsMap);

                List<String> logEntries = logService.getLogEntries();
                if (!logEntries.isEmpty()) {
                    fileProcessing.writeLogToFile(logEntries);
                } else {
                    System.out.println("Лог операций пуст, нечего сохранять.");
                }

                System.out.println("Обработка переводов завершена");

            } else {
                System.err.println("Критическая ошибка: transferService равен null");
            }
        } else {
            System.out.println("Нет файлов с переводами для обработки");
        }
    }

    //Просматривает историю операций по датам
    public void viewOperationHistory() {
        System.out.println("\n=== ПРОСМОТР ИСТОРИИ ОПЕРАЦИЙ ПО ДАТАМ ===");

        List<String> logEntries = fileProcessing.readLogFile();

        if (logEntries.isEmpty()) {
            System.out.println("Лог операций пуст. Сначала выполните обработку переводов.");
            return;
        }

        System.out.print("Введите начальную дату (ГГГГ-ММ-ДД): ");
        String startDate = scanner.nextLine().trim();

        System.out.print("Введите конечную дату (ГГГГ-ММ-ДД): ");
        String endDate = scanner.nextLine().trim();

        List<String> filteredEntries = logService.filterLogByDateRange(logEntries, startDate, endDate);

        logService.printFilteredLog(filteredEntries, startDate, endDate);
    }

    //Печатает текущие счета
    public void printAccounts() {
        System.out.println("\n=== ТЕКУЩЕЕ СОСТОЯНИЕ СЧЕТОВ ===");

        if (currentAccountsMap == null || currentAccountsMap.isEmpty()) {
            // Попробуем загрузить из сохраненного файла
            List<String> savedAccounts = fileProcessing.readBankAccounts();
            if (!savedAccounts.isEmpty()) {
                List<String> validAccounts = validator.accValidation(savedAccounts);
                currentAccountsMap = accountsService.createAccountsMap(validAccounts);
                accountsService.printAccountsMap();
            } else {
                System.out.println("Файл с сохраненными счетами пуст или не существует.");
            }

        } else {
            accountsService.printAccountsMap();
        }
    }

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}