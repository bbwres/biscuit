package cn.bbwres.biscuit.enums;

import java.io.Serializable;

/**
 * 枚举基础类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public interface BaseEnum<T extends Serializable> {
    /**
     * 枚举value
     *
     * @return 枚举value
     */
    T getValue();

    /**
     * 枚举的显示名字
     *
     * @return 枚举的显示名字
     */
    String getDisplayName();
}
