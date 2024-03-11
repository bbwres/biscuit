package cn.bbwres.biscuit.mybatis.dialect;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;

/**
 * hana 数据库方言
 *
 * @author zhanglinfeng
 */
public class HanaDialect implements IDialect {
    /**
     * 组装分页语句
     *
     * @param originalSql 原始语句
     * @param offset      偏移量
     * @param limit       界限
     * @return 分页模型
     */
    @Override
    public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
        StringBuilder sql = new StringBuilder(originalSql).append(" LIMIT ").append(FIRST_MARK);
        if (offset != 0L) {
            sql.append(" OFFSET ").append(SECOND_MARK);
            return new DialectModel(sql.toString(), offset, limit).setConsumerChain();
        } else {
            return new DialectModel(sql.toString(), limit).setConsumer(true);
        }
    }
}
