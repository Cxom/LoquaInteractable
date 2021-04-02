package net.punchtree.loquainteractable.displayutil;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class MapIterationTesting {

	public static void main(String[] args) {
		
		LinkedHashMap<Integer, String> testMap = new LinkedHashMap<>();
		testMap.put(1, "apple");
		testMap.put(2, "green");
		testMap.put(3, "silver");

		System.out.println(testMap);
		
		Iterator<String> valuesIterator = testMap.values().iterator();
		while(valuesIterator.hasNext()) {
			String s = valuesIterator.next();
			if (s.contains("r")) {
				valuesIterator.remove();
			}
		}
		
//		testMap.values().removeIf(s -> s.contains("r"));
		
		System.out.println(testMap);
		
	}
	
}
