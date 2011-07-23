package com.sk89q.mapbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.maps.MapView;
import org.bukkit.maps.MapCanvas;
import org.bukkit.maps.MapRenderer;

public class GlitteringOceanOverlay implements MapRenderer {

	final class Piece {
		public int X;
		public int Y;
		public byte Color;

		public Piece(int x, int y, byte col) {
			X = x;
			Y = y;
			Color = col;
		}
	}

	Random ran = new Random();
	int c = 0;
	HashMap<Integer, ArrayList<Piece>> Pieces = new HashMap<Integer, ArrayList<Piece>>();

	public void render(MapView map, MapCanvas canvas) {

		if (((c++) % 100) != 0) {
			if (((c) % 30) == 0) {
				int gm = map.getId();
				ArrayList<Piece> f = Pieces.get(gm);
				if (f == null) {
					Pieces.put(gm, f = new ArrayList<Piece>());
				}

				for (int i = f.size() - 1; i >= 0; i--) {
					Piece p = f.get(i);
					canvas.setPixel(p.X, p.Y, p.Color);
				}
				f.clear();
				return;
			}
			return;
		}

		int gm = map.getId();
		ArrayList<Piece> f = Pieces.get(gm);
		if (f == null) {
			Pieces.put(gm, f = new ArrayList<Piece>());
		}

		for (int x = 0; x < 128; x++) {
			for (int y = 0; y < 128; y++) {
				byte j = canvas.getPixel(x, y);
				if (j == (byte) 48 || j == (byte) 49 || j == (byte) 50) {
					if (ran.nextInt(100) <= 5) {
						f.add(new Piece(x, y, j));
						canvas.setPixel(x, y, (byte) 34);
					}
				}
				if (j == (byte) 20 || j == (byte) 21 || j == (byte) 22) {
					if (ran.nextInt(1500) <= 3) {
						f.add(new Piece(x, y, j));
						canvas.setPixel(x, y, (byte) 34);
					}
				}
			}
		}

	}

	public void initialize(MapView map) {

	}

}