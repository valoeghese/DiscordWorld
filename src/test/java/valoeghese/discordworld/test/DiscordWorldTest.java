package valoeghese.discordworld.test;

import net.dv8tion.jda.api.hooks.EventListener;
import valoeghese.discordworld.Bootstrap;

public class DiscordWorldTest implements EventListener {
	public static void main(String[] args) {
		Bootstrap.start(properties -> new DiscordWorldTest());
	}
	
	
}
