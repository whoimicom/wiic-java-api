package kim.kin.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicBoolean;
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
        Stream<String> lines = Files.lines(Paths.get(SOURCE_PATH));
        Path path = Paths.get(TARGET_PATH);
        Files.deleteIfExists(path);
        Files.createFile(path);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            lines.forEach(lin -> {
                AtomicReferenceArray<String> split = new AtomicReferenceArray<>(lin.split("\\|➤"));
                if ((split.length() >= 2)) {
                    try {
                        bufferedWriter.write(String.format("%1$-" + 27 + "s", split.get(0).trim()) + " |➤" + split.get(1).trim() + LINE_SEPARATOR);
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

    public static void formatShortcut() throws IOException {
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
                        bufferedWriter.write(String.format("%1$-" + 20 + "s", key) + "  ➤" + lin + lineSeparator);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        System.out.println("FINISH");
    }
}
