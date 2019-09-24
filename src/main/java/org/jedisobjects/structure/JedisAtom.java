package org.jedisobjects.structure;

import org.jedisobjects.serializer.Serializer;

import redis.clients.jedis.Jedis;

public class JedisAtom<E> extends JedisObject implements Atom<E> {
	private final Serializer<E> serializer;
	
	public JedisAtom(Jedis jedis, String key, Serializer<E> serializer) {
		super(jedis, key);
		this.serializer = serializer;
	}
	
	@Override
	public E get() {
		return serializer.deserialize(jedis.get(key));
	}

	@Override
	public void set(E e) {
		jedis.set(key, serializer.serialize(e));
	}

}
