package net.evmodder.EvLib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Collector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import net.evmodder.EvLib.ReflectionUtils;
import net.evmodder.EvLib.ReflectionUtils.RefClass;
import net.evmodder.EvLib.ReflectionUtils.RefField;
import net.evmodder.EvLib.ReflectionUtils.RefMethod;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class EvUtils{// version = X1.0
	public static float getBlockStrength(Material block){
		RefClass classBlock = ReflectionUtils.getRefClass("{nms}.Block");
		RefMethod methodGetByName = classBlock.getMethod("getByName");
		RefField field = classBlock.getField("strength");
		return (Float) field.of( methodGetByName.of(null).call(block.name()) ).get();
	}

	public static String getNormalizedName(EntityType entity){
		//TODO: improve this algorithm / test for errors
		switch(entity){
		case PIG_ZOMBIE:
			return "Zombie Pigman";
		case MUSHROOM_COW:
			return "Mooshroom";
		default:
			boolean wordStart = true;
			char[] arr = entity.name().toCharArray();
			for(int i=0; i<arr.length; ++i){
				if(wordStart) wordStart = false;
				else if(arr[i] == '_' || arr[i] == ' '){arr[i] = ' '; wordStart = true;}
				else arr[i] = Character.toLowerCase(arr[i]);
			}
			return new String(arr);
		}
	}

	static long[] scale = new long[]{31536000000L, /*2628000000L,*/ 604800000L, 86400000L, 3600000L, 60000L, 1000L};
	static char[] units = new char[]{'y', /*'m',*/ 'w', 'd', 'h', 'm', 's'};
	public static String formatTime(long time, ChatColor timeColor, ChatColor unitColor){
		return formatTime(time, timeColor, unitColor, scale, units);
	}
	public static String formatTime(long time, ChatColor timeColor, ChatColor unitColor, long[] scale, char[] units){
		int i = 0;
		while(time < scale[i]) ++i;
		StringBuilder builder = new StringBuilder("");
		for(; i < scale.length-1; ++i){
			builder.append(timeColor).append(time / scale[i]).append(unitColor).append(units[i]).append(", ");
			time %= scale[i];
		}
		return builder.append(timeColor).append(time / scale[scale.length-1])
					  .append(unitColor).append(units[units.length-1]).toString();
	}

	public static Location getLocationFromString(String s){
		String[] data = s.split(",");
		World world = org.bukkit.Bukkit.getWorld(data[0]);
		if(world != null){
			try{return new Location(world,
					Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));}
			catch(NumberFormatException ex){}
		}
		return null;
	}

	public static Collection<Advancement> getVanillaAdvancements(Player p){
		Vector<Advancement> advs = new Vector<Advancement>();
		Iterator<Advancement> it = Bukkit.getServer().advancementIterator();
		while(it.hasNext()){
			Advancement adv = it.next();
			if(adv.getKey().getNamespace().equals(NamespacedKey.MINECRAFT) 
					&& p.getAdvancementProgress(adv).isDone())
				advs.add(adv);
		}
		return advs;
	}
	public static Collection<Advancement> getVanillaAdvancements(Player p, Collection<String> include){
		Vector<Advancement> advs = new Vector<Advancement>();
		Iterator<Advancement> it = Bukkit.getServer().advancementIterator();
		while(it.hasNext()){
			Advancement adv = it.next();
			int i = adv.getKey().getKey().indexOf('/');
			if(adv.getKey().getNamespace().equals(NamespacedKey.MINECRAFT) && i != -1
					&& include.contains(adv.getKey().getKey().substring(0, i))
					&& p.getAdvancementProgress(adv).isDone())
				advs.add(adv);
		}
		return advs;
	}
	public static Collection<Advancement> getVanillaAdvancements(Collection<String> include){
		Vector<Advancement> advs = new Vector<Advancement>();
		Iterator<Advancement> it = Bukkit.getServer().advancementIterator();
		while(it.hasNext()){
			Advancement adv = it.next();
			int i = adv.getKey().getKey().indexOf('/');
			if(adv.getKey().getNamespace().equals(NamespacedKey.MINECRAFT) && i != -1
					&& include.contains(adv.getKey().getKey().substring(0, i)))
				advs.add(adv);
		}
		return advs;
	}

	public static boolean notFar(Location from, Location to){
		int x1 = from.getBlockX(), y1 = from.getBlockY(), z1 = from.getBlockZ(),
			x2 = to.getBlockX(), y2 = to.getBlockY(), z2 = to.getBlockZ();

		return (Math.abs(x1 - x2) < 20 &&
				Math.abs(y1 - y2) < 15 &&
				Math.abs(z1 - z2) < 20 &&
				from.getWorld().getName().equals(to.getWorld().getName()));
	}

	public static String executePost(String post){
		URLConnection connection = null;
		try{
			connection = new URL(post).openConnection();
			connection.setUseCaches(false);
			connection.setDoOutput(true);

			// Get response
//			Scanner s = new Scanner(connection.getInputStream()).useDelimiter("\\A");
//			String response = s.hasNext() ? s.next() : null;
//			s.close();
//			return response;
			BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = rd.readLine();
			rd.close();
			return line;
		}
		catch(IOException e){
			System.out.println(e.getStackTrace());
			return null;
		}
	}

	public static int maxCapacity(Inventory inv, Material item){
		int sum = 0;
		for(ItemStack i : inv.getContents()){
			if(i == null || i.getType() == Material.AIR) sum += item.getMaxStackSize();
			else if(i.getType() == item) sum += item.getMaxStackSize() - i.getAmount();
		}
		return sum;
	}

	public static Vector<String> installedEvPlugins(){
		Vector<String> evPlugins = new Vector<String>();
		for(Plugin pl : Bukkit.getServer().getPluginManager().getPlugins()){
			try{
				@SuppressWarnings("unused")
				String ver = pl.getClass().getField("EvLib_ver").get(null).toString();
				evPlugins.add(pl.getName());
				//TODO: potentially return list of different EvLib versions being used
			}
			catch(IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e){}
		}
		return evPlugins;
	}

	static HashMap<String, Boolean> exists = new HashMap<String, Boolean>();
	public static boolean checkExists(String player){
		if(!exists.containsKey(player)){
			//Sample data (braces included): {"id":"34471e8dd0c547b9b8e1b5b9472affa4","name":"EvDoc"}
			String data = executePost("https://api.mojang.com/users/profiles/minecraft/"+player);
			exists.put(player, data != null);
		}
		return exists.get(player);
	}

	public static ArrayList<Player> getNearbyPlayers(Location loc, int range){//+
		range = range*range;
		ArrayList<Player> ppl = new ArrayList<Player>();
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			if(p.getWorld().getName().equals(loc.getWorld().getName()) && p.getLocation().distanceSquared(loc) > range)
				ppl.add(p);
		}
		return ppl;
	}

	final static String MAIN_WORLD = Bukkit.getWorlds().get(0).getName();//[0] is the default world
	public static GameMode getWorldGameMode(World world){
		MultiverseCore mv = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
		if(mv != null && mv.isEnabled()) return mv.getMVWorldManager().getMVWorld(world).getGameMode();
		else if(world.getName().contains(MAIN_WORLD)) return Bukkit.getDefaultGameMode();
		else return null;
	}

	public static boolean pickIsAtLeast(Material pickType, Material needPick){//+
		switch(pickType){
			case DIAMOND_PICKAXE:
				return true;
			case IRON_PICKAXE:
				return needPick != Material.DIAMOND_PICKAXE;
			case STONE_PICKAXE:
				return needPick != Material.DIAMOND_PICKAXE && needPick != Material.IRON_PICKAXE;
			case GOLDEN_PICKAXE:
			case WOODEN_PICKAXE:
			default:
				return needPick != Material.DIAMOND_PICKAXE && needPick != Material.IRON_PICKAXE
					&& needPick != Material.STONE_PICKAXE;
				
		}
	}
	public static boolean swordIsAtLeast(Material swordType, Material needSword){//+
		switch(swordType){
			case DIAMOND_SWORD:
				return true;
			case IRON_SWORD:
				return needSword != Material.DIAMOND_SWORD;
			case STONE_SWORD:
				return needSword != Material.IRON_SWORD && needSword != Material.DIAMOND_SWORD;
			case GOLDEN_SWORD:
			case WOODEN_SWORD:
			default:
				return needSword != Material.IRON_SWORD && needSword != Material.DIAMOND_SWORD
					&& needSword != Material.STONE_SWORD;
		}
	}

	public static List<Block> getBlockStructure(Block block0, Function<Block, Boolean> test, 
			List<BlockFace> dirs, int MAX_SIZE){//+
		HashSet<Block> visited = new HashSet<Block>();
		List<Block> results = new ArrayList<Block>();
		ArrayDeque<Block> toProcess = new ArrayDeque<Block>();
		toProcess.addLast(block0);
		while(results.size() < MAX_SIZE && !toProcess.isEmpty()){
			Block b = toProcess.pollFirst();
			if(b != null && test.apply(b) && !visited.contains(b)){
				results.add(b);
				visited.add(b);
				for(BlockFace dir : dirs) toProcess.addLast(b.getRelative(dir));
			}
		}
		return results;
	}

	final static List<BlockFace> dirs6 = Arrays.asList(BlockFace.UP, BlockFace.DOWN,
			BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);//+
	public static ArrayDeque<Container> getStorageDepot(Location loc){//+
		return getBlockStructure(loc.getBlock(), (b -> b.getState() instanceof Container), dirs6, 1000).stream()
				.map(b -> (Container)b.getState()).collect(
						Collector.of(ArrayDeque::new,
								ArrayDeque::add,
								(a, b) -> {a.addAll(b); return a;}
						//(deq, t) -> deq.addFirst(t),
						//(d1, d2) -> {d2.addAll(d1); return d2;}
						));
	}

	public static boolean checkHeight(Material blockType, double offset){
		switch(blockType){//TODO: RaysWorks world w/ every block height & TypeUtils.isStair() etc
			case ACACIA_SLAB:
				return offset == .5;
			case ACACIA_STAIRS:
				return offset == .5 || offset == 1;
			case ACACIA_FENCE:
				return offset == 1.5;
			default:
				return offset == 1;
		}
	}

	public static boolean isOnGround(Location loc){//for parkour world anti-cheat &/| checkpoints
		if(loc == null) return false;
		boolean useBelow = loc.getBlock() == null || loc.getBlock().isEmpty();
		if(useBelow){
			loc.setY(loc.getY() - 1D);
			if(loc.getBlock() == null || loc.getBlock().isEmpty()) return false;
			return checkHeight(loc.getBlock().getType(), 1D + loc.getY() - loc.getBlockY());
		}
		return checkHeight(loc.getBlock().getType(), loc.getY() - loc.getBlockY());
	}
}