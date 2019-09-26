package nl.jchmb.jedisobjects.tests.structure;

import org.junit.Test;

import junit.framework.TestCase;
import nl.jchmb.jedisobjects.serializer.Serializer;
import nl.jchmb.jedisobjects.structure.JedisAtom;
import redis.clients.jedis.Jedis;

public class JedisAtomTest extends TestCase {
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
