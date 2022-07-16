package vn.zalopay.benchmark.util;

/**
 * <b>Exception print utility class</b>
 *
 * <p>reference:
 * https://github.com/yl-yue/yue-library/blob/j11.2.6.0/yue-library-base/src/main/java/ai/yue/library/base/util/ExceptionUtils.java
 *
 * @author ylyue
 * @since 2018-9-9
 */
public class ExceptionUtils {
    private ExceptionUtils() {
        throw new IllegalStateException("Utility class");
    }

    @SuppressWarnings("unchecked")
    private static synchronized <T> T getPrintException(Throwable e, Integer line) {
        T msg = (T) new StringBuffer();
        if (e == null) {
            ((StringBuffer) msg).append("The stack trace is null");
            return msg;
        } else {
            ((StringBuffer) msg).append(e).append("\n");
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
            ((StringBuffer) msg)
                    .append("\tat ")
                    .append(className)
                    .append(".")
                    .append(methodName)
                    .append("(")
                    .append(fileName)
                    .append(":")
                    .append(lineNumber)
                    .append(")\n");
        }

        return msg;
    }

    /**
     * Get print abnormal content
     *
     * @param e exception
     * @param line Print the number of rows
     * @return exception data
     */
    public static synchronized String getPrintExceptionToStr(Throwable e, Integer line) {
        StringBuffer printException = getPrintException(e, line);
        return printException.toString();
    }
}
