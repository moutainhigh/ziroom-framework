package com.ziroom.framework.autoconfigure.jdbc;

/**
 * @author LDM
 */
public final class DataBaseVisitedManager {
    private static final ThreadLocal<String> DB_VISITED = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "";
        }
    };


    public static String getVisitedDB() {
        return DB_VISITED.get();
    }


    public static void setVisitedDB(String dbId) {
        DB_VISITED.set(dbId);
    }


    public static void clear() {
        DB_VISITED.remove();
    }
}
