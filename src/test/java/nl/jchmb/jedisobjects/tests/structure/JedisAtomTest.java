package nl.jchmb.jedisobjects.tests.structure;

import org.junit.Test;

import junit.framework.TestCase;
import nl.jchmb.jedisobjects.serializer.Serializer;
import nl.jchmb.jedisobjects.structure.JedisAtom;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

public class JedisAtomTest extends TestCase {
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
	
	@Test
	public void testJedisAtom() {
		byte[] key = Serializer.forString().serialize("test:atom:dummy");
		JedisAtom<String> atom = new JedisAtom<>(jedis, key, Serializer.forString());
		
		/* Assert key doesn't already exist. */
		assertNull(atom.get());
		
		/* Set the key and assert that get() returns that key. */
		String dummyValue = "dummyValue";
		atom.set(dummyValue);
		assertEquals(dummyValue, atom.get());
		
		/* Assert that deleting the atom causes the value to become null. */
		atom.delete();
		assertNull(atom.get());
	}
}
