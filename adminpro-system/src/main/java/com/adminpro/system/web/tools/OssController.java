package com.adminpro.system.web.tools;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.IdGenerator;
import com.adminpro.framework.exceptions.BaseRuntimeException;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.helper.FileHelper;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.rbac.domains.vo.oss.ListOssVo;
import com.adminpro.system.rbac.domains.vo.oss.ListOssVoConverter;
import com.adminpro.system.tools.domains.entity.oss.OSSEntity;
import com.adminpro.system.tools.domains.entity.oss.OSSService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 对象存储服务管理控制器
 * <p>
 * 提供文件上传、查询和删除功能，包括：
 * <ul>
 * <li>文件列表查询（支持分页）</li>
 * <li>单文件上传（支持批量ID管理）</li>
 * <li>单文件和批量文件删除</li>
 * </ul>
 * </p>
 *
 * @author simon
 * @date 2020-08-22
 */
@Tag(name = "对象存储管理", description = "对象存储服务管理接口，提供文件上传、查询和删除功能")
@RestController
@RequestMapping(value = OssController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:oss')")
/**
 * 使用 Lombok @RequiredArgsConstructor 自动生成构造器进行依赖注入。
 * 所有 final 字段将通过构造器自动注入，无需显式编写 @Autowired。
 * 添加新依赖时，只需添加 private final 字段即可。
 */
@RequiredArgsConstructor
public class OssController extends BaseController {

    protected static final String PREFIX_URL = "/api/v1/tools/oss";

    private final FileHelper fileHelper;
    private final OSSService ossService;

    /**
     * 分页查询文件列表
     * <p>
     * 查询对象存储服务中的文件列表，支持分页
     * </p>
     *
     * @param request HTTP请求对象，用于获取分页参数
     * @return 包含文件列表和分页信息的查询结果集
     */
    @Operation(summary = "分页查询文件列表", description = "查询对象存储服务中的文件列表，支持分页")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = R.class))),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public R<QueryResultSet<ListOssVo>> paging(HttpServletRequest request) {
        SearchParam param = startPaging();
        QueryResultSet<ListOssVo> map = ossService.search(param).map(ListOssVoConverter.class);
        return R.ok(map);
    }

    /**
     * 上传文件
     * <p>
     * 上传文件到对象存储服务，支持批量ID管理和单文件模式
     * </p>
     *
     * @param file    上传的文件对象
     * @param request HTTP请求对象，包含batchId、single、ext等参数
     * @return 上传成功的文件实体对象
     * @throws Exception 上传失败时抛出异常
     */
    @Operation(summary = "上传文件", description = "上传文件到对象存储服务，支持批量ID管理和单文件模式")
    @Parameter(name = "file", description = "上传的文件对象", required = true, schema = @Schema(type = "string", format = "binary"))
    @Parameter(name = "batchId", description = "批量ID，用于文件分组管理", required = false, schema = @Schema(type = "string"))
    @Parameter(name = "single", description = "是否单文件模式，true时会删除同batchId的旧文件", required = false, schema = @Schema(type = "boolean"))
    @Parameter(name = "ext", description = "扩展信息", required = false, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(schema = @Schema(implementation = OSSEntity.class))),
            @ApiResponse(responseCode = "400", description = "上传失败，文件为空或格式错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseRuntimeException("上传文件不能为空");
        }

        String batchId = request.getParameter("batchId");
        String singleStr = request.getParameter("single");
        String ext = request.getParameter("ext");
        if (StringUtils.equals(batchId, "undefined")) {
            batchId = "";
        }
        boolean single = Boolean.parseBoolean(singleStr);
        if (StringUtils.isNotEmpty(batchId) && single) {
            ossService.deleteByBatchId(batchId);
        }
        OSSEntity ossEntity = fileHelper.uploadOssFile(file);
        if (StringUtils.isNotEmpty(batchId)) {
            ossEntity.setBatchId(batchId);
        } else {
            ossEntity.setBatchId(IdGenerator.getInstance().nextStringId());
        }
        ossService.update(ossEntity);
        ossEntity.setExt(ext);
        return R.ok(ossEntity);
    }

    /**
     * 删除文件
     * <p>
     * 根据文件ID删除指定的文件
     * </p>
     *
     * @param id 文件ID
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "删除文件", description = "根据文件ID删除指定的文件")
    @Parameter(name = "id", description = "文件ID", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public R delete(@PathVariable String id) {
        OSSEntity entity = ossService.findById(id);
        if (entity != null) {
            ossService.delete(entity);
            return R.ok();
        }
        return R.error("删除文件失败");
    }

    /**
     * 批量删除文件
     * <p>
     * 支持批量删除多个文件，多个ID用逗号分隔
     * </p>
     *
     * @param ids 文件ID列表，多个ID用逗号分隔
     * @return 操作结果，成功返回空数据，失败返回错误信息
     */
    @Operation(summary = "批量删除文件", description = "支持批量删除多个文件，多个ID用逗号分隔")
    @Parameter(name = "ids", description = "文件ID列表，多个ID用逗号分隔", required = true, schema = @Schema(type = "string"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权限访问")
    })
    @Transactional
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public R deleteMany(@RequestParam String ids) {
        // 使用验证工具类解析和验证参数
        List<String> ossIdList = BatchOperationValidator.validateAndParseIds(ids);
        try {
            for (String id : ossIdList) {
                OSSEntity ossEntity = ossService.findById(id);
                if (ossEntity != null) {
                    ossService.delete(ossEntity);
                }
            }
        } catch (Exception e) {
            return R.error(e);
        }
        return R.ok();
    }
}
