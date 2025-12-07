package dao;

import java.util.List;
import java.util.Optional;

/**
 * 通用DAO接口 - 使用泛型定义基础CRUD操作
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
public interface BaseDAO<T, ID> {
    
    /**
     * 保存实体
     * @param entity 要保存的实体
     * @return 保存后的实体（包含生成的ID）
     */
    T save(T entity);
    
    /**
     * 更新实体
     * @param entity 要更新的实体
     * @return 是否更新成功
     */
    boolean update(T entity);
    
    /**
     * 根据ID删除
     * @param id 实体ID
     * @return 是否删除成功
     */
    boolean deleteById(ID id);
    
    /**
     * 根据ID查找
     * @param id 实体ID
     * @return Optional包装的实体
     */
    Optional<T> findById(ID id);
    
    /**
     * 查找所有实体
     * @return 实体列表
     */
    List<T> findAll();
    
    /**
     * 检查实体是否存在
     * @param id 实体ID
     * @return 是否存在
     */
    boolean existsById(ID id);
    
    /**
     * 统计实体数量
     * @return 实体总数
     */
    long count();
}
