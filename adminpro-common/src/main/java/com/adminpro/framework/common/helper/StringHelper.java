package com.adminpro.framework.common.helper;

import com.adminpro.framework.common.helper.text.StrFormatter;
import com.adminpro.web.BaseConstants;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字串工具类
 */
public class StringHelper extends StringUtils {

    private static final Logger logger = LoggerFactory.getLogger(StringHelper.class);

    public static final String HTMLELEMENT_REGEX = "<[^<|^>]*>";
    public static final String INT_REGEX = "^[-\\+]?[\\d]*$";
    public static final String DOUBLE_REGEX = "^[-\\+]?[.\\d]*$";
    /**
     * 空字符串
     */
    private static final String NULLSTR = "";

    /**
     * 下划线
     */
    private static final char SEPARATOR = '_';

    /**
     * 将一个Double转为int的String，将省略小数点后面的值
     *
     * @param d
     * @return
     */
    public static String doubleToIntString(Double d) {
        int value = ((Double) d).intValue();
        return String.valueOf(value);
    }

    /**
     * 检查浮点数
     *
     * @param num
     * @param type "0+":非负浮点数 "+":正浮点数 "-0":非正浮点数 "-":负浮点数 "":浮点数
     * @return
     */
    public static boolean checkFloat(String num, String type) {
        String eL = "";
        if ("0+".equals(type)) {
            eL = "^\\d+(\\.\\d+)?$";// 非负浮点数
        } else if ("+".equals(type)) {
            eL = "^((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*))$";// 正浮点数
        } else if ("-0".equals(type)) {
            eL = "^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$";// 非正浮点数
        } else if ("-".equals(type)) {
            eL = "^(-((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*)))$";// 负浮点数
        } else {
            eL = "^(-?\\d+)(\\.\\d+)?$";// 浮点数
        }
        Pattern p = Pattern.compile(eL);
        Matcher m = p.matcher(num);
        boolean b = m.matches();
        return b;
    }

    public static String formatNum(String num, Boolean kBool) {
        StringBuffer sb = new StringBuffer();
        if (!StringUtils.isNumeric(num)) {
            return "0";
        }
        if (kBool == null) {
            kBool = false;
        }

        BigDecimal b0 = new BigDecimal("1000");
        BigDecimal b1 = new BigDecimal("10000");
        BigDecimal b2 = new BigDecimal("100000000");
        BigDecimal b3 = new BigDecimal(num);

        String formatNumStr = "";
        String nuit = "";

        // 以千为单位处理
        if (kBool) {
            if (b3.compareTo(b0) == 0 || b3.compareTo(b0) == 1) {
                return "999+";
            }
            return num;
        }

        // 以万为单位处理
        if (b3.compareTo(b1) == -1) {
            sb.append(b3.toString());
        } else if ((b3.compareTo(b1) == 0 && b3.compareTo(b1) == 1)
                || b3.compareTo(b2) == -1) {
            formatNumStr = b3.divide(b1).toString();
            nuit = "万";
        } else if (b3.compareTo(b2) == 0 || b3.compareTo(b2) == 1) {
            formatNumStr = b3.divide(b2).toString();
            nuit = "亿";
        }
        if (!"".equals(formatNumStr)) {
            int i = formatNumStr.indexOf(".");
            if (i == -1) {
                sb.append(formatNumStr).append(nuit);
            } else {
                i = i + 1;
                String v = formatNumStr.substring(i, i + 1);
                if (!v.equals("0")) {
                    sb.append(formatNumStr.substring(0, i + 1)).append(nuit);
                } else {
                    sb.append(formatNumStr.substring(0, i - 1)).append(nuit);
                }
            }
        }
        if (sb.length() == 0) {
            return "0";
        }
        return sb.toString();
    }

    /**
     * MD5加密方法
     *
     * @param str String
     * @return String
     */
    public static String md5(String str) {
        return md5(str, true);
    }

