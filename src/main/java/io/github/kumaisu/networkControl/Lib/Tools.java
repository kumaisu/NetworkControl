package io.github.kumaisu.networkControl.Lib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Tools {

    //  stop : 非表示
    //  print : 強制表示
    //  normal , full , max : 表示レベル
    public static enum consoleMode { print, stop, normal, full, max }
    public static Map< String, consoleMode > consoleFlag = new HashMap<>();

    public static void entryDebugFlag( String programCode, consoleMode key ) {
        consoleFlag.put( programCode, key );
    }

    /**
     * 一時的にDebugModeを設定しなおす
     *
     * @param key
     * @param programCode
     * @return
     */
    public static boolean setDebug( String key, String programCode ) {
        try {
            consoleFlag.put( programCode, consoleMode.valueOf( key ) );
            return true;
        } catch( IllegalArgumentException e ) {
            return false;
        }
    }

    /**
     * keyに対して、設定されているDebugMode下での可否判定を返す
     *
     * @param key
     * @param programCode
     * @return
     */
    public static boolean isDebugFlag( consoleMode key, String programCode ) {
        return ( consoleFlag.get( programCode ).ordinal() >= key.ordinal() );
    }

    /**
     * メッセージ表示
     * @param player    表示するプレイヤー
     * @param msg       表示内容
     * @param key       システムコンソールに表示するか？
     * @param programCode
     */
    public static void Prt( Player player, String msg, consoleMode key, String programCode ) {
        if ( ( key != consoleMode.stop ) && isDebugFlag( key, programCode ) ) {

            String printString = StringBuild( ChatColor.YELLOW.toString(), "(", programCode );
            if ( key != consoleMode.print ) {
                printString = StringBuild( printString, ":", key.toString().substring( 0,1 ).toUpperCase() );
            }
            printString = StringBuild( printString, ") " );

            if ( player != null ) {
                printString = StringBuild( printString, player.getDisplayName(), " " );
            }

            printString = StringBuild( printString, ChatColor.WHITE.toString(), msg );
            Bukkit.getServer().getConsoleSender().sendMessage( printString );
        }
        if ( player != null ) player.sendMessage( msg.split( "/n" ) );
    }

    public static void Prt( String msg, String programCode ) {
        Prt( ( Player ) null, msg, consoleMode.print, programCode );
    }

    public static void Prt( String msg, consoleMode key, String programCode ) {
        Prt( ( Player ) null, msg, key, programCode );
    }

    public static void Prt( Player player, String msg, String programCode ) {
        Prt( player, msg, ( ( player == null ) ? consoleMode.print : consoleMode.stop ), programCode );
    }

    /**
     * Config.ymlで指定されたコンソールコマンドを実行する
     *
     * @param player
     * @param ExecCommand
     * @param Message
     */
    public static void ExecOtherCommand( Player player, String ExecCommand, String Message ) {
        ExecCommand = ExecCommand.replace( "%message%", Message );
        ExecCommand = ExecCommand.replace( "%player%", player.getName() );
        Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), ExecCommand );
    }

    private static Method getMethod(String name, Class<?> clazz ) {
        for ( Method m : clazz.getDeclaredMethods() ) {
            if ( m.getName().equals( name ) ) return m;
        }
        return null;
    }

    public static String getLanguage( Player player ) {
        try {
            Object ep = getMethod( "getHandle", player.getClass() ).invoke( player, ( Object[] ) null );
            Field f = ep.getClass().getDeclaredField( "locale" );
            f.setAccessible( true );
            String language = (String) f.get( ep );
            return language;
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException |
               InvocationTargetException t ) {
            return null;
        }
    }

    /**
     * UUIDを取得する
     *
     * @param name
     * @return
     */
    public static UUID getUUID(String name ) {
        UUID uuid;
        Player player = Bukkit.getServer().getPlayer(name);
        if (player == null) {
            OfflinePlayer offPlayer = Bukkit.getServer().getOfflinePlayer(name);
            if (offPlayer == null) {
                uuid = null;
            } else {
                uuid = offPlayer.getUniqueId();
            }
        } else {
            uuid = player.getUniqueId();
        }
        return uuid;
    }

    /**
     * 複数の文字列を連結するる
     * 通常の＋による連結よりも若干速くなるので速度重視の場所に利用
     * @param StrItem   文字列の集合
     * @return          完成した一つの文章
     */
    public static String StringBuild( String ... StrItem ) {
        StringBuilder buf = new StringBuilder();

        for ( String StrItem1 : StrItem ) buf.append( StrItem1 );

        return buf.toString();
    }

    /**
     * カラーコードを書き換える
     * @param data  書き換え元の文章
     * @return      書き換え後の文章
     */
    public static String ReplaceString( String data ) {
        return data.replace( "%$", "§" );
    }

}
