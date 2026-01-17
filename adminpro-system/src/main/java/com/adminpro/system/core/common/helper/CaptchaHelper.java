package com.adminpro.system.core.common.helper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码辅助工具类
 * <p>
 * 本类提供图形验证码的生成功能，用于防止机器人恶意攻击。
 * 生成的验证码包含随机字符、干扰线和随机颜色，有效防止OCR识别
 * <p>
 * 主要功能：
 * <ul>
 * <li>生成包含4位随机字符的验证码</li>
 * <li>添加5条随机干扰线</li>
 * <li>使用随机颜色增加识别难度</li>
 * <li>返回验证码字符串和图片对象</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 * <li>用户登录验证</li>
 * <li>防止恶意注册</li>
 * <li>防止暴力破解</li>
 * <li>防止爬虫抓取</li>
 * </ul>
 * <p>
 * 注意：验证码图片尺寸为80x40像素，字体大小为30px
 *
 * @author simon
 * @date 2019/1/11
 */
public class CaptchaHelper {
    /**
     * 验证码字符集
     * <p>
     * 包含数字（0-9）、小写字母（a-z）、大写字母（A-Z）
     */
    private static final char[] chars = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    /**
     * 验证码字符数量（4位）
     */
    private static final int SIZE = 4;
    /**
     * 干扰线数量（5条）
     */
    private static final int LINES = 5;
    /**
     * 验证码图片宽度（80像素）
     */
    private static final int WIDTH = 80;
    /**
     * 验证码图片高度（40像素）
     */
    private static final int HEIGHT = 40;
    /**
     * 验证码字体大小（30px）
     */
    private static final int FONT_SIZE = 30;

    /**
     * 生成随机验证码及图片
     * <p>
     * 生成包含随机字符、干扰线和随机颜色的验证码图片。
     * 验证码包含4位随机字符（数字和字母混合）和5条干扰线
     * <p>
     * 使用场景：
     * <ul>
     * <li>用户登录时生成验证码</li>
     * <li>注册时生成验证码</li>
     * <li>敏感操作前验证</li>
     * </ul>
     *
     * @return Captcha对象，包含验证码字符串（key）和验证码图片（image）
     */
    public static Captcha generate() {
        StringBuffer sb = new StringBuffer();
        // 1.创建空白图片
        BufferedImage image = new BufferedImage(
                WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        // 2.获取图片画笔
        Graphics graphic = image.getGraphics();
        // 3.设置画笔颜色
        graphic.setColor(Color.LIGHT_GRAY);
        // 4.绘制矩形背景
        graphic.fillRect(0, 0, WIDTH, HEIGHT);
        // 5.画随机字符
        Random ran = new Random();
        for (int i = 0; i < SIZE; i++) {
            // 取随机字符索引
            int n = ran.nextInt(chars.length);
            // 设置随机颜色
            graphic.setColor(getRandomColor());
            // 设置字体大小
            graphic.setFont(new Font(
                    null, Font.BOLD + Font.ITALIC, FONT_SIZE));
            // 画字符
            graphic.drawString(
                    chars[n] + "", i * WIDTH / SIZE, HEIGHT * 2 / 3);
            // 记录字符
            sb.append(chars[n]);
        }
        // 6.画干扰线
        for (int i = 0; i < LINES; i++) {
            // 设置随机颜色
            graphic.setColor(getRandomColor());
            // 随机画线
            graphic.drawLine(ran.nextInt(WIDTH), ran.nextInt(HEIGHT),
                    ran.nextInt(WIDTH), ran.nextInt(HEIGHT));
        }
        // 7.返回验证码和图片
        return new Captcha(sb.toString(), image);
    }

    /**
     * 随机取色
     * <p>
     * 生成RGB值在0-255之间的随机颜色
     *
     * @return 随机颜色对象
     */
    protected static Color getRandomColor() {
        Random ran = new Random();
        Color color = new Color(ran.nextInt(256),
                ran.nextInt(256), ran.nextInt(256));
        return color;
    }

    /**
     * 验证码封装类
     * <p>
     * 用于封装验证码的文本内容和图片对象
     */
    public static class Captcha {
        /**
         * 构造验证码对象
         *
         * @param key   验证码文本（4位随机字符）
         * @param image 验证码图片对象
         */
        public Captcha(String key, BufferedImage image) {
            this.key = key;
            this.image = image;
        }

        private String key;
        private BufferedImage image;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public BufferedImage getImage() {
            return image;
        }

        public void setImage(BufferedImage image) {
            this.image = image;
        }
    }
}
