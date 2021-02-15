package valoeghese.discordworld.test;

import java.io.File;
import java.util.Arrays;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import valoeghese.discordworld.Bootstrap;
import valoeghese.discordworld.World;

public class DiscordWorldTest extends ListenerAdapter {
	public static void main(String[] args) {
		Bootstrap.start(properties -> new DiscordWorldTest(), Arrays.asList());
	}

	private World world;

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		this.world.setPosition(2, 3, event.getMember(), false);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getMessage().getContentRaw().equals("start")) {
			try {
				event.getChannel().sendMessage("Starting").queue();
			} catch (Throwable t) {
			}

			try {
				File file = new File("./guild.dat.zfg");
				file.createNewFile();
				this.world = new World(event.getGuild(), 3, 3, 4, 4, file);
			} catch (Exception e) {
				try {
					event.getChannel().sendMessage(e.getMessage()).queue();
				} catch (Throwable t) {
				}
				throw new RuntimeException(e);
			}

			try {
				event.getChannel().sendMessage("Finished Starting").queue();
			} catch (Throwable t) {
			}
		} else if (event.getMessage().getContentRaw().equals("reset")) {
			try {
				event.getChannel().sendMessage("Resetting").queue();
			} catch (Throwable t) {
			}

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
			world = null;

			try {
				event.getChannel().sendMessage("Finished Resetting").queue();
			} catch (Throwable t) {
			}
		}
	}
}
