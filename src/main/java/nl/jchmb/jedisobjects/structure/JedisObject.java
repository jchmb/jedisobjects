package nl.jchmb.jedisobjects.structure;

import redis.clients.jedis.Jedis;

public abstract class JedisObject implements Deletable, Watchable {
	protected final Jedis jedis;
	protected final byte[] key;
	
	protected JedisObject(Jedis jedis, byte[] key) {
		this.jedis = jedis;
		this.key = key;
	}
	
	@Override
	public boolean delete() {
		return jedis.del(key) > 0L;
	}
	
	@Override
	public void watch() {
		jedis.watch(key);
	}
}
