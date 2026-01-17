package com.adminpro.system.core.common.helper;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.imports.ExcelImportServer;
import cn.afterturn.easypoi.exception.excel.ExcelImportException;

import java.io.Closeable;
import java.io.InputStream;
import java.util.List;

/**
 * Excel导入导出辅助工具类
 * <p>
 * 本类基于EasyPOI提供Excel文件的导入功能，支持：
 * <ul>
 * <li>Excel文件到实体对象的转换</li>
 * <li>基于注解的字段映射</li>
 * <li>自动类型转换</li>
 * <li>异常处理和资源管理</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 * <li>批量数据导入</li>
 * <li>数据迁移</li>
 * <li>Excel数据解析</li>
 * </ul>
 * <p>
 * 注意：导入完成后会自动关闭输入流
 */
public class ExcelHelper {
    /**
     * 导入Excel文件
     * <p>
     * 从输入流读取Excel文件数据，并转换为指定类型的实体列表
     * <p>
     * 使用场景：
     * <ul>
     * <li>用户批量导入数据</li>
     * <li>系统数据迁移</li>
     * <li>Excel数据解析</li>
     * </ul>
     *
     * @param <T>       实体类型
     * @param is        Excel文件输入流，不能为空，使用后自动关闭
     * @param pojoClass 实体类的Class对象，需要使用EasyPOI注解，不能为空
     * @param params    导入参数配置，不能为空
     * @return 导入的实体列表
     * @throws ExcelImportException 如果导入过程中发生异常
     */
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

    /**
     * 安静地关闭可关闭对象
     * <p>
     * 关闭Closeable对象，如果发生异常则忽略不抛出。
     * 用于资源清理时的异常处理
     *
     * @param closeable 可关闭对象，如果为null则不执行任何操作
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ex) {

            }
        }
    }
}
