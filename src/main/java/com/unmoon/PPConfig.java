package com.unmoon;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("bigpp")
public interface PPConfig extends Config {
	@ConfigItem(
			keyName = "highlightChests",
			name = "Highlight Chests",
			description = "Highlight un-opened chests",
			position = 1
	)
	default boolean highlightChests() {return true;}

	@ConfigItem(
			keyName = "highlightSarcophagi",
			name = "Highlight Sarcophagi",
			description = "Highlight un-opened sarcophagi",
			position = 2
	)
	default boolean highlightSarcophagi() {return true;}

	@ConfigItem(
			keyName = "highlightUrns",
			name = "Highlight Urns",
			description = "Highlight un-opened urns",
			position = 3
	)
	default boolean highlightUrns() {return true;}

	@ConfigItem(
			keyName = "highlightTraps",
			name = "Highlight Traps",
			description = "Highlight traps",
			position = 4
	)
	default boolean highlightTraps() {return true;}

	@ConfigItem(
			keyName = "highlightDoors",
			name = "Highlight Doors",
			description = "Highlight un-opened doors",
			position = 5
	)
	default boolean highlightDoors() {return true;}
}
