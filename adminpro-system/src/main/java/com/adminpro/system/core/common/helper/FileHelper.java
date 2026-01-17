package com.adminpro.system.core.common.helper;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.util.DateUtil;
import com.adminpro.framework.base.util.FileUtil;
import com.adminpro.framework.base.util.IdGenerator;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.system.core.common.constants.ConfigKeys;
import com.adminpro.system.core.common.constants.WebConstants;
import com.adminpro.system.tools.api.OSSFactory;
import com.adminpro.system.tools.domains.entity.oss.OSSEntity;
import com.adminpro.system.tools.domains.entity.oss.OSSService;
import com.adminpro.system.web.BaseConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;

/**
 * 文件上传下载辅助类
 * <p>
 * 本类提供文件的上传、下载、URL处理等功能，支持：
 * <ul>
 * <li>本地文件系统存储（公共/私有目录）</li>
 * <li>OSS云存储</li>
 * <li>图片压缩和缩略图生成</li>
 * <li>Base64图片上传</li>
 * <li>视频封面提取</li>
 * </ul>
 * <p>
 * 主要功能：
 * <ul>
 * <li>文件上传（公共文件/私有文件）</li>
 * <li>Base64图片上传</li>
 * <li>OSS云存储管理</li>
 * <li>文件下载</li>
 * <li>URL前缀处理</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 * <li>用户头像上传</li>
 * <li>附件上传下载</li>
 * <li>图片存储和处理</li>
 * <li>视频存储</li>
 * </ul>
 */
@Component
public class FileHelper {

    /**
     * 给URL添加前缀（用于返回给前端）
     * <p>
     * 将相对路径URL转换为包含上下文路径和前缀的完整URL。
     * 如果URL已经是http开头的绝对路径，则不做处理
     * <p>
     * 使用场景：
     * <ul>
     * <li>返回文件访问URL给前端</li>
     * <li>图片路径转换</li>
     * </ul>
     *
     * @param url 文件相对路径或绝对URL
     * @return 添加了前缀的完整URL，如果输入为null或已是http开头则原样返回
     */
    public String getUrlWithPrefix(String url) {
        if (url == null || url.startsWith("http")) {
            return url;
        }

        String contextPath = WebHelper.getContextPath();
        String prefix = FileUtil.FILE_URL_PREFIX;
        if ("/".equals(contextPath)) {
            contextPath = "";
        }

        String intendedPrefix = contextPath + prefix;
        if (!url.startsWith(intendedPrefix)) {
            // Check if it already has the prefix partially?
            // e.g. url starts with /upload but missing contextPath?
            // Safer to just ensure it starts with full intended prefix.
            // Assumption: input url is relative path e.g. /2023/file.jpg or
            // /upload/2023/file.jpg (if saved with it)
            // If saved with /upload, we might duplicate.
            // Let's assume input url handles relative path after upload root or absolute
            // path without context.

            // If url is "/upload/file.jpg" and intended is "/ctx/upload", result
            // "/ctx/upload/upload/file.jpg" -> Bad.
            // If url starts with prefix, we do nothing.
            if (url.startsWith(prefix)) {
                return contextPath + url;
            }
            return intendedPrefix + url;
        }
        return url;
    }

    /**
     * 去除URL的前缀 (用于存入数据库)
     */
    public String getUrlWithoutPrefix(String url) {
        String contextPath = WebHelper.getContextPath();
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        String prefix = contextPath + FileUtil.FILE_URL_PREFIX;

        if (url != null && url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }
        return url;
    }

    public static FileHelper getInstance() {
        return SpringUtil.getBean(FileHelper.class);
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
        return url.toString();
    }

    /**
     * 上传Base64文件
     *
     * @param base64Data Base64字符串
     * @param category   分类
     * @return 文件URL
     */
    public String uploadPublicFile(String base64Data, String category) throws IOException {
        if (StringUtils.isEmpty(base64Data)) {
            return null;
        }

        String suffix = ".png"; // Default
        String data = base64Data;
        if (base64Data.contains(",")) {
            String[] parts = base64Data.split(",");
            String header = parts[0];
            data = parts[1];
            if (header.contains("image/jpeg")) {
                suffix = ".jpg";
            } else if (header.contains("image/png")) {
                suffix = ".png";
            } else if (header.contains("image/gif")) {
                suffix = ".gif";
            }
        }

        byte[] bytes = java.util.Base64.getDecoder().decode(data);

        String fileName = IdGenerator.getInstance().nextStringId() + suffix;
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

        File f = new File(filePath);
        FileUtils.writeByteArrayToFile(f, bytes);

        return url.toString();
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
        return url.toString();
    }

    public String getSuffix(String fileName) {
        return FileUtil.getSuffix(fileName);
    }

    public String getFileType(String fileName) {
        return FileUtil.getFileType(fileName);
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
        boolean aBoolean = ConfigHelper.getBoolean(ConfigKeys.Oss.FETCH_FRAME, false);
        if (aBoolean && isVideo(entity)) {
            // String tempPath = getClass().getClassLoader().getResource("").getPath();
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
            return StringUtils.equalsIgnoreCase(suffix, "png") || StringUtils.equalsIgnoreCase(suffix, "jpg")
                    || StringUtils.equalsIgnoreCase(suffix, "jpeg");
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
    public void download(String url, HttpServletRequest request, HttpServletResponse response,
            MessageBundle messageBundle) throws IOException {
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
        String filePath = WebConstants.getServerAddress() + FileUtil.FILE_URL_PREFIX + dir + file.getName();
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
