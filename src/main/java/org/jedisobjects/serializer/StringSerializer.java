package org.jedisobjects.serializer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
