package tech.tgls.mms.auth.common;


import org.springframework.util.StringUtils;

/**
 * @author shenyuyang
 * @date 16/9/24
 */
public class PageHelper {

    private String _bar;
    private long _current = 0;
    private int _pagesize = 20;

    public String Condition;

    public long PageCount;
    public long RecordCount;

    public PageHelper(long recordCount, int pageSize, long currentPage) {

        this.setSize(pageSize);
        this.RecordCount = recordCount;
        this.computePageCount();

        this.setCurrent(currentPage);
        this.resetCurrent();
    }

    public long getCurrent() {
        if (_current <= 0) {
            return 1;
            //return CurrentRequest.getCurrentPage();
        }
        return _current;
    }

    public void setCurrent(long page) {
        _current = page;
    }

    public int getSize() {
        return _pagesize;
    }

    public void setSize(int size) {
        _pagesize = size;
    }

    public void resetCurrent() {
        if (this.getCurrent() <= 1) {
            this.setCurrent(1);
        } else if (this.getCurrent() > this.PageCount) {
            this.setCurrent(this.PageCount);
        }
    }

    public long computePageCount() {
        this.PageCount = GetPageCount(this.RecordCount, this.getSize());
        return this.PageCount;
    }

    public String getPageBar() {
        if (StrUtil.isNullOrEmpty(_bar)) {
            setPagerBar();
        }
        return _bar;
    }

    public void setPageBar(String bar) {
        _bar = bar;
    }


    private void setPagerBar() {

        String url = this.getPath();

        StringBuilder sb = new StringBuilder();

        sb.append(StrUtil.format("<nav style=\"float:right;\"><ul class=\"pagination pagination-sm\" data-records={0} data-page-size={1} data-pages={2}>\n", RecordCount, this.getSize(), PageCount));
        sb.append("\n");

        if (this.getCurrent() == 1) {
            sb.append("<li><span class=\"pagePrev pagePrev_button pagePrev_disabled\">Previous</span></li>\n");
        }
        if (this.getCurrent() > 1) {
            sb.append("<li><a href=\"");
            appendLink(url, sb, this.getCurrent() - 1);
            sb.append("\" class=\"pagePrev pagePrev_button\">Previous</a></li>\n");
        }
        if (this.getCurrent() <= 8) {
            loopPage(url, sb, 1, this.getCurrent());
        } else {
            loopPage(url, sb, 1, 3);
            sb.append("<li><span class=\"pagepoint\" href=\"javascript:void(0)\">...</span></li>");
            if ((PageCount - this.getCurrent()) < 3) {
                loopPage(url, sb, PageCount - 6, this.getCurrent());
            } else {
                loopPage(url, sb, this.getCurrent() - 3, this.getCurrent());
            }
        }
        sb.append("<li class=\"active\"><span >");
        sb.append(this.getCurrent());
        sb.append("</span></li>\n");
        if ((PageCount - this.getCurrent()) <= 7) {
            loopPage(url, sb, this.getCurrent() + 1, PageCount + 1);
        } else {
            if ((this.getCurrent() + 3) < 7) {
                loopPage(url, sb, this.getCurrent() + 1, 8);
            } else {
                loopPage(url, sb, this.getCurrent() + 1, this.getCurrent() + 4);
            }
            sb.append("<li><span class=\"pagepoint\" href=\"javascript:void(0)\">...</span></li>");
            loopPage(url, sb, PageCount - 1, PageCount + 1);
        }
        if (this.getCurrent() < PageCount) {
            sb.append("<li><a href=\"");
            appendLink(url, sb, this.getCurrent() + 1);
            sb.append("\" class=\"pageNext pagePrev_button\">Next</a></li>\n");
        }
        if (this.getCurrent() == PageCount) {
            sb.append("<li><span class=\"pageNext pagePrev_button pageNext_disabled\">Next</span></li>\n");
        }
        sb.append("</ul>");
        sb.append(StrUtil.format("\t\t<div class=\"pageInfo\" style=\"display:none;\">共{0}项, {1}项/页</div>\n" +
                "\t\t<div style=\"clear:both;\"></div>", RecordCount, this.getSize()));
        sb.append("</nav>");
        setPageBar(sb.toString());
    }

    private String _path;

