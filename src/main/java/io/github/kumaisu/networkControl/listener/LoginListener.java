/*
 *  Copyright (c) 2019 sugichan. All rights reserved.
 */
package io.github.kumaisu.networkControl.listener;

import io.github.kumaisu.networkControl.Lib.Tools;
import io.github.kumaisu.networkControl.Lib.Utility;
import io.github.kumaisu.networkControl.config.Config;
import io.github.kumaisu.networkControl.database.Database;
import io.github.kumaisu.networkControl.database.HostData;
import io.github.kumaisu.networkControl.database.ListData;
import io.github.kumaisu.networkControl.tools.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.net.UnknownHostException;
import java.util.Date;

/**
 *
 * @author sugichan
 */
public class LoginListener implements Listener {

    private Date date;
    private final Plugin plugin;

    /**
     *
     * @param plugin
     */
    public LoginListener( Plugin plugin ) {
        plugin.getServer().getPluginManager().registerEvents( this, plugin );
        this.plugin = plugin;
    }

    /**
     * プレイヤーがログインしようとした時に起きるイベント
     * BANなどされていてもこのイベントは発生する
     *
     * @param event
     */
    @EventHandler
    public void prePlayerLogin( AsyncPlayerPreLoginEvent event ) {
        Tools.Prt( "PrePlayerLogin process", Tools.consoleMode.max, Config.programCode );
        date = new Date();
        ListData.AddSQL( date, event.getName(), event.getUniqueId().toString(), event.getAddress().getHostAddress(), 0 );
        HostData.AddPlayerToSQL( event.getAddress().getHostAddress(), event.getName() );
    }

    /**
     * プレイヤーがログインを成功すると発生するイベント
     * ここでプレイヤーに対して、様々な処理を実行する
     *
     * @param event
     * @throws UnknownHostException
     */
    @EventHandler( priority = EventPriority.LOW )
    public void onPlayerLogin( PlayerJoinEvent event ) throws UnknownHostException {

        Tools.Prt( "onPlayerLogin process", Tools.consoleMode.max, Config.programCode );
        Player player = event.getPlayer();

        // Database 更新作業
        ListData.ChangeStatus( date, 1 );
        ListData.LogPrint( player, 3, false );
        HostData.AddCountHost( player.getAddress().getHostString(), -1 );
        ListData.CheckIP( player );
        ListData.CheckUUID( player );

        if ( Config.Announce ) {
            Tools.Prt( player, Utility.ReplaceString( Config.AnnounceMessage, player.getDisplayName() ), Tools.consoleMode.max, Config.programCode );
        }

        if ( !player.hasPlayedBefore() || ( Config.OpJumpStats && player.isOp() ) ) {
            Tools.Prt( ChatColor.AQUA + "The First Login Player", Tools.consoleMode.full, Config.programCode );

            if ( Config.JumpStats ) {
                if ( !Teleport.Beginner( player ) ) {
                    Tools.Prt( player, "You failed the beginner teleport", Tools.consoleMode.full, Config.programCode );
                }
            } else Tools.Prt( "not Beginner Teleport", Tools.consoleMode.full, Config.programCode );
    
            Config.present.stream().forEach( CP -> {
                Tools.Prt( ChatColor.AQUA + "Command Execute : " + ChatColor.WHITE + CP, Tools.consoleMode.max, Config.programCode );
                Tools.ExecOtherCommand( player, CP, "" );
            } );
            
        } else {
            Tools.Prt( ChatColor.AQUA + "The Repeat Login Player", Tools.consoleMode.full, Config.programCode );
        }

        //  プレイヤーの言語設定を取得するために遅延処理の後 Welcome メッセージの表示を行う
        //  ラグが大きいが現状はこれが精一杯の状態
        for (Player pl : Bukkit.getOnlinePlayers()) {
            String locale = pl.getLocale(); // プレイヤーのロケールを取得
            Tools.Prt( "Player " + pl.getName() + " has locale: " + locale, Config.programCode );
        }

        String locale2byte = player.getLocale().substring(0, 2).toUpperCase();

        Tools.Prt( ChatColor.AQUA + "Player Menu is " + locale2byte, Tools.consoleMode.full, Config.programCode );
            
        if ( !player.hasPlayedBefore() || ( Config.OpJumpStats && player.isOp() ) ) {
            if( Config.NewJoin ) {
                Tools.Prt( "Player host = " + player.getAddress().getHostString(), Tools.consoleMode.normal, Config.programCode );
                Tools.Prt( "Get Locale = " + locale2byte, Tools.consoleMode.normal, Config.programCode );
                String WelcomeMessage = ( Config.NewJoinMessage.get( locale2byte) == null ? Config.New_Join_Message : Config.NewJoinMessage.get( locale2byte ) );
                Bukkit.broadcastMessage( Utility.ReplaceString( WelcomeMessage, player.getDisplayName() ) );
            }
        } else {
            if( Config.ReturnJoin ) {
                String ReturnMessage =
                    Utility.ReplaceString(
                        ( Config.ReturnJoinMessage.get( locale2byte ) == null ?
                            Config.Returning_Join_Message
                            :
                            Config.ReturnJoinMessage.get( locale2byte )
                        ), player.getDisplayName() );
                if ( player.hasPermission( "LoginCtl.silentjoin" ) ) {
                    Tools.Prt( player, ChatColor.YELLOW + "You are silent Join", Tools.consoleMode.full, Config.programCode );
                    Tools.Prt( player, ReturnMessage, Config.programCode );
                } else {
                    Bukkit.broadcastMessage( ReturnMessage );
                }
            }
        }
    }

    /**
     * プレイヤーがログアウトした時に発生するイベント
     *
     * @param event
     */
    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent event ) {
        event.setQuitMessage( null );
        if ( event.getPlayer().hasPermission( "LoginCtl.silentquit" ) ) {
            Tools.Prt( event.getPlayer(), ChatColor.YELLOW + "You are silent Quit", Tools.consoleMode.full, Config.programCode );
            return;
        }
        if ( Config.PlayerQuit ) {
            //  event.setQuitMessage( Utility.ReplaceString( Config.PlayerQuitMessage, event.getPlayer().getDisplayName() ) );
            Bukkit.broadcastMessage( Utility.ReplaceString( Config.PlayerQuitMessage, event.getPlayer().getDisplayName() ) );
        }
    }
}
