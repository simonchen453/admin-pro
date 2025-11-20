package com.adminpro.web.tools;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.helper.UploadDownloadHelper;
import com.adminpro.rbac.domains.vo.oss.ListOssVo;
import com.adminpro.rbac.domains.vo.oss.ListOssVoConverter;
import com.adminpro.tools.domains.entity.oss.OSSEntity;
import com.adminpro.tools.domains.entity.oss.OSSService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/admin/oss")
@PreAuthorize("@ss.hasPermission('system:oss')")
public class OssController extends BaseRoutingController {

    @Autowired
    private UploadDownloadHelper uploadDownloadHelper;

    @Autowired
    private OSSService ossService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String user() {
        prepareData();
        return "admin/oss/list";
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public R<QueryResultSet<ListOssVo>> paging(HttpServletRequest request) {
        SearchParam param = startPaging();
        QueryResultSet<ListOssVo> map = ossService.search(param).map(ListOssVoConverter.class);
        return R.ok(map);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
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
        OSSEntity ossEntity = uploadDownloadHelper.uploadOssFile(file);
        if (StringUtils.isNotEmpty(batchId)) {
            ossEntity.setBatchId(batchId);
        } else {
            ossEntity.setBatchId(IdGenerator.getInstance().nextStringId());
        }
        ossService.update(ossEntity);
        ossEntity.setExt(ext);
        return R.ok(ossEntity);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public R delete(@PathVariable String id) {
        OSSEntity entity = ossService.findById(id);
        if (entity != null) {
            ossService.delete(entity);
            return R.ok();
        }
        return R.error("删除文件失败");
    }

    @Transactional
    @RequestMapping(value = "/deletemany", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public R deleteMany(@RequestParam String ids) {
        String[] split = ids.split(",");
        try {
            for (int i = 0; i < split.length; i++) {
                String id = split[i];
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
