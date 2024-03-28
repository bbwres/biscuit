package cn.bbwres.biscuit.i18n;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.MessageSourceAccessor;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = I18nAutoConfigure.class)
class I18nAutoConfigureTest {

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @Test
    void systemMessageSource() {
        System.out.println(messageSourceAccessor.getMessage("100000400"));
    }
}