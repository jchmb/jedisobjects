package nl.jchmb.jedisobjects.tests.structure;

import java.util.UUID;

import org.junit.Test;

import junit.framework.TestCase;
import nl.jchmb.jedisobjects.serializer.Serializer;
import nl.jchmb.jedisobjects.structure.JedisSet;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

public class JedisSetTest extends TestCase {
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
	
	private JedisSet<String> createNewSet() {
		return new JedisSet<String>(jedis, Serializer.forString().serialize(UUID.randomUUID().toString()), Serializer.forString());
	}
	
	private JedisSet<String> createFilledSet(int n) {
		JedisSet<String> set = createNewSet();
		for (int i = 0; i < n; i++) {
			set.add("element_" + i);
		}
		return set;
	}
	
	@Test
	public void test_whenCreated_isEmpty() {
		JedisSet<String> set = createNewSet();
		assertTrue(set.isEmpty());
	}
	
	@Test
	public void test_forEveryElementAdded_sizeIncreasesByOne() {
		JedisSet<String> set = createNewSet();
		int n = 5;
		for (int i = 0; i < n; i++) {
			assertEquals(i, set.size());
			set.add("element_" + i);
		}
		assertEquals(n, set.size());
	}
	
	@Test
	public void test_givenNonEmptySet_whenCleared_isEmpty() {
		JedisSet<String> set = createFilledSet(5);
		assertFalse(set.isEmpty());
		set.clear();
		assertTrue(set.isEmpty());
	}
	
	@Test
	public void test_whenElementIsAdded_containsThatElementIsTrue() {
		JedisSet<String> set = createFilledSet(2);
		assertTrue(set.contains("element_0"));
		assertTrue(set.contains("element_1"));
		assertFalse(set.contains("element_2"));
	}
	
	@Test
	public void test_whenElementIsRemoved_setNoLongerContainsThatElement() {
		JedisSet<String> set = createFilledSet(2);
		set.remove("element_0");
		assertFalse(set.contains("element_0"));
		set.remove("element_1");
		assertFalse(set.contains("element_1"));
	}
}
