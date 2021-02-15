package valoeghese.discordworld.test;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

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
			try {
				File file = new File("./guild.dat.zfg");
				file.createNewFile();
				this.world = new World(event.getGuild(), 2, 2, file);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
