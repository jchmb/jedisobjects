package nl.jchmb.jedisobjects.serializer;

public class BooleanSerializer implements Serializer<Boolean> {
	public static final BooleanSerializer INSTANCE = new BooleanSerializer();
	
	@Override
	public String serialize(Boolean o) {
		return o.toString();
	}

	@Override
	public Boolean deserialize(String s) {
		return Boolean.valueOf(s);
	}
	
}
