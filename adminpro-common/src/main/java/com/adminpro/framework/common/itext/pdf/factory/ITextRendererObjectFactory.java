package com.adminpro.framework.common.itext.pdf.factory;

import com.adminpro.framework.common.itext.utils.ResourceLoader;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.IOException;

public class ITextRendererObjectFactory extends
        BasePoolableObjectFactory {
    private static GenericObjectPool itextRendererObjectPool = null;

    @Override
    public Object makeObject() throws Exception {
        ITextRenderer renderer = createTextRenderer();
        return renderer;
    }

    public static GenericObjectPool getObjectPool() {
        synchronized (ITextRendererObjectFactory.class) {
            if (itextRendererObjectPool == null) {
                itextRendererObjectPool = new GenericObjectPool(
                        new ITextRendererObjectFactory());
                GenericObjectPool.Config config = new GenericObjectPool.Config();
                config.lifo = false;
                config.maxActive = 15;
                config.maxIdle = 5;
                config.minIdle = 1;
                config.maxWait = 5 * 1000;
                itextRendererObjectPool.setConfig(config);
            }
        }

        return itextRendererObjectPool;
    }

    public static synchronized ITextRenderer createTextRenderer()
            throws DocumentException, IOException {
        ITextRenderer renderer = new ITextRenderer();
        ITextFontResolver fontResolver = renderer.getFontResolver();
        addFonts(fontResolver);
        return renderer;
    }

    public static ITextFontResolver addFonts(ITextFontResolver fontResolver)
            throws DocumentException, IOException {
        File fontsDir = new File(ResourceLoader.getPath("config/fonts"));
        if (fontsDir != null && fontsDir.isDirectory()) {
            File[] files = fontsDir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    if (f == null || f.isDirectory()) {
                        break;
                    }
                    fontResolver.addFont(f.getAbsolutePath(), BaseFont.IDENTITY_H,
                            BaseFont.NOT_EMBEDDED);
                }
            }
        }
        return fontResolver;
    }
}
