package kim.kin.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Stream;

/**
 * @author kin.kim
 * @Date 2021-10-28
 */
public class ShortcutUtils {
    public static final String SOURCE_PATH = "C:\\Users\\choky\\Desktop\\online\\shortcut.txt";
    public static final String TARGET_PATH = "C:\\Users\\choky\\Desktop\\online\\shortcut_.txt";
    public static final String LINE_SEPARATOR = System.lineSeparator();

    public static void main(String[] args) throws IOException {
        formatMarkdown();
    }

    /**
     * | Format Specifier | Data Type | Output |
     * | ---              | ---       | ---    |
     *
     * @throws IOException
     */
    public static void formatMarkdown() throws IOException {
        Stream<String> lines = Files.lines(Paths.get(SOURCE_PATH));
        Path path = Paths.get(TARGET_PATH);
        Files.deleteIfExists(path);
        Files.createFile(path);
        AtomicInteger countLine = new AtomicInteger(1);
        Map<Integer, Map<Integer, String>> columnData = new HashMap<>(1);
        Map<Integer, Integer> maxLength = new HashMap<>(1);
        AtomicInteger columnCount = new AtomicInteger(0);
        lines.forEach(lin -> {
            AtomicReferenceArray<String> split = new AtomicReferenceArray<>(lin.split("\\|"));
            int length = split.length();
            if (countLine.get() == 1) {
                columnCount.set(length);
                for (int column = 1; column < length; column++) {
                    String value = split.get(column);
                    AtomicReference<Map<Integer, String>> columnMap = new AtomicReference<>(new HashMap<Integer, String>());
                    columnMap.get().put(1, value);
                    columnData.put(column, columnMap.get());
                }
            } else if (countLine.get() == 2) {
                for (int column = 1; column < columnCount.get(); column++) {
//                    String value = split.get(column);
                    Map<Integer, String> columnMap = columnData.get(column);
                    columnMap.put(countLine.get() - 1, "-");
                }
            } else {
                for (int column = 1; column < columnCount.get(); column++) {
                    String value = split.get(column);
                    Map<Integer, String> columnMap = columnData.get(column);
                    columnMap.put(countLine.get() - 1, value);
                }
            }
            countLine.getAndIncrement();
        });
        for (int column = 1; column < columnCount.get(); column++) {
            Map<Integer, String> columnMap = columnData.get(column);
            Integer max = columnMap.values().stream().map(String::length).max(Integer::compareTo).orElse(0);
            maxLength.put(column, max);
        }
        for (int line = 0; line < countLine.get() - 1; line++) {
            String allLine = "|";
            for (int column = 1; column < columnCount.get(); column++) {
                Map<Integer, String> integerStringMap = columnData.get(column);
                String s = integerStringMap.get(line);
                if (line==1) {
                    allLine = allLine + String.format("%1$-" + maxLength.get(column) + "s", s).replace("","-") + " |";
                }else{
                    allLine = allLine + String.format("%1$-" + maxLength.get(column) + "s", s) + " |";
                }

            }
            System.out.println(allLine);
        }

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {

      /*          if ((split.length() >= 2)) {
                    try {
                        String str = String.format("%1$-" + 27 + "s", split.get(0).trim()) + " |➤" + split.get(1).trim() + LINE_SEPARATOR;
                        System.out.print(str);
                        bufferedWriter.write(str);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        bufferedWriter.write(lin + LINE_SEPARATOR);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/

        }
        System.out.println(maxLength);
        System.out.println(columnData);
        System.out.println("FINISH");
    }

    public static void formatShortcut() throws IOException {
        Stream<String> lines = Files.lines(Paths.get(SOURCE_PATH));
        Path path = Paths.get(TARGET_PATH);
        Files.deleteIfExists(path);
        Files.createFile(path);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            lines.forEach(lin -> {
                AtomicReferenceArray<String> split = new AtomicReferenceArray<>(lin.split("\\|➤"));
                if ((split.length() >= 2)) {
                    try {
                        String str = String.format("%1$-" + 27 + "s", split.get(0).trim()) + " |➤" + split.get(1).trim() + LINE_SEPARATOR;
                        System.out.print(str);
                        bufferedWriter.write(str);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        bufferedWriter.write(lin + LINE_SEPARATOR);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
        }
        System.out.println("FINISH");
    }

    public static void formatShortcutHaveBreak() throws IOException {
        Stream<String> lines = Files.lines(Paths.get(SOURCE_PATH));
        String lineSeparator = System.lineSeparator();
        AtomicReference<String> key = new AtomicReference<>("");
        AtomicReference<String> val = new AtomicReference<>("");
        AtomicBoolean isKey = new AtomicBoolean(false);
        Path path = Paths.get(TARGET_PATH);
        Files.deleteIfExists(path);
        Files.createFile(path);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            lines.forEach(lin -> {
                isKey.getAndSet(!isKey.get());
                if (isKey.get()) {
                    key.set(lin);
                } else {
                    val.set(lin);
                    try {
                        String str = String.format("%1$-" + 20 + "s", key) + "  ➤" + lin + lineSeparator;
                        System.out.print(str);
                        bufferedWriter.write(str);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        System.out.println("FINISH");
    }


}
