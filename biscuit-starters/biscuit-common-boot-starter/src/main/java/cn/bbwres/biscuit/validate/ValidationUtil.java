package cn.bbwres.biscuit.validate;

import cn.bbwres.biscuit.exception.ParamsCheckRuntimeException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * 数据校验工具类
 *
 * @author zhanglinfeng
 */
public class ValidationUtil {

    private final static ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();

    private final static Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();


    /**
     * 执行数据校验
     *
     * @param bean
     * @param groups
     */
    public static void doValidate(Object bean, Class<?>... groups) {
        Set<ConstraintViolation<Object>> result = VALIDATOR.validate(bean, groups);
        if (!result.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Object> violation : result) {
                sb.append("[").append(violation.getMessage())
                        .append("]");
            }
            throw new ParamsCheckRuntimeException(sb.toString());
        }
    }

    /**
     * 应用退出时调用，释放ValidatorFactory资源
     * 需在应用关闭钩子中调用（如main方法结束前、容器销毁时）
     */
    public static void shutdown() {
        if (VALIDATOR_FACTORY != null) {
            VALIDATOR_FACTORY.close();
        }
    }

}
