JedisObjects
============

JedisObjects is a Java wrapper around Jedis that implements standard Java interfaces (e.g. Map and List) for Redis structures, allowing you to use Redis structures as standard Java data structures.

**This is a WIP.**

Example usage
-------------

```java
Jedis jedis = ...; // It is assumed that a Jedis instance is given.
JedisObjects jedisObjects = JedisObjects.createSimple(jedis);
Atom<String> atom = jedisObjects.getAtom("example:atom");
atom.set("Testing.");
System.out.println(atom.get());
```
