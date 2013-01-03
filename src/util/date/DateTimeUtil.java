package util.date;

import java.sql.Timestamp;
import java.util.Calendar;

public class DateTimeUtil {

	public static  String getQueryTimeStr(Calendar cal) {
		// 返回用于数据库查询的日期时间字符串
			return 	cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-" +
					+cal.get(Calendar.DAY_OF_MONTH)+" "+cal.get(Calendar.HOUR_OF_DAY)+
					":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
		
	}
	
	/*
	 * 通过日期字符串返回calendar对象
	 * 日期格式2012-02-12R
	 */
	public static  Calendar getCalendarByStr(String  day) {
		// 返回用于数据库查询的日期时间字符串
		Calendar cal = Calendar.getInstance();
		String [] dayStrs = day.split("-");
		int year = Integer.parseInt(dayStrs[0]);
		int month = Integer.parseInt(dayStrs[1])-1;
		int date = Integer.parseInt(dayStrs[2])-1;
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, date);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.MILLISECOND,0);
		cal.set(Calendar.SECOND, 0);
		return cal;
	}
	
	public static  String getQueryDateStr(Calendar cal) {
		// 返回用于数据库查询的日期字符串
			return 	cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-" +
					cal.get(Calendar.DAY_OF_MONTH);
		
	}
	
	public static  String getQueryDateStrWithZero(Calendar cal) {
		// 返回用于数据库查询的日期字符串
		 String monthStr =  cal.get(Calendar.MONTH)+1<10?"0"+(cal.get(Calendar.MONTH)+1):(cal.get(Calendar.MONTH)+1)+"";
		    String dateStr =  cal.get(Calendar.DAY_OF_MONTH)<10?"0"+cal.get(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH)+"";
			return 	cal.get(Calendar.YEAR)+"-"+monthStr+"-"+dateStr;
		
	}

	public static  String getQueryDateTimeWithZero(Calendar cal) {
		// 返回用于数据库查询的日期时间字符串（例如：2011-01-03 00:00:00）
		 String monthStr =  cal.get(Calendar.MONTH)+1<10?"0"+(cal.get(Calendar.MONTH)+1):(cal.get(Calendar.MONTH)+1)+"";
		    String dateStr =  cal.get(Calendar.DAY_OF_MONTH)<10?"0"+cal.get(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH)+"";
			return 	cal.get(Calendar.YEAR)+"-"+monthStr+"-"+dateStr+" "+cal.get(Calendar.HOUR_OF_DAY)+
			":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
		
	}
	
	public static  String getDateStrEight(Calendar cal) {
		// 返回用8位的日期字符串
		    String monthStr =  cal.get(Calendar.MONTH)+1<10?"0"+(cal.get(Calendar.MONTH)+1):(cal.get(Calendar.MONTH)+1)+"";
		    String dateStr =  cal.get(Calendar.DAY_OF_MONTH)<10?"0"+cal.get(Calendar.DAY_OF_MONTH):cal.get(Calendar.DAY_OF_MONTH)+"";
			return 	cal.get(Calendar.YEAR)+monthStr+dateStr;
		
	}
	
	public static  String getDateStrEightYesterday() {
		// 返回昨天8位的日期字符串
		Calendar cal = Calendar.getInstance();
		cal = getYesterDay(cal);
			return 	getDateStrEight(cal);
		
	}

	public static java.util.Date parseTimeStr(String fromTimeStr) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String args[])
		{
		String dateStr = "2012-02-12";
		Calendar cal = getCalendarByStr(dateStr);
		System.out.println(cal.toString());
		
	//	Calendar cal = Calendar.getInstance();
		
	//	System.out.println(getDateStrEightYesterday());
/*		
	String str;
	try {
		str = URLDecoder.decode("http://www.baidu.com/s?wd=%D1%A7%C0%FA%B5%CD%D4%F5%C3%B4%D5%D2%B9%A4%D7%F7&rsp=7&oq=%D4%DA%CE%E4%BA%BA%D5%D2%B9%A4%D7%F7&f=1&tn=sitehao123&rsv_ers=xn1","gb2312");
	
	//str.toCharArray();
	System.out.println("str:"+str);
	String str1;
		str1 = new String(str.getBytes("gbk"), "utf-8");
		System.out.println("str:"+str1);
		
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/

		}
	
	public static Calendar getYesterDay(Calendar cal)
	{
		long result =  cal.getTimeInMillis()-24*3600*1000;
		cal.setTimeInMillis(result);
		return cal;
	}
	
	public static Calendar getNextDay(Calendar cal)
	{
		long result =  cal.getTimeInMillis()+24*3600*1000;
		cal.setTimeInMillis(result);
		return cal;
	}

	/**
	 * @param beginWorkDay
	 * @return
	 */
	public static String getYear2Now(String beginWorkDay) {
		// TODO Auto-generated method stub
		if(beginWorkDay.indexOf("/")<0)
		return null;
		else{
			try{
			String year = beginWorkDay.split("/")[0];
			if(year!=null)
			{
				int yearNum = Integer.parseInt(year); 
				int currentYear = Calendar.getInstance().get(Calendar.YEAR)-yearNum;
				if(currentYear>0)
				return currentYear +"";
			}
			return null;
			}catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * @param instance
	 * @return
	 */
	public static Object getSqlDate(Calendar instance) {
		// TODO Auto-generated method stub
		return new java.sql.Timestamp(instance.getTimeInMillis());
	}

	/**
	 * @param lastQueryTime
	 * @return
	 */
	public static String getTimeStampQueryStr(Timestamp lastQueryTime) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(lastQueryTime.getTime());
		int nanos = lastQueryTime.getNanos()/1000;
		return getQueryDateTimeWithZero(cal)+"."+nanos;
		
	}
}
