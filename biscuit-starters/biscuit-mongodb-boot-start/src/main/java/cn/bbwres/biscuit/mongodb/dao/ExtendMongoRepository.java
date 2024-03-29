package cn.bbwres.biscuit.mongodb.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.SortInfo;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;

/**
 * 扩展的MongoRepository
 *
 * @author zhanglinfeng
 */
@NoRepositoryBean
public interface ExtendMongoRepository<T, ID> extends MongoRepository<T, ID> {


    /**
     * 批量插入
     *
     * @param objectsToSave
     * @return
     */
    Collection<T> insertAll(Collection<? extends T> objectsToSave);


    /**
     * 根据查询条件查询一条记录
     *
     * @param query
     * @return
     */
    T findOne(Criteria query);


    /**
     * 查询多条数据
     *
     * @param query
     * @return
     */
    List<T> find(Query query);


    /**
     * 根据条件删除
     *
     * @param query
     * @return
     */
    DeleteResult delete(Query query);

    /**
     * 更新数据
     *
     * @param update
     * @param query
     * @return
     */
    UpdateResult update(Update update, Query query);


    /**
     * 分页查询
     *
     * @param criteria
     * @param pageSize
     * @param current
     * @param sortInfos 排序
     * @return
     */
    <Q> Page<T, Q> pageList(List<CriteriaDefinition> criteria, int pageSize, long current, List<SortInfo> sortInfos);

    /**
     * 获取总条数
     *
     * @param query
     * @return
     */
    Long count(Query query);


}
