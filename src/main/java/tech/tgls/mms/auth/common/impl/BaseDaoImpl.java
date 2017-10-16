package tech.tgls.mms.auth.common.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tech.tgls.mms.auth.common.BaseDao;
import tech.tgls.mms.auth.common.Page;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author shenyuyang
 * @date 16/9/22
 */
@Repository
public class BaseDaoImpl<T> implements BaseDao {

    private static final Logger logger = LoggerFactory.getLogger("BaseDaoImpl");

    @PersistenceContext
    private EntityManager db;


    @Override
    public Object findById(Class c, long id) {
        return db.find(c, id);
    }

    @Override
    public List findAll(Class c) {
        return db.createQuery("from " + c.getSimpleName()).getResultList();
    }

    @Override
    public List<T> find(String hql) {
        return find(hql, null);
    }

    public List<T> find(String hql, Map obj) {


        Query q = db.createQuery(hql);
        setParameters(q, obj);


        return q.getResultList();
    }

    @Override
    public Page<T> findPageByParams(String hql, Map params, int pageNumber, int size) {
        Page<T> results = new Page<T>();
        Query query = query(hql);
        setParameters(query, params);
        results.setQuerySelect(query);
        Query query2 = query("select count(id) " + hql);
        setParameters(query2, params);
        results.setQueryCount(query2);

        results.setCurrent(pageNumber);
        results.setSize(size);

        return results;
    }

    @Override
    public Page<T> findPage(String hql, int pageNo, int pageSize) {


        Page<T> results = new Page<T>();

        results.setQuerySelect(query(hql));
        results.setQueryCount(query("select count(id) " + hql));

        results.setCurrent(pageNo);
        results.setSize(pageSize);

        return results;
    }

    @Override
    public long count(String hql) {

        return count(hql, null);
    }

    @Override
    public long count(String hql, Map obj) {


        Query q = db.createQuery(hql);
        setParameters(q, obj);

        return getCountByQuery(q);
    }

    public static long getCountByQuery(Query q) {
        if (q.getSingleResult() != null) {
            if (q.getSingleResult() instanceof Integer) {
                return ((Integer) q.getSingleResult());
            }
            return (long) q.getSingleResult();
        }
        return 0;
    }

    @Override
    @Transactional
    public void insert(Object obj) {
        db.persist(obj);
    }

    @Override
    @Transactional
    public void update(Object obj) {
        db.persist(obj);
        db.merge(obj);
    }

    @Override
    @Transactional
    public void merge(Object obj) {
        db.merge(obj);
    }

    @Override
    @Transactional
    public void delete(Object obj) {
        if (obj != null) {
            db.remove(obj);
        }
    }

    @Override
    @Transactional
    public void delete(Class c, long id) {
        Object o = findById(c, id);
        if (o != null) {
            db.remove(o);
        }
    }

    @Override
    public Query query(String hql) {
        return db.createQuery(hql);
    }

    @Override
    @Transactional
    public int runBatch(String hql, Map obj) {

        Query q = db.createQuery(hql);

        setParameters(q, obj);

        return q.executeUpdate();
    }

    public void setParameters(Query query, Map obj) {

        Map<String, Object> params = (Map<String, Object>) obj;

        if (params != null) {
            setQueryParameters(query, params);
        }

    }

    private void setQueryParameters(Query query, Map<String, Object> paramMap) {

        query.getParameters();

        if (query != null && paramMap != null && !paramMap.isEmpty()) {

            List namedParam = getListByParameterSet(query);

            Iterator itr = paramMap.keySet().iterator();
            while (itr.hasNext()) {
                String paramName = (String) itr.next();
                if (namedParam.contains(paramName)) {

                    Object paramValue = paramMap.get(paramName);
                    if (paramValue instanceof List) {
                        query.setParameter(paramName, (List) paramValue);
                    } else if (paramValue instanceof Object[]) {
                        query.setParameter(paramName, (Object[]) paramValue);
                    } else {
                        query.setParameter(paramName, paramValue);
                    }
                }
            }
        }
    }

    private List getListByParameterSet(Query query) {

        List list = new ArrayList();

        Iterator itr = query.getParameters().iterator();
        while (itr.hasNext()) {
            Parameter<String> p = (Parameter<String>) itr.next();
            list.add(p.getName());
        }

        return list;
    }

    public List nativeFind(String sql) {
        Query q = db.createNativeQuery(sql);
        return q.getResultList();
    }

    @Transactional
    public int nativeExecuteUpdate(String sql) {
        Query q = db.createNativeQuery(sql);
        return q.executeUpdate();
    }

    @Override
    public Query queryBySql(String sqlString) {
        return db.createNativeQuery(sqlString);
    }

    @Override
    public void dbFlush() {
        db.flush();
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        return db.createStoredProcedureQuery(procedureName);
    }

}
