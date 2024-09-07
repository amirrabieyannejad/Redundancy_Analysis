package org.henshin.backlog.code.rule;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
	private static ConfigLoader instance;
	private String baseDirectory;
	private String[] projectIds;
	private String inputDirectroy;
	private String cdaReportsDirectory;

	// Private constructor to prevent instantiation
	private ConfigLoader() throws IOException {
		loadProperties();
	}

	// Public method to provide access to the singleton instance
	public static ConfigLoader getInstance() throws IOException {
		if (instance == null) {
			synchronized (ConfigLoader.class) {
				if (instance == null) {
					instance = new ConfigLoader();
				}
			}
		}
		return instance;
	}

	// Load base directory from properties file
	private void loadProperties() throws IOException {
		Properties properties = new Properties();
		try (FileInputStream input = new FileInputStream("config.properties")) {
			properties.load(input);
			baseDirectory = properties.getProperty("base.directory");
			if (baseDirectory == null || baseDirectory.isEmpty()) {
				throw new IllegalArgumentException("Base directory is not defined in the config file.");
			}

			String stringProjectIds = properties.getProperty("project.ids");
			if (stringProjectIds == null || stringProjectIds.isEmpty()) {
				throw new IllegalArgumentException("Project ID is not defined in the config file.");
			}
			stringProjectIds = stringProjectIds.replaceAll("\\s+", "");
			projectIds = stringProjectIds.split(",");

			inputDirectroy = baseDirectory + "\\Final_Reports\\Textual_Report_g" + projectIds + "\\g" + projectIds
					+ "_baseline_pos.json";
			cdaReportsDirectory = properties.getProperty("cda.reports");
			if (cdaReportsDirectory == null || cdaReportsDirectory.isEmpty()) {
				throw new IllegalArgumentException("CDA reports directroy is not defined in the config file.");
			}

		}
	}

	// Method to get the base directory
	public String getBaseDirectory() {
		return baseDirectory;
	}

	// Method to get the Project ID
	public String[] getProjectIds() {
		return projectIds;
	}

	// Method to get the input directory
	public String getInputDirectroy() {
		return inputDirectroy;
	}
	
	// Method to get the CDA report directory
	public String getCdaReportsDirectroy() {
		return cdaReportsDirectory;
	}
}
