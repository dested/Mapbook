package com.sk89q.mapbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.craftbukkit.block.CraftDispenser;
import org.bukkit.craftbukkit.block.CraftFurnace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
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
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.maps.ContextualMapRenderer;
import org.bukkit.maps.MapCanvas;
import org.bukkit.maps.MapCursorCollection;
import org.bukkit.maps.MapPrintOrder;
import org.bukkit.maps.MapRenderer;
import org.bukkit.maps.MapType;
import org.bukkit.maps.MapView;
import org.bukkit.maps.Shape;

import com.sk89q.mapbook.SubwayBase.ListenerObject;
import com.sk89q.worldedit.blocks.ItemType;

public class SubwayBase implements ContextualMapRenderer {
	private MapBookPlugin plugin;

	public SubwayBase(MapBookPlugin s) {
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
				canvas.setPixel(i, j, (byte) 14);
			}
		}
		int x, z;

		map.setCenterX(x = player.getLocation().getBlockX());
		map.setCenterZ(z = player.getLocation().getBlockZ());

		int scale = 8;
		int halfScreen = (128 / 2);
		int halfWorldScreen = halfScreen * scale;
		for (Location loc : stations) {
			if (loc.getBlockX() > (x - halfWorldScreen)
					&& loc.getBlockZ() > (z - halfWorldScreen)
					&& loc.getBlockX() < (x + halfWorldScreen)
					&& loc.getBlockZ() < (z + halfWorldScreen)) {

				map.getDrawer().setForeColor((byte) 44);
				map.getDrawer().setBackColor((byte) 39);

				map.getDrawer().fillCircle(
						(loc.getBlockX() - x) / scale + halfScreen,
						(loc.getBlockZ() - z) / scale + halfScreen, 5);
			}
		}

		ArrayList<Little> lits = new ArrayList<Little>();
		for (int i = 0; i < tracks.size(); i++) {
			Location track = tracks.get(i).Loc;
			if (track.getBlockX() > (x - halfWorldScreen)
					&& track.getBlockZ() > (z - halfWorldScreen)
					&& track.getBlockX() < (x + halfWorldScreen)
					&& track.getBlockZ() < (z + halfWorldScreen)) {

				lits.add(new Little((track.getBlockX() - x) / scale
						+ halfScreen, (track.getBlockZ() - z) / scale
						+ halfScreen, tracks.get(i).HasCart));

			}
		}

		map.getDrawer().setForeColor((byte) 48);
		map.getDrawer().setBackColor((byte) 29);
		for (int i = 0; i < lits.size() - 1; i++) {
			Little t = lits.get(i);
			Little t2 = lits.get(i + 1);
			map.getDrawer().drawLine(t.X, t.Z, t2.X, t2.Z, 2);

			map.getDrawer().setForeColor((byte) 47);
			map.getDrawer().setBackColor((byte) 35);

			if (t.HasCart) {
				map.getDrawer().fillCircle((t.X) / scale + halfScreen,
						(t.Z) / scale + halfScreen, 9);
			}

		}

	}

	public class Little {
		int X;
		int Z;
		boolean HasCart;

		public Little(int x, int z, boolean cn) {
			X = x;
			Z = z;
			HasCart = cn;
		}
	}

	int tick = 0;
	public boolean ReadyToSetRailwayStation;

	ArrayList<Location> stations = new ArrayList<Location>();
	ArrayList<TrackPiece> tracks = new ArrayList<TrackPiece>();

	public class TrackPiece {
		Location Loc;
		boolean HasCart;

		public TrackPiece(Location l, boolean c) {
			Loc = l;
			HasCart = c;
		}
	}

	public void initialize(MapView map) {

		Listener pe = new PlayerListener() {

			private Player player;

			public void onPlayerInteract(PlayerInteractEvent event) {
				// if (!ReadyToSetRailwayStation)
				// return;
				player = event.getPlayer();
				ReadyToSetRailwayStation = true;
				Block block = event.getClickedBlock();
				Material type = block.getType();
				if (type == Material.OBSIDIAN) {
					stations.add(block.getLocation());
					player.sendMessage("Adding station " + stations.size());
				}

				if (type == Material.RAILS) {

					searchForTracks(block.getLocation().getWorld(), block
							.getLocation().getBlockX(), block.getLocation()
							.getBlockY(), block.getLocation().getBlockZ());

					player.sendMessage("Done. Found " + tracks.size());
				}
			}

			private void searchForTracks(World world, int x, int y, int z) {

				int count = 0;
				count += searchForTracksRec(world, x - 1, y, z) ? 1 : 0;
				count += searchForTracksRec(world, x + 1, y, z) ? 1 : 0;
				if (count > 2)
					return;
				count += searchForTracksRec(world, x, y, z - 1) ? 1 : 0;
				if (count > 2)
					return;
				count += searchForTracksRec(world, x, y, z + 1) ? 1 : 0;
				if (count > 2)
					return;

				count += searchForTracksRec(world, x - 1, y - 1, z) ? 1 : 0;
				if (count > 2)
					return;
				count += searchForTracksRec(world, x + 1, y - 1, z) ? 1 : 0;
				if (count > 2)
					return;
				count += searchForTracksRec(world, x, y - 1, z - 1) ? 1 : 0;
				if (count > 2)
					return;
				count += searchForTracksRec(world, x, y - 1, z + 1) ? 1 : 0;
				if (count > 2)
					return;

				count += searchForTracksRec(world, x - 1, y + 1, z) ? 1 : 0;
				if (count > 2)
					return;
				count += searchForTracksRec(world, x + 1, y + 1, z) ? 1 : 0;
				if (count > 2)
					return;
				count += searchForTracksRec(world, x, y + 1, z - 1) ? 1 : 0;
				if (count > 2)
					return;
				count += searchForTracksRec(world, x, y + 1, z + 1) ? 1 : 0;
			}

			private boolean searchForTracksRec(World world, int x, int y, int z) {
				player.sendMessage("doing " + x + " " + y + " " + z);
				if (world.getBlockTypeIdAt(x, y, z) != Material.RAILS.getId())
					return false;

				Block block;
				block = world.getBlockAt(x, y, z);

				for (TrackPiece tc : tracks) {
					Location t = tc.Loc;
					if (t.getBlockX() == x && t.getBlockY() == y
							&& t.getBlockZ() == z)
						return false;
				}

				tracks.add(new TrackPiece(block.getLocation(), false));
				searchForTracks(world, block.getLocation().getBlockX(), block
						.getLocation().getBlockY(), block.getLocation()
						.getBlockZ());
				return true;

			}
		};

		Listener pec = new VehicleListener() {

			public void onVehicleMove(VehicleMoveEvent event) {
				World world = event.getVehicle().getWorld();
				Location loc = event.getVehicle().getLocation();
				int tid = world.getBlockTypeIdAt(loc.getBlockX(),
						loc.getBlockY() , loc.getBlockZ());

				if (tid == Material.RAILS.getId()) {
					for (TrackPiece tc : tracks) {
						Location t = tc.Loc;
						if (t.getBlockX() == loc.getBlockX()
								&& t.getBlockY() == loc.getBlockY()
								&& t.getBlockZ() == loc.getBlockZ()) {
							tc.HasCart = true;
						}
					}
				}
			}
		};

		plugin.getServer()
				.getPluginManager()
				.registerEvent(Event.Type.PLAYER_INTERACT, pe, Priority.Normal,
						plugin);

		plugin.getServer()
				.getPluginManager()
				.registerEvent(Event.Type.VEHICLE_MOVE, pec, Priority.Normal,
						plugin);

	}
}