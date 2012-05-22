package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.util.Random;

public class AltRandom {

	private static long random1seed;
	private static long random2seed;
	
	public AltRandom() {
		random1seed = (long) ((((System.currentTimeMillis() * .4458 ) * 1118 ) / 9) * Math.random());
		random2seed = (long) ((((System.currentTimeMillis() * .7114 ) * 5427 ) / 12) * Math.random());		
	}
	
	public static int RndInt(int from, int to) {
		random1seed = (long) ((((System.currentTimeMillis() * .4458 ) * 1118 ) / 9) * Math.random());
		random2seed = (long) ((((System.currentTimeMillis() * .7114 ) * 5427 ) / 12) * Math.random());	
		int range = ( to - from ) + 1;
		Random r1 = new Random();
		r1.setSeed(random1seed);
		Random r2 = new Random();
		r2.setSeed(random2seed);
		int[] random1 = new int[10];
		int[] random2 = new int[10];
		for (int i = 0; i < 10; i++) {
			random1[i] = r1.nextInt(range) + from;
			random2[i] = r2.nextInt(range) + from;
		}
		if (r1.nextInt(2)==1) {
			return random2[r1.nextInt(10)];
		} else {
			return random1[r2.nextInt(10)];
		}
	}
	
	public static double RndDbl() {
		random1seed = (long) ((((System.currentTimeMillis() * .4458 ) * 1118 ) / 9) * Math.random());
		random2seed = (long) ((((System.currentTimeMillis() * .7114 ) * 5427 ) / 12) * Math.random());	
		Random r1 = new Random();
		Random r2 = new Random();
		r1.setSeed(random1seed);
		r2.setSeed(random2seed);
		double x,y;
		x = r1.nextDouble();
		y = r2.nextDouble();
		return Math.sqrt(x * y);
	}
	
}
