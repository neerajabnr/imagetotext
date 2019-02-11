package it.sella.f24.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class LoadPropertiesUtil {
	private static Properties props = null;
	private static Map<String, String> propslist = new HashMap<>();
	static {
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
		String resourceName = "f24template1.properties"; // could also be a constant
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		props = new Properties();
		try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
			props.load(resourceStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static Properties loadPropertiesFile() {
		return props;
	}
	
	

}
