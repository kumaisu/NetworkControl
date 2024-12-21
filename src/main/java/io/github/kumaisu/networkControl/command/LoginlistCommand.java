/*
 *  Copyright (c) 2019 sugichan. All rights reserved.
 */
package io.github.kumaisu.networkControl.command;

import io.github.kumaisu.networkControl.Lib.Tools;
import io.github.kumaisu.networkControl.Lib.Utility;
import io.github.kumaisu.networkControl.NetworkControl;
import io.github.kumaisu.networkControl.config.Config;
import io.github.kumaisu.networkControl.config.ConfigManager;
import io.github.kumaisu.networkControl.database.ListData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.kumaisu.networkControl.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class LoginlistCommand implements CommandExecutor {
    private final NetworkControl instance;
    private final ConfigManager config;

    public LoginlistCommand( NetworkControl instance ) {
        this.instance = instance;
        this.config = instance.config;
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
        int lineSet = 30;
        Player p = ( sender instanceof Player ) ? ( Player )sender:( Player )null;

        if ( cmd.getName().toLowerCase().equalsIgnoreCase( "loginlist" ) ) {
            int PrtF = 0;
            String Param = "";
            boolean FullFlag = false;

            for ( String arg : args ) {
                String[] param = arg.split( ":" );
                switch ( param[0] ) {
                    case "d":
                        PrtF = 1;
                        Param = param[1];
                        break;
                    case "u":
                        PrtF = 2;
                        Param = param[1];
                        break;
                    case "i":
                        PrtF = 3;
                        Param = param[1];
                        break;
                    case "l":
                        try {
                            lineSet = Integer.valueOf( param[1] );
                        } catch ( NumberFormatException e ) {
                            lineSet = 30;
                        }
                        break;
                    case "full":
                        Tools.Prt( p, Utility.ReplaceString( Config.LogFull ), Tools.consoleMode.full, programCode );
                        FullFlag = true;
                        break;
                    default:
                        Tools.Prt( p, Utility.ReplaceString( Config.ArgsErr ), Tools.consoleMode.full, programCode );
                        return false;
                }
            }

            switch ( PrtF ) {
                case 0:
                    ListData.LogPrint( p, ( sender instanceof Player ) ? 15:lineSet, FullFlag );
                    break;
                case 1:
                case 2:
                case 3:
                    ListData.exLogPrint( p, Param, FullFlag, PrtF, lineSet );
                    break;
                default:
                    Tools.Prt( p, Utility.ReplaceString( Config.OptError ), Tools.consoleMode.full, programCode );
                    return false;
            }
            return true;
        }

        return false;
    }    
    
}
