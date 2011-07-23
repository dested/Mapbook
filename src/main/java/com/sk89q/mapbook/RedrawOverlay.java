package com.sk89q.mapbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.maps.ContextualMapRenderer;
import org.bukkit.maps.MapView;
import org.bukkit.maps.MapCanvas;
import org.bukkit.maps.MapRenderer;

public class RedrawOverlay implements ContextualMapRenderer {
 

	public void initialize(MapView map) {

	}

	public void render(MapView map, MapCanvas canvas, Player player) {
		map.setCenterX(player.getLocation().getBlockX());		
		map.setCenterZ(player.getLocation().getBlockZ());		
	}

}