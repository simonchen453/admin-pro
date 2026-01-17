package com.adminpro.system.web.tools;

import com.adminpro.framework.base.entity.R;
import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.BatchOperationValidator;
import com.adminpro.framework.base.util.BeanUtil;
import com.adminpro.framework.base.util.DateUtil;
import com.adminpro.framework.base.util.FileUtil;
import com.adminpro.framework.base.util.IdGenerator;
import com.adminpro.framework.exceptions.BaseRuntimeException;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.helper.ConfigHelper;
import com.adminpro.system.core.common.helper.FileHelper;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.rbac.domains.entity.apk.APKEntity;
import com.adminpro.system.rbac.domains.entity.apk.APKService;
import com.adminpro.system.rbac.domains.vo.apk.APKVO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(AdminAPKController.PREFIX_URL)
@PreAuthorize("@ss.hasPermission('system:apk')")
public class AdminAPKController extends BaseController {

    protected static final String PREFIX_URL = "/api/v1/apks";

    @Autowired
    private APKService apkService;

    /**
     * 查询APK版本管理列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<QueryResultSet<APKEntity>> list() {
        String condition = request.getParameter("condition");
        SearchParam param = startPaging();

        if (!StringUtils.isEmpty(condition)) {
            param.addFilter("condition", condition);
        }

        QueryResultSet<APKEntity> resultSet = apkService.search(param);
        return R.ok(resultSet);
    }

    /**
     * 新增保存APK版本管理
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public R create(@RequestBody APKVO apk) {
        BeanUtil.beanAttributeValueTrim(apk);
        MessageBundle messageBundle = getMessageBundle();

        String type = apk.getType();
        boolean forceUpdate = apk.isForceUpdate();
        String verName = apk.getVerName();
        Integer verCode = apk.getVerCode();
        String message = apk.getMessage();

        if (StringUtils.isEmpty(type)) {
            messageBundle.addErrorMessage("type", "必填项");
        }

        if (StringUtils.isEmpty(verName)) {
            messageBundle.addErrorMessage("verName", "必填项");
        }

        List<APKEntity> apkEntityList = apkService.findByTypeAndVerCode(apk.getType(), apk.getVerCode());
        if (apkEntityList != null && apkEntityList.size() > 0) {
            messageBundle.addErrorMessage("verCode", "不能重复，请重新填写!");
        }
        String batchId = apk.getBatchId();
        String url = (String) request.getSession().getAttribute(batchId);
        if (StringUtils.isEmpty(batchId) || StringUtils.isEmpty(url)) {
            messageBundle.addErrorMessage("batchId", "必填项");
        }

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            APKEntity apkEntity = new APKEntity();
            apkEntity.setType(type);
            apkEntity.setVerCode(verCode);
            apkEntity.setVerName(verName);
            apkEntity.setDownloadUrl(url);
            apkEntity.setForceUpdate(forceUpdate);
            apkEntity.setMessage(message);
            apkEntity.setOsVersion(ConfigHelper.getString("apk.os.version.default", "1"));
            apkService.create(apkEntity);
            return R.ok();
        }
    }

    /**
     * 修改保存APK版本管理
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public R editSave(@RequestBody APKVO apk) {
        BeanUtil.beanAttributeValueTrim(apk);
        MessageBundle messageBundle = getMessageBundle();

        String id = apk.getId();
        String type = apk.getType();
        Boolean forceUpdate = apk.isForceUpdate();
        String verName = apk.getVerName();
        Integer verCode = apk.getVerCode();
        String osVersion = apk.getOsVersion();
        String message = apk.getMessage();
        String batchId = apk.getBatchId();

        APKEntity apkEntity = null;

        if (StringUtils.isEmpty(id)) {
            messageBundle.addErrorMessage("id", "必填项");
        } else {
            apkEntity = apkService.findById(id);
            if (apkEntity == null) {
                messageBundle.addErrorMessage("id", "记录不存在");
            }
        }

        if (StringUtils.isEmpty(type)) {
            messageBundle.addErrorMessage("type", "必填项");
        }

        if (StringUtils.isEmpty(type)) {
            messageBundle.addErrorMessage("type", "必填项");
        }

        if (StringUtils.isEmpty(verName)) {
            messageBundle.addErrorMessage("verName", "必填项");
        }

        if (StringUtils.isEmpty(osVersion)) {
            messageBundle.addErrorMessage("osVersion", "必填项");
        }

        List<APKEntity> apkEntityList = apkService.findByTypeAndVerCode(apk.getType(), apk.getVerCode());
        if (apkEntityList != null && apkEntityList.size() > 0) {
            for (int i = 0; i < apkEntityList.size(); i++) {
                APKEntity entity = apkEntityList.get(i);
                if (!StringUtils.equals(entity.getId(), id)) {
                    messageBundle.addErrorMessage("verCode", "不能重复，请重新填写!");
                }
            }
        }

        if (messageBundle.hasErrorMessage()) {
            return R.error(messageBundle);
        } else {
            apkEntity.setType(type);
            apkEntity.setVerCode(verCode);
            apkEntity.setVerName(verName);
            apkEntity.setForceUpdate(forceUpdate);
            apkEntity.setMessage(message);
            apkEntity.setOsVersion(apk.getOsVersion());
            apkService.update(apkEntity);

            if (StringUtils.isNotEmpty(batchId)) {
                String url = (String) request.getSession().getAttribute(batchId);
                String oldUrl = apkEntity.getDownloadUrl();
                if (StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(oldUrl)) {
                    apkEntity.setDownloadUrl(url);
                    apkService.update(apkEntity);
                    FileUtil.deleteFile(oldUrl);
                }
            }
            return R.ok();
        }
    }

    /**
     * 删除APK版本管理
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @Transactional
    public R remove(@RequestParam("ids") String ids) {
        List<String> idList = BatchOperationValidator.validateAndParseIds(ids);
        for (String id : idList) {
            APKEntity apkEntity = apkService.findById(id);
            if (apkEntity != null) {
                apkService.delete(id);
                FileUtil.deleteFile(apkEntity.getDownloadUrl());
            }
        }
        return R.ok();
    }

    /**
     * APK下载界面
     *
     * @param id
     */
    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public void download(@PathVariable String id) {
        APKEntity latestVersion = apkService.findById(id);
        if (latestVersion != null) {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            String downLoadUrl = latestVersion.getDownloadUrl();
            String name = downLoadUrl.substring(downLoadUrl.lastIndexOf("/") + 1);
            response.setHeader("Content-Disposition", "attachment;filename=" + name);
            File file = new File(FileHelper.getInstance().getFileDir()
                    + downLoadUrl);
            if (file.exists()) {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    response.setHeader("Content-Disposition",
                            "attachment;Filename=" + URLEncoder.encode(name, "UTF-8"));
                    OutputStream outputStream = response.getOutputStream();
                    byte[] bytes = new byte[2048];
                    int len = 0;
                    while ((len = fileInputStream.read(bytes)) > 0) {
                        outputStream.write(bytes, 0, len);
                    }
                    fileInputStream.close();
                    outputStream.flush();
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseRuntimeException("上传文件不能为空");
        }
        StringBuffer fileName = new StringBuffer();
        String sep = "/";
        String dir = DateUtil.formatDate(new Date(), "yyyyMMdd");
        fileName.append("apks").append(sep).append(dir);
        String fileDir = FileHelper.getInstance().makePublicFileDir(fileName.toString());
        String filePath = fileDir + sep + file.getOriginalFilename();
        File fileDb = new File(filePath);
        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
        String webPath = fileName + sep + file.getOriginalFilename();
        try {
            FileUtils.copyInputStreamToFile(bis, fileDb);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("CopyInputStreamToFile", e);
        }
        String batchId = IdGenerator.getInstance().nextStringId();
        request.getSession().setAttribute(batchId, webPath);
        return R.ok(batchId);
    }
}
