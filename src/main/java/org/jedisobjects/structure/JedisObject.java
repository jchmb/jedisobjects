package org.jedisobjects.structure;

import redis.clients.jedis.Jedis;

public abstract class JedisObject {
	protected final Jedis jedis;
	protected final String key;
	
	protected JedisObject(Jedis jedis, String key) {
		this.jedis = jedis;
		this.key = key;
	}
	
	public boolean delete() {
		return jedis.del(key) > 0L;
	}
}
