package nl.jchmb.jedisobjects.structure;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import nl.jchmb.jedisobjects.serializer.Serializer;
import redis.clients.jedis.Jedis;

public class JedisList<E> extends JedisObject implements List<E> {
	private final Serializer<E> serializer;
	
	public JedisList(Jedis jedis, byte[] key, Serializer<E> serializer) {
		super(jedis, key);
		this.serializer = serializer;
	}
	
	@Override
	public boolean add(E e) {
		return jedis.rpush(key, serializer.serialize(e)) > 0L;
	}

	@Override
	public void add(int index, E element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		byte[][] values = (byte[][]) c.stream()
			.map(e -> serializer.serialize(e))
			.toArray();
		return jedis.rpush(key, values) > 0L;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		delete();
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E get(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		return serializer.deserialize(jedis.lindex(key, index));
	}

	@Override
	public int indexOf(Object o) {
		return jedis.lrange(key, 0, -1).indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}
	
	private List<byte[]> getRawList() {
		return jedis.lrange(key, 0, -1);
	}

	@Override
	public Iterator<E> iterator() {
		return getRawList().stream()
			.map(s -> serializer.deserialize(s))
			.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E set(int index, E element) {
		if (element == null) {
			throw new NullPointerException();
		}
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		E previousElement = get(index);
		jedis.lset(key, index, serializer.serialize(element));
		return previousElement;
	}

	@Override
	public int size() {
		return jedis.llen(key).intValue();
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return null; // TODO
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
