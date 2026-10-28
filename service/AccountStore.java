package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

public class AccountStore {
    private ConcurrentHashMap<String, Object> accountsMap = new ConcurrentHashMap<String, Object>();
    
    public void add(Object account) {
        if (account != null) {
            try {
                java.lang.reflect.Method m = account.getClass().getMethod("getAccountNumber");
                Object num = m.invoke(account);
                if (num != null) {
                    accountsMap.put(num.toString(), account);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public Object get(String accountNumber) {
        return accountsMap.get(accountNumber);
    }
    
    public ArrayList<Object> getAll() {
        return new ArrayList<Object>(accountsMap.values());
    }
    
    public ArrayList<Object> sortedByBalance() {
        ArrayList<Object> list = getAll();
        Collections.sort(list, new Comparator<Object>() {
            @Override
            public int compare(Object a1, Object a2) {
                try {
                    java.lang.reflect.Method m1 = a1.getClass().getMethod("getBalance");
                    java.lang.reflect.Method m2 = a2.getClass().getMethod("getBalance");
                    long b1 = ((Number)m1.invoke(a1)).longValue();
                    long b2 = ((Number)m2.invoke(a2)).longValue();
                    if (b1 < b2) return -1;
                    if (b1 > b2) return 1;
                    return 0;
                } catch (Exception e) {
                    return 0;
                }
            }
        });
        return list;
    }
}
