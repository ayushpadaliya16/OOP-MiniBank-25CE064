package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TransactionLog {
    
    public static void append(Path file, String line) {
        try {
            if (!Files.exists(file)) {
                Files.write(file, (line + "\n").getBytes(), StandardOpenOption.CREATE);
            } else {
                Files.write(file, (line + "\n").getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
