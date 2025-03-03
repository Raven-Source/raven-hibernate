package org.raven.hibernate.sql;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SqlFunctionConditionCleaner {

    // 正则表达式：匹配 WHERE 或 HAVING 中的 = 1 或 = true
    private static final String regex = "((json_contains|json_overlaps|has|hasAll|hasAny)\\(.*?\\))\\s*=\\s*(1|TRUE)";

    public String optimize(String sql) {

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        // 创建 Matcher 对象来执行替换操作
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            String optimizedSql = matcher.replaceAll("$1");

            if (log.isDebugEnabled()) {
                BasicFormatterImpl formatter = new BasicFormatterImpl();
                log.info("original sql : {}", formatter.format(sql));
                log.info("optimized sql : {}", formatter.format(optimizedSql));
            }
        }

        return sql;
    }
}
