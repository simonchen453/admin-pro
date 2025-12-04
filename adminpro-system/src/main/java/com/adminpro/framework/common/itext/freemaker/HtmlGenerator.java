package com.adminpro.framework.common.itext.freemaker;

import com.adminpro.core.base.util.SpringUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Component
public class HtmlGenerator {

    protected static Logger logger = LoggerFactory.getLogger(HtmlGenerator.class);

    public static HtmlGenerator getInstance() {
        return SpringUtil.getBean(HtmlGenerator.class);
    }

    /**
     * Generate html string.
     *
     * @param template  the name of freemarker teamlate.
     * @param variables the data of teamlate.
     * @return htmlStr
     * @throws IOException
     * @throws TemplateException
     * @throws Exception
     */
    public String generate(String template, Map<String, Object> variables) throws IOException, TemplateException {
        BufferedWriter writer = null;
        String htmlContent = "";
        try {
            Configuration config = FreemarkerConfiguration.getConfiguation();
            Template tp = config.getTemplate(template, "UTF-8");
            StringWriter stringWriter = new StringWriter();
            writer = new BufferedWriter(stringWriter);

            tp.process(variables, writer);
            htmlContent = stringWriter.toString();
            writer.flush();

        } catch (Exception e) {
            logger.error("HtmlGenerator Faile: ", e);
            return null;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return htmlContent;
    }

}
