package com.sk89q.mapbook;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bukkit.craftbukkit.maps.CraftShape;
import org.bukkit.entity.Player;
import org.bukkit.maps.ContextualMapRenderer;
import org.bukkit.maps.MapCanvas;
import org.bukkit.maps.MapCursorCollection;
import org.bukkit.maps.MapPrintOrder;
import org.bukkit.maps.MapRenderer;
import org.bukkit.maps.MapType;
import org.bukkit.maps.MapView;

public class NyanCatBase  implements MapRenderer{
	int inj = -1;
	int cont = 0;

	public void render(MapView map, MapCanvas canvas) {
		map.setRate(128);
		map.setPrintOrder(MapPrintOrder.Sequential);
		if ((cont++ % 60 ) != 0)
			return ;
		inj++; 
		BufferedImage img;
		try {
			img = ImageIO.read(new File("c:/nyancat/acertaincat.gif Frame 0 "
					+ (((inj) % 12) + 1) + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}

		map.getDrawer().drawImage(img);
		return ;
	}

	public void initialize(MapView map) {
		// TODO Auto-generated method stub

	}

}