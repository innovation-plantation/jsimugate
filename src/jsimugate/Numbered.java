package jsimugate;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Numbered {

	static Map<String, String> idList = new HashMap<String, String>();
	static Pattern id_pattern = Pattern.compile("([A-Za-z0-9_.]+)@[0-9a-f]{8}");

	public String sn(String name) {
		String key = name + "^" + hashCode();
		if (idList.containsKey(key)) return idList.get(key);
		for (int i = 0;; i++) {
			String value = name + "#" + i;
			if (idList.containsValue(value)) continue;
			idList.put(key, value);
			return value;
		}
	}

	public String sn() {
		return sn(getClass().getSimpleName());
	}

	public String toString() {
		return sn();
	}

	public static void renumber() {
		idList.clear();
	}
}
