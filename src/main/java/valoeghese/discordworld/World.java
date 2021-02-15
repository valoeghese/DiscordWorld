package valoeghese.discordworld;

import java.io.File;

import tk.valoeghese.zoesteriaconfig.api.ZoesteriaConfig;
import tk.valoeghese.zoesteriaconfig.api.container.WritableConfig;
import tk.valoeghese.zoesteriaconfig.api.template.ConfigTemplate;

public class World {
	public World(int rootWidth, int rootHeight, File file) {
		this.worldData = ZoesteriaConfig.loadConfigWithDefaults(file, ConfigTemplate.builder()
				.addContainer("size", c -> c.addDataEntry("rootWidth", rootWidth).addDataEntry("rootHeight", rootHeight))
				.build());
	}

	private final WritableConfig worldData;
}
