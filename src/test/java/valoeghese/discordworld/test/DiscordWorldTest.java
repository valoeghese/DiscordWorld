package valoeghese.discordworld.test;

import java.io.File;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import valoeghese.discordworld.Bootstrap;
import valoeghese.discordworld.World;

public class DiscordWorldTest extends ListenerAdapter {
	public static void main(String[] args) {
		Bootstrap.start(properties -> new DiscordWorldTest());
	}

	private World world;

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getMessage().getContentRaw().equals("start")) {
			this.world = new World(event.getGuild(), 2, 2, new File("./guild.dat.zfg"));
		}
	}
}
