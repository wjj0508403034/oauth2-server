package tech.tgls.mms.auth.common;

import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import java.util.List;
import java.util.Map;

/**
 * @author shenyuyang
 * @date 16/9/22
 */
public interface BaseDao<T> {

    T findById(Class<T> c, long id);

    List<T> findAll(Class<T> c);

    List<T> find(String hql);
    List<T> find(String hql, Map<String, Object> params);

    long count(String hql);

    long count(String hql, Map<String, Object> params);

    void insert(T obj);

    void update(T obj);

    void merge(T obj);

    void delete(Object obj);

    void delete(Class<T> c, long id);

    int runBatch(String hql, Map<String, Object> params);

    Query query(String hql);

    void setParameters(Query query, Map obj);

    Page<T> findPageByParams(String hql, Map<String, Object> params, int pageNumber, int size);

    Page<T> findPage(String hql, int pageNumber, int size);

    public List nativeFind(String sql);

    public int nativeExecuteUpdate(String sql);

    Query queryBySql(String sqlString);

    void dbFlush();

    StoredProcedureQuery createStoredProcedureQuery(String procedureName);
}