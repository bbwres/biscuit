package cn.bbwres.biscuit.id.generator;

import cn.bbwres.biscuit.id.IdGeneratorAutoConfigure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * redis 生成id 单元测试
 */
@SpringBootTest(classes = {IdGeneratorAutoConfigure.class})
class RedisGeneratorTest {

    @Autowired
    private RedisGenerator redisGenerator;

    @Test
    void nextId() {
    }

    @Test
    void nextIds() {
        List<String> ids = redisGenerator.nextIds("test", 10000L);
        System.out.println(ids);
    }

    @Test
    void testNextId() {
    }
}