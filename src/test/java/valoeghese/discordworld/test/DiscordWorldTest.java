package valoeghese.discordworld.test;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Role;
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
		} else if (event.getMessage().getContentRaw().equals("reset")) {
			String regex = "x[0-9]+-y[0-9]+";
			for (GuildChannel channel : event.getGuild().getChannels()) {
				if (channel.getName().matches(regex)) {
					channel.delete().complete();
				}
			}

			regex = "(l|s)(x|y)[0-9]+";
			for (Role role : event.getGuild().getRoles()) {
				if (role.getName().matches(regex)) {
					role.delete().complete();
				}
			}
		}
	}
}
