package valoeghese.discordworld;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import tk.valoeghese.zoesteriaconfig.api.ZoesteriaConfig;
import tk.valoeghese.zoesteriaconfig.api.container.EditableContainer;
import tk.valoeghese.zoesteriaconfig.api.container.WritableConfig;
import tk.valoeghese.zoesteriaconfig.api.template.ConfigTemplate;

public class World {
	public World(Guild guild, int megaW, int megaH, int smallW, int smallH, File file) {
		if (smallH * megaH * smallW * megaW > 500) {
			throw new RuntimeException("The given size would exceed the channel limit!");
		}

		this.worldData = ZoesteriaConfig.loadConfigWithDefaults(file, ConfigTemplate.builder()
				.addContainer("size", c -> c
						.addDataEntry("megaW", megaW)
						.addDataEntry("megaH", megaH)
						.addDataEntry("smallW", smallW)
						.addDataEntry("smallH", smallH))
				.build());

		this.smallW = this.worldData.getIntegerValue("size.smallW");
		this.smallH = this.worldData.getIntegerValue("size.smallH");
		this.width = this.smallW * this.worldData.getIntegerValue("size.megaW");
		this.height = this.smallH * this.worldData.getIntegerValue("size.megaH");

		if (!this.worldData.containsKey("roles")) {
			EditableContainer roles = ZoesteriaConfig.createWritableConfig(new LinkedHashMap<>());
			EditableContainer channels = ZoesteriaConfig.createWritableConfig(new LinkedHashMap<>());
			List<Role> roleList = new ArrayList<>();
			roleList.add(createRole(guild, "unlocated", roles));

			// generate roles
			for (int lx = 0; lx < megaW; ++lx) { // large x
				String name = "lx" + lx;
				roleList.add(createRole(guild, name, roles));
			}

			for (int ly = 0; ly < megaH; ++ly) { // large y
				String name = "ly" + ly;
				roleList.add(createRole(guild, name, roles));
			}

			for (int sx = 0; sx < this.smallW; ++sx) { // small x
				String name = "sx" + sx;
				roleList.add(createRole(guild, name, roles));
			}

			for (int sy = 0; sy < this.smallH; ++sy) { // small y
				String name = "sy" + sy;
				roleList.add(createRole(guild, name, roles));
			}

			// generate channels
			for (int lx = 0; lx < megaW; ++lx) { // large x
				String lxn = "lx" + lx;

				for (int ly = 0; ly < megaH; ++ly) { // large y
					String lyn = "ly" + ly;

					for (int sx = 0; sx < this.smallW; ++sx) { // small x
						final int x = lx * this.smallW + sx;
						String sxn = "sx" + sx;

						for (int sy = 0; sy < this.smallH; ++sy) { // small y
							final int y = ly * this.smallH + sy;
							String syn = "sy" + sy;

							String channelName = "x" + x + "-y" + y;
							ChannelAction<TextChannel> channel = guild.createTextChannel(channelName);

							for (Role role : roleList) {
								if (!role.getName().equals(lxn) && !role.getName().equals(lyn) && !role.getName().equals(sxn) && !role.getName().equals(syn)) {
									channel.addRolePermissionOverride(role.getIdLong(), 0, 0x400);
								}
							}

							channels.putStringValue(channelName, channel.complete().getId());
						}
					}
				}
			}

			this.worldData.putMap("roles", roles.asMap());
			this.worldData.putMap("channels", channels.asMap());
			this.worldData.writeToFile(file);
		}

		this.unlocated = guild.getRoleById(this.worldData.getStringValue("roles.unlocated"));
	}

	private final WritableConfig worldData;
	private final int smallW, smallH;
	private final int width, height;
	private final Role unlocated;

	public void setPosition(int x, int y, Member member, boolean unlocateTemporarily) {
		System.out.println(this.width);
		if (x > 0 && y > 0 && x < this.width && y < this.height) {
			Guild guild = member.getGuild();

			// set unlocated
			if (unlocateTemporarily) {
				guild.addRoleToMember(member, this.unlocated).queue();
			}

			// remove existing position roles
			for (Role role : member.getRoles()) {
				if (role.getName().matches(ROLE_REGEX)) {
					guild.removeRoleFromMember(member, role).queue();
				}
			}

			// add new position roles (happens after prev queued actions bc queue)
			int sx = x / this.smallW;
			int lx = x % this.smallW;
			int sy = y / this.smallH;
			int ly = y % this.smallH;

			guild.addRoleToMember(member, guild.getRoleById(this.worldData.getStringValue("lx" + lx))).queue();
			guild.addRoleToMember(member, guild.getRoleById(this.worldData.getStringValue("sx" + sx))).queue();
			guild.addRoleToMember(member, guild.getRoleById(this.worldData.getStringValue("ly" + ly))).queue();
			guild.addRoleToMember(member, guild.getRoleById(this.worldData.getStringValue("sy" + sy))).queue();
			
			// remove unlocated
			if (unlocateTemporarily) {
				guild.removeRoleFromMember(member, this.unlocated).queue();
			}
		}
	}

	private static Role createRole(Guild guild, String name, EditableContainer storage) {
		Role role = guild.createRole().setName(name).complete();
		storage.putStringValue(name, role.getId());
		return role;
	}

	private static final String ROLE_REGEX = "(l|s)(x|y)[0-9]+";
}
