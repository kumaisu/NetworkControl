/*
 *  Copyright (c) 2019 sugichan. All rights reserved.
 */
package io.github.kumaisu.networkControl.command;

import io.github.kumaisu.networkControl.Lib.Tools;
import io.github.kumaisu.networkControl.NetworkControl;
import io.github.kumaisu.networkControl.config.Config;
import io.github.kumaisu.networkControl.tools.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.kumaisu.networkControl.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class BeginnerCommand implements CommandExecutor {
    private final NetworkControl instance;

    public BeginnerCommand( NetworkControl instance ) {
        this.instance = instance;
    }

    /**
     * コマンド入力があった場合に発生するイベント
     *
     * @param sender
     * @param cmd
     * @param commandLabel
     * @param args
     * @return
     */
    @Override
    public boolean onCommand( CommandSender sender,Command cmd, String commandLabel, String[] args ) {
        if ( Config.JumpStats ) {
            if ( args.length > 0 ) {
                Player targetPlayer = Bukkit.getPlayer( args[0] );
                if ( !( targetPlayer == null ) ) {
                    Teleport.Beginner( targetPlayer );
                } else { Tools.Prt( "No Match Target Player", Tools.consoleMode.full, programCode); }
            } else { Tools.Prt( "Select Target Player", Tools.consoleMode.full, programCode ); }
            return true;
        }
        return false;
    }    

}
