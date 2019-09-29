package nl.jchmb.jedisobjects.structure;

import java.util.Collection;
import java.util.Collections;
import java.util.FormatterClosedException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import nl.jchmb.jedisobjects.serializer.Serializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ListPosition;

public class JedisList<E> extends JedisObject implements List<E> {
	private final Serializer<E> serializer;
	private final byte[] PLACEHOLDER = "__PLACEHOLDER__".getBytes();
	
	public JedisList(Jedis jedis, byte[] key, Serializer<E> serializer) {
		super(jedis, key);
		this.serializer = serializer;
	}
	
	@Override
	public boolean add(E e) {
		if (e == null) {
			throw new NullPointerException();
		}
		return jedis.rpush(key, serializer.serialize(e)) > 0L;
	}

	@Override
	public void add(int index, E element) {
		if (element == null) {
			throw new NullPointerException();
		}
		addAll(index, Collections.singletonList(element));
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
		boolean changed = false;
		if (c == null) {
			throw new NullPointerException();
		}
		int n = size();
		if (index < 0 || index > n) {
			throw new IndexOutOfBoundsException();
		}
		if (index == n) {
			return addAll(c);
		}
		byte[] serializedPreviousElement = jedis.lindex(key, index);
		jedis.lset(key, index, PLACEHOLDER);
		jedis.linsert(key, ListPosition.AFTER, PLACEHOLDER, serializedPreviousElement);
		for (E e : c) {
			changed |= jedis.linsert(key, ListPosition.BEFORE, PLACEHOLDER, serializer.serialize(e)) > 0L;
		}
		jedis.lrem(key, 1, PLACEHOLDER);
		return changed;
	}

	@Override
	public void clear() {
		delete();
	}

	@Override
	public boolean contains(Object o) {
		return stream().anyMatch(o::equals);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return c.stream()
				.allMatch(this::contains);
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
		return listIterator(0);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new JedisListIterator<>(this, index);
	}

	@Override
	public boolean remove(Object o) {
		try {
			byte[] s = serializer.trySerialize(o);
			return jedis.lrem(key, 1, s) > 0L;
		} catch (ClassCastException exception) {
			return false;
		}
	}

	@Override
	public E remove(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		byte[] s = jedis.lindex(key, index);
		jedis.lset(key, index, PLACEHOLDER);
		jedis.lrem(key, 1, PLACEHOLDER);
		return serializer.deserialize(s);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			changed |= remove(o);
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
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
		return stream().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		return String.format(
				"[%s]",
				stream()
					.map(Object::toString)
					.collect(Collectors.joining(", "))
		);
	}
	
	private static class JedisListIterator<E> implements ListIterator<E> {
		private final JedisList<E> list;
		private int cursor;
		
		private JedisListIterator(JedisList<E> list, int cursor) {
			this.list = list;
			this.cursor = cursor;
		}
		
		@Override
		public void add(E e) {
			list.add(cursor, e);
		}

		@Override
		public boolean hasNext() {
			return cursor < list.size();
		}

		@Override
		public boolean hasPrevious() {
			return cursor > 0;
		}

		@Override
		public E next() {
			return list.get(cursor++);
		}

		@Override
		public int nextIndex() {
			return cursor;
		}

		@Override
		public E previous() {
			return list.get(--cursor);
		}

		@Override
		public int previousIndex() {
			return cursor - 1;
		}

		@Override
		public void remove() {
			list.remove(cursor);
		}

		@Override
		public void set(E e) {
			list.set(cursor, e);
		}
		
	}
}
