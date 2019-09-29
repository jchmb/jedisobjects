package nl.jchmb.jedisobjects.serializer;

import java.util.UUID;

public class UUIDSerializer implements Serializer<UUID>{
	private final StringSerializer stringSerializer = Serializer.forString();
	
	@Override
	public byte[] serialize(UUID o) {
		return stringSerializer.serialize(o.toString());
	}

	@Override
	public UUID deserialize(byte[] s) {
		return UUID.fromString(stringSerializer.deserialize(s));
	}
	
}
