package valoeghese.discordworld;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bootstrap {
	public static void start(Function<Properties, EventListener> listener, @Nullable Collection<GatewayIntent> enable) {
		// bootstrap JDA
		try (FileInputStream fis = new FileInputStream(new File("./properties.txt"))) {
			Properties p = new Properties();
			p.load(fis);
			JDABuilder builder = JDABuilder.createDefault(p.getProperty("key"));

			if (enable != null) {
				builder.enableIntents(enable);
			}

			builder.addEventListeners(listener.apply(p)).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Exception running bot!", e);
		}
	}
}
