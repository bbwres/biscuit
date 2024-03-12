package cn.bbwres.biscuit.mybatis.handler;

import cn.bbwres.biscuit.mybatis.config.MybatisPlusAutoConfigure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = {MybatisPlusAutoConfigure.class})
class BiscuitMybatisEnumTypeHandlerTest {


    @Test
    public void testMybatisConfigurationInitializer() {
        Assertions.assertTrue(true);
    }


}