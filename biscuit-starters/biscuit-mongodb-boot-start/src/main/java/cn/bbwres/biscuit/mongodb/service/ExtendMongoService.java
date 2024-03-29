package cn.bbwres.biscuit.mongodb.service;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.mongodb.dao.ExtendMongoRepository;

import java.util.Collection;

/**
 * 扩展的mongodb操作服务
 *
 * @author zhanglinfeng
 */
public interface ExtendMongoService<T, Q, ID, D extends ExtendMongoRepository<T, ID>> {


    /**
     * 获取操作类
     *
     * @return
     */
    D getMongodbDao();


    /**
     * 批量插入
     *
     * @param objectsToSave
     * @return
     */
    Collection<T> insertAll(Collection<? extends T> objectsToSave);

    /**
     * 保存单条数据
     *
     * @param entity
     * @return
     */
    T insert(T entity);

    /**
     * 保存数据,如果不存在则插入，如果存在则更新
     *
     * @param entity
     * @return
     */
    T insertOrUpdate(T entity);


    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    T findById(ID id);


    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    void deleteById(ID id);


    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    Page<T, Q> pageList(Page<T, Q> page);


}
