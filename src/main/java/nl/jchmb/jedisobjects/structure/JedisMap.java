package nl.jchmb.jedisobjects.structure;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nl.jchmb.jedisobjects.serializer.Serializer;
import redis.clients.jedis.Jedis;

public class JedisMap<F, V> extends JedisObject implements Map<F, V> {
	private final Serializer<F> fieldSerializer;
	private final Serializer<V> valueSerializer;
	
	public JedisMap(Jedis jedis, byte[] key, Serializer<F> fieldSerializer, Serializer<V> valueSerializer) {
		super(jedis, key);
		this.fieldSerializer = fieldSerializer;
		this.valueSerializer = valueSerializer;
	}

	@Override
	public void clear() {
		delete();
	}

	@Override
	public boolean containsKey(Object field) {
		try {
			byte[] s = fieldSerializer.trySerialize(field);
			return jedis.hexists(key, s);
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public boolean containsValue(Object value) {
		try {
			byte[] s = valueSerializer.trySerialize(value);
			return jedis.hvals(key).contains(s);
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public Set<Entry<F, V>> entrySet() {
		return getRawMap().entrySet().stream()
				.map(
						entry -> new AbstractMap.SimpleImmutableEntry<F, V>(
								fieldSerializer.deserialize(entry.getKey()),
								valueSerializer.deserialize(entry.getValue())
						)
				)
				.collect(Collectors.toSet());
	}

	@Override
	public V get(Object field) {
		try {
			byte[] serializedField = fieldSerializer.trySerialize(field);
			byte[] serializedValue = jedis.hget(key, serializedField);
			if (serializedValue == null) {
				return null;
			}
			return valueSerializer.deserialize(serializedValue);
		} catch (ClassCastException exception) {
			return null;
		}
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Set<F> keySet() {
		return getRawKeys().stream()
				.map(s -> fieldSerializer.deserialize(s))
				.collect(Collectors.toSet());
	}

	@Override
	public V put(F field, V value) {
		V previousValue = get(field);
		jedis.hset(key, fieldSerializer.serialize(field), valueSerializer.serialize(value));
		return previousValue;
	}

	@Override
	public void putAll(Map<? extends F, ? extends V> m) {
		// TODO: write optimized version
		for (Entry<? extends F, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public V remove(Object field) {
		try {
			byte[] s = fieldSerializer.trySerialize(field);
			byte[] v = jedis.hget(key, s);
			if (v == null) {
				return null;
			}
			V previousValue = valueSerializer.deserialize(v);
			if (jedis.hdel(key, s) == 0L) {
				return null;
			}
			return previousValue;
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Override
	public int size() {
		return jedis.hlen(key).intValue();
	}

	@Override
	public Collection<V> values() {
		return getRawValues().stream()
				.map(s -> valueSerializer.deserialize(s))
				.collect(Collectors.toList());
	}
	
	private Map<byte[], byte[]> getRawMap() {
		return jedis.hgetAll(key);
	}
	
	private Set<byte[]> getRawKeys() {
		return jedis.hkeys(key);
	}
	
	private Collection<byte[]> getRawValues() {
		return jedis.hvals(key);
	}
}
