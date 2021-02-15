package valoeghese.discordworld;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.function.Function;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;

public class Bootstrap {
	public static void start(Function<Properties, EventListener> listener) {
		// bootstrap JDA
		try (FileInputStream fis = new FileInputStream(new File("./properties.txt"))) {
			Properties p = new Properties();
			p.load(fis);
			JDABuilder.createDefault(p.getProperty("key")).addEventListeners(listener.apply(p)).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception running bot!", e);
		}
	}
}
