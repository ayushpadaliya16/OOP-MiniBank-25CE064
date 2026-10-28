package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class ReportGenerator {
    
    public static void generateReport(Path directory, Path reportFile) {
        long totalDeposits = 0;
        long totalWithdrawals = 0;
        
        try (BufferedWriter writer = Files.newBufferedWriter(reportFile)) {
            writer.write("=== Transaction Report ===");
            writer.newLine();
            
            try (java.nio.file.DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.log")) {
                for (Path logFile : stream) {
                    writer.write("File: " + logFile.getFileName());
                    writer.newLine();
                    BasicFileAttributes attrs = Files.readAttributes(logFile, BasicFileAttributes.class);
                    writer.write("Size: " + attrs.size() + " bytes");
                    writer.newLine();
                    writer.write("Last Modified: " + attrs.lastModifiedTime());
                    writer.newLine();
                    
                    try (BufferedReader reader = Files.newBufferedReader(logFile)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("DEPOSIT")) {
                                String[] parts = line.split(" ");
                                for (String part : parts) {
                                    if (part.matches("\\d+")) {
                                        totalDeposits += Long.parseLong(part);
                                    }
                                }
                            } else if (line.contains("WITHDRAW")) {
                                String[] parts = line.split(" ");
                                for (String part : parts) {
                                    if (part.matches("\\d+")) {
                                        totalWithdrawals += Long.parseLong(part);
                                    }
                                }
                            }
                        }
                    }
                    writer.newLine();
                }
            }
            
            writer.write("Total Deposits: " + totalDeposits);
            writer.newLine();
            writer.write("Total Withdrawals: " + totalWithdrawals);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
