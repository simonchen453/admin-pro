package com.adminpro.tools.ueditor.upload;

import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.framework.common.helper.UploadDownloadHelper;
import com.adminpro.tools.domains.entity.oss.OSSEntity;
import com.adminpro.tools.ueditor.PathFormat;
import com.adminpro.tools.ueditor.define.AppInfo;
import com.adminpro.tools.ueditor.define.BaseState;
import com.adminpro.tools.ueditor.define.FileType;
import com.adminpro.tools.ueditor.define.State;
import com.adminpro.web.BaseConstants;
// commons-fileupload 已移除，使用 Spring Boot 内置的 multipart 支持
// import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BinaryUploader {

    public static final State save(HttpServletRequest request,
                                   Map<String, Object> conf) {
        // FileItemStream fileStream = null;
        // boolean isAjaxUpload = request.getHeader( "X_Requested_With" ) != null;

        // 使用 Spring Boot 内置的 multipart 支持检查
        if (!(request instanceof MultipartHttpServletRequest)) {
            return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
        }

        // ServletFileUpload upload = new ServletFileUpload(
        // 	new DiskFileItemFactory());
        //
        // if ( isAjaxUpload ) {
        //     upload.setHeaderEncoding( "UTF-8" );
        // }

        try {
            // FileItemIterator iterator = upload.getItemIterator(request);
            //
            // while (iterator.hasNext()) {
            // 	fileStream = iterator.next();
            //
            // 	if (!fileStream.isFormField())
            // 		break;
            // 	fileStream = null;
            // }
            //
            // if (fileStream == null) {
            // 	return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
            // }
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = multipartRequest.getFile(conf.get("fieldName").toString());
            if (multipartFile == null) {
                return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
            }

            String savePath = (String) conf.get("savePath");
            //String originFileName = fileStream.getName();
            String originFileName = multipartFile.getOriginalFilename();
            String suffix = FileType.getSuffixByFilename(originFileName);

            originFileName = originFileName.substring(0,
                    originFileName.length() - suffix.length());
            savePath = savePath + suffix;

            long maxSize = ((Long) conf.get("maxSize")).longValue();

            if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
                return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
            }

            savePath = PathFormat.parse(savePath, originFileName);

            //String physicalPath = (String) conf.get("rootPath") + savePath;
            String basePath = (String) conf.get("basePath");
            String physicalPath = basePath + savePath;

            //InputStream is = fileStream.openStream();
            InputStream is = multipartFile.getInputStream();
            State storageState = StorageManager.saveFileByInputStream(is,
                    physicalPath, maxSize);
            is.close();

            String fileUrl = null;
            String type = ConfigHelper.getString(BaseConstants.APP_UEDITOR_FILE_STORE_TYPE, "local");
            if (StringHelper.equals(type, "local")) {
                fileUrl = UploadDownloadHelper.getInstance().uploadPublicFile(multipartFile, "ueditor");
            } else if (StringHelper.equals(type, "oss")) {
                OSSEntity ossEntity = UploadDownloadHelper.getInstance().uploadOssFile(multipartFile);
                fileUrl = ossEntity.getUrl();
            }
            if (storageState.isSuccess()) {
                storageState.putInfo("url", fileUrl);
                storageState.putInfo("type", suffix);
                storageState.putInfo("original", originFileName + suffix);
            }

            return storageState;
            // } catch (FileUploadException e) {
            // 	return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
        } catch (Exception e) {
        }
        return new BaseState(false, AppInfo.IO_ERROR);
    }

    private static boolean validType(String type, String[] allowTypes) {
        List<String> list = Arrays.asList(allowTypes);

        return list.contains(type);
    }
}
