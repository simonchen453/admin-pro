package com.adminpro.framework.common.word;


import com.adminpro.framework.common.helper.StringHelper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Word生成API
 * 注意：poi-tl 依赖暂未可用，如需使用请添加正确的依赖版本
 *
 * @author simon
 */
public class WordHelper {
    public static void generate(String templatName, OutputStream outputStream, Map<String, Object> map) {
        // TODO: poi-tl 依赖暂未可用，需要添加正确的依赖版本
        // 当前实现已禁用，如需使用请：
        // 1. 在 pom.xml 中添加正确的 poi-tl 依赖版本
        // 2. 取消下面的注释
        /*
        try {
            //核心API采用了极简设计，只需要一行代码
            XWPFTemplate template = XWPFTemplate.compile(templatName).render(map);
            template.write(outputStream);
            template.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        throw new UnsupportedOperationException("WordHelper.generate() 当前不可用，需要添加 poi-tl 依赖");
    }
}
