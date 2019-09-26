package nl.jchmb.jedisobjects;

import java.util.List;

import nl.jchmb.jedisobjects.serializer.Serializer;
import nl.jchmb.jedisobjects.structure.Atom;
import nl.jchmb.jedisobjects.structure.JedisAtom;
import nl.jchmb.jedisobjects.structure.JedisList;
import redis.clients.jedis.Jedis;

public class JedisObjectsFactory<K> {
	private final Jedis jedis;
	private final Serializer<K> keySerializer;
	
	public JedisObjectsFactory(Jedis jedis, Serializer<K> keySerializer) {
		this.jedis = jedis;
		this.keySerializer = keySerializer;
	}
	
	public <E> Atom<E> getAtom(K key, Serializer<E> serializer) {
		return new JedisAtom<>(jedis, keySerializer.serialize(key), serializer);
	}
	
	public <E> List<E> getList(K key, Serializer<E> serializer) {
		return new JedisList<>(jedis, keySerializer.serialize(key), serializer);
	}
}
