package com.adminpro.tools.gen.util;

import com.adminpro.config.GenConfig;
import com.adminpro.core.base.entity.BaseAuditEntity;
import com.adminpro.core.base.util.DateUtil;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.tools.gen.ColumnInfo;
import com.adminpro.tools.gen.TableInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成器 工具类
 */
public class GenUtils {

    public static final String[] AUDIT_COLS = new String[]{
            BaseAuditEntity.COL_CREATED_BY_USER_ID, BaseAuditEntity.COL_CREATED_BY_USER_DOMAIN,
            BaseAuditEntity.COL_UPDATED_BY_USER_ID, BaseAuditEntity.COL_UPDATED_BY_USER_DOMAIN,
            BaseAuditEntity.COL_CREATED_DATE, BaseAuditEntity.COL_UPDATED_DATE
    };

    /**
     * 项目空间路径
     */
    private static final String PROJECT_PATH = "main/java";

    /**
     * mybatis空间路径
     */
    private static final String MYBATIS_PATH = "main/resources/mybatis";


    private static final String RESOURCES_PATH = "main/resources/";
    /**
     * html空间路径
     */
    private static final String TEMPLATES_PATH = "main/resources/templates";

    /**
     * React前端路径
     */
    private static final String REACT_PATH = "frontend/src";

    /**
     * 自动去除表前缀
     */
    public static String AUTO_REMOVE_PRE = "true";

    /**
     * 设置列信息
     */
    public static List<ColumnInfo> transColums(List<ColumnInfo> columns, boolean ignoreAudit) {
        // 列信息
        List<ColumnInfo> columsList = new ArrayList<>();
        for (ColumnInfo column : columns) {
            if (ignoreAudit && ArrayUtils.contains(AUDIT_COLS, column.getColumnName().toUpperCase())) {
                continue;
            }
            transferColumn(column);
            columsList.add(column);
        }
        return columsList;
    }

    public static ColumnInfo transferColumn(ColumnInfo column) {
        // 列名转换成Java属性名
        String attrName = StringHelper.convertToCamelCase(column.getColumnName().toLowerCase().replace(GenConfig.getInstance().getColPrefix(), ""));
        column.setAttrName(attrName);
        column.setAttrname(StringUtils.uncapitalize(attrName));
        column.setColumnNameUp(StringUtils.upperCase(column.getColumnName()));
        // 列的数据类型，转换成Java类型
        String attrType = CommonMap.javaTypeMap.get(column.getDataType());
        String attrtype = CommonMap.javatypeMap.get(column.getDataType());
        column.setAttrType(attrType);
        column.setAttrtype(attrtype);
        column.setATTRTYPE(StringHelper.upperCase(attrtype));
        return column;
    }

    /**
     * 获取 TypeScript 类型
     */
    public static String getTsType(String dataType) {
        String tsType = CommonMap.tsTypeMap.get(dataType);
        return tsType != null ? tsType : "string";
    }

    /**
     * 获取模板数据
     *
     * @return 模板数据Map
     */
    public static Map<String, Object> getTemplateData(TableInfo table) {
        Map<String, Object> data = new HashMap<>();
        String packageName = GenConfig.getInstance().getPackageName();
        data.put("tableName", table.getTableName());
        data.put("entityColumns", table.getEntityColumns());
        data.put("TABLENAME", table.getTableName().toUpperCase());
        data.put("tableComment", replaceKeyword(table.getTableComment()));
        data.put("primaryKey", table.getPrimaryKey());
        data.put("className", table.getClassName());
        data.put("classname", table.getClassname());
        data.put("CLASSNAME", StringHelper.upperCase(table.getClassname()));
        data.put("moduleName", GenUtils.getModuleName(packageName));
        data.put("modulename", StringHelper.lowerCase(GenUtils.getModuleName(packageName)));
        data.put("columns", table.getColumns());
        data.put("package", "com.adminpro." + table.getClassname());
        data.put("author", GenConfig.getInstance().getAuthor());
        data.put("datetime", DateUtil.getDate());
        data.put("parentEntity", parentEntity(table.getColumns()));
        data.put("id_key_m", IdGenerator.getInstance().nextStringId());
        data.put("id_key_r", IdGenerator.getInstance().nextStringId());
        data.put("date", DateUtil.formatDateTime(DateUtil.now()));
        data.put("tsTypeMap", CommonMap.tsTypeMap);
        return data;
    }