    private String getPath() {
        // TODO
        return _path;
    }

    public void setPath(String url) {
        _path = url;
    }


    //--------------------静态方法--------------------------------------------

    public static long GetPageCount(long recordCount, int pageSize) {
        long pcount = recordCount / pageSize;
        long imod = recordCount % pageSize;
        if (imod > 0) pcount = pcount + 1;

        return pcount;
    }


    /**
     * 计算分页的页码
     *
     * @param count    数据量
     * @param pageSize 每页数量
     * @return 总计多少页
     */
    public static int GetPageIndex(int count, int pageSize) {

        if (count == 0) return 1;

        int mod = count % pageSize;
        if (mod == 0) return count / pageSize;

        return count / pageSize + 1;
    }

    private static void loopPage(String url, StringBuilder sb, long startNo, long endNo) {
        for (long i = startNo; i < endNo; i++) {
            sb.append("<li><a href=\"");
            appendLink(url, sb, i);
            sb.append("\" class=\"pageNo\">");
            sb.append(i);
            sb.append("</a></li>\n");
        }
    }

    private static void loopHtmlPage(String url, StringBuilder sb, long startNo, long endNo) {
        for (long i = startNo; i < endNo; i++) {
            sb.append("<li><a href=\"");
            appendHtmlLink(url, sb, i);
            sb.append("\" class=\"pageNo\">");
            sb.append(i);
            sb.append("</a></li>\n");
        }
    }

    private static void appendLink(String url, StringBuilder sb, long pageNo) {
        sb.append(appendNo(url, pageNo));
    }

    private static void appendHtmlLink(String url, StringBuilder sb, long pageNo) {
        sb.append(appendHtmlNo(url, pageNo));
    }

    /// <summary>
    /// "/html/2010/11/22/195.html" => "/html/2010/11/22/195_2.html"
    /// </summary>
    /// <param name="srcUrl"></param>
    /// <param name="pageNumber"></param>
    /// <returns></returns>
    public static String appendHtmlNo(String srcUrl, long pageNumber) {

        if (StringUtils.isEmpty(srcUrl)) return srcUrl;
        if (pageNumber <= 1) return srcUrl;

        int lastDot = srcUrl.lastIndexOf('.');
        if (lastDot <= 0 || lastDot >= srcUrl.length() - 1) return srcUrl;

        String path = StrUtil.substring(srcUrl, 0, lastDot);
        String ext = StrUtil.substring(srcUrl, lastDot + 1, srcUrl.length() - lastDot - 1);

        return path + "_" + pageNumber + "." + ext;
    }

    private static final String urlSeparator = "/";

    private static String getExt(String url) {
        return "";
    }


    /// <summary>
    /// 在已有网址url后加上页码 post/list.aspx=>post/list/p6.aspx
    /// </summary>
    /// <param name="srcUrl">原始网址</param>
    /// <param name="pageNumber">页码</param>
    /// <returns></returns>
    public static String appendNo(String srcUrl, long pageNumber) {

        if (StrUtil.isNullOrEmpty(srcUrl)) return srcUrl;

        String url = srcUrl;

        // 查询字符串
        int qIndex = url.indexOf("?");
        String query = "";
        if (qIndex > 0) {
            url = StrUtil.substring(srcUrl, 0, qIndex);
            query = StrUtil.substring(srcUrl, qIndex, (srcUrl.length() - qIndex));
        }

        String ext = getExt(url);

        // 有扩展名
        if (ext.length() > 0) {
            url = StrUtil.trimEnd(url, ext);
        }

        String originalPage = getPageNumberLabel(url);
        if (originalPage.length() > 0) url = StrUtil.trimEnd(url, originalPage);

        if (pageNumber < 1) {
            return url + ext + query;
        } else {
            return url + urlSeparator + "p" + pageNumber + ext + query;
        }

    }

    private static String getPageNumberLabel(String url) {

        if (StrUtil.isNullOrEmpty(url)) return "";

        String[] arr = url.split(urlSeparator);
        if (arr.length < 2) return "";

        String end = arr[arr.length - 1];

        if (end.startsWith("p") && Convert.isInt(StrUtil.substring(end, 1))) {
            return urlSeparator + end;
        }

        return "";
    }

}
