package com.adminpro.tools.gen;

import com.adminpro.config.GenConfig;
import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.rbac.common.RbacConstants;
import com.adminpro.tools.gen.util.FreeMarkerInitializer;
import com.adminpro.tools.gen.util.GenUtils;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成 服务层处理
 *
 * @author simon
 */
@Service
public class GenService {

    @Autowired
    private GenDao genDao;

    /**
     * 查询ry数据库表信息
     *
     * @param param 表信息
     * @return 数据库表列表
     */
    public QueryResultSet<TableInfo> search(SearchParam param) {
        return genDao.search(param);
    }

    public List<TableInfo> findAll() {
        return genDao.findAll();
    }

    /**
     * 生成代码
     *
     * @param tableName 表名称
     * @return 数据
     */
    public byte[] generatorCode(String tableName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        // 查询表信息
        TableInfo table = genDao.findTableByName(tableName);
        // 查询列信息
        List<ColumnInfo> columns = genDao.findTableColumnsByName(tableName);
        // 生成代码
        generatorCode(table, columns, zip);
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 批量生成代码
     *
     * @param tableNames 表数组
     * @return 数据
     */
    public byte[] generatorCode(String[] tableNames) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames) {
            // 查询表信息
            TableInfo table = genDao.findTableByName(tableName);
            // 查询列信息
            List<ColumnInfo> columns = genDao.findTableColumnsByName(tableName);
            // 生成代码
            generatorCode(table, columns, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 生成代码
     */
    public void generatorCode(TableInfo table, List<ColumnInfo> columns, ZipOutputStream zip) {
        // 表名转换成Java属性名
        String className = GenUtils.tableToJava(table.getTableName());
        table.setClassName(className);
        table.setClassname(StringHelper.uncapitalize(className));
        // 列信息
        table.setColumns(GenUtils.transColums(columns, false));
        table.setEntityColumns(GenUtils.transColums(columns, true));
        // 设置主键
        table.setPrimaryKey(GenUtils.transferColumn(table.getColumnsLast()));

        String packageName = GenConfig.getInstance().getPackageName();
        String moduleName = GenUtils.getModuleName(packageName);

        Map<String, Object> data = GenUtils.getTemplateData(table);

        // 获取模板列表
        List<String> templates = GenUtils.getTemplates();
        freemarker.template.Configuration config = FreeMarkerInitializer.getConfiguration();
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            try {
                Template tpl = config.getTemplate(template, RbacConstants.UTF8);
                tpl.process(data, sw);
                // 添加到zip
                zip.putNextEntry(new ZipEntry(GenUtils.getFileName(template, table, moduleName)));
                IOUtils.write(sw.toString(), zip, RbacConstants.UTF8);
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (Exception e) {
                throw new BaseRuntimeException("渲染模板失败，表名：" + table.getTableName(), e);
            }
        }
    }
}
