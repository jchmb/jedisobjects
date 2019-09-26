package nl.jchmb.jedisobjects.tests.structure;

import java.util.UUID;

import org.junit.Test;

import junit.framework.TestCase;
import nl.jchmb.jedisobjects.serializer.Serializer;
import nl.jchmb.jedisobjects.structure.JedisList;
import redis.clients.jedis.Jedis;

public class JedisListTest extends TestCase {
	private Jedis jedis;
	
	@Override
	protected void setUp() throws Exception {
		jedis = new Jedis();
	}
	
	@Override
	protected void tearDown() throws Exception {
		jedis.close();
		jedis = null;
	}
	
	private JedisList<String> createNewList() {
		byte[] key = Serializer.forString().serialize(UUID.randomUUID().toString());
		JedisList<String> list = new JedisList<>(jedis, key, Serializer.forString());
		return list;
	}
	
	@Test
	public void test_isEmpty_whenCreated() {
		JedisList<String> list = createNewList();
		assertTrue(list.isEmpty());
	}
	
	@Test
	public void test_isNotEmpty_whenElementAdded() {
		JedisList<String> list = createNewList();
		list.add("dummyValue");
		assertFalse(list.isEmpty());
	}
	
	@Test
	public void test_sizeIncreasesByOne_withEveryElementAdded() {
		JedisList<String> list = createNewList();
		assertEquals(0, list.size());
		for (int i = 0; i < 5; i++) {
			list.add("element_" + i);
			int expectedSize = i + 1;
			assertEquals(expectedSize, list.size());
		}
	}
	
	@Test
	public void test_isEmpty_afterClear() {
		JedisList<String> list = createNewList();
		list.add("dummyValue");
		assertFalse(list.isEmpty());
		list.clear();
		assertTrue(list.isEmpty());
	}
}
