package nl.jchmb.jedisobjects.tests.structure;

import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import junit.framework.TestCase;
import nl.jchmb.jedisobjects.serializer.Serializer;
import nl.jchmb.jedisobjects.structure.JedisMap;
import redis.clients.jedis.Jedis;

public class JedisMapTest extends TestCase {
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
	
	private Map<String, String> createNewMap() {
		return new JedisMap<>(
			jedis,
			UUID.randomUUID().toString(),
			Serializer.forString(),
			Serializer.forString()
		);
	}
	
	private Map<String, String> createNewFilledMap(int n) {
		Map<String, String> map = createNewMap();
		for (int i = 0; i < n; i++) {
			map.put("field_" + i, "dummyValue_" + i);
		}
		return map;
	}
	
	@Test
	public void test_whenCreated_isEmpty() {
		Map<String, String> map = createNewMap();
		assertTrue(map.isEmpty());
	}
	
	@Test
	public void test_forEveryPairAdded_sizeIncreasesByOne() {
		Map<String, String> map = createNewMap();
		int n = 5;
		for (int i = 0; i < n; i++) {
			assertEquals(i, map.size());
			map.put("field_" + i, "dummyValue_" + i);
		}
		assertEquals(n, map.size());
	}
	
	@Test
	public void test_givenNonEmptyMap_whenCleared_isEmpty() {
		Map<String, String> map = createNewFilledMap(5);
		assertFalse(map.isEmpty());
		map.clear();
		assertTrue(map.isEmpty());
	}
	
	public void test_givenNonEmptyMap_whenSet_returnAndReplacePreviousValue() {
		Map<String, String> map = createNewFilledMap(5);
		int i = 2;
		String field = "field_" + i;
		String expectedReturnValue = map.get(field);
		String expectedNewValue = "dummyValue_replaced_" + i;
		String actualPreviousValue = map.put(field, expectedNewValue);
		assertEquals(expectedReturnValue, actualPreviousValue);
		String actualNewValue = map.get(field);
		assertEquals(expectedNewValue, actualNewValue);
	}
}
