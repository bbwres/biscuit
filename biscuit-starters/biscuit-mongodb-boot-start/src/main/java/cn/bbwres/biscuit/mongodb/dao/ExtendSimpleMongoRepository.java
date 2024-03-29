package cn.bbwres.biscuit.mongodb.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.SortInfo;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 扩展的MongoRepository
 *
 * @author zhanglinfeng
 */
public class ExtendSimpleMongoRepository<T, ID> extends SimpleMongoRepository<T, ID> implements ExtendMongoRepository<T, ID> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;

    /**
     * Creates a new {@link SimpleMongoRepository} for the given {@link MongoEntityInformation} and {@link MongoTemplate}.
     *
     * @param metadata        must not be {@literal null}.
     * @param mongoOperations must not be {@literal null}.
     */
    public ExtendSimpleMongoRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.entityInformation = metadata;
    }

    /**
     * 批量插入
     *
     * @param objectsToSave
     * @return
     */
    @Override
    public Collection<T> insertAll(Collection<? extends T> objectsToSave) {
        return mongoOperations.insertAll(objectsToSave);
    }


    /**
     * 根据查询条件查询一条记录
     *
     * @param query
     * @return
     */
    @Override
    public T findOne(Criteria query) {
        return mongoOperations.findOne(Query.query(query), entityInformation.getJavaType(), entityInformation.getCollectionName());
    }


    /**
     * 查询多条数据
     *
     * @param query
     * @return
     */
    @Override
    public List<T> find(Query query) {
        return mongoOperations.find(query, entityInformation.getJavaType(), entityInformation.getCollectionName());
    }


    /**
     * 根据条件删除
     *
     * @param query
     * @return
     */
    @Override
    public DeleteResult delete(Query query) {
        return mongoOperations.remove(query, entityInformation.getJavaType(), entityInformation.getCollectionName());
    }

    /**
     * 更新数据
     *
     * @param update
     * @param query
     * @return
     */
    @Override
    public UpdateResult update(Update update, Query query) {
        return mongoOperations.updateMulti(query, update, entityInformation.getJavaType(), entityInformation.getCollectionName());
    }


    /**
     * 分页查询
     *
     * @param criteria
     * @param pageSize
     * @param current
     * @param sortInfos 排序
     * @return
     */
    @Override
    public <Q> Page<T, Q> pageList(List<CriteriaDefinition> criteria, int pageSize, long current, List<SortInfo> sortInfos) {
        Query query = new Query();
        if (!CollectionUtils.isEmpty(criteria)) {
            for (CriteriaDefinition criteriaDefinition : criteria) {
                query.addCriteria(criteriaDefinition);
            }
        }
        Sort sort = null;
        if (!CollectionUtils.isEmpty(sortInfos)) {
            List<Sort.Order> orderList = new ArrayList<>(16);
            for (SortInfo sortInfo : sortInfos) {
                if (cn.bbwres.biscuit.dto.Sort.DESC.equals(sortInfo.getSort())) {
                    orderList.add(Sort.Order.desc(sortInfo.getSortField()));
                } else {
                    orderList.add(Sort.Order.asc(sortInfo.getSortField()));
                }
            }
            sort = Sort.by(orderList);
        }
        Long total = this.count(Query.of(query).limit(-1).skip(-1));
        PageRequest pageRequest = PageRequest.of((int) current - 1, pageSize);
        query = query.with(pageRequest).with(sort == null ? Sort.unsorted() : sort);
        List<T> list = mongoOperations.find(query, entityInformation.getJavaType(), entityInformation.getCollectionName());
        Page<T, Q> page = new Page<>();
        page.setRecords(list);
        page.setTotal(total);
        page.setCurrent(current);
        page.setSize(pageSize);
        page.calculationPages();
        return page;
    }

    /**
     * 获取总条数
     *
     * @param query
     * @return
     */
    @Override
    public Long count(Query query) {
        return mongoOperations.count(query, entityInformation.getJavaType(), entityInformation.getCollectionName());
    }


}
