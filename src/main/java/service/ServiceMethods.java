package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static util.Constants.COLON;
import static util.Constants.COUNT_ELEMENTS;
import static util.Constants.FLOAT_VALUE;
import static util.Constants.INTEGER_NUMBERS_VALUE;
import static util.Constants.STATISTIC_FOR;
import static util.Constants.STRING_VALUE;

/**
 * Сервисный класс для работы с файлами
 */
public class ServiceMethods {
    /**
     * Чтение файла и возврат его содержимого в виде списка строк
     *
     * @param filePath путь к файлу для чтения
     * @return список строк, содержащий содержимое файла
     * @throws IOException если возникла ошибка при чтении файла
     */
    public static List<String> readFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * Разделение данных на целые числа, числа с плавающей точкой и строки
     *
     * @param lines список строк для разделения
     * @param integers список целых чисел, полученных в результате разделения
     * @param floats список чисел с плавающей точкой, полученных в результате разделения
     *@param strings список строк, полученных в результате разделения
     */
    public static void separateData(List<String> lines,
                                    List<Integer> integers,
                                    List<Float> floats,
                                    List<String> strings) {
        for (String line : lines) {
            try {
                int intValue = Integer.parseInt(line);
                integers.add(intValue);
            } catch (NumberFormatException e) {
                try {
                    float floatValue = Float.parseFloat(line);
                    floats.add(floatValue);
                } catch (NumberFormatException ex) {
                    strings.add(line);
                }
            }
        }
    }

    /**
     * Обработка файлов и запись данных в отдельные файлы
     *
     * @param filePaths список путей к файлам для обработки
     * @param outputDir каталог для записи файлов с результатами
     * @param prefix префикс для имен файлов с результатами
     * @param append флаг, указывающий на необходимость добавления к существующим файлам
     * @param shortStats флаг, указывающий на необходимость вывода статистики
     * @throws IOException если возникла ошибка при чтении или записи файлов
     */
    public static void processFiles(List<String> filePaths,
                                    String outputDir,
                                    String prefix,
                                    boolean append,
                                    boolean shortStats) throws IOException {
        List<Integer> integers = new ArrayList<>();
        List<Float> floats = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        for (String filePath : filePaths) {
            List<String> lines = readFile(filePath);
            separateData(lines, integers, floats, strings);
        }
        if (!integers.isEmpty()) {
            writeToFile(outputDir, prefix + "integers.txt", integers, append);
            if (shortStats) {
                printStatistics(INTEGER_NUMBERS_VALUE, integers);
            } else {
                printStatisticsFull(INTEGER_NUMBERS_VALUE, integers);
            }
        }
        if (!floats.isEmpty()) {
            writeToFile(outputDir, prefix + "floats.txt", floats, append);
            if (shortStats) {
                printStatistics(FLOAT_VALUE, floats);
            } else {
                printStatisticsFull(FLOAT_VALUE, floats);
            }
        }
        if (!strings.isEmpty()) {
            writeToFile(outputDir, prefix + "strings.txt", strings, append);
            if (shortStats) {
                printStatistics(STRING_VALUE, strings);
            } else {
                printStatisticsFull(STRING_VALUE, strings);
            }
        }
    }

    /**
     * Метод для вывода данных минимальной статистики
     *
     * @param typeName имя типа данных
     * @param data данные
     */
    private static void printStatistics(String typeName, List<?> data) {
        System.out.println(STATISTIC_FOR + typeName + COLON);
        System.out.println(COUNT_ELEMENTS + data.size());
    }

    /**
     * Метод для вывода данных полной статистики
     *
     * @param typeName имя типа данных
     * @param data данные
     */
    private static void printStatisticsFull(String typeName, List<?> data) {
        System.out.println(STATISTIC_FOR + typeName + COLON);
        System.out.println(COUNT_ELEMENTS + data.size());
        if (data.stream().allMatch(Number.class::isInstance)) {
            List<Number> numbers = (List<Number>) data;
            double sum = numbers.stream().mapToDouble(Number::doubleValue).sum();
            double average = sum / numbers.size();
            System.out.println("Сумма: " + sum);
            System.out.println("Среднее значение: " + average);
        } else if (data.stream().allMatch(String.class::isInstance)) {
            List<String> strings = (List<String>) data;
            int minLength = strings.stream().mapToInt(String::length).min().getAsInt();
            int maxLength = strings.stream().mapToInt(String::length).max().getAsInt();
            System.out.println("Минимальная длина строки: " + minLength);
            System.out.println("Максимальная длина строки: " + maxLength);
        }
    }

    /**
     * Метод для записи данных в файл
     *
     * @param outputDir каталог
     * @param fileName имя файла
     * @param data данные
     * @param append флаг, указываеит перезаписать файл или добавить к существующему файлу
     * @throws IOException ошибка при записи в файл
     */
    public static void writeToFile(String outputDir, String fileName, List<?> data, boolean append)
            throws IOException {
        File file = new File(outputDir, fileName);
        if (file.exists() && !append) {
            file.delete();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, !append))) {
            for (Object value : data) {
                writer.write(value.toString());
                writer.newLine();
            }
        }
    }
}
