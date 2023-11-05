package com.unmoon;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.WallObject;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@PluginDescriptor(name="Big PP")
public class PPPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private PPConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PPOverlay ppOverlay;

	private static final Integer CHEST_ID = 20946;
	private static final Integer SARCOPHAGUS_ID = 21255;
	private static final List<Integer> UNLOOTED_URN_IDS = Arrays.asList(21261, 21262, 21263);
	private static final List<Integer> LOOTED_URN_IDS = Arrays.asList(21265, 21266, 21267);
	private static final Integer TRAP_ID = 21280;
	private static final Integer DOOR_ID = 20948;


	final Set<GameObject> chests = new HashSet<>();
	final Set<GameObject> sarcophagi = new HashSet<>();
	final Set<GameObject> urns = new HashSet<>();
	final Set<GameObject> traps = new HashSet<>();
	final Set<WallObject> doors = new HashSet<>();

	@Provides
	PPConfig provideConfig(ConfigManager configManager)	{return configManager.getConfig(PPConfig.class);}

	@Override
	protected void startUp() throws Exception {
		overlayManager.add(ppOverlay);
		reload();
	}

	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(ppOverlay);
		chests.clear();
		sarcophagi.clear();
		urns.clear();
		traps.clear();
		doors.clear();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOADING) {
			traps.clear();
			reload();
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {
		GameObject gameObject = event.getGameObject();
		if (gameObject.getId() == TRAP_ID) {traps.add(gameObject);}
	}

	public void addGameObject(GameObject gameObject) {
		if (client.getLocalPlayer().getLocalLocation().distanceTo(gameObject.getLocalLocation()) <= 16 * 128) {return;}

		ObjectComposition objectComposition = client.getObjectDefinition(gameObject.getId());
		if (objectComposition.getImpostorIds() == null) {return;}
		int impostor_id = objectComposition.getImpostor().getId();

		if (impostor_id == CHEST_ID) {chests.add(gameObject);}
		else if (impostor_id == SARCOPHAGUS_ID) {sarcophagi.add(gameObject);}
		else if (UNLOOTED_URN_IDS.contains(impostor_id)) {urns.add(gameObject);}
	}

	public void addWallObject(WallObject wallObject) {
		if (client.getLocalPlayer().getLocalLocation().distanceTo(wallObject.getLocalLocation()) <= 16 * 128) {return;}

		ObjectComposition objectComposition = client.getObjectDefinition(wallObject.getId());
		if (objectComposition.getImpostorIds() == null) {return;}

		if (objectComposition.getImpostor().getId() == DOOR_ID){doors.add(wallObject);}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event) {
		chests.remove(event.getGameObject());
		sarcophagi.remove(event.getGameObject());
		urns.remove(event.getGameObject());
		traps.remove(event.getGameObject());
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		chests.removeIf(gameObject -> client.getObjectDefinition(gameObject.getId()).getImpostorIds() == null || client.getObjectDefinition(gameObject.getId()).getImpostor().getId() != CHEST_ID);
		sarcophagi.removeIf(gameObject -> client.getObjectDefinition(gameObject.getId()).getImpostorIds() == null || client.getObjectDefinition(gameObject.getId()).getImpostor().getId() != SARCOPHAGUS_ID);
		urns.removeIf(gameObject -> client.getObjectDefinition(gameObject.getId()).getImpostorIds() == null || LOOTED_URN_IDS.contains(client.getObjectDefinition(gameObject.getId()).getImpostor().getId()));
		doors.removeIf(wallObject -> client.getObjectDefinition(wallObject.getId()).getImpostorIds() == null || client.getObjectDefinition(wallObject.getId()).getImpostor().getId() != DOOR_ID);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (event.getType() == ChatMessageType.SPAM && event.getMessage().equals("You deactivate the trap!")) {
			reload();
			traps.removeIf(gameObject -> client.getLocalPlayer().getLocalLocation().distanceTo(gameObject.getLocalLocation()) <= 5 * 128);
		}
	}

	private void reload() {
		chests.clear();
		sarcophagi.clear();
		urns.clear();
		doors.clear();

		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getTiles();
		int z = client.getPlane();
		for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
			for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
				Tile tile = tiles[z][x][y];
				if (tile == null) {continue;}

				GameObject[] gameObjects = tile.getGameObjects();
				if (gameObjects != null) {
					for (GameObject gameObject : gameObjects) {
						if (gameObject != null) {addGameObject(gameObject);}
					}
				}

				WallObject wallObject = tile.getWallObject();
				if (wallObject != null) {addWallObject(wallObject);}
			}
		}
	}
}
