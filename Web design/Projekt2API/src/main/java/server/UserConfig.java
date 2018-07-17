package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserConfig {
	
	public static int MAXIMAGESIZE;
	public static int MAXAUDIOSIZE;
	public static int MAXVIDEOSIZE;
	public static int SERVERPORT;
	public static double MAXIMAGERATIO;
	public static double MINIMAGERATIO;
	public static String DB_URL;
	public static String DB_USER;
	public static String DB_PASSWORD;
	public static String API_MAIL;
	public static String API_MAIL_PASSWORD;
	public static String SERVER_ADDRESS;
	
	private static Logger logger = LogManager.getLogger(UserConfig.class);
	private final String fileName = "config.properties";
	
	private Charset charset = StandardCharsets.UTF_8;
	
	static Properties properties = new Properties();
	
	public UserConfig() {
		loadConfig();
	}
	
	private void loadConfig() {
		InputStream input = null;
		try {
			input = new FileInputStream(fileName);
			try {
				properties.load(input);
				parseSettings();
			} catch (IOException e) {
				logger.error("could not load properties file, check file structure or remove to create new", e);
			}
		} catch (FileNotFoundException e) {
			logger.info("could not find configuration file, creating new...");
			createNewFile();
		}
	}
	
	private void parseSettings() {
		try {
			MAXIMAGESIZE = Integer.parseInt(properties.getProperty("maximagesize"));
			MAXAUDIOSIZE = Integer.parseInt(properties.getProperty("maxaudiosize"));
			MAXVIDEOSIZE = Integer.parseInt(properties.getProperty("maxvideosize"));
			SERVERPORT = Integer.parseInt(properties.getProperty("serverport"));
			MAXIMAGERATIO = Double.parseDouble(properties.getProperty("maximageratio"));
			MINIMAGERATIO = Double.parseDouble(properties.getProperty("minimageratio"));
			DB_URL = properties.getProperty("dburl");
			DB_USER = properties.getProperty("dbuser");
			DB_PASSWORD = properties.getProperty("dbpassword");
			API_MAIL = properties.getProperty("apimail");
			API_MAIL_PASSWORD = properties.getProperty("apimailpassword");
			SERVER_ADDRESS = properties.getProperty("serveraddress");
		} catch (NumberFormatException e) {
			logger.error("Invalid format in configuration file.");
			logger.error("Check values or remove file to make the server create a new.");
			System.exit(0);
		}
	}
	
	private void createNewFile() {
		try {
			Files.write(Paths.get(fileName), getNewFileContent(), charset);
		} catch (IOException e) {
			logger.error("could not create configuration file", e);
		}
		logger.info("Configuration file created. Complete it with your keys.");
		System.exit(0);
	}
	
	private ArrayList<String> getNewFileContent() {
		ArrayList<String> content = new ArrayList<>();
		content.add("#Q.C.A.P.I configuration file");
		content.add("#Print all values without \"\" and without space after = .");
		content.add("#");
		content.add("#API URL ");
		content.add("serveraddress=PLACE YOUR SERVERADDRESS HERE");
		content.add("#");
		content.add("serverport=7500");
		content.add("#");
		content.add("#Image aspect ratio");
		content.add("#Ratio = width / height.");
		content.add("maximageratio=2");
		content.add("minimageratio=1.5");
		content.add("#");
		content.add("#Media file size (bytes):");
		content.add("maximagesize=" + Constants.MAXFILESIZE_IMAGE);
		content.add("maxaudiosize=" + Constants.MAXFILESIZE_AUDIO);
		content.add("maxvideosize=" + Constants.MAXFILESIZE_VIDEO);
		content.add("#");
		content.add("#Database settings:");
		content.add("dburl=PLACE YOUR DB URL HERE");
		content.add("dbuser=PLACE YOUR DB USER NAME HERE");
		content.add("dbpassword=PLACE YOUR DB PASSWORD HERE");
		content.add("#");
		content.add("#Mail settings:");
		content.add("apimail=PLACE API MAIL HERE (GMAIL ACCOUNT)");
		content.add("apimailpassword=PLACE API MAIL PASSWORD HERE");
		return content;
	}

}
