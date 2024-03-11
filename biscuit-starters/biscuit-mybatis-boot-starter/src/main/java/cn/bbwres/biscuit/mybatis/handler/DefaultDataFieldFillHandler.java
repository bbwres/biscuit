package cn.bbwres.biscuit.mybatis.handler;

import cn.bbwres.biscuit.entity.BaseEntity;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.mybatis.config.MybatisProperties;
import cn.bbwres.biscuit.mybatis.config.MybatisTenantProperties;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 默认数据填充处理器
 *
 * @author zhanglinfeng
 */
public class DefaultDataFieldFillHandler implements MetaObjectHandler {

    private final MybatisProperties mybatisProperties;
    private final MybatisTenantProperties mybatisTenantProperties;

    public DefaultDataFieldFillHandler(MybatisProperties mybatisProperties,
                                       MybatisTenantProperties mybatisTenantProperties) {
        this.mybatisProperties = mybatisProperties;
        this.mybatisTenantProperties = mybatisTenantProperties;
    }

    private static final String UPDATE_TIME = "updateTime";
    private static final String UPDATER = "updater";

    /**
     * 插入元对象字段填充（用于插入时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity) {
            BaseEntity baseDO = (BaseEntity) metaObject.getOriginalObject();

            LocalDateTime current = LocalDateTime.now();
            // 创建时间为空，则以当前时间为插入时间
            if (Objects.isNull(baseDO.getCreateTime())) {
                baseDO.setCreateTime(current);
            }
            String userId = mybatisProperties.obtainUserInfo(UserBaseInfo::getUserId);
            // 当前登录用户不为空，创建人为空，则当前登录用户为创建人
            if (Objects.nonNull(userId) && Objects.isNull(baseDO.getCreator())) {
                baseDO.setCreator(userId);
            }
            //是否启用租户插件
            if (mybatisTenantProperties.isEnabled()) {
                //获取租户id
                String tenantId = mybatisProperties.obtainUserInfo(userBaseInfo -> ObjectUtils.isEmpty(userBaseInfo.getTenantId()) ?
                        mybatisTenantProperties.getDefaultTenant() : userBaseInfo.getTenantId());
                if (Objects.isNull(baseDO.getTenantId())) {
                    baseDO.setTenantId(tenantId);
                }

            }
        }
    }

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Object modifyTime = getFieldValByName(UPDATE_TIME, metaObject);
        if (Objects.isNull(modifyTime)) {
            setFieldValByName(UPDATE_TIME, LocalDateTime.now(), metaObject);
        }

        // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
        Object modifier = getFieldValByName(UPDATER, metaObject);

        String userId = mybatisProperties.obtainUserInfo(UserBaseInfo::getUserId);
        if (Objects.nonNull(userId) && Objects.isNull(modifier)) {
            setFieldValByName(UPDATER, userId, metaObject);
        }
    }


}