    protected static String parentEntity(List<ColumnInfo> cols) {
        for (int i = 0; i < cols.size(); i++) {
            ColumnInfo columnInfo = cols.get(i);
            if (StringUtils.equals(columnInfo.getColumnNameUp(), BaseAuditEntity.COL_CREATED_BY_USER_DOMAIN)) {
                return "BaseAuditEntity";
            }
        }

        return "BaseEntity";
    }

    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<String>();
        templates.add("templates/ftl/java/entity.java.ftl");
        templates.add("templates/ftl/java/Service.java.ftl");
        templates.add("templates/ftl/java/Controller.java.ftl");
        templates.add("templates/ftl/java/CreateValidator.java.ftl");
        templates.add("templates/ftl/java/UpdateValidator.java.ftl");
        templates.add("templates/ftl/java/dao.java.ftl");
        templates.add("templates/ftl/react/List.tsx.ftl");
        templates.add("templates/ftl/react/Form.tsx.ftl");
        templates.add("templates/ftl/react/api.ts.ftl");
        templates.add("templates/ftl/react/index.ts.ftl");
        templates.add("templates/ftl/react/types.ts.ftl");
        templates.add("templates/ftl/sql/sql.ftl");
        return templates;
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName) {
        if (AUTO_REMOVE_PRE.equals(GenConfig.getInstance().getAutoRemovePre())) {
            tableName = tableName.substring(tableName.indexOf("_") + 1);
        }
        if (StringUtils.isNotEmpty(GenConfig.getInstance().getTablePrefix())) {
            tableName = tableName.replace(GenConfig.getInstance().getTablePrefix(), "");
        }
        if (StringUtils.isNotEmpty(GenConfig.getInstance().getTableSuffix())) {
            tableName = tableName.replace(GenConfig.getInstance().getTableSuffix(), "");
        }
        return StringHelper.convertToCamelCase(tableName);
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, TableInfo table, String moduleName) {
        // 小写类名
        String classname = table.getClassname();
        // 大写类名
        String className = table.getClassName();
        String javaPath = PROJECT_PATH + "/" + moduleName + "/";
        String mybatisPath = MYBATIS_PATH + "/" + moduleName + "/" + className;
        String htmlPath = TEMPLATES_PATH + "/" + moduleName + "/" + classname;
        String jsPath = RESOURCES_PATH + "/static/js/system/";

        if (StringUtils.isNotEmpty(classname)) {
            javaPath += classname.replace(".", "/") + "/";
        }

        if (template.contains("entity.java.ftl")) {
            return javaPath + "/" + className + "Entity.java";
        }

        if (template.contains("Repository.java.ftl")) {
            return javaPath + "/" + className + "Repo.java";
        }

        if (template.contains("dao.java.ftl")) {
            return javaPath + "/" + className + "Dao.java";
        }

        if (template.contains("Service.java.ftl")) {
            return javaPath + "/" + className + "Service.java";
        }

        if (template.contains("Controller.java.ftl")) {
            return javaPath + "/" + className + "Controller.java";
        }
        if (template.contains("CreateValidator.java.ftl")) {
            return javaPath + "/" + className + "CreateValidator.java";
        }
        if (template.contains("UpdateValidator.java.ftl")) {
            return javaPath + "/" + className + "UpdateValidator.java";
        }
        if (template.contains("Mapper.xml.ftl")) {
            return mybatisPath + "Mapper.xml";
        }
        if (template.contains("Repository.java.ftl")) {
            return javaPath + "/" + className + "Repo.java";
        }

        if (template.contains("list.html.ftl")) {
            return htmlPath + "/list.html";
        }
        if (template.contains("create.html.ftl")) {
            return htmlPath + "/" + "create.html";
        }
        if (template.contains("edit.html.ftl")) {
            return htmlPath + "/" + "edit.html";
        }
        if (template.contains("tools.js.ftl")) {
            return jsPath + "/" + classname + ".js";
        }
        if (template.contains("List.tsx.ftl")) {
            return REACT_PATH + "/pages/" + className + "/" + className + "List.tsx";
        }
        if (template.contains("Form.tsx.ftl")) {
            return REACT_PATH + "/pages/" + className + "/" + className + "Form.tsx";
        }
        if (template.contains("api.ts.ftl")) {
            return REACT_PATH + "/api/" + classname + ".ts";
        }
        if (template.contains("index.ts.ftl")) {
            return REACT_PATH + "/pages/" + className + "/index.ts";
        }
        if (template.contains("types.ts.ftl")) {
            return REACT_PATH + "/pages/" + className + "/types.ts";
        }
        if (template.contains("sql.ftl")) {
            return classname + "Menu.sql";
        }
        return null;
    }

    /**
     * 获取模块名
     *
     * @param packageName 包名
     * @return 模块名
     */
    public static String getModuleName(String packageName) {
        int lastIndex = packageName.lastIndexOf(".");
        int nameLength = packageName.length();
        String moduleName = StringUtils.substring(packageName, lastIndex + 1, nameLength);
        return moduleName;
    }

    public static String replaceKeyword(String keyword) {
        String keyName = keyword.replaceAll("(?:表|信息)", "");
        return keyName;
    }

}
