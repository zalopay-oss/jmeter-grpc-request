package vn.zalopay.benchmark.exception;

import java.util.Arrays;
import java.util.List;

public class ShortExceptionName {

  private static final List<String> list = Arrays.asList(
      "DEADLINE_EXCEEDED"
  );

  public static String shortest(String exception) {

    for (String shortName : list) {
      if (exception.contains(shortName)) {
        return shortName;
      }
    }

    return exception;
  }
}
