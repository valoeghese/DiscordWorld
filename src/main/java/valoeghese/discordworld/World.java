package valoeghese.discordworld;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import tk.valoeghese.zoesteriaconfig.api.ZoesteriaConfig;
import tk.valoeghese.zoesteriaconfig.api.container.EditableContainer;
import tk.valoeghese.zoesteriaconfig.api.container.WritableConfig;
import tk.valoeghese.zoesteriaconfig.api.template.ConfigTemplate;

public class World {
	public World(Guild guild, int rootWidth, int rootHeight, File file) {
		this.worldData = ZoesteriaConfig.loadConfigWithDefaults(file, ConfigTemplate.builder()
				.addContainer("size", c -> c.addDataEntry("rootWidth", rootWidth).addDataEntry("rootHeight", rootHeight))
				.build());

		this.rootWidth = this.worldData.getIntegerValue("rootWidth");
		this.rootHeight = this.worldData.getIntegerValue("rootHeight");
		this.width = this.rootWidth * this.rootWidth;
		this.height = this.rootHeight * this.rootHeight;

		if (!this.worldData.containsKey("roles")) {
			EditableContainer roles = ZoesteriaConfig.createWritableConfig(new LinkedHashMap<>());
			EditableContainer channels = ZoesteriaConfig.createWritableConfig(new LinkedHashMap<>());
			List<Role> roleList = new ArrayList<>();

			// generate roles
			for (int lx = 0; lx <= this.rootWidth; ++lx) { // large x
				String name = "lx" + lx;
				roleList.add(createRole(guild, name, roles));
			}
			
			for (int ly = 0; ly <= this.rootHeight; ++ly) { // large y
				String name = "ly" + ly;
				roleList.add(createRole(guild, name, roles));
			}
			
			for (int sx = 0; sx <= this.rootWidth; ++sx) { // small x
				String name = "sx" + sx;
				roleList.add(createRole(guild, name, roles));
			}
			
			for (int sy = 0; sy <= this.rootHeight; ++sy) { // small y
				String name = "sy" + sy;
				roleList.add(createRole(guild, name, roles));
			}

			// generate channels
			for (int lx = 0; lx <= this.rootWidth; ++lx) { // large x
				String lxn = "lx" + lx;
	
				for (int ly = 0; ly <= this.rootHeight; ++ly) { // large y
					String lyn = "ly" + ly;

					for (int sx = 0; sx <= this.rootWidth; ++sx) { // small x
						final int x = lx * this.rootWidth + sx;
						String sxn = "sx" + sx;

						for (int sy = 0; sy <= this.rootHeight; ++sy) { // small y
							final int y = ly * this.rootHeight + sy;
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
	}

	private final WritableConfig worldData;
	private final int rootWidth, rootHeight;
	private final int width, height;

	private static Role createRole(Guild guild, String name, EditableContainer storage) {
		Role role = guild.createRole().setName(name).complete();
		storage.putStringValue(name, role.getId());
		return role;
	}
}
