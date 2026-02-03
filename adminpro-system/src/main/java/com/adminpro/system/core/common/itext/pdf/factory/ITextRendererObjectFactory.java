package com.adminpro.system.core.common.itext.pdf.factory;

import com.adminpro.system.core.common.itext.utils.ResourceLoader;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.IOException;

public class ITextRendererObjectFactory extends BasePooledObjectFactory<ITextRenderer> {
    private static GenericObjectPool<ITextRenderer> itextRendererObjectPool = null;

    @Override
    public ITextRenderer create() throws Exception {
        return createTextRenderer();
    }

    @Override
    public PooledObject<ITextRenderer> wrap(ITextRenderer renderer) {
        return new DefaultPooledObject<>(renderer);
    }

    public static GenericObjectPool<ITextRenderer> getObjectPool() {
        synchronized (ITextRendererObjectFactory.class) {
            if (itextRendererObjectPool == null) {
                itextRendererObjectPool = new GenericObjectPool<>(new ITextRendererObjectFactory());
                itextRendererObjectPool.setLifo(false);
                itextRendererObjectPool.setMaxTotal(15);
                itextRendererObjectPool.setMaxIdle(5);
                itextRendererObjectPool.setMinIdle(1);
                itextRendererObjectPool.setMaxWaitMillis(5 * 1000);
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
