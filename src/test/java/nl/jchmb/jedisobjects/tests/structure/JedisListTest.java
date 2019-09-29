package nl.jchmb.jedisobjects.tests.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import org.junit.Test;

import junit.framework.TestCase;
import nl.jchmb.jedisobjects.serializer.Serializer;
import nl.jchmb.jedisobjects.structure.JedisList;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

public class JedisListTest extends TestCase {
	private RedisServer mockServer;
	private Jedis jedis;
	
	@Override
	protected void setUp() throws Exception {
		mockServer = RedisServer.builder()
				.port(6379)
				.setting("daemonize no")
				.build();
		mockServer.start();
		jedis = new Jedis("localhost", 6379);
	}
	
	@Override
	protected void tearDown() throws Exception {
		jedis.close();
		jedis = null;
		mockServer.stop();
		mockServer = null;
	}
	
	private JedisList<String> createNewList() {
		byte[] key = Serializer.forString().serialize(UUID.randomUUID().toString());
		JedisList<String> list = new JedisList<>(jedis, key, Serializer.forString());
		return list;
	}
	
	private JedisList<String> createFilledList(int n) {
		JedisList<String> list = createNewList();
		for (int i = 0; i < n; i++) {
			list.add("element_" + i);
		}
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
	public void test_whenElementsAreAdded_thenOrderIsPreserved() {
		JedisList<String> list = createFilledList(3);
		assertEquals(list.get(0), "element_0");
		assertEquals(list.get(1), "element_1");
		assertEquals(list.get(2), "element_2");
	}
	
	@Test
	public void test_isEmpty_afterClear() {
		JedisList<String> list = createNewList();
		list.add("dummyValue");
		assertFalse(list.isEmpty());
		list.clear();
		assertTrue(list.isEmpty());
	}
	
	@Test
	public void test_givenNonEmptyList_listIteratorReturnsElementsInOrder() {
		JedisList<String> list = createFilledList(4);
		List<String> expected = Arrays.asList("element_0", "element_1", "element_2", "element_3");
		ListIterator<String> listIterator = list.listIterator();
		int i = 0;
		while (listIterator.hasNext()) {
			assertEquals(listIterator.next(), expected.get(i));
			i++;
		}
	}
	
	@Test
	public void test_whenElementIsInsertedAtIndex_allPreviousElementsFromThatIndexAreShiftedToTheRight() {
		JedisList<String> list = createFilledList(3);
		list.add(1, "x");
		List<String> expected = Arrays.asList("element_0", "x", "element_1", "element_2");
		assertEquals(expected, list);
	}
}
