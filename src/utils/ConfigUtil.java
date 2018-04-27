package utils;

import javax.servlet.ServletContext;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigUtil {
	private static ServletContext servletContext;
	private static final String SETTINGS_CONFIG_FILE_NAME = "/WEB-INF/conf/settings.properties";
	private static final String MESSAGES_CONFIG_FILE_NAME = "/WEB-INF/conf/messages.properties";
	
	private static Properties settingsConfig = null;
	private static Properties messagesConfig = null;

	public static void setContextPath(ServletContext context) {
        servletContext = context;
	}

	public static Properties getConfig() {
        if (settingsConfig == null) {
            settingsConfig = new Properties();

            try {
                settingsConfig.load(servletContext.getResourceAsStream(SETTINGS_CONFIG_FILE_NAME));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return settingsConfig;
    }

    public static Properties getMsgConfig() {
        if (messagesConfig == null) {
            messagesConfig = new Properties();

            try {
                messagesConfig.load(new InputStreamReader(servletContext.getResourceAsStream(MESSAGES_CONFIG_FILE_NAME), "UTF-8"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messagesConfig;
    }

	private ConfigUtil() {
	}
	
}
