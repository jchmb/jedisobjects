package org.jedisobjects.serializer;

public class StringSerializer implements Serializer<String> {
	public static final StringSerializer INSTANCE = new StringSerializer();
	
	@Override
	public String serialize(String o) {
		return o;
	}

	@Override
	public String deserialize(String s) {
		return s;
	}

}
