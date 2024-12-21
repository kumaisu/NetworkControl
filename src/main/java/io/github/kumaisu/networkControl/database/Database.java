package io.github.kumaisu.networkControl.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;

public class Database {
    public static Connection dataSource = null;
    public static final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    //  sql = "CREATE TABLE IF NOT EXISTS hosts (ip INTEGER UNSIGNED, host varchar(60), count int, newdate DATETIME, lastdate DATETIME, warning TINYINT )";
    public static String IP = "0.0.0.0";
    public static String Host = "Unknown";
    public static int Count = 0;
    public static Date NewDate = new Date();
    public static Date LastDate = new Date();
    public static boolean Warning = false;
    public static String DB_URL = "";
}
