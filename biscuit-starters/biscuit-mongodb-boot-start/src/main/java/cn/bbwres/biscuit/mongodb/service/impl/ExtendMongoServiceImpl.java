package cn.bbwres.biscuit.mongodb.service.impl;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.mongodb.dao.ExtendMongoRepository;
import cn.bbwres.biscuit.mongodb.service.ExtendMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 扩展的mongodb处理类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
public class ExtendMongoServiceImpl<T,Q, ID, D extends ExtendMongoRepository<T, ID>> implements ExtendMongoService<T,Q, ID, D> {

    @Autowired
    protected D mongodbDao;


    /** {@inheritDoc} */
    @Override
    public D getMongodbDao() {
        return mongodbDao;
    }

    /**
     * {@inheritDoc}
     *
     * 批量插入
     */
    @Override
    public Collection<T> insertAll(Collection<? extends T> objectsToSave) {
        return mongodbDao.insertAll(objectsToSave);
    }

    /**
     * {@inheritDoc}
     *
     * 保存单条数据
     */
    @Override
    public T insert(T entity) {
        return mongodbDao.insert(entity);
    }

    /**
     * {@inheritDoc}
     *
     * 保存数据,如果不存在则插入，如果存在则更新
     */
    @Override
    public T insertOrUpdate(T entity) {
        return mongodbDao.save(entity);
    }


    /**
     * {@inheritDoc}
     *
     * 根据id查询
     */
    @Override
    public T findById(ID id) {
        return mongodbDao.findById(id).orElse(null);
    }


    /**
     * {@inheritDoc}
     *
     * 根据id删除
     */
    @Override
    public void deleteById(ID id) {
        mongodbDao.deleteById(id);
    }


    /**
     * {@inheritDoc}
     *
     * 分页查询
     */
    @Override
    public Page<T, Q> pageList(Page<T, Q> page) {
        List<CriteriaDefinition> criteria = new ArrayList<>(16);
        //组装查询条件
        pageListAddCriteria(criteria, page.getQuery());

        //分页查询
        Page<T, Q> pageImpl = mongodbDao.pageList(criteria,
                (int) page.getSize(), page.getCurrent(), page.getSortInfos());
        pageImpl.setQuery(page.getQuery());
        return pageImpl;
    }

    /**
     * 分页增加查询条件
     *
     * @param criteria a {@link java.util.List} object
     * @param query a Q object
     */
    protected  void pageListAddCriteria(List<CriteriaDefinition> criteria, Q query) {

    }

}
