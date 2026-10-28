package service;

import java.io.*;

public class StatePersister {
    
    public static void save(Object[] accounts, java.nio.file.Path file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            oos.writeObject(accounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Object[] load(java.nio.file.Path file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            Object obj = ois.readObject();
            if (obj instanceof Object[]) {
                return (Object[]) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
