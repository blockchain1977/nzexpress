package model;

import org.apache.log4j.Logger;

/**
 * Created by yplyf on 2015/9/21.
 * Save the session of user
 */
public class UserSession {
    private static Logger logger = Logger.getLogger(UserSession.class);
    private String currentCommand;
    private String preCommand;
    private int commandStatus;

    public UserSession() {
        currentCommand = UserCommands.COMMANDS_NONE;
        preCommand = UserCommands.COMMANDS_NONE;
        commandStatus = UserCommands.COMMANDS_INITIALSTATUS;
    }

    public String getCurrentCommand() {
        return currentCommand;
    }

    public String getPreCommand() {
        return preCommand;
    }

    private void setPreCommand(String preCommand) {
        this.preCommand = preCommand;
    }

    public void setCurrentCommand(String currentCommand) {
        logger.debug("User current command set to : " + currentCommand);
        setPreCommand(this.currentCommand);
        this.currentCommand = currentCommand;
    }

    public int getCommandStatus() {
        return commandStatus;
    }

    public void setCommandStatus(int commandStatus) {
        this.commandStatus = commandStatus;
    }
}
