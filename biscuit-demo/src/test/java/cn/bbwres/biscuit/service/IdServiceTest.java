package cn.bbwres.biscuit.service;

import cn.bbwres.biscuit.id.generator.RedisGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class IdServiceTest {

    @Autowired
    private RedisGenerator redisGenerator;


    @Test
    public void testIds() {
        List<String> strings =  redisGenerator.nextIds("test-18-",null,18,10000L);
        System.out.println(strings);
    }

}