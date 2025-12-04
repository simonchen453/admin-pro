package com.adminpro.web.tools;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.tools.gen.GenService;
import com.adminpro.tools.gen.TableInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成器
 *
 * @author simon
 * @date 2018-09-06
 */
@RestController
@RequestMapping("/admin/generator")
@PreAuthorize("@ss.hasPermission('system:generator')")
public class CodeGeneratorController extends BaseRoutingController {
    protected static final String PREFIX = "admin/generator";

    @Autowired
    private GenService genService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<QueryResultSet<TableInfo>> list() {
        String tableName = request.getParameter("tableName");
        SearchParam param = startPaging();
        if (StringUtils.isNotEmpty(tableName)) {
            param.addFilter("tableName", tableName);
        }
        QueryResultSet<TableInfo> resultSet = genService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 批量生成代码
     */
    @GetMapping("/batchGenCode")
    public void batchGenCode(@RequestParam("tables") String tables) throws IOException {
        String[] tableNames = tables.split(",");
        byte[] data = genService.generatorCode(tableNames);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IOUtils.write(data, response.getOutputStream());
    }

    /**
     * 生成所有代码
     */
    @GetMapping("/genAll")
    public void genAll() throws IOException {
        List<TableInfo> tableInfos = genService.findAll();
        List<String> tableNames = new ArrayList<>();
        for (int i = 0; i < tableInfos.size(); i++) {
            TableInfo tableInfo = tableInfos.get(i);
            tableNames.add(tableInfo.getTableName());
        }
        byte[] data = genService.generatorCode(tableNames.toArray(new String[tableNames.size()]));

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IOUtils.write(data, response.getOutputStream());
    }
}
