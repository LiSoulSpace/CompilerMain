package xyz.soulspace.symbols;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
//查填符号表

public class SymbolTable {
    public static class Properties extends HashMap<String, String> {
    }

    public static int size = 0;

    static Map<String, Properties> symbolTable = new HashMap<String, Properties>();

    public static void addItem(String id) {
        if (!symbolTable.containsKey(id)) {
            symbolTable.put(id, new Properties());
        }
    }

    public static void setItem(String id, String property, String value) {
        if (!symbolTable.containsKey(id)) {
            symbolTable.put(id, new Properties());
        }
        symbolTable.get(id).put(property, value);
        if (property.equals("type")) {
            if (value.equals("int")) {
                symbolTable.get(id).put("width", String.valueOf(4));
                symbolTable.get(id).put("address", String.valueOf(size));
                size += 4;
            } else if (value.equals("float")) {
                symbolTable.get(id).put("width", String.valueOf(4));
                symbolTable.get(id).put("address", String.valueOf(size));
                size += 4;
            }
        }
    }

    public static Set<String> getIds() {
        return symbolTable.keySet();
    }

    public static Properties getProperties(String id) {
        return symbolTable.get(id);
    }

    public static String getProperty(String id, String property) {
        return getProperties(id).get(property);
    }

    public static String printSymbolTableToString() {
        StringBuilder sb = new StringBuilder();
        for (String id : getIds()) {
            sb.append(id).append(" ");
            for (String property : getProperties(id).keySet()) {
                sb.append(property).append(":").append(getProperty(id, property)).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
