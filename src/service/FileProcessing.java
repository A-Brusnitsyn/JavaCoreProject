package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileProcessing {

    private final String bankAccountsPath = "src\\resources\\bank_accounts.txt";
    private static String transfersFolderPath = "src\\resources\\input";
    private static String transfersArchive = "src\\resources\\archive";
    private static String logPath = "src\\resources\\operations_log.txt";

    public List<String> readBankAccounts() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new FileReader(bankAccountsPath))) {
            String line;
            while ((line = bf.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
        return lines;
    }

    public void writeHashMapToFile(Map<String, Double> accountsMap) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(bankAccountsPath))) {

            for (Map.Entry<String, Double> entry : accountsMap.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
            System.out.println("Актуальное состояние счетов сохранено в файл: " + bankAccountsPath);

        } catch (IOException e) {
            System.err.println("Ошибка записи файла: " + e.getMessage());
        }
    }

    public Map<String, List<String>> moneyTransferRead() {
        Map<String, List<String>> filesWithTransfers = new HashMap<>();
        File folder = new File(transfersFolderPath);
        File[] files = folder.listFiles();

        if (files == null) {
            System.out.println("Папка пуста или недоступна: " + transfersFolderPath);
            return filesWithTransfers;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {
                List<String> fileTransfers = new ArrayList<>();

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String trimmedLine = line.trim();
                        if (!trimmedLine.isEmpty() && !trimmedLine.startsWith("#")) {
                            fileTransfers.add(trimmedLine);
                        }
                    }
                    filesWithTransfers.put(file.getName(), fileTransfers);
                } catch (IOException e) {
                    System.err.println("Ошибка при чтении файла " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        moveToArchive(transfersFolderPath,transfersArchive);
        return filesWithTransfers;
    }

    private static List<String> moveToArchive(String sourceFolderPath, String targetFolderPath) {
        List<String> movedFiles = new ArrayList<>();

        File sourceFolder = new File(transfersFolderPath);
        File targetFolder = new File(transfersArchive);

        if (!targetFolder.exists()) {
            if (targetFolder.mkdirs()) {
                System.out.println("Создана папка архива: " + targetFolderPath);
            } else {
                System.err.println("Не удалось создать папку архива: " + targetFolderPath);
                return movedFiles; // возвращаем пустой список, если папку не удалось создать
            }
        }

        // Получаем все файлы в исходной папке
        File[] files = sourceFolder.listFiles();
        if (files == null) {
            return movedFiles; // папка пуста или недоступна
        }

        // Проходим по всем файлам
        for (File file : files) {
            // Фильтруем только .txt-файлы
            if (file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {
                // Формируем путь к целевому файлу
                File targetFile = new File(targetFolder, file.getName());

                // Пытаемся переместить (переименовать) файл
                if (file.renameTo(targetFile)) {
                    movedFiles.add(file.getName());
                    System.out.println("Перемещён: " + file.getName() + " → " + targetFolderPath);
                } else {
                    System.err.println("Не удалось переместить файл: " + file.getName());
                }
            }
        }
        return movedFiles;
    }

    public void writeLogToFile(List<String> logEntries) {
        if (logEntries == null || logEntries.isEmpty()) {
            System.out.println("Нет данных для записи в лог");
            return;
        }

        try (FileWriter writer = new FileWriter(logPath,true)) {
            for (String logEntry : logEntries) {
                writer.write(logEntry + "\n");
            }
            System.out.println("Лог операций записан в файл: " + logPath);
            System.out.println("Записано записей: " + logEntries.size());
        } catch (IOException e) {
            System.err.println("Ошибка записи лог файла: " + e.getMessage());
        }
    }

    public List<String> readLogFile() {
        List<String> logEntries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(logPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logEntries.add(line);
            }
            System.out.println("Прочитано записей из лога: " + logEntries.size());
        } catch (IOException e) {
            System.err.println("Ошибка чтения лог файла: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка при чтении лог файла: " + e.getMessage());
        }

        return logEntries;
    }

}