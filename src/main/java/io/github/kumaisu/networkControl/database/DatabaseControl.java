package io.github.kumaisu.networkControl.database;

import io.github.kumaisu.networkControl.Lib.Utility;
import io.github.kumaisu.networkControl.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import io.github.kumaisu.networkControl.Lib.Tools;
import static io.github.kumaisu.networkControl.config.Config.programCode;

public class DatabaseControl {

    /**
     * Database Connection(接続) 処理
     */
    public static void connect() throws SQLException {
        if ( Database.dataSource != null ) {
            if ( Database.dataSource.isClosed() ) {
                Tools.Prt( ChatColor.RED + "database closed.", programCode );
                disconnect();
            } else {
                Tools.Prt( ChatColor.AQUA + "dataSource is not null", programCode );
                return;
            }
        }

        Database.DB_URL = "jdbc:mysql://" + Config.host + ":" + Config.port + "/" + Config.database;
    }

    /**
     * Database disConnect(切断) 処理
     */
    public static void disconnect() throws SQLException {
        if ( Database.dataSource != null ) {
            try {
                Database.dataSource.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Database Table Initialize
     */
    public static void TableUpdate() {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            //  sql = "CREATE TABLE IF NOT EXISTS list(id int auto_increment, date DATETIME,name varchar(20), uuid varchar(36), ip INTEGER UNSIGNED, status int, index(id))";
            //  id int auto_increment   DB_ID
            //  date DATETIME           Login Date
            //  name varchar(20)        Login Player
            //  uuid varchar(36),       Loign UUID
            //  ip INTEGER UNSIGNED     IP Address
            //  status int              Success Flag
            //  Login List テーブルの作成
            //  存在すれば、無視される
            String sql = "CREATE TABLE IF NOT EXISTS list(id int auto_increment, date DATETIME,name varchar(20), uuid varchar(36), ip INTEGER UNSIGNED, status int, index(id))";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement pstmt = con.prepareStatement( sql );
            int rowsAffected = pstmt.executeUpdate();
            Tools.Prt( "Create Table Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );

            //  sql = "CREATE TABLE IF NOT EXISTS hosts (ip INTEGER UNSIGNED, host varchar(60), count int, newdate DATETIME, lastdate DATETIME, warning TINYINT )";
            //  ip INTEGER UNSIGNED IP Address
            //  host varchar(60)    Host name
            //  count int           Reference Count
            //  newdate DATETIME    First Log Date
            //  lastdate DATETIME   Last Log Date
            //  warning TINYINT     Warning Flag 0:Warning 1:Silent
            //  Host テーブルの作成
            //  存在すれば、無視される
            sql = "CREATE TABLE IF NOT EXISTS hosts (ip INTEGER UNSIGNED, host varchar(60), count int, newdate DATETIME, lastdate DATETIME, warning TINYINT )";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            pstmt = con.prepareStatement( sql );
            rowsAffected = pstmt.executeUpdate();
            Tools.Prt( "Create Table Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
            Tools.Prt( ChatColor.AQUA + "dataSource Open Success.", programCode );
            con.close();
        } catch( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Table Initialize Error : " + e.getMessage(), programCode);
        }
    }

    /**
     * 新規の照会があった場合に、テキストファイルへ日時と共に記録する
     *
     * @param IP
     * @param DataFolder
     * @return
     */
    public static boolean WriteFileUnknown( String IP, String DataFolder ) {
        File UKfile = new File( DataFolder, "UnknownIP.yml" );
        FileConfiguration UKData = YamlConfiguration.loadConfiguration( UKfile );

        SimpleDateFormat cdf = new SimpleDateFormat("yyyyMMddHHmmss");

        HostData.GetSQL( IP );
        UKData.set( cdf.format( new Date() ), Utility.StringBuild( IP, "[", Database.Host, "]" ) );
        try {
            UKData.save( UKfile );
        }
        catch ( IOException e ) {
            Tools.Prt( Utility.StringBuild( ChatColor.RED.toString(), "Could not save UnknownIP File." ), programCode );
            return false;
        }

        return true;
    }

    /**
     * MySQLコマンドを直接送信する
     *
     * @param player
     * @param cmd
     */
    public static void SQLCommand(Player player, String cmd ) {
        Tools.Prt( player, "== Original SQL Command ==", programCode );

        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            Statement stmt = con.createStatement();
            Tools.Prt( "SQL Command : " + cmd, Tools.consoleMode.max, programCode );
            ResultSet rs = stmt.executeQuery( cmd );

            while( rs.next() ) {
                Tools.Prt( player, ListData.LinePrt( player, rs ),  programCode );
            }

            Tools.Prt( player, "================", programCode );
            con.close();
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error SQL Command : " + e.getMessage(), programCode );
        }
    }
}
