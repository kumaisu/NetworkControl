/*
 *  Copyright (c) 2018 NineTailedFox. All rights reserved.
 */
package io.github.kumaisu.networkControl;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.kumaisu.networkControl.listener.LoginListener;
import io.github.kumaisu.networkControl.listener.ServerListener;
import io.github.kumaisu.networkControl.command.LoginlistCommand;
import io.github.kumaisu.networkControl.command.AdminCommand;
import io.github.kumaisu.networkControl.config.Config;
import io.github.kumaisu.networkControl.config.ConfigManager;
import io.github.kumaisu.networkControl.config.MotDControl;
import io.github.kumaisu.networkControl.database.DatabaseControl;

import java.sql.SQLException;

/**
 *
 * @author NineTailedFox
 */
public class NetworkControl extends JavaPlugin implements Listener {

    public static ConfigManager config;
    public static MotDControl MotData;

    @Override
    public void onEnable() {
        // Plugin startup logic

        this.getServer().getPluginManager().registerEvents( this, this );
        config = new ConfigManager( this );
        MotData = new MotDControl( this );
        try {
            DatabaseControl.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        DatabaseControl.TableUpdate();

        new LoginListener( this );

        if ( Config.MotDControl ) {
            new ServerListener( this );
        }

        getCommand( "loginlist" ).setExecutor( new LoginlistCommand( this ) );
        getCommand( "loginctl" ).setExecutor( new AdminCommand( this ) );
    }

    @Override
    public void onDisable() {
        super.onDisable(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onLoad() {
        super.onLoad(); //To change body of generated methods, choose Tools | Templates.
    }
}
