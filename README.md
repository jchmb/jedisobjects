JedisObjects
============

JedisObjects is a Java wrapper around Jedis that implements standard Java interfaces (e.g. Map and List) for Redis structures, allowing you to use Redis structures as standard Java data structures.

**This is a WIP.**

Example usage
-------------

Managing a single key-value pair (called Atoms):

```java
Jedis jedis = ...; // It is assumed that a Jedis instance is given.
JedisObjects<String> jedisObjects = JedisObjects.createSimple(jedis);
Atom<String> atom = jedisObjects.getAtom("example:atom");
atom.set("Testing.");
System.out.println(atom.get());
```

Managing a Map:

```java
Jedis jedis = ...; // It is assumed that a Jedis instance is given.
JedisObjects<String> jedisObjects = JedisObjects.createSimple(jedis);
Map<String, String> map = jedisObjects.getMap("example:map");
map.put("key1", "value1");
map.put("key2", "value2");
for (Entry<String, String> entry : map.entrySet()) {
	System.out.println(entry);
}
map.clear();
```
