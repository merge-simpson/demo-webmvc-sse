package example.common.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
    public static String stackTraceToString(Throwable e) {
        try (
                StringWriter sw = new StringWriter();
                PrintWriter ps = new PrintWriter(sw)
        ) {
            e.printStackTrace(ps);
            return sw.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
