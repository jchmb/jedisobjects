package nl.jchmb.jedisobjects.serializer;

public class FloatSerializer implements Serializer<Float> {
	public static final FloatSerializer INSTANCE = new FloatSerializer();
	
	@Override
	public String serialize(Float o) {
		return o.toString();
	}

	@Override
	public Float deserialize(String s) {
		return Float.valueOf(s);
	}

}
