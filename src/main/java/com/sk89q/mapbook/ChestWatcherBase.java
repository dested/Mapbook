package com.sk89q.mapbook;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.craftbukkit.block.CraftDispenser;
import org.bukkit.craftbukkit.block.CraftFurnace;
import org.bukkit.craftbukkit.maps.CraftShape;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.map.MapListener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.maps.ContextualMapRenderer;
import org.bukkit.maps.MapCanvas;
import org.bukkit.maps.MapCursorCollection;
import org.bukkit.maps.MapPrintOrder;
import org.bukkit.maps.MapRenderer;
import org.bukkit.maps.MapType;
import org.bukkit.maps.MapView;
import org.bukkit.maps.Shape;

import com.sk89q.mapbook.ChestWatcherBase.ListenerObject;

public class ChestWatcherBase implements ContextualMapRenderer {
	private MapBookPlugin plugin;

	public ChestWatcherBase(MapBookPlugin s) {
		plugin = s;
	}

	HashMap<Player, ListenerObject> players = new HashMap<Player, ListenerObject>();

	public class ListenerObject {
		protected CraftFurnace lastFurnaceClicked;
		protected CraftDispenser lastDispencerClicked;
		protected CraftChest lastChestClicked;
	}

	public void render(MapView map, MapCanvas canvas, Player player) {
		map.setRate(15);
		map.setPrintOrder(MapPrintOrder.Sequential);
		for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 128; j++) {
				canvas.setPixel(i, j, (byte) 21);
			}
		}

		ListenerObject curPlayer;
		if ((curPlayer = players.get(player)) == null)
			return;

		if (curPlayer.lastFurnaceClicked != null) {
			map.getStringDrawer().drawText(30, 2, "Furnace");
			map.getStringDrawer().drawText(
					10,
					118,
					"CookTime " + curPlayer.lastFurnaceClicked.getCookTime()
							+ " \\ "
							+ curPlayer.lastFurnaceClicked.getLightLevel());

			ItemStack[] cnt = curPlayer.lastFurnaceClicked.getInventory()
					.getContents();
			drawInventory(map, cnt);
		}
		if (curPlayer.lastDispencerClicked != null) {
			map.getStringDrawer().drawText(30, 2, "Dispencer");

			ItemStack[] cnt = curPlayer.lastDispencerClicked.getInventory()
					.getContents();
			drawInventory(map, cnt);
		}

		if (curPlayer.lastChestClicked != null) {
			map.getStringDrawer().drawText(35, 2, "Chest");
			ItemStack[] cnt = curPlayer.lastChestClicked.getInventory()
					.getContents();
			drawInventory(map, cnt);

		}
	}

	private void drawInventory(MapView map, ItemStack[] cnt) {
		int x = 16;
		int y = 16;
		// map.getDrawer().fillRectangle(8, 8, 112, 112);

		Shape square = CraftShape.Square.cloneMe();
		square.setCenter(1, 1);
		for (int i = 0; i < cnt.length; i++) {
			ItemStack item = cnt[i];

			map.getDrawer().setForeColor((byte) 14);
			map.getDrawer().setBackColor((byte) 34);

			map.getDrawer().fillShape(x, y, square, 8);
			if (item != null) {
				map.getDrawer().setForeColor((byte) 19);
				map.getStringDrawer().drawText(x + 3, y + 5,
						item.getTypeId() + "");
			}
			x += 16;
			if (x > 96) {
				x = 16;
				y += 16;
			}
		}
	}

	int tick = 0;

	public void initialize(MapView map) {
		Listener pe = new PlayerListener() {

			public void onPlayerInteract(PlayerInteractEvent event) {
				Material type = event.getClickedBlock().getType();

				ListenerObject curPlayer;
				if ((curPlayer = players.get(event.getPlayer())) == null) {
					players.put(event.getPlayer(),
							curPlayer = new ListenerObject());
				}

				if (type == Material.CHEST) {
					curPlayer.lastChestClicked = new CraftChest(
							event.getClickedBlock());
					curPlayer.lastFurnaceClicked = null;
					curPlayer.lastDispencerClicked = null;

				}
				if (type == Material.DISPENSER) {
					curPlayer.lastDispencerClicked = new CraftDispenser(
							event.getClickedBlock());
					curPlayer.lastFurnaceClicked = null;
					curPlayer.lastChestClicked = null;
				}
				if (type == Material.FURNACE
						|| type == Material.BURNING_FURNACE) {
					curPlayer.lastFurnaceClicked = new CraftFurnace(
							event.getClickedBlock());
					curPlayer.lastDispencerClicked = null;
					curPlayer.lastChestClicked = null;

				} 
			} 
		};
		plugin.getServer()
				.getPluginManager()
				.registerEvent(Event.Type.PLAYER_INTERACT, pe, Priority.Normal,
						plugin);

	}

}