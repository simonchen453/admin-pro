package com.adminpro.system.core.common.word;

import com.deepoove.poi.XWPFTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Word生成API
 * 基于 poi-tl 实现 Word 文档模板渲染
 *
 * @author simon
 */
public class WordHelper {
    public static void generate(String templateName, OutputStream outputStream, Map<String, Object> map) {
        try {
            // 核心API采用了极简设计，只需要一行代码
            XWPFTemplate template = XWPFTemplate.compile(templateName).render(map);
            template.write(outputStream);
            template.close();
        } catch (IOException e) {
            throw new RuntimeException("生成 Word 文档失败: " + e.getMessage(), e);
        }
    }
}
