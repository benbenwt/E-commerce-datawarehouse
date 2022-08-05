package com.atguigu.gmall.realtime
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object test {
  def main(args:Array[String]):Unit={
    println("hi jedis")
    var jedisPoolConfig:JedisPoolConfig=new JedisPoolConfig
    jedisPoolConfig.setMaxTotal(100)
    jedisPoolConfig.setMaxIdle(20)
    jedisPoolConfig.setMinIdle(20)

    var jedisPool=new JedisPool(jedisPoolConfig,"172.18.65.186",6379)
    println(jedisPool)
    var client=jedisPool.getResource()
    client.hset("test","21","21value")

    println(client.hgetAll("test"))


  }
}
