package com.sk89q.mapbook;

import org.bukkit.maps.MapCanvas;
import org.bukkit.maps.MapPrintOrder;
import org.bukkit.maps.MapRenderer;
import org.bukkit.maps.MapView;
import com.sk89q.mapbook.Liquid.LiquidTest;


public class LiquidBase implements MapRenderer {

	private LiquidTest liquidTest;
	private int step;

	public void initialize(MapView map) {

		liquidTest = new LiquidTest(34, 34, 25, 25);
		liquidTest.init();
	}

	public void render(MapView map, MapCanvas canvas) {
		canvas.getCursorCollection().getCursor(0).setVisible(false);
		if((step++%2)!=0)return;
		map.setPrintOrder(MapPrintOrder.Sequential);
		map.setRate(128);
		for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 128; j++) {
				canvas.setPixel(i, j, (byte) 28);
			}
		}
		liquidTest.paint(map.getDrawer());
		liquidTest.simulate();
	}

}
