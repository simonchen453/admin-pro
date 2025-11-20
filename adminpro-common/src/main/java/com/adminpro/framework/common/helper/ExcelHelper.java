package com.adminpro.framework.common.helper;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.imports.ExcelImportServer;
import cn.afterturn.easypoi.exception.excel.ExcelImportException;

import java.io.Closeable;
import java.io.InputStream;
import java.util.List;

public class ExcelHelper {
    public static <T> List<T> importExcel(InputStream is, Class<?> pojoClass, ImportParams params) {

        List var4;
        try {
            var4 = (new ExcelImportServer()).importExcelByIs(is, pojoClass, params).getList();
        } catch (ExcelImportException var9) {
            throw new ExcelImportException(var9.getType(), var9);
        } catch (Exception var10) {
            throw new ExcelImportException(var10.getMessage(), var10);
        } finally {
            closeQuietly(is);
        }
        return var4;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ex) {

            }
        }
    }
}
