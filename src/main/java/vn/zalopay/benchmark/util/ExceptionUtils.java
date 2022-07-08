package vn.zalopay.benchmark.util;

import com.alibaba.fastjson.JSONObject;

/**
 * <b>异常打印工具类</b>
 * <p>reference: https://github.com/yl-yue/yue-library/blob/j11.2.6.0/yue-library-base/src/main/java/ai/yue/library/base/util/ExceptionUtils.java</p>
 *
 * @author	ylyue
 * @since	2018年9月9日
 */
public class ExceptionUtils {

	@SuppressWarnings("unchecked")
	private synchronized static <T> T getPrintException(Throwable e, Integer line, ExceptionConvertEnum exceptionConvertEnum) {
		T msg = null;
		if (exceptionConvertEnum == ExceptionConvertEnum.JSONObject) {
			msg = (T) new JSONObject(true);
			if (e == null) {
				((JSONObject) msg).put("0", "The stack trace is null");
				return msg;
			} else {
				((JSONObject) msg).put("0", e.toString());
			}
		} else if (exceptionConvertEnum == ExceptionConvertEnum.StringBuffer) {
			msg = (T) new StringBuffer();
			if (e == null) {
				((StringBuffer) msg).append("The stack trace is null");
				return msg;
			} else {
				((StringBuffer) msg).append(e + "\n");
			}
		}
		
		StackTraceElement[] stackTraceElementArray = e.getStackTrace();
		int maxLine = stackTraceElementArray.length;
		if (line == null) {
			line = maxLine;
		} else {
			line = line > maxLine ? maxLine : line;
		}
		
		for (int i = 0; i < line; i++) {
			StackTraceElement stackTraceElement = stackTraceElementArray[i];
			String fileName = stackTraceElement.getFileName();
			String className = stackTraceElement.getClassName();
			String methodName = stackTraceElement.getMethodName();
			int lineNumber = stackTraceElement.getLineNumber();
			
			if (exceptionConvertEnum == ExceptionConvertEnum.JSONObject) {
				((JSONObject) msg).put(i + 1 + "", "　　at " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")");
			}
			
			if (exceptionConvertEnum == ExceptionConvertEnum.StringBuffer) {
				((StringBuffer) msg).append("\tat " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")\n");
			}
		}
		
		return msg;
	}
	
	/**
	 * 获得打印异常内容
	 * <p>默认只打印关键的5行内容
	 * 
	 * @param e 异常
	 * @return 异常内容
	 */
	public synchronized static String getPrintExceptionToStr(Throwable e) {
		return getPrintExceptionToStr(e, 4);
	}
	
	/**
	 * 获得打印异常内容
	 * 
	 * @param e    异常
	 * @param line 打印行数
	 * @return 异常内容
	 */
	public synchronized static String getPrintExceptionToStr(Throwable e, Integer line) {
		StringBuffer printException = getPrintException(e, line, ExceptionConvertEnum.StringBuffer);
		return printException.toString();
	}
	
	/**
	 * 获得打印异常内容
	 * <p>默认只打印关键的5行内容
	 * 
	 * @param e    异常
	 * @return 异常内容
	 */
	public synchronized static JSONObject getPrintExceptionToJson(Throwable e) {
		return getPrintExceptionToJson(e, 4);
	}
	
	/**
	 * 获得打印异常内容
	 * 
	 * @param e    异常
	 * @param line 打印行数
	 * @return 异常内容
	 */
	public synchronized static JSONObject getPrintExceptionToJson(Throwable e, Integer line) {
		return getPrintException(e, line, ExceptionConvertEnum.JSONObject);
	}
	
	/**
	 * 获得打印异常内容
	 * <p>默认只打印关键的5行内容
	 * 
	 * @param e    异常
	 */
	public synchronized static void printException(Throwable e) {
		printException(e, 4);
	}
	
	/**
	 * 获得打印异常内容
	 * 
	 * @param e    异常
	 * @param line 打印行数
	 */
	public synchronized static void printException(Throwable e, Integer line) {
		System.err.print(getPrintExceptionToStr(e, line));
	}
	
	private enum ExceptionConvertEnum {
		JSONObject, StringBuffer;
	}
	
}
