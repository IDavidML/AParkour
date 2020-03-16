package me.davidml16.aparkour.utils;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class RandomFirework {
	private static ArrayList<Color> colors = new ArrayList();
	private static ArrayList<FireworkEffect.Type> types = new ArrayList();
	private static Random random = null;

	private static void loadColors() {
		colors.add(Color.WHITE);
		colors.add(Color.PURPLE);
		colors.add(Color.RED);
		colors.add(Color.GREEN);
		colors.add(Color.AQUA);
		colors.add(Color.BLUE);
		colors.add(Color.FUCHSIA);
		colors.add(Color.GRAY);
		colors.add(Color.LIME);
		colors.add(Color.MAROON);
		colors.add(Color.YELLOW);
		colors.add(Color.SILVER);
		colors.add(Color.TEAL);
		colors.add(Color.ORANGE);
		colors.add(Color.OLIVE);
		colors.add(Color.NAVY);
		colors.add(Color.BLACK);
	}

	private static void loadTypes() {
		types.add(FireworkEffect.Type.BURST);
		types.add(FireworkEffect.Type.BALL);
		types.add(FireworkEffect.Type.BALL_LARGE);
		types.add(FireworkEffect.Type.CREEPER);
		types.add(FireworkEffect.Type.STAR);
	}

	public static void loadFireworks() {
		random = new Random();
		loadColors();
		loadTypes();
	}

	private static FireworkEffect.Type getRandomType() {
		int i = types.size();
		return (FireworkEffect.Type) types.get(random.nextInt(i));
	}

	private static Color getRandomColor() {
		int i = colors.size();
		return (Color) colors.get(random.nextInt(i));
	}

	public static void launchRandomFirework(Location paramLocation) {
		Firework firework = (Firework) paramLocation.getWorld().spawn(paramLocation, Firework.class);
		FireworkMeta fireworkMeta = firework.getFireworkMeta();
		fireworkMeta.setPower(1);

		fireworkMeta.addEffects(new FireworkEffect[] { FireworkEffect.builder().flicker(true).with(getRandomType())
				.withColor(new Color[] { getRandomColor(), getRandomColor() }).withFade(getRandomColor()).build() });

		firework.setFireworkMeta(fireworkMeta);
	}
}
