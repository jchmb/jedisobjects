package nl.jchmb.jedisobjects.serializer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class IntegerSerializer implements Serializer<Integer> {
	private final ByteOrder byteOrder;
	public static final IntegerSerializer INSTANCE = new IntegerSerializer();
	
	public IntegerSerializer(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}
	
	public IntegerSerializer() {
		this(ByteOrder.BIG_ENDIAN);
	}
	
	@Override
	public byte[] serialize(Integer o) {
		return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(o.intValue()).array();
	}

	@Override
	public Integer deserialize(byte[] s) {
		return ByteBuffer.wrap(s).order(byteOrder).getInt();
	}
	
}
