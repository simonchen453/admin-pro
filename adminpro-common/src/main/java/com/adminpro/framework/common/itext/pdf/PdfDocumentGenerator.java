package com.adminpro.framework.common.itext.pdf;

import com.adminpro.framework.common.itext.freemaker.HtmlGenerator;
import com.adminpro.framework.common.itext.pdf.exception.DocumentGeneratingException;
import com.adminpro.framework.common.itext.pdf.factory.ITextRendererObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.Map;

public class PdfDocumentGenerator {
    private final static Logger logger = LoggerFactory.getLogger(PdfDocumentGenerator.class);

    private final static HtmlGenerator HTMLGENERATOR;

    static {
        HTMLGENERATOR = new HtmlGenerator();
    }

    public boolean generate(String template, DocumentVo documentVo,
                            OutputStream out) throws DocumentGeneratingException {
        try {
            Map<String, Object> variables = documentVo.fillDataMap();
            String htmlContent = HTMLGENERATOR.generate(template,
                    variables);
            this.generate(htmlContent, out);

        } catch (Exception e) {
            String error = "The document [primarykey="
                    + documentVo.findPrimaryKey() + "] is failed to generate";
            logger.error(error);
            throw new DocumentGeneratingException(error, e);
        }

        return true;
    }

    /**
     * Output a pdf to the specified outputstream
     *
     * @param htmlContent the htmlstr
     * @param out         the specified outputstream
     * @throws Exception
     */
    public void generate(String htmlContent, OutputStream out)
            throws Exception {
        ITextRenderer iTextRenderer = null;

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(htmlContent
                    .getBytes("UTF-8")));

            iTextRenderer = (ITextRenderer) ITextRendererObjectFactory
                    .getObjectPool().borrowObject();//获取对象池中对象

            try {
                iTextRenderer.setDocument(doc, null);
                iTextRenderer.layout();
                iTextRenderer.createPDF(out);
            } catch (Exception e) {
                ITextRendererObjectFactory.getObjectPool().invalidateObject(iTextRenderer);
                iTextRenderer = null;
                throw e;
            }

        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null) {
                out.close();
            }

            if (iTextRenderer != null) {
                try {
                    ITextRendererObjectFactory.getObjectPool().invalidateObject(iTextRenderer);
                    iTextRenderer = null;
                } catch (Exception ex) {
                    logger.error("Cannot return object from pool.", ex);
                }
            }
        }
    }

}
