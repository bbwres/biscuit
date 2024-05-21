package cn.bbwres.biscuit.id.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.DecimalFormat;

/**
 * 基于redis的有序的id生成
 *
 * @author zhanglinfeng
 */
@Slf4j
@RequiredArgsConstructor
public class RedisGenerator {

    private static final String REDIS_ID_KEY = "REDIS_ID_KEY:";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 根据前缀获取id
     *
     * @param prefix
     * @return
     */
    public String nextId(String prefix) {
        Long num = redisTemplate.opsForValue().increment(REDIS_ID_KEY + prefix);
        return prefix + num;
    }


    /**
     * 获取id
     *
     * @param key key
     * @param prefix 前缀
     * @param noPrefixSize 不包含前缀的长度
     * @param stemNum 步进
     * @return
     */
    public String nextId(String key,String prefix, Integer noPrefixSize, Long stemNum) {
        Long num = redisTemplate.opsForValue().increment(REDIS_ID_KEY + key, stemNum == null ? 1L : stemNum);
        String strNum = num + "";
        int size = noPrefixSize == null ? 10 : noPrefixSize;
        if (strNum.length() > size) {
            return prefix + num;
        }
        return prefix + new DecimalFormat("0".repeat(size)).format(num);
    }
}
