package nl.jchmb.jedisobjects.tests.structure;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import junit.framework.TestCase;
import nl.jchmb.jedisobjects.serializer.Serializer;
import nl.jchmb.jedisobjects.structure.JedisMap;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

public class JedisMapTest extends TestCase {
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
	
	private Map<String, String> createNewMap() {
		return new JedisMap<>(
			jedis,
			Serializer.forString().serialize(UUID.randomUUID().toString()),
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
	
	@Test
	public void test_givenNonEmptyMap_returnAndReplacePreviousValue() {
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
	
	@Test
	public void test_givenNonEmptyMap_replacingValuesDoesNotChangeSize() {
		int n = 5;
		Map<String, String> map = createNewFilledMap(n);
		for (int i = 0; i < n; i++) {
			map.put("field_" + i, "dummyValue_replaced_" + i);
			assertEquals(n, map.size());
		}
	}
	
	@Test
	public void test_givenNonEmptyMap_keySetEqualsExpectedKeySet() {
		int n = 3;
		Map<String, String> map = createNewFilledMap(n);
		Set<String> expectedKeys = Stream.of("field_0", "field_1", "field_2")
				.collect(Collectors.toSet());
		assertEquals(expectedKeys, map.keySet());
	}
	
	@Test
	public void test_givenNonEmptyMap_valuesEqualsExpectedValues() {
		int n = 2;
		Map<String, String> map = createNewFilledMap(n);
		Collection<String> expectedValues = Stream.of("dummyValue_0", "dummyValue_1")
				.collect(Collectors.toList());
		assertEquals(expectedValues, map.values());
	}
	
	@Test
	public void test_givenNonEmptyMap_entrySetEqualsExpectedEntries() {
		int n = 2;
		Map<String, String> map = createNewFilledMap(n);
		Set<Entry<String, String>> expectedEntries = Stream.of(
					new AbstractMap.SimpleImmutableEntry<>(
							"field_0",
							"dummyValue_0"
					),
					new AbstractMap.SimpleImmutableEntry<>(
							"field_1",
							"dummyValue_1"
					)
				)
				.collect(Collectors.toSet());
		assertEquals(expectedEntries, map.entrySet());
	}
	
	@Test
	public void test_givenNonEmptyMap_forEveryRemovedEntry_sizeDecreasesByOne() {
		int n = 5;
		Map<String, String> map = createNewFilledMap(n);
		for (int i = 0; i < n; i++) {
			assertEquals(n - i, map.size());
			map.remove("field_" + i);
		}
		assertTrue(map.isEmpty());
	}
}
