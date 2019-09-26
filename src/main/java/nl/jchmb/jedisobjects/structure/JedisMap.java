package nl.jchmb.jedisobjects.structure;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import nl.jchmb.jedisobjects.serializer.Serializer;
import redis.clients.jedis.Jedis;

public class JedisMap<F, V> extends JedisObject implements Map<F, V> {
	private final Serializer<F> fieldSerializer;
	private final Serializer<V> valueSerializer;
	
	public JedisMap(Jedis jedis, String key, Serializer<F> fieldSerializer, Serializer<V> valueSerializer) {
		super(jedis, key);
		this.fieldSerializer = fieldSerializer;
		this.valueSerializer = valueSerializer;
	}

	@Override
	public void clear() {
		delete();
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Entry<F, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V get(Object field) {
		try {
			String serializedField = fieldSerializer.trySerialize(field);
			String serializedValue = jedis.hget(key, serializedField);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V put(F field, V value) {
		V previousValue = get(field);
		jedis.hset(key, fieldSerializer.serialize(field), valueSerializer.serialize(value));
		return previousValue;
	}

	@Override
	public void putAll(Map<? extends F, ? extends V> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public V remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		return jedis.hlen(key).intValue();
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
