package nl.jchmb.jedisobjects.serializer;

import java.nio.charset.Charset;

public class StringSerializer implements Serializer<String> {
	private final Charset charset;
	
	public StringSerializer(Charset charset) {
		this.charset = charset;
	}
	
	@Override
	public byte[] serialize(String o) {
		return o.getBytes(charset);
	}

	@Override
	public String deserialize(byte[] s) {
		if (s == null) {
			return null;
		}
		return new String(s, charset);
	}
}
