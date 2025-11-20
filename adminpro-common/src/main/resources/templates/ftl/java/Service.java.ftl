package ${package};

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.core.jdbc.SearchParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ${tableComment} 服务层实现
 *
 * @author ${author}
 * @date ${datetime}
 */
@Slf4j
@Service
public class ${className}Service extends BaseService<${className}Entity, ${primaryKey.attrType}>{

    private ${className}Dao dao;

    @Autowired
    public ${className}Service(${className}Dao dao) {
        super(dao);
        this.dao = dao;
    }

    public static ${className}Service getInstance() {
        return SpringUtil.getBean(${className}Service.class);
    }

    public QueryResultSet<${className}Entity> search(SearchParam param){
        return dao.search(param);
    }

    public List<${className}Entity> findByParam(SearchParam param){
        return dao.findByParam(param);
    }

    @Transactional
    public void importExcel(List<${className}Entity> list){
        for (int i = 0; i < list.size(); i++) {
            ${className}Entity ${classname} = list.get(i);
            ${className}Entity entity = null;//find By unique key
            if(entity == null){
                super.create(${classname});
            }else{
                <#list entityColumns as column>
                    <#if column.attrname != primaryKey.attrname>
                        ${column.attrType} ${column.attrname} = ${classname}.get${column.attrName}();
                    </#if>
                </#list>

                <#list entityColumns as column>
                    <#if column.attrname != primaryKey.attrname>
                        entity.set${column.attrName}(${column.attrname});
                    </#if>
                </#list>
                super.update(entity);
            }
        }
    }

    public void exportExcel(HttpServletResponse response, List<${className}Entity> list) throws Exception {
        Workbook book = generateWorkbook(list);

        response.reset();
        response.setContentType("application/x-msdownload");
        String fileName = "${tableComment}";
        fileName = fileName + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("gb2312"), "ISO-8859-1") + ".xls");
        ServletOutputStream outStream = null;
        try {
            outStream = response.getOutputStream();
            book.write(outStream);
        } finally {
            book.close();
            outStream.close();
        }
    }

    private Workbook generateWorkbook(List<${className}Entity> list) {
        ExportParams params = new ExportParams();
        params.setSheetName("${tableComment}");
        Map dataMap = new HashMap<>();
        dataMap.put("title", params);
        dataMap.put("entity", ${className}Entity.class);
        dataMap.put("data", list);
        List<Map<String, Object>> sheetsList = new ArrayList<>();
        sheetsList.add(dataMap);
        // 执行方法
        return ExcelExportUtil.exportExcel(sheetsList, ExcelType.HSSF);
    }

    @Transactional
    public void deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return;
        }
        String[] split = ids.split(",");
        for (int i = 0; i < split.length; i++) {
            dao.delete(split[i]);
        }
    }
}

