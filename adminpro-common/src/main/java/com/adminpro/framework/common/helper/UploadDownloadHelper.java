package com.adminpro.framework.common.helper;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.util.DateUtil;
import com.adminpro.core.base.util.FileUtil;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.framework.common.constants.WebConstants;
import com.adminpro.tools.api.OSSFactory;
import com.adminpro.tools.domains.entity.oss.OSSEntity;
import com.adminpro.tools.domains.entity.oss.OSSService;
import com.adminpro.web.BaseConstants;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

/**
 * 上传下载帮助类
 */
@Component
public class UploadDownloadHelper {

    public static UploadDownloadHelper getInstance() {
        return SpringUtil.getBean(UploadDownloadHelper.class);
    }

    /**
     * 上传文件
     *
     * @param multipartFile 文件
     */
    public String uploadPublicFile(MultipartFile multipartFile, String category) throws IOException {
        String fileName = makeFileName(multipartFile.getOriginalFilename());
        StringBuffer url = new StringBuffer();
        String dir = DateUtil.formatDate(new Date(), "yyyyMMdd");
        String sep = "/";
        if (StringHelper.isNotEmpty(category)) {
            url.append(sep).append(category);
        }
        url.append(sep).append(dir);

        String fileDir = makePublicFileDir(url.toString());
        String filePath = fileDir + sep + fileName;
        url.append(sep).append(fileName);

        ByteArrayInputStream bis = new ByteArrayInputStream(multipartFile.getBytes());
        File f = new File(filePath);
        Boolean thumbnail = ConfigHelper.getBoolean(BaseConstants.APP_UEDITOR_FILE_STORE_THUMBNAIL, true);
        String fileType = getFileType(fileName);
        if (thumbnail && isImage(fileName)) {
            Thumbnails.of(bis).size(900, 900).outputFormat(fileType).toFile(f);
        } else {
            FileUtils.copyInputStreamToFile(bis, f);
        }
        return WebConstants.getServerAddress() + WebHelper.getContextPath() + "/upload" + url;
    }

    /**
     * 上传私有文件
     *
     * @param multipartFile 文件
     */
    public String uploadPrivateFile(MultipartFile multipartFile, String category) throws IOException {
        String fileName = makeFileName(multipartFile.getOriginalFilename());
        StringBuffer url = new StringBuffer();
        String dir = DateUtil.formatDate(new Date(), "yyyyMMdd");
        String sep = "/";
        if (StringHelper.isNotEmpty(category)) {
            url.append(sep).append(category);
        }
        url.append(sep).append(dir);

        String fileDir = makePrivateFileDir(url.toString());
        String filePath = fileDir + sep + fileName;
        url.append(sep).append(fileName);

        ByteArrayInputStream bis = new ByteArrayInputStream(multipartFile.getBytes());
        File f = new File(filePath);
        FileUtils.copyInputStreamToFile(bis, f);
        return WebConstants.getServerAddress() + WebHelper.getContextPath() + "/upload" + url;
    }

    public String getSuffix(String fileName) {
        return FileUtil.getSuffix(fileName);
    }

    public String getFileType(String fileName) {
        return FileUtil.getFileType(fileName);
    }

    public static void main(String[] args) {
        System.out.println(new UploadDownloadHelper().getSuffix("a.png"));
    }

    /**
     * 上传文件
     *
     * @param file 文件
     */
    public OSSEntity uploadOssFile(MultipartFile file) throws Exception {
        int index = file.getOriginalFilename().lastIndexOf(".");
        String suffix = ".png";
        if (index > 0) {
            suffix = file.getOriginalFilename().substring(index);
        }
        String originalFilename = file.getOriginalFilename();
        OSSEntity entity = OSSFactory.build().uploadSuffix(file.getBytes(), originalFilename, suffix);
        boolean aBoolean = ConfigHelper.getBoolean("app.oss.fetch.frame", false);
        if (aBoolean && isVideo(entity)) {
//            String tempPath = getClass().getClassLoader().getResource("").getPath();
            String tempPath = makePublicFileDir("temp");
            File f = new File(tempPath + "/" + IdGenerator.getInstance().nextId() + suffix);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(file.getBytes());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FileUtil.fetchFrame(f, bos);
            byte[] bytes = bos.toByteArray();
            OSSEntity coverEntity = OSSFactory.build().uploadSuffix(bytes, "cover.jpg", ".jpg");
            entity.setCover(coverEntity.getUrl());
            entity.setCoverKey(coverEntity.getKey());
            entity.setType(OSSEntity.TYPE_VIDEO);
            if (f.exists()) {
                f.delete();
            }
        } else {
            entity.setType(OSSEntity.TYPE_IMAGE);
        }
        OSSService.getInstance().create(entity);
        return entity;
    }

    public boolean isVideo(OSSEntity entity) {
        if (StringUtils.equalsIgnoreCase(entity.getSuffix(), "mp4")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isImage(String fileName) {
        String suffix = getSuffix(fileName);
        if (StringHelper.isEmpty(suffix)) {
            return false;
        } else {
            suffix = suffix.substring(1);
            return StringUtils.equalsIgnoreCase(suffix, "png") || StringUtils.equalsIgnoreCase(suffix, "jpg") || StringUtils.equalsIgnoreCase(suffix, "jpeg");
        }
    }

    public boolean delete(String key) {
        boolean delete = OSSFactory.build().delete(key);
        return delete;
    }

    /**
     * 创建缩略图
     *
     * @param dir
     * @param fileName
     * @return
     */
    public File createThumbnailFile(String dir, String fileName) {
        return FileUtil.createThumbnailFile(dir, fileName);
    }

    /**
     * @param category
     * @return
     */
    public String getTodayFileDir(String category) {
        return FileUtil.getTodayFileDir(category);
    }

    /**
     * 下载文件
     *
     * @param url           例：WEB-INF\resources\appVersion\2017-08-09_10-10-10_v1.0.apk
     * @param request
     * @param response
     * @param messageBundle
     */
    public void download(String url, HttpServletRequest request, HttpServletResponse response, MessageBundle messageBundle) throws IOException {
        FileUtil.download(url, request, response, messageBundle);
    }

    /**
     * 获取文件带http url
     *
     * @param dir
     * @param file
     * @return
     */
    public String getFilePath(String dir, File file) {
        String filePath = WebConstants.getServerAddress() + "/upload" + dir + file.getName();
        return filePath;
    }

    /**
     * 生成上传文件的文件名，文件名以："yyyy-MM-dd_HH-mm-ss-SSS_"+文件的原始名称hashcode
     *
     * @param originalFilename
     * @return
     */
    private String makeFileName(String originalFilename) {
        String tempFileName = IdGenerator.getInstance().nextStringId() + getSuffix(originalFilename);
        return tempFileName;
    }


    /**
     * 构造新的存储目录
     *
     * @return
     */
    public String makePublicFileDir(String dir) {
        return FileUtil.makePublicFileDir(dir);
    }

    /**
     * 构造新的存储目录
     *
     * @return
     */
    public String makePrivateFileDir(String dir) {
        return FileUtil.makePrivateFileDir(dir);
    }

    /**
     * 获取文件根目录绝对地址
     *
     * @return
     */
    public String getFileDir() {
        return FileUtil.getPublicFileDir();
    }

    public String getPrivateFileDir() {
        return FileUtil.getPrivateFileDir();
    }

    public File getFile(String relativeName) {
        return FileUtil.getPublicFile(relativeName);
    }

    public File getPrivateFile(String relativeName) {
        return FileUtil.getPrivateFile(relativeName);
    }
}
