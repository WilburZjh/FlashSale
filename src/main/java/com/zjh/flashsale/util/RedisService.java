package com.zjh.flashsale.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

@Slf4j
@Service
public class RedisService {
    @Autowired
    private JedisPool jedisPool;

    /**
     * 添加限购名单
     *
     * @param activityId
     * @param userId
     */
    public void addLimitMember(long activityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.sadd("flashsaleActivity_users:" + activityId, String.valueOf(userId));
    }

    /**
     * 判断是否在限购名单中
     *
     * @param activityId
     * @param userId
     * @return
     */
    public boolean isInLimitMember(long activityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        boolean sismember = jedisClient.sismember("flashsaleActivity_users:" + activityId, String.valueOf(userId));
        log.info("activityId:{} userId:{}  在已购名单中:{}", activityId, userId, sismember);
        return sismember;
    }

    /**
     * 移除限购名单
     *
     * @param activityId
     * @param userId
     */
    public void removeLimitMember(long activityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.srem("flashsaleActivity_users:" + activityId, String.valueOf(userId));
    }

    /**
     * 超时未支付 Redis 库存回滚
     *
     * @param key
     */
    public void revertStock(String key) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.incr(key);
    }

    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    public void setValue(String key, Long value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value.toString());
    }

    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    public void setValue(String key, String value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value);
    }


    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public String getValue(String key) {
        Jedis jedisClient = jedisPool.getResource();
        return jedisClient.get(key);
    }


    /**
     * 缓存中库存判断和扣减
     * @param key
     * @return
     * @throws Exception
     */
    public boolean stockDeductValidator(String key)  {
        Jedis jedisClient = null;
        try {
            jedisClient = jedisPool.getResource();
            /**
             if (redis.call('exists',KEYS[1]) == 1)) then
             local stock = tonumber(redis.call('get', KEYS[1]));
             if( stock <=0 ) then
             return -1
             end;
             redis.call('decr',KEYS[1]);
             return stock - 1;
             end;
             return -1;
             */
            String script = "if redis.call('exists',KEYS[1]) == 1 then\n" +
                    "                 local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                    "                 if( stock <=0 ) then\n" +
                    "                    return -1\n" +
                    "                 end;\n" +
                    "                 redis.call('decr',KEYS[1]);\n" +
                    "                 return stock - 1;\n" +
                    "             end;\n" +
                    "             return -1;";


            Long stock = (Long) jedisClient.eval(script, Collections.singletonList(key), Collections.emptyList());
            if (stock < 0) {
                System.out.println("库存不足");
                return false;
            } else {
                System.out.println("恭喜，抢购成功");
            }
            return true;
        } catch (Throwable throwable) {
            System.out.println("库存扣减失败：" + throwable.toString());
            return false;
        } finally {
            jedisClient.close();
        }
    }


}