    public static String md5(String str, boolean zero) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return null;
        }
        byte[] resultByte = messageDigest.digest(str.getBytes());
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < resultByte.length; ++i) {
            int v = 0xFF & resultByte[i];
            if (v < 16 && zero) {
                result.append("0");
            }
            result.append(Integer.toHexString(v));
        }
        return result.toString();
    }

    /**
     * 验证Email地址是否有效
     *
     * @param sEmail
     * @return
     */
    public static boolean validEmail(String sEmail) {
        String pattern = "^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return sEmail.matches(pattern);
    }

    /**
     * 验证字符最大长度
     *
     * @param str
     * @return
     */
    public static boolean validMaxLen(String str, int length) {
        if (str == null || "".equals(str)) {
            return false;
        }
        if (str.length() > length) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 验证字符最小长度
     *
     * @param str
     * @return
     */
    public static boolean validMinLen(String str, int length) {
        if (str == null || "".equals(str)) {
            return false;
        }
        if (str.length() < length) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 验证两个字符串是否相等且不能为空
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null || "".equals(str1) || str2 == null || "".equals(str2)) {
            return false;
        }
        return str1.equals(str2);
    }

    /**
     * 将字串转为数字
     *
     * @param str
     * @param checked 如果为treu格式不正确抛出异常
     * @return
     */
    public static int toInt(String str, boolean checked) {
        int value = 0;
        if (str == null || "".equals(str)) {
            return 0;
        }
        try {
            value = Integer.parseInt(str);
        } catch (Exception ex) {
            if (checked) {
                throw new RuntimeException("整型数字格式不正确");
            } else {
                return 0;
            }
        }
        return value;
    }


    /**
     * 将object转为数字
     *
     * @param obj     需要转object的对象
     * @param checked 如果为true格式不正确抛出异常
     * @return
     */
    public static int toInt(Object obj, boolean checked) {
        int value = 0;
        if (obj == null) {
            return 0;
        }
        try {
            value = Integer.parseInt(obj.toString());
        } catch (Exception ex) {
            if (checked) {
                throw new RuntimeException("整型数字格式不正确");
            } else {
                return 0;
            }
        }
        return value;
    }

    /**
     * 将一个字串转为int，如果无空，则返回默认值
     *
     * @param str          要转换的数字字串
     * @param defaultValue 默认值
     * @return
     */
    public static Integer toInt(String str, Integer defaultValue) {
        Integer value = defaultValue;
        if (str == null || "".equals(str)) {
            return defaultValue;
        }
        try {
            value = Integer.parseInt(str);
        } catch (Exception ex) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 将字符型转为Int型
     *
     * @param str
     * @return
     */
    @Deprecated
    public static int toInt(String str) {
        int value = 0;
        if (str == null || "".equals(str)) {
            return 0;
        }
        try {
            value = Integer.parseInt(str);
        } catch (Exception ex) {
            value = 0;
            ex.printStackTrace();
        }
        return value;
    }

    @Deprecated
    public static Double toDouble(String str) {
        Double value = 0d;
        if (str == null || "".equals(str)) {
            return 0d;
        }
        try {
            value = Double.valueOf(str);
        } catch (Exception ex) {
            value = 0d;
            // ex.printStackTrace();
        }
        return value;
    }

    /**
     * 将一个字串转为double
     *
     * @param str
     * @param checked 如果为treu格式不正确抛出异常
     * @return
     */
    public static Double toDouble(String str, boolean checked) {
        Double value = 0d;
        if (str == null || "".equals(str)) {
            return 0d;
        }
        try {
            value = Double.valueOf(str);
        } catch (Exception ex) {
            if (checked) {
                throw new RuntimeException("数字格式不正确");
            } else {
                return 0D;
            }
        }
        return value;
    }

    /**
     * 将一个object转为double
     * 如果object 为 null 则返回0；
     *
     * @param obj     需要转成Double的对象
     * @param checked 如果为true格式不正确抛出异常
     * @return
     */
    public static Double toDouble(Object obj, boolean checked) {
        Double value = 0d;
        if (obj == null) {
            if (checked) {
                throw new RuntimeException("数字格式不正确");
            } else {
                return 0D;
            }
        }
        try {
            value = Double.valueOf(obj.toString());
        } catch (Exception ex) {
            if (checked) {
                throw new RuntimeException("数字格式不正确");
            } else {
                return 0D;
            }
        }
        return value;
    }

    public static Double toDouble(String str, Double defaultValue) {
        Double value = defaultValue;
        if (str == null || "".equals(str)) {
            return 0d;
        }
        try {
            value = Double.valueOf(str);
        } catch (Exception ex) {
            ex.printStackTrace();
            value = defaultValue;
        }
        return value;
    }


    /**
     * 将一个list转为以split分隔的string
     *
     * @param list
     * @param split
     * @return
     */
    public static String listToString(List list, String split) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Object obj : list) {
            if (sb.length() != 0) {
                sb.append(split);
            }
            sb.append(obj.toString());
        }
        return sb.toString();
    }

    /**
     * 得到WEB-INF的绝对路径
     *
     * @return
     */
    public static String getWebInfPath() {
        String filePath = Thread.currentThread().getContextClassLoader()
                .getResource("").toString();
        if (filePath.toLowerCase().indexOf("file:") > -1) {
            filePath = filePath.substring(6, filePath.length());
        }
        if (filePath.toLowerCase().indexOf("classes") > -1) {
            filePath = filePath.replaceAll("/classes", "");
        }
        if (System.getProperty("os.name").toLowerCase().indexOf("window") < 0) {
            filePath = "/" + filePath;
        }
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        return filePath;
    }

    /**
     * 得到根目录绝对路径(不包含WEB-INF)
     *
     * @return
     */
    public static String getRootPath() {

        //测试模式下由request获取根目录，为了支撑单元测试
        //此时的request是spring test 框架mock的request
        if (!(WebHelper.getServletContext() == null)) {
            String path = WebHelper.getServletContext().getRealPath("");
            return path;
        }

        String filePath = StringUtils.class.getResource("").toString();

        int index = filePath.indexOf("WEB-INF");
        if (index == -1) {
            index = filePath.indexOf("build");
        }

        if (index == -1) {
            index = filePath.indexOf("bin");
        }

        if (index != -1) {
            filePath = filePath.substring(0, index);
        }

        if (filePath.startsWith("jar")) {
            // 当class文件在jar文件中时，返回”jar:file:/F:/ …”样的路径
            filePath = filePath.substring(10);
        } else if (filePath.startsWith("file")) {
            // 当class文件在jar文件中时，返回”file:/F:/ …”样的路径
            filePath = filePath.substring(6);
        } else if (filePath.startsWith("zip:")) {
            // weblogic发布war的情况
            filePath = filePath.substring(4);
        } else if (filePath.startsWith("vfs:")) {
            filePath = filePath.substring(5);
        } else if (filePath.startsWith("/vfs:")) {
            //jboss发布war的情况
            filePath = filePath.substring(6);
        }
        if (System.getProperty("os.name").toLowerCase().indexOf("window") < 0) {
            filePath = "/" + filePath;
        }

        if (filePath.endsWith("/")) {
            filePath = filePath.substring(0, filePath.length() - 1);
        }
//		//System.out.println("getRoot path is "+filePath );
        return filePath;
    }

    public static String getRootPath(String resource) {
        HttpServletRequest request = WebHelper.getHttpRequest();
        try {

            return WebUtils.getRealPath(request.getSession().getServletContext(), resource);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
            return "";
        }

    }

    /**
     * 格式化页码
     *
     * @param page
     * @return
     */
    public static int formatPage(String page) {
        int iPage = 1;
        if (page == null || "".equals(page)) {
            return iPage;
        }
        try {
            iPage = Integer.parseInt(page);
        } catch (Exception ex) {
            iPage = 1;
        }
        return iPage;
    }

    /**
     * 将计量单位字节转换为相应单位
     *
     * @param fileSize
     * @return
     */
    public static String getFileSize(String fileSize) {
        String temp = "";
        DecimalFormat df = new DecimalFormat("0.00");
        double dbFileSize = Double.parseDouble(fileSize);
        if (dbFileSize >= 1024) {
            if (dbFileSize >= 1048576) {
                if (dbFileSize >= 1073741824) {
                    temp = df.format(dbFileSize / 1024 / 1024 / 1024) + " GB";
                } else {
                    temp = df.format(dbFileSize / 1024 / 1024) + " MB";
                }
            } else {
                temp = df.format(dbFileSize / 1024) + " KB";
            }
        } else {
            temp = df.format(dbFileSize / 1024) + " KB";
        }
        return temp;
    }

    /**
     * 得到一个32位随机字符
     *
     * @return
     */
    public static String getEntry() {
        Random random = new Random(100);
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(new String(
                "yyyyMMddHHmmssS"));
        return md5(formatter.format(now) + random.nextDouble());
    }

    /**
     * 将中文汉字转成UTF8编码
     *
     * @param str
     * @return
     */
    public static String toUTF8(String str) {
        if (str == null || "".equals(str)) {
            return "";
        }
        try {
            return new String(str.getBytes("ISO8859-1"), "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String to(String str, String charset) {
        if (str == null || "".equals(str)) {
            return "";
        }
        try {
            return new String(str.getBytes("UTF-8"), charset);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static byte[] getBytes(String str) {
        if (str == null || "".equals(str)) {
            return new byte[0];
        }
        try {
            return str.getBytes(BaseConstants.DEFAULT_ENCODING);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new byte[0];
        }
    }

    public static String getString(byte[] bytes) {
        if (ArrayUtils.isEmpty(bytes)) {
            return "";
        }
        try {
            return new String(bytes, BaseConstants.DEFAULT_ENCODING);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String getRandStr(int n) {
        Random random = new Random();
        String sRand = "";
        for (int i = 0; i < n; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
        }
        return sRand;
    }

    /**
     * 得到一个数字的大写(一到十之内)
     *
     * @param num
     * @return
     */
    public static String getChineseNum(int num) {
        String[] chineseNum = new String[]{"一", "二", "三", "四", "五", "六", "七",
                "八", "九", "十"};
        return chineseNum[num];
    }

    public static String replaceEnter(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\r", "").replaceAll("\n", "");
    }

    public static String replaceAll(String source, String target, String content) {
        StringBuffer buffer = new StringBuffer(source);
        int start = buffer.indexOf(target);
        if (start > 0) {
            source = buffer.replace(start, start + target.length(), content)
                    .toString();
        }
        return source;
    }

    /**
     * 去除HTML 元素
     *
     * @param element
     * @return
     */
    public static String getTxtWithoutHTMLElement(String element) {
        if (null == element || "".equals(element.trim())) {
            return element;
        }

        Pattern pattern = Pattern.compile(HTMLELEMENT_REGEX);
        Matcher matcher = pattern.matcher(element);
        StringBuffer txt = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group();
            if (group.matches("<[\\s]*>")) {
                matcher.appendReplacement(txt, group);
            } else {
                matcher.appendReplacement(txt, "");
            }
        }
        matcher.appendTail(txt);
        String temp = txt.toString().replaceAll("\n", "");
        temp = temp.replaceAll(" ", "");
        return temp;
    }

    /**
     * clear trim to String
     *
     * @return
     */
    public static String toTrim(String strtrim) {
        if (null != strtrim && !"".equals(strtrim)) {
            return strtrim.trim();
        }
        return "";
    }

    /**
     * 转义字串的$
     *
     * @param str
     * @return
     */
    public static String filterDollarStr(String str) {
        String sReturn = "";
        if (!"".equals(toTrim(str))) {
            if (str.indexOf('$', 0) > -1) {
                while (str.length() > 0) {
                    if (str.indexOf('$', 0) > -1) {
                        sReturn += str.subSequence(0, str.indexOf('$', 0));
                        sReturn += "\\$";
                        str = str.substring(str.indexOf('$', 0) + 1,
                                str.length());
                    } else {
                        sReturn += str;
                        str = "";
                    }
                }

            } else {
                sReturn = str;
            }
        }
        return sReturn;
    }

    public static String compressHtml(String html) {
        if (html == null) {
            return null;
        }

        html = html.replaceAll("[\\t\\n\\f\\r]", "");
        return html;
    }

    public static String toCurrency(Double d) {
        if (d != null) {
            DecimalFormat df = new DecimalFormat("�?,###.00");
            return df.format(d);
        }
        return "";
    }

    public static String toString(Integer i) {
        if (i != null) {
            return String.valueOf(i);
        }
        return "";
    }

    public static String toString(Double d) {
        if (null != d) {
            return String.valueOf(d);
        }
        return "";
    }

    /**
     * Object转String的方法
     *
     * @param o       需要转String的对象
     * @param checked 是否检测异常
     * @return String 转换之后的字符串
     */
    public static String toString(Object o, boolean checked) {
        String value = "";
        if (null != o) {
            try {
                value = o.toString();

            } catch (Exception e) {
                if (checked) {
                    throw new RuntimeException("String类型不正确");
                }
            }
        }
        return value;
    }

    /**
     * Object转String的方法
     * 若为空，或者转换出现异常
     *
     * @param o 需要转String的对象
     * @return 转换之后的字符串
     */
    public static String toString(Object o) {
        String value = "";
        if (null != o) {
            value = o.toString();
        }
        return value;
    }

    public static String getRandom() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random rand = new Random();
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < 6; i++) {
            result = result * 10 + array[i];
        }

        return "" + result;
    }

    /**
     * 处理树型码 获取本级别最大的code 如:301000 返回301999
     *
     * @param code
     * @return
     */
    public static int getMaxLevelCode(int code) {
        String codeStr = "" + code;
        StringBuffer str = new StringBuffer();
        boolean flag = true;
        for (int i = codeStr.length() - 1; i >= 0; i--) {
            char c = codeStr.charAt(i);
            if (c == '0' && flag) {
                str.insert(0, '9');
            } else {
                str.insert(0, c);
                flag = false;
            }
        }
        return Integer.valueOf(str.toString());
    }

    /**
     * 去掉sql的注释
     */
    public static String delSqlComment(String content) {
        String pattern = "/\\*(.|[\r\n])*?\\*/";
        Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
        Matcher m = p.matcher(content);
        if (m.find()) {
            content = m.replaceAll("");
        }
        return content;
    }

    public static String inputStream2String(InputStream is) {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static String decode(String keyword) {
        try {
            keyword = URLDecoder.decode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return keyword;
    }

    /**
     * 进行解析
     *
     * @param regex
     * @param rpstr
     * @param source
     * @return
     */
    public static String doFilter(String regex, String rpstr, String source) {
        Pattern p = Pattern.compile(regex, 2 | Pattern.DOTALL);
        Matcher m = p.matcher(source);
        return m.replaceAll(rpstr);
    }

    /**
     * 脚本过滤
     *
     * @param source
     * @return
     */
    public static String formatScript(String source) {
        source = source.replaceAll("javascript", "&#106avascript");
        source = source.replaceAll("jscript:", "&#106script:");
        source = source.replaceAll("js:", "&#106s:");
        source = source.replaceAll("value", "&#118alue");
        source = source.replaceAll("about:", "about&#58");
        source = source.replaceAll("file:", "file&#58");
        source = source.replaceAll("document.cookie", "documents&#46cookie");
        source = source.replaceAll("vbscript:", "&#118bscript:");
        source = source.replaceAll("vbs:", "&#118bs:");
        source = doFilter("(on(mouse|exit|error|click|key))", "&#111n$2",
                source);
        return source;
    }

    /**
     * 格式化HTML代码
     *
     * @param htmlContent
     * @return
     */
    public static String htmlDecode(String htmlContent) {
        htmlContent = formatScript(htmlContent);
        htmlContent = htmlContent.replaceAll(" ", "&nbsp;")
                .replaceAll("<", "&lt;").replaceAll(">", "&gt;")
                .replaceAll("\n\r", "<br>").replaceAll("\r\n", "<br>")
                .replaceAll("\r", "<br>");
        return htmlContent;
    }

    /**
     * 动态添加表前缀，对没有前缀的表增加前缀
     *
     * @param table
     * @param prefix
     * @return
     */
    public static String addPrefix(String table, String prefix) {
        String result = "";
        if (table.length() > prefix.length()) {
            if (table.substring(0, prefix.length()).toLowerCase()
                    .equals(prefix.toLowerCase())) {
                result = table;
            } else {
                result = prefix + table;
            }
        } else {
            result = prefix + table;
        }

        return result;
    }

    public static String addSuffix(String table, String suffix) {
        String result = "";
        if (table.length() > suffix.length()) {
            int start = table.length() - suffix.length();
            int end = start + suffix.length();
            if (table.substring(start, end).toLowerCase()
                    .equals(suffix.toLowerCase())) {
                result = table;
            } else {
                result = table + suffix;
            }
        } else {
            result = table + suffix;
        }

        return result;
    }


    /**
     * 得到异常的字串
     *
     * @param aThrowable
     * @return
     */
    public static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();

    }

    /**
     * 判断判断字符串是否为数字
     *
     * @param str 传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    public static boolean isNumber(String str) {
        Pattern patternInteger = Pattern.compile(INT_REGEX);        //是否是整数
        Pattern patternDouble = Pattern.compile(DOUBLE_REGEX);    //是否是小数
        return patternInteger.matcher(str).matches() || patternDouble.matcher(str).matches();
    }

    public static Set<String> toStringSet(long[] array) {
        Set<String> set = new HashSet<>();
        if (ArrayUtils.isNotEmpty(array)) {
            for (int i = 0; i < array.length; i++) {
                set.add(String.valueOf(array[i]));
            }
        }
        return set;
    }

    public static String appendWhiteSpace(String text, int length) {
        if (StringUtils.isEmpty(text)) {
            return writeString(" ", length);
        }
        if (text.length() > length) {
            return text;
        }

        int count = (length - text.length()) / 2;
        String s = writeString(" ", count);

        String other = "";
        if ((length - text.length()) % 2 == 1) {
            other = writeString(" ", 1);
        }
        return s + text + s + other;
    }

    private static String writeString(String t, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(t);
        }
        return sb.toString();
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。 例如：HELLO_WORLD->HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String convertToCamelCase(String name) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!name.contains("_")) {
            // 不含下划线，仅将首字母大写
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = name.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * 驼峰首字符小写
     */
    public static String uncapitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StrBuilder(strLen).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
    }

    /**
     * * 判断一个Collection是否为空， 包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Collection<?> coll) {
        return isNull(coll) || coll.isEmpty();
    }

    /**
     * * 判断一个Collection是否非空，包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * * 判断一个对象数组是否为空
     *
     * @param objects 要判断的对象数组
     *                * @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    /**
     * * 判断一个对象数组是否非空
     *
     * @param objects 要判断的对象数组
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * * 判断一个字符串是否为空串
     *
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str.trim()) || "undefined".equals(str)) {
            return true;
        }
        return isNull(str) || NULLSTR.equals(str.trim());
    }

    /**
     * * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * * 判断一个对象是否非空
     *
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * * 判断一个对象是否是数组类型（Java基本型别的数组）
     *
     * @param object 对象
     * @return true：是数组 false：不是数组
     */
    public static boolean isArray(Object object) {
        return isNotNull(object) && object.getClass().isArray();
    }

    /**
     * 去空格
     */
    public static String trim(String str) {
        return (str == null ? "" : str.trim());
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @return 结果
     */
    public static String substring(final String str, int start) {
        if (str == null) {
            return NULLSTR;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return NULLSTR;
        }

        return str.substring(start);
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @param end   结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return NULLSTR;
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return NULLSTR;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params   参数值
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params) {
        if (isEmpty(params) || isEmpty(template)) {
            return template;
        }
        return StrFormatter.format(template, params);
    }

    /**
     * 字符串转set
     *
     * @param str 字符串
     * @param sep 分隔符
     * @return set集合
     */
    public static final Set<String> str2Set(String str, String sep) {
        return new HashSet<String>(str2List(str, sep, true, false));
    }

    /**
     * 字符串转list
     *
     * @param str         字符串
     * @param sep         分隔符
     * @param filterBlank 过滤纯空白
     * @param trim        去掉首尾空白
     * @return list集合
     */
    public static final List<String> str2List(String str, String sep, boolean filterBlank, boolean trim) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isEmpty(str)) {
            return list;
        }

        // 过滤空白字符串
        if (filterBlank && StringUtils.isBlank(str)) {
            return list;
        }
        String[] split = str.split(sep);
        for (String string : split) {
            if (filterBlank && StringUtils.isBlank(string)) {
                continue;
            }
            if (trim) {
                string = string.trim();
            }
            list.add(string);
        }

        return list;
    }

    /**
     * 下划线转驼峰命名
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase = true;
        // 当前字符是否大写
        boolean curreCharIsUpperCase = true;
        // 下一字符是否大写
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                sb.append(SEPARATOR);
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 驼峰式命名法 例如：user_name->userName
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static int getInt(String val) {
        return getInt(val, 0);
    }

    public static int getInt(String val, int defaultValue) {
        int ret = defaultValue;
        if (isNotBlank(val)) {
            try {
                ret = Integer.parseInt(val);
            } catch (Exception x) {
                logger.warn("The input [{}] is not a integer.", val);
            }
        }
        return ret;
    }

    public static long getLong(String val) {
        return getLong(val, 0L);
    }

    public static long getLong(String val, long defaultValue) {
        long ret = defaultValue;
        if (isNotBlank(val)) {
            try {
                ret = Long.parseLong(val);
            } catch (Exception x) {
                logger.warn("The input [{}] is not a long.", val);
            }
        }
        return ret;
    }

    public static double getDouble(String val) {
        return getDouble(val, 0);
    }

    public static double getDouble(String val, double defaultValue) {
        double ret = defaultValue;
        if (isNotBlank(val)) {
            try {
                ret = Double.parseDouble(val);
            } catch (Exception x) {
                logger.warn("The input [{}] is not a double.", val);
            }
        }
        return ret;
    }
}
