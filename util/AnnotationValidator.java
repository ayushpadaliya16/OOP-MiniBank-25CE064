package util;

import java.lang.reflect.Field;
import model.annotation.MaxLength;
import model.annotation.Positive;

public class AnnotationValidator {
    
    public static String[] validate(Object obj) {
        if (obj == null) {
            return new String[0];
        }
        
        java.util.ArrayList<String> errors = new java.util.ArrayList<String>();
        Field[] fields = obj.getClass().getDeclaredFields();
        
        for (Field field : fields) {
            if (field.isAnnotationPresent(Positive.class)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    if (value instanceof Number) {
                        Number num = (Number) value;
                        if (num.longValue() <= 0) {
                            Positive positive = field.getAnnotation(Positive.class);
                            errors.add(field.getName() + ": " + positive.message());
                        }
                    }
                } catch (IllegalAccessException e) {
                    // Skip
                }
            }
            
            if (field.isAnnotationPresent(MaxLength.class)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    if (value instanceof String) {
                        String str = (String) value;
                        MaxLength maxLength = field.getAnnotation(MaxLength.class);
                        if (str.length() > maxLength.value()) {
                            errors.add(field.getName() + ": exceeds max length of " + maxLength.value());
                        }
                    }
                } catch (IllegalAccessException e) {
                    // Skip
                }
            }
        }
        
        return errors.toArray(new String[0]);
    }
}
