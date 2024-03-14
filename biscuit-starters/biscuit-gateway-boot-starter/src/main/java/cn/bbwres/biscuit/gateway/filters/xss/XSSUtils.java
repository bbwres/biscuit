package cn.bbwres.biscuit.gateway.filters.xss;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * xss 过滤工具
 * @author zhanglinfeng
 */
public final class XSSUtils {

    private XSSUtils() {
    }

    private static final Pattern[] PATTERNS = {
            // Avoid anything in a <script> type of expression
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            // Avoid anything in a src='...' type of expression
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Remove any lonesome </script> tag
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            // Avoid anything in a <iframe> type of expression
            Pattern.compile("<iframe>(.*?)</iframe>", Pattern.CASE_INSENSITIVE),
            // Remove any lonesome <script ...> tag
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Remove any lonesome <img ...> tag
            Pattern.compile("<img(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid eval(...) expressions
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid expression(...) expressions
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid javascript:... expressions
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            // Avoid vbscript:... expressions
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            // Avoid onload= expressions
            Pattern.compile("on(load|error|mouseover|submit|reset|focus|click)(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };


    /**
     * 过滤xss
     * @param value
     * @return
     */
    public static String stripXSS(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        for (Pattern scriptPattern : PATTERNS) {
            value = scriptPattern.matcher(value).replaceAll("");
        }
        return value;
    }

}
