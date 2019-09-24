package org.jedisobjects.serializer;

/**
 * The Serializer<T> can serialize objects of type T to Strings and deserialize from Strings to objects of type T,
 * for the purpose of storing and retrieving values from a Redis database.
 * 
 * The Serializer<T> is also a contract. Any implementation promises to preserve equality and inequality relationships, i.e.,
 * x.equals(y) <==> f(x).equals(f(y)) and !x.equals(y) <==> !f(x).equals(f(y)).
 * 
 * @author jochem
 *
 * @param <T>
 */
public interface Serializer<T> {
	public String serialize(T o);
	public T deserialize(String s);
	
	@SuppressWarnings("unchecked")
	public default String trySerialize(Object o) throws ClassCastException {
		return serialize((T) o);
	}
	
	public static StringSerializer forString() {
		return StringSerializer.INSTANCE;
	}
}
