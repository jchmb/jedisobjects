package nl.jchmb.jedisobjects.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import nl.jchmb.jedisobjects.serializer.exception.SerializerException;

public class DefaultSerializer<T extends Serializable> implements Serializer<T> {

	@Override
	public byte[] serialize(T o) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(o);
			oos.close();
			return bos.toByteArray();
		} catch (IOException e) {
			throw new SerializerException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(byte[] s) {
		ByteArrayInputStream bis = new ByteArrayInputStream(s);
		try {
			ObjectInputStream ois = new ObjectInputStream(bis);
			Object o = ois.readObject();
			return (T) o;
		} catch (IOException | ClassNotFoundException | ClassCastException e) {
			throw new SerializerException(e);
		}
		
	}

}
