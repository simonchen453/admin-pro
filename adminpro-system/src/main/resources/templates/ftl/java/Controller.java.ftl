package ${package};
import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.base.web.BaseSearchForm;
import com.adminpro.core.base.util.BeanUtil;
import org.springframework.stereotype.Controller;
import com.adminpro.framework.common.helper.ExcelHelper;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

/**
 * ${tableComment} 信息操作处理
 *
 * @author ${author}
 * @date ${datetime}
 */
@Slf4j
@Controller
@RequestMapping(${className}Controller.ACCESS_URL_PREFIX)
@PreAuthorize("@ss.hasPermission('system:${classname}')")
public class ${className}Controller extends BaseRoutingController {
    protected static final String HTML_FOLDER_PREFIX = "${moduleName}/${classname}" ;
    protected static final String ACCESS_URL_PREFIX = "/${moduleName}/${classname}";
    protected static final String SEARCH_FORM_KEY = "${classname}SearchForm";

    @Autowired
    private ${className}Service ${classname}Service;

    @Autowired
    private ${className}CreateValidator ${classname}CreateValidator;

    @Autowired
    private ${className}UpdateValidator ${classname}UpdateValidator;

    @GetMapping()
    public String prepareList() {
<#--        cleanSearchForm(); -->
        return "forward:" + ACCESS_URL_PREFIX + "/list";
    }

    @GetMapping("/list")
    public String ${classname}() {
        prepareData();
        getSearchForm();
        return HTML_FOLDER_PREFIX + "/list" ;
    }

    /**
     * 查询${tableComment}列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public R<QueryResultSet<${className}Entity>> list(@RequestBody SearchForm searchForm) {
        BeanUtil.beanAttributeValueTrim(searchForm);
        <#list entityColumns as column>
            <#if column.attrname != primaryKey.attrname>
                ${column.attrType} ${column.attrname} = searchForm.get${column.attrName}();
            </#if>
        </#list>
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        <#list entityColumns as column>
            <#if column.attrname != primaryKey.attrname>
                <#if column.attrType == 'String'>
                if(StringUtils.isNotEmpty(${column.attrname})){
                <#else>
                if(${column.attrname} == null){
                </#if>
                    param.addFilter("${column.attrname}", ${column.attrname});
                }
            </#if>
        </#list>
        QueryResultSet<${className}Entity> resultSet = ${classname}Service.search(param);
        return R.ok(resultSet);
    }

    /**
     * 新增${tableComment}
     */
    @GetMapping("/create")
    public String add() {
        prepareData();
        return HTML_FOLDER_PREFIX + "/create" ;
    }

    /**
     * 新增保存${tableComment}
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public R create(@RequestBody ${className}Entity ${classname}) {
        MessageBundle messageBundle = getMessageBundle();
        BeanUtil.beanAttributeValueTrim(${classname});
        ${classname}CreateValidator.validate(${classname}, messageBundle);
        if(messageBundle.hasErrorMessage()){
            return R.error(messageBundle);
        }else {
            <#list entityColumns as column>
                <#if column.attrname != primaryKey.attrname>
                    ${column.attrType} ${column.attrname} = ${classname}.get${column.attrName}();
                </#if>
            </#list>

            ${className}Entity entity = new ${className}Entity();
            <#list entityColumns as column>
                <#if column.attrname != primaryKey.attrname>
                    entity.set${column.attrName}(${column.attrname});
                </#if>
            </#list>

            ${classname}Service.create(entity);
            return R.ok();
        }
    }

    /**
     * 修改${tableComment}
     */
    @GetMapping("/edit")
    public String edit(@RequestParam("${primaryKey.attrname}") ${primaryKey.attrType} ${primaryKey.attrname}) {
        prepareData();
        ${className}Entity ${classname} =${classname}Service.findById(${primaryKey.attrname});
        request.setAttribute("${classname}" , ${classname});
        return HTML_FOLDER_PREFIX + "/edit" ;
    }

    /**
     * 修改保存${tableComment}
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH)
    @ResponseBody
    public R editSave(@RequestBody ${className}Entity ${classname}) {
        MessageBundle messageBundle = getMessageBundle();
        BeanUtil.beanAttributeValueTrim(${classname});
        ${classname}UpdateValidator.validate(${classname}, messageBundle);

        if(messageBundle.hasErrorMessage()){
            return R.error(messageBundle);
        }else {
            ${className}Entity entity = ${classname}Service.findById(${classname}.getId());
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

            ${classname}Service.update(entity);
            return R.ok();
        }
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public R import${className}(@RequestParam("file") MultipartFile file) {
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);

        try {
            List<${className}Entity> result = ExcelHelper.importExcel(file.getInputStream(), ${className}Entity.class, params);
            ${classname}Service.importExcel(result);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return R.ok();
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public void export(@RequestParam String ids) throws Exception {
        String[] split = ids.split(",");

        List<${className}Entity> list = new ArrayList<>();
        if (split != null && split.length > 0) {
            for (String id : split) {
                ${className}Entity ${classname}Entity = ${classname}Service.findById(id);
                if (${classname}Entity != null) {
                    list.add(${classname}Entity);
                }
            }
        }
        ${classname}Service.exportExcel(response, list);
    }

    @RequestMapping(value = "/excelAll", method = RequestMethod.GET)
    @ResponseBody
    public void excelAll(SearchForm searchForm) throws Exception {
        BeanUtil.beanAttributeValueTrim(searchForm);
        <#list entityColumns as column>
            <#if column.attrname != primaryKey.attrname>
                ${column.attrType} ${column.attrname} = searchForm.get${column.attrName}();
            </#if>
        </#list>
        SearchParam param = startPaging(searchForm);
        setSearchForm(searchForm);
        <#list entityColumns as column>
            <#if column.attrname != primaryKey.attrname>
                <#if column.attrType == 'String'>
                if(StringUtils.isNotEmpty(${column.attrname})){
                <#else>
                    if(${column.attrname} == null){
                </#if>
                param.addFilter("${column.attrname}", ${column.attrname});
            }
            </#if>
        </#list>

        List<${className}Entity> list = ${classname}Service.findByParam(param);
        ${classname}Service.exportExcel(response, list);
    }

    /**
     * 获取详细信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public R<${className}Entity> detail(@PathVariable String id) {
            ${className}Entity entity = ${classname}Service.findById(id);
        if(entity != null) {
            entity.emptyAuditTime();
            return R.ok(entity);
        }else{
            return R.error("对象不存在");
        }
    }

    /**
     * 删除${tableComment}
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public R remove(@RequestParam("ids") String ids) {
        ${classname}Service.deleteByIds(ids);
        return R.ok();
    }

    @Data
    public static class SearchForm extends BaseSearchForm {
        <#list entityColumns as column>
            <#if column.columnNameUp != primaryKey.columnNameUp>
            /**
             * ${column.columnComment}
             */
            private ${column.attrType} ${column.attrname};
            </#if>
        </#list>
    }

    private SearchForm getSearchForm(){
        SearchForm searchForm = (SearchForm)request.getSession().getAttribute(SEARCH_FORM_KEY);
        if(searchForm == null){
            searchForm = new SearchForm();
        }
        setSearchForm(searchForm);
        return searchForm;
    }

    private void setSearchForm(SearchForm searchForm){
        request.getSession().setAttribute(SEARCH_FORM_KEY, searchForm);
    }

    private void cleanSearchForm(){
        request.getSession().removeAttribute(SEARCH_FORM_KEY);
    }

}

