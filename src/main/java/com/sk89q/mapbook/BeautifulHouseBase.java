package com.sk89q.mapbook;

import org.bukkit.craftbukkit.maps.CraftShape;
import org.bukkit.entity.Player;
import org.bukkit.maps.ContextualMapRenderer;
import org.bukkit.maps.MapCanvas;
import org.bukkit.maps.MapCursorCollection;
import org.bukkit.maps.MapPrintOrder;
import org.bukkit.maps.MapRenderer;
import org.bukkit.maps.MapType;
import org.bukkit.maps.MapView;

public class BeautifulHouseBase implements ContextualMapRenderer{

	public void render(MapView map, MapCanvas canvas, Player player) {
		map.setRate(15);
		map.setPrintOrder(MapPrintOrder.Sequential);
		for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 128; j++) {
				canvas.setPixel(i, j, (byte) 30);
			}
		} 

		CraftShape s = new CraftShape();
		s.pushCoordinate(1, 1);
		s.pushCoordinate(1, -1);
		s.pushCoordinate(0, -2);
		s.pushCoordinate(-1, -1);
		s.pushCoordinate(-1, 1);

		if (tick++ % 200 == 0) {
			if (increase > 128 - map.getStringDrawer()
					.measureString(player.getDisplayName()+"'s House!").getWidth())
				direction = false;
			if (increase < 1)
				direction = true;
			increase += (direction ? 8 : -8);
		}

		map.getDrawer().setForeColor((byte) 15);
		map.getDrawer().setBackColor((byte) 19);
		map.getDrawer().fillShape(50 + increase, 50, s, 20);

		map.getDrawer().setForeColor((byte) 22);
		map.getDrawer().setBackColor((byte) 41);
		map.getDrawer().fillShape(40 + increase, 40, CraftShape.Square, 7);
		map.getDrawer().fillShape(60 + increase, 40, CraftShape.Square, 7);

		map.getDrawer().fillRectangle(45 + increase, 55, 10, 14);

		map.getStringDrawer().drawText(increase, 100, player.getDisplayName()+"'s House!");
	}

	boolean direction = true;
	int increase = 1;
	int tick = 0;
	public void initialize(MapView map) {
		// TODO Auto-generated method stub
		
	} 

}