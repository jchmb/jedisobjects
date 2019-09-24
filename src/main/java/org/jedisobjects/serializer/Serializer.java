package org.jedisobjects.serializer;

public interface Serializer<T> {
	public String serialize(T o);
	public T deserialize(String s);
	
	public static StringSerializer forString() {
		return StringSerializer.INSTANCE;
	}
}
