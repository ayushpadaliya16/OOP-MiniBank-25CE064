package service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import model.annotation.Id;

public class Repository<T> {
    private ConcurrentHashMap<String, T> map = new ConcurrentHashMap<String, T>();
    
    public T save(T entity) {
        if (entity == null) return null;
        String id = getId(entity);
        if (id != null) {
            map.put(id, entity);
        }
        return entity;
    }
    
    @SafeVarargs
    public final void saveAll(T... items) {
        if (items == null) return;
        for (T item : items) {
            save(item);
        }
    }
    
    public T findById(String id) {
        return map.get(id);
    }
    
    public ArrayList<T> findAll() {
        return new ArrayList<T>(map.values());
    }
    
    public boolean delete(String id) {
        return map.remove(id) != null;
    }
    
    private String getId(T entity) {
        Class<?> clazz = entity.getClass();
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(entity);
                        if (value != null) return value.toString();
                    } catch (Exception e) {
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
