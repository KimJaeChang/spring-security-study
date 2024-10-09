package kr.co.kjc.springsecuritystudy.global.config.common;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import java.util.Locale;
import org.hibernate.engine.jdbc.internal.FormatStyle;

public class P6spySqlFormatConfig implements MessageFormattingStrategy {

  private static String getServiceNameFromStackTrace() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

    for (StackTraceElement element : stackTrace) {
      // 예: "com.example.service" 패키지의 클래스를 서비스로 가정
      if (element.getClassName().contains("service")) {
        return element.getClassName() + "." + element.getMethodName();
      }
    }
    return "UnknownService";
  }

  @Override
  public String formatMessage(int connectionId, String now, long elapsed, String category,
      String prepared, String sql, String url) {

    String serviceInfo = getServiceNameFromStackTrace();

    sql = formatSql(category, sql);
    return serviceInfo + " | " + now + " | " + elapsed + "ms | " + category + " | connection " + connectionId + " | "
        + P6Util.singleLine(prepared) + sql;
  }

  private String formatSql(String category, String sql) {

    if (sql == null || sql.trim().equals("")) {
        return sql;
    }

    // Only format Statement, distinguish DDL And DML
    if (Category.STATEMENT.getName().equals(category)) {
      String tmpsql = sql.trim().toLowerCase(Locale.ROOT);
      if (tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith(
          "comment")) {
        sql = FormatStyle.DDL.getFormatter().format(sql);
      } else {
        sql = FormatStyle.BASIC.getFormatter().format(sql);
      }
      sql = " | \nformatSql(P6Spy sql,Hibernate format):" + sql;
    }

    return sql;
  }

}
