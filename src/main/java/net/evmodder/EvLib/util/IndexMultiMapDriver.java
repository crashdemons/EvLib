package net.evmodder.EvLib.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class IndexMultiMapDriver{

	
	public static void main(String[] args){
		HashMap<Double, Vector<String>> test = new HashMap<>();
		IndexTreeMultiMap2<Double, Vector<String>, String> yeet = new IndexTreeMultiMap2<>(Vector::new, test);
		yeet.putTree(0.5, "x00");

		yeet.putTree(5.1, "l11");
		yeet.putTree(5.2, "m12");
		yeet.putTree(5.3, "n13");
		yeet.putTree(5.4, "o14");//
		yeet.putTree(5.4, "o14_2");
		yeet.putTree(5.5, "p15");
		yeet.putTree(5.6, "q16");

		yeet.putTree(0.5, "x01");

		yeet.putTree(2.61, "f5");
		yeet.putTree(2.62, "g6");
		yeet.putTree(2.63, "h7");
		yeet.putTree(2.64, "i8");
		yeet.putTree(2.65, "j9");
		yeet.putTree(2.66, "k10");

		yeet.putTree(0.5, "x02");

		yeet.putTree(2.45, "a0");
		yeet.putTree(2.46, "b1");
		yeet.putTree(2.47, "c2");
		yeet.putTree(2.48, "d3");
		yeet.putTree(2.49, "e4");

		yeet.putTree(0.5, "x03");
		yeet.putTree(0.1, "huehuehue");

		System.out.println(yeet.getKeyIndex(0.5)+","+yeet.getLowerIndex(0.5)+","+yeet.getUpperIndex(0.5));
		System.out.println(yeet.getKeyIndex(2.45)+","+yeet.getLowerIndex(2.45)+","+yeet.getUpperIndex(2.45));
		System.out.println(yeet.getKeyIndex(5.4)+","+yeet.getLowerIndex(5.4)+","+yeet.getUpperIndex(5.4));
		System.out.println(yeet.getKeyIndex(5.6)+","+yeet.getLowerIndex(5.6)+","+yeet.getUpperIndex(5.6));
		for(Iterator<Double> it = yeet.keyIterator(); it.hasNext();){
			Double key = it.next();
			System.out.println(yeet.get(key));
		}
		for(int i=0; i<yeet.size(); ++i){
			System.out.println(i+": "+yeet.atIndex(i));
		}
		for(int i=0; i<yeet.valuesSize(); ++i){
			System.out.println(i+": "+yeet.atValueIndex(i));
		}
	}
}