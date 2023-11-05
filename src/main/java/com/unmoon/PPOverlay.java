package com.unmoon;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.WallObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Set;

public class PPOverlay extends Overlay {
    private final Client client;
    private final PPPlugin plugin;
    private final PPConfig config;

    @Inject
    PPOverlay(Client client, PPPlugin plugin, PPConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.highlightChests()) {renderGameObjects(graphics, plugin.chests, Color.ORANGE);}
        if (config.highlightSarcophagi()) {renderGameObjects(graphics, plugin.sarcophagi, Color.ORANGE);}
        if (config.highlightUrns()) {renderGameObjects(graphics, plugin.urns, Color.GREEN);}
        if (config.highlightTraps()) {renderGameObjects(graphics, plugin.traps, Color.RED);}

        if (config.highlightDoors()) {
            for (WallObject wallObject : plugin.doors) {
                if (wallObject.getPlane() == client.getPlane()) {
                    Shape clickbox = wallObject.getClickbox();
                    if (clickbox != null) {
                        graphics.setColor(Color.CYAN);
                        graphics.draw(clickbox);
                    }
                }
            }
        }
        return null;
    }

    private void renderGameObjects(Graphics2D graphics, Set<GameObject> gameObjects, Color color) {
        for (GameObject tileObject : gameObjects) {
            if (tileObject.getPlane() == client.getPlane()) {
                Shape clickbox = tileObject.getClickbox();
                if (clickbox != null) {
                    graphics.setColor(color);
                    graphics.draw(clickbox);
                }
            }
        }
    }
}
