package nl.jchmb.jedisobjects;

import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.jchmb.jedisobjects.serializer.Serializer;
import nl.jchmb.jedisobjects.structure.Atom;
import nl.jchmb.jedisobjects.structure.JedisAtom;
import nl.jchmb.jedisobjects.structure.JedisList;
import nl.jchmb.jedisobjects.structure.JedisMap;
import nl.jchmb.jedisobjects.structure.JedisSet;
import nl.jchmb.jedisobjects.structure.Watchable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class JedisObjects<K> {
	private final Jedis jedis;
	private final Serializer<K> keySerializer;
	
	public JedisObjects(Jedis jedis, Serializer<K> keySerializer) {
		this.jedis = jedis;
		this.keySerializer = keySerializer;
	}
	
	public <E> Atom<E> getAtom(K key, Serializer<E> serializer) {
		return new JedisAtom<>(jedis, keySerializer.serialize(key), serializer);
	}
	
	public <E> List<E> getList(K key, Serializer<E> serializer) {
		return new JedisList<>(jedis, keySerializer.serialize(key), serializer);
	}
	
	public <F, V> Map<F, V> getMap(K key, Serializer<F> fieldSerializer, Serializer<V> valueSerializer) {
		return new JedisMap<F, V>(jedis, keySerializer.serialize(key), fieldSerializer, valueSerializer);
	}
	
	public <E> Set<E> getSet(K key, Serializer<E> serializer) {
		return new JedisSet<>(jedis, keySerializer.serialize(key), serializer);
	}
	
	public Transaction multi() {
		return jedis.multi();
	}
	
	public void watch(Object o) {
		if (o instanceof Watchable) {
			((Watchable) o).watch();
		}
	}
	
	public static JedisObjects<String> createSimple(Jedis jedis) {
		return new JedisObjects<>(jedis, Serializer.forString());
	}
}
