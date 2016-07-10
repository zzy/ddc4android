package com.moershizhi.ddc.util;

import android.graphics.Color;
import android.os.Build;
import android.view.ViewGroup.LayoutParams;

public class CONSTANT {
	
	// 启动数据
	public static final int SDK_VERSION = Build.VERSION.SDK_INT;
    public static int APK_VERSION = 0;
    
    // 屏幕大小、密度，以及分辨率
	public static int heightPixels;
	public static int widthPixels;
	
	public static float density;
    
    public static int screenHeight;
    public static int screenWidth;
	
    public static boolean is1280x800;
    
    // gesture
    public String CURRENT_MOTION;
    public final static int MOTION_X = 50;
    public final static int MOTION_Y = 100;
    public final static String MOTION_UP = "向上手势";
    public final static String MOTION_DOWN = "向下手势";
    public final static String MOTION_LESS = "静止手势";

	// 用户数据
    public static boolean ADD_RES = false; //增加    
    public static boolean QIE_PING = false; // 分屏
    
    public static String SERVER = "ouds.biz";
    public static String HTTP_PORT = "80";
    public static boolean USE_SSL = false;
    public static String HTTPS_PORT = "8443";
    public static String ROOT_PATH = "blog";

	public static String SID = "";
	public static String USER_ID = "";
	public static String LOGIN_ID = "";
	public static String USERNAME = "";
	public static String USER_ROLE = "";
	public static String USER_ORG = "";
	public static String USER_AREA = "";

	// 回退键判断类
	public static final String BACK_HOLD = "com.hwadee.ssp.util.LoginActivity";
	
	// 通用样式数据
    public static final int[] bg = {Color.WHITE, Color.GRAY};
    public static int bgIndex = 0;
    
    // 表单格式
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    // 数据分隔线
    public static final String BOUNDARY = "Ouds --- http://dingdongquan.com --- http://chuncu.com --";
    public static final String BOUNDARY_BEGIN = "\r\n----------- BEGIN data from android -----------\r\n";
    public static final String BOUNDARY_END = "\r\n----------- END data from android -----------\r\n";
    public static final String R_N = "\r\n";
    
    // 超时设定
    public static final int DELAYED_TIME = 3 * 1000;
    public static final int CONNECTION_TIMEOUT = 6 * 1000;
    public static final int SO_TIMEOUT = 6 * 1000;
    public static final int CODE_200 = 200;
    public static final int CODE_404 = 404;
    public static final int CODE_500 = 500;
    
    // LayoutParams
    public static final LayoutParams MATCH_PARENT = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    public static final LayoutParams MATCH_PARENT_WRAP_CONTENT = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    public static final LayoutParams WRAP_CONTENT_MATCH_PARENT = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    public static final LayoutParams WRAP_CONTENT = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
	//配置
	public static final String XIN_JIN = "新津县";
	public static final String PHOTO_DIR = "图库";
	public static final String CONFIG_FILE = "网络配置.txt";
	public static final String ERROR_MSG = "网络配置错误！";
	 
	// 路径 -1; 全部 0; 泵站 1; 水闸 2; 堤防 3; 滚水堰 4; 排涝站 5; 桥梁 6
	public static int AREA = -2;
	public static int MAP_PATH = -1;
	public static int ALL_RES = 0;
	public static int BENG_ZHAN = 1;
	public static int SHUI_ZHA = 2;
	public static int DI_FANG = 3;
	public static int GUN_SHUI_YAN = 4;
	public static int PAI_LAO_ZHAN = 5;
	public static int QIAO_LIANG = 6;
	
	//基本值
	public static final int PAGE_CAPACITY = 10;
	public static final String WAVY = "~";
	public static final String SUI = "岁";
	public static final String HU = "户";
	public static final String REN = "人";
	public static final String TIAO = "条";
	public static final String CI = "次";
	public static final String NIAN = "年";
	public static final String YI_SHANG = "以上";
	public static final String RECOMMAND_TIMES = "推荐次数：";
	
	// gender
	public static final String MALE = "男";
	public static final String MALE_CODE = "198E351756E847138FC6DE75A0A0C549";
	public static final String FEMALE = "女";
	public static final String FEMALE_CODE = "2686C204CA1E47B2AFDDBB0304B62CA5";
	
	// cert type
	public static final String ID_CODE = "A3D3D465B86345D9B5C3072A54954998";
	public static final String MILITARY_CODE = "D227E45AEB334E98BFF36DA068EC01B8";
	public static final String PASSPORT_CODE = "CBEB7E8BCFD7480F82729356681B80FF";
	public static final String CERT_CODE = "6F56823B40704C6A98AA180E05E1D48E";
	
	// person status
	public static final String PS_YJ_CODE = "AA3238B23BE141B291A1D0BBDB5B47E1";
    public static final String PS_QC_CODE = "11BBA3AAF7804F9198966518AD5DB1D7";
    public static final String PS_SW_CODE = "58BB3029A79F4237B9BD381D489345B6";
    public static final String PS_CODE = "Ab3238B23BE141B291A1D0BBDB5B47E1";
    
    // political
    public static final String POLITICAL_CODE = "EE410A802EC540C795CE88CC6DA82AA8";
    public static final String POLITICAL_T_CODE = "C56A566508FD46F39202D22AC7F1F86F";
    public static final String POLITICAL_D_CODE = "0A0BEFDEB534469B972FC47591B681B7";
    public static final String POLITICAL_M_CODE = "8D144DD4330D42CE8BA20DB18E65C179";
    
    // wen wei
    public static final String WW_CODE = "C5823F04E6FC4250AE1A375411623AF6";
    public static final String WW_XJ_CODE = "03183B679ADF4C989E298AD91CA8155D";
    public static final String WW_XM_CODE = "C5823F04E6FC4250AE1A375411623AF7";
    
    // gong zhong
    public static final String GZ_QZ = "C774FA60F6F74001B7070C3B56844C8E";
    public static final String GZ_JZ = "B70CDF25073A4E04A86D372662F79712";
    
    // xue li
    public static final String XL_QT = "091";
    public static final String XL_BS = "09";
    public static final String XL_SS = "08";
    public static final String XL_BK = "07";
    public static final String XL_DZ = "06";
    public static final String XL_ZZ = "05";
    public static final String XL_GJ = "04";
    public static final String XL_ZJ = "03";
    public static final String XL_GZ = "02";
    public static final String XL_CZ = "01";
    
    // 信息提示
    public static final String MESSAGE = "MESSAGE";


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
