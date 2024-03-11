package cn.bbwres.biscuit.mybatis.handler;

import cn.bbwres.biscuit.entity.BaseEntity;
import cn.bbwres.biscuit.mybatis.config.MybatisPlusAutoConfigure;
import cn.bbwres.biscuit.mybatis.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = {MybatisPlusAutoConfigure.class})
class BiscuitMybatisEnumTypeHandlerTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() {
        Assertions.assertTrue(true);
    }


    @Test
    public void testUserMapper() {
        BaseEntity baseEntity = userMapper.selectById("1");
        Assertions.assertNotNull(baseEntity);

    }

}