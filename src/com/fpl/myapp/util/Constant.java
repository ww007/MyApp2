package com.fpl.myapp.util;

/**
 * @author ww
 *
 */
public interface Constant {
	/**
	 * 二维码请求的type
	 */
	public static final String REQUEST_SCAN_TYPE = "type";
	/**
	 * 普通类型，扫完即关闭
	 */
	public static final int REQUEST_SCAN_TYPE_COMMON = 0;
	/**
	 * 服务商登记类型，扫描
	 */
	public static final int REQUEST_SCAN_TYPE_REGIST = 1;

	/**
	 * 扫描类型 条形码或者二维码：REQUEST_SCAN_MODE_ALL_MODE 条形码：
	 * REQUEST_SCAN_MODE_BARCODE_MODE 二维码：REQUEST_SCAN_MODE_QRCODE_MODE
	 *
	 */
	public static final String REQUEST_SCAN_MODE = "ScanMode";
	/**
	 * 条形码： REQUEST_SCAN_MODE_BARCODE_MODE
	 */
	public static final int REQUEST_SCAN_MODE_BARCODE_MODE = 0X100;
	/**
	 * 二维码：REQUEST_SCAN_MODE_ALL_MODE
	 */
	public static final int REQUEST_SCAN_MODE_QRCODE_MODE = 0X200;
	/**
	 * 条形码或者二维码：REQUEST_SCAN_MODE_ALL_MODE
	 */
	public static final int REQUEST_SCAN_MODE_ALL_MODE = 0X300;

	public static final String STUDENT_URL = "http://192.168.0.65:8080/sys_tccx/phone/studentInfo/getInfo.action";
	public static final String ITEM_URL = "http://192.168.0.65:8080/sys_tccx/phone/item/getItems.action";
	public static final String STUDENT_ITEM_URL = "http://192.168.0.65:8080/sys_tccx/phone/StuItem/getStuItems.action";
	public static final String STUDENT_ITEM_SAVE_URL = "http://192.168.0.65:8080/sys_tccx/phone/StuItem/saveStuItem.action";
	public static final String ROUND_RESULT_SAVE_URL = "/sys_tccx/phone/RoundResult/saveRoundResult.action";

	public static final String TOKEN = "fpl@*!";
	public static final String GET = "GET";
	public static final String POST = "POST";

	public static final int HEIGHT_WEIGHT = 1;// 身高体重
	public static final String HEIGHT_CODE = "E01";// 身高
	public static final String WEIGHT_CODE = "E02";// 体重

	public static final int VITAL_CAPACITY = 2;// 肺活量
	// public static final String VITAL_CAPACITY_CODE = 2;// 肺活量

	public static final int BROAD_JUMP = 3;// 立定跳远
	public static final int JUMP_HEIGHT = 4;// 摸高
	public static final int PUSH_UP = 5;// 俯卧撑
	public static final int SIT_UP = 6;// 仰卧起坐
	public static final int SIT_AND_REACH = 7;// 坐位体前屈
	public static final int ROPE_SKIPPING = 8;// 跳绳
	public static final int VISION = 9;// 视力
	public static final int PULL_UP = 10;// 引体向上
	public static final int INFRARED_BALL = 11;// 红外实心球
	public static final int MIDDLE_RACE = 12;// 中长跑
	public static final int VOLLEYBALL = 13;// 排球
	public static final int BASKETBALL_SKILL = 14;// 篮球运球
	public static final int SHUTTLE_RUN = 15;// 折返跑
	public static final int WALKING1500 = 16;// 1500米健步走
	public static final int WALKING2000 = 17;// 2000米健步走
	public static final int RUN50 = 18;// 50米跑
	public static final int FOOTBALL_SKILL = 19;// 足球运球
	public static final int KICKING_SHUTTLECOCK = 20;// 踢毽子
	public static final int SWIM = 21;// 游泳
	public static final String runGradeInput = "11111";

}
