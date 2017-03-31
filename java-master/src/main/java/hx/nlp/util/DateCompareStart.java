package hx.nlp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import com.mongodb.DBObject;

/**
 * @ClassName: DateCompareStart
 * @Description:
 * @author arvin.yang
 * @date 2016-11-8 上午10:45:34
 * 
 */
public class DateCompareStart {
	private TimeNormalizer normalizer;

	public DateCompareStart(String filePath) {
		normalizer = new TimeNormalizer(filePath);
	}

	public String dateToLong(String date, String rule, String date_rule)
			throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(rule);
		SimpleDateFormat df = new SimpleDateFormat(date_rule);// 设置日期格式
		return df.format(simpleDateFormat.parse(date));
	}

	public String currentDate(String date_rule) {
		SimpleDateFormat df = new SimpleDateFormat(date_rule);// 设置日期格式
		return df.format(new Date(System.currentTimeMillis()));
	}

	public boolean dateCompareDate(String str) {
		int y_index = str.indexOf("年");
		int m_index = str.indexOf("月");
		int d_index = str.indexOf("日");
		String dis_date = null;
		String cur_date = null;
		String date_rule;
		int compare;
		boolean flag = false;
		if (d_index > -1 && m_index > -1) {
			str = str.substring(0, d_index + 1);
			date_rule = "yyyyMMdd";
			try {
				dis_date = dateToLong(str, "yyyy年MM月dd日", date_rule);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			cur_date = currentDate(date_rule);
		}

		if (d_index == -1 && m_index > -1) {
			str = str.substring(0, m_index + 1);
			date_rule = "yyyyMM";
			try {
				dis_date = dateToLong(str, "yyyy年MM月", date_rule);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			cur_date = currentDate(date_rule);
		}

		if (d_index == -1 && m_index == -1 && y_index > -1) {
			str = str.substring(0, y_index + 1);
			date_rule = "yyyy";
			try {
				dis_date = dateToLong(str, "yyyy年", date_rule);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			cur_date = currentDate(date_rule);
		}
		compare = Integer.valueOf(dis_date) - Integer.valueOf(cur_date);
		if (compare >= 0)
			flag = true;
		return flag;
	}

	/**
	 * @Description:true：判断日期大于等于当前日期，没有分析出日期   false：判断日期小于当前日期
	 * @return boolean
	 */
	public boolean dateCompareStart(String str) {
		boolean flag = true;
		normalizer.parse(str);
		TimeUnit[] unit = normalizer.getTimeUnit();
		String norm;
		for (int i = 0; i < unit.length; i++) {
			System.out.println(unit[i]);
			norm = unit[i].Time_Norm;
			if (!norm.equals(""))
				flag = dateCompareDate(unit[i].Time_Norm);
		}
		return flag;
	}

	
	/**
	 * @Description:true：判断日期大于等于当前日期，没有分析出日期 false：判断日期小于当前日期
	 * @return boolean
	 */
	public boolean dateCompareStart(String str, DBObject timeDB) {
		boolean flag = true;
		normalizer.parse(str);
		TimeUnit[] unit = normalizer.getTimeUnit();
		String norm;
		Set<String> timeSet=new LinkedHashSet<String>();
		for (int i = 0; i < unit.length; i++) {
//			System.out.println(unit[i]);
			norm = unit[i].Time_Norm;
			if (!norm.equals("")){
				timeSet.add(unit[i].toString());
				flag = dateCompareDate(norm);
			}
		}
		timeDB.put("t", timeSet);
		return flag;
	}
	
//	public void strOptCompare(String str) {
//		String[] split1 = str.split("：");
//		if (split1.length > 1)
//			str = split1[1];
//		String[] split2 = str.split(":");
//		if (split2.length > 1)
//			str = split2[1];
//
//
//	}

	
	public static void main(String[] args) throws java.text.ParseException {
		DateCompareStart dateRegexTest = new DateCompareStart("data/model/TimeExp.m");
		String target = "1年前在重大资产重组项目终止去年3月份后";
		System.out.println(dateRegexTest.dateCompareStart(target));
	}
	
}
