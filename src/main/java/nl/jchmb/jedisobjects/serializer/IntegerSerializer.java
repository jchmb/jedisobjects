package nl.jchmb.jedisobjects.serializer;

public class IntegerSerializer implements Serializer<Integer> {
	public static final IntegerSerializer INSTANCE = new IntegerSerializer();
	
	@Override
	public String serialize(Integer o) {
		return o.toString();
	}

	@Override
	public Integer deserialize(String s) {
		return Integer.valueOf(s);
	}
	
}
