package tech.tgls.mms.auth.common.consts;

/**
 * Created by darcy on 2016/12/8.
 */
public interface Constants {

    /**
     * 会员状态 启用-1 停用-0
     */
    public static final int USER_STATUS_NO = 0;
    public static final int USER_STATUS_YES = 1;

    /**
     * 是否是普通会员 是-1 否-0
     */
    public static final int USER_IS_NORMAL_NO = 0;
    public static final int USER_IS_NORMAL_YES = 1;

    /**
     * 是否是燃气会员 是-1 否-0
     */
    public static final int USER_IS_GASUSER_NO = 0;
    public static final int USER_IS_GASUSER_YES = 1;

    /**
     * 是否通过港华员工数据匹对 是-1 否-0
     */
    public static final int USER_IS_EMPLOYEE_NO = 0;
    public static final int USER_IS_EMPLOYEE_YES = 1;

    /**
     * json返回值 成功-1 失败-0,- -1其他
     */
    public static final int RETURN_CODE_SUCCESS = 1;
    public static final int RETURN_CODE_FAIL = 0;
    public  static  final  int RETURN_CODE_OTHER=-1;
    /**
     * jv公司是否控股 是-1 否-0
     */
    public static final int JV_STATUS_YES = 1;
    public static final int JV_STATUS_NO = 0;

    /**
     *  权限错误码
     */
    public static final String PERM_ERROR_CODE = "000003";

    /**
     *  token异常错误码
     */
    public static final String TOKEN_ERROR_CODE = "000002";

    /**
     *  普通异常错误码
     */
    public static final String COMMON_ERROR_CODE = "000001";

    /**
     *  普通正常返回请求错误码
     */
    public static final String COMMON_SUCCESS_CODE = "000000";

    /**
     *  无法解析的错误统一用语
     */
    public static final String COMMON_ERROR_MSG = "请检查网络连接，或服务异常，请联系管理员！";

    /**
     * 积分是否有效 是-1 否-0
     */
    public static final int POINT_STATUS_YES = 1;
    public static final int POINT_STATUS_NO = 0;

    /**
     * 平安接口是否开启 关闭-（-1） 开启-0
     */
    public static final int PINGAN_ON = 0;
    public static final int PINGAN_OFF = -1;

    /**
     * 员工是否有效 是-1 否-0
     */
    public static final int EMPLOYEE_STATUS_YES = 1;
    public static final int EMPLOYEE_STATUS_NO = 0;

    public final static String mobileCookieName = "mobileUser";
    public final static String mobileTokenKey = "mobileToken";
    //微信用户
    public final static String wxCookieName = "wxUser";
    public final static String wxTokenKey = "wxToken";



    /**
     * 会员Id格式化长度
     */
    public static final String MEMBER_FORMAT = "%08d";



    /**
     * 抽奖类别 每天抽奖- 0 积分抽奖- 1 绑卡抽奖- 2
     */
    public static final int LOTTERY_TYPE_DAY = 0;
    public static final int LOTTERY_TYPE_POINT = 1;
    public static final int LOTTERY_TYPE_TIED_CARD = 2;

    /**
     * 设置签到积分规则起始积分，连续天数，上限积分
     */
    public  static  final  int START_INTEGRAL=5;
    public static  final  int DAYS=5;
    public static  final  int LIMIT_INTEGRAL=30;

    /**
     * 权限分类 0--只读  1--可修改
     */
    public  static  final  int PERM_READONLY=0;
    public static  final  int PERM_MODIFY=1;

    /**
     * 创建会员卡 0-普通创建 1-燃气户号创建
     */
    public static int CARD_RECORD_STATUS_COMMON = 0;
    public static int CARD_RECORD_STATUS_GAS = 1;


    /*奖品类型，1表示积分，2表示代金券，3表示实物*/
    public  static  int PRIZE_TYPE_INTEGRAL=1;
    public  static  int PRIZE_TYPE_CASH=2;
    public  static  int PRIZE_TYPE_MATERIAL=3;

    /**
     * 收获地址是否为默认地址 1表示默认收货地址，0表示普通收货地址
     */

    public  static  int DELIVERY_ADDRESS_YES_DEFAULT=1;
    public static  int DELIVERY_ADDRESS_NOT_DEFAULT=0;
    /**
     * 地址是否可用状态 -1表示删除，1表示可用状态
     */

    public  static  int DELIVERY_ADDRESS_STATUS_DEL=-1;
    public static  int DELIVERY_ADDRESS_STATUS_NORMAL=1;

    /**
     * 是否发货1-是 0-否
     */
    int IS_DELIVERY_YES = 1;
    int IS_DELIVERY_NO = 0;

    /**
     * 奖品是否可用-1不可用，1可用
     */
    public  static  int PRIZE_STATUS_YES=1;
    public static  int PRIZE_STATUS_NO=-1;

    /**
     * 会员卡是否废弃,可用1
     */
    int CARD_STATUS_NO=-1;
    int CARD_STATUS_YES=1;

    /**
     * 模板是否可用状态 -1表示删除，1表示可用状态
     */
    int WX_MP_TEMPLATE_STATUS_NO=-1;
    int WX_MP_TEMPLATE_STATUS_YES=1;

    /**
     * 抽奖状态 -1表示已失效  1且截止日期大于当前日期表示进行中  1且截止日期小于当前日期表示已过期
     */
    String progress = "进行";
    String invalid = "已失效";
    String overdue = "已过期";

    String BIND_CARD_TEMPLATE_MSG_SHORT_ID = "OPENTM407523138";

    /**
     * 广告位是否可用状态 -1表示删除，1表示可用状态
     */
    int WX_ADVERT_SPACE_STATUS_NO=-1;
    int WX_ADVERT_SPACE_STATUS_YES=1;

    /**
     * 广告是否可用状态 -1表示删除，1表示可用状态
     */
    int WX_ADVERT_STATUS_NO=-1;
    int WX_ADVERT_STATUS_YES=1;

    /**
     * 验证码code标示
     */
    String KAPTCHA_CODE = "kaptchaCode";

    /*
    短信验证码30秒提示
     */
    public static final String TIP_IN_30S = "30秒只能发送一条，请稍后再试。";

    /*
    云片30秒内重复发送返回的错误码
     */
    public static final int COED_RESEND_IN_30S = 33;
}
