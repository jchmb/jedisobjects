package nl.jchmb.jedisobjects.structure;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import nl.jchmb.jedisobjects.serializer.Serializer;
import redis.clients.jedis.Jedis;

public class JedisSet<E> extends JedisObject implements Set<E> {
	private final Serializer<E> serializer;
	
	public JedisSet(Jedis jedis, byte[] key, Serializer<E> serializer) {
		super(jedis, key);
		this.serializer = serializer;
	}

	@Override
	public boolean add(E e) {
		return jedis.sadd(key, serializer.serialize(e)) > 0L;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO: implement optimized version instead of calling add() one by one.
		boolean changed = false;
		for (E e: c) {
			changed |= add(e);
		}
		return changed;
	}

	@Override
	public void clear() {
		delete();
	}

	@Override
	public boolean contains(Object o) {
		try {
			byte[] serializedElement = serializer.trySerialize(o);
			return jedis.sismember(key, serializedElement);
		} catch (ClassCastException ex) {
			return false;
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO: implement optimized version.
		return c.stream()
				.allMatch(this::contains);
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Iterator<E> iterator() {
		return jedis.smembers(key).stream()
				.map(serializer::deserialize)
				.iterator();
	}

	@Override
	public boolean remove(Object o) {
		try {
			byte[] serializedElement = serializer.trySerialize(o);
			return jedis.srem(key, serializedElement) > 0L;
		} catch (ClassCastException ex) {
			return false;
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO: implement optimized version
		boolean changed = false;
		for (Object o : c) {
			changed |= remove(o);
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO: optimize
		boolean changed = false;
		for (E e : this) {
			if (!c.contains(e)) {
				changed = true;
				remove(e);
			}
		}
		return changed;
	}

	@Override
	public int size() {
		return jedis.scard(key).intValue();
	}

	@Override
	public Object[] toArray() {
		return jedis.smembers(key).stream()
				.map(serializer::deserialize)
				.toArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a == null) {
			throw new NullPointerException();
		}
		int n = size();
		if (n > a.length) {
			a = (T[]) Array.newInstance(a.getClass(), n);
		}
		int i = 0;
		for (Object o : this) {
			try {
				a[i++] = (T) o;
			} catch (ClassCastException exception) {
				ArrayStoreException newException = new ArrayStoreException();
				newException.initCause(exception);
				throw newException;
			}
		}
		if (a.length > n) {
			a[n] = null;
		}
		return a;
	}

}
