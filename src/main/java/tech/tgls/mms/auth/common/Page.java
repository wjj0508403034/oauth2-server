package tech.tgls.mms.auth.common;

import org.apache.commons.lang3.StringUtils;

import tech.tgls.mms.auth.common.impl.BaseDaoImpl;

import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shenyuyang
 * @date 16/9/22
 */
public class Page<T> {

    private long current;
    private String pageBar;
    private long pageCount;
    private long recordCount;
    private List<T> results;
    private int size;

    private Query querySelect;

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public String getPageBar(HttpServletRequest request) {
        return getPageBar(request, false);
    }

    public String getPageBar(HttpServletRequest request, Boolean beginHash) {

        Object forwardUri = request.getAttribute("javax.servlet.forward.request_uri");
        String query = request.getQueryString();
        if(StringUtils.length(query)>0) {
            query = "?"+query;
        }else {
            query = "";
        }
        if (forwardUri == null) {
            return getPageBar(request.getRequestURI()+query, beginHash);
        } else {
            return getPageBar(forwardUri.toString()+query, beginHash);
        }
    }

    public String getPageBar(String url) {
        return getPageBar(url, false);
    }

    public String getPageBar(String url, Boolean beginHash) {
        if (pageBar == null) {

            PageHelper pageHelper = new PageHelper(this.recordCount, this.size, this.current);
            pageHelper.setPath(url);
            this.setPageCount(pageHelper.PageCount);

            String bar = pageHelper.getPageBar();
            if (beginHash) {
                bar = bar.replace("href=\"/", "href=\"#/");
            }

            this.setPageBar(bar);

        }
        return pageBar;
    }

    public void setPageBar(String pageBar) {
        this.pageBar = pageBar;
    }

    public long getPageCount() {
        return pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    public long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    /*
    *
    * 返回空的分页结果集
    *
     */
    public static <T> Page<T> GetEmpty() {
        Page<T> p = new Page<T>();
        p.setResults(new ArrayList<T>());
        p.setCurrent(1);
        p.setSize(20);
        p.setRecordCount(0);
        return p;
    }


    //-----------------------------------------------------------

    public void setQuerySelect(Query query) {
        this.querySelect = query;
    }

    public Page<T> setParameter(String name, Object value) {
        this.querySelect.setParameter(name, value);
        this.queryCount.setParameter(name, value);
        return this;
    }

    public Page<T> getResultPage() {

        List<T> list = querySelect.setFirstResult((int) (getCurrent() - 1) * getSize()).setMaxResults(getSize()).getResultList();
        long totalCount = BaseDaoImpl.getCountByQuery(queryCount);

        setResults(list);
        setRecordCount(totalCount);

        return this;
    }

    private Query queryCount;

    public void setQueryCount(Query countQuery) {
        this.queryCount = countQuery;
    }


}