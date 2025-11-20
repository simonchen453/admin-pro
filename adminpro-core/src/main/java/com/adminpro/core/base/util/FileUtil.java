package com.adminpro.core.base.util;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.core.exceptions.SysException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    private static final Map<String, BigInteger> UNIT_TYPES = new HashMap<>();
    public static String PUBLIC_FILE_DIR = ConfigUtil.getString("app.upload.public.dir");
    public static String PRIVATE_FILE_DIR = ConfigUtil.getString("app.upload.private.dir");

    static {
        // bytes in a kilobyte
        final BigInteger ONE_KB = BigInteger.valueOf(1024L);
        // bytes in a megabyte
        final BigInteger ONE_MB = ONE_KB.multiply(ONE_KB);
        // bytes in a gigabyte
        final BigInteger ONE_GB = ONE_KB.multiply(ONE_MB);
        // bytes in a terabyte
        final BigInteger ONE_TB = ONE_KB.multiply(ONE_GB);
        // bytes in a petabyte
        final BigInteger ONE_PB = ONE_KB.multiply(ONE_TB);
        // bytes in an exabyte (zettabyte, yottabyte)
        final BigInteger ONE_EB = ONE_KB.multiply(ONE_PB);

        UNIT_TYPES.put("KB", ONE_KB);
        UNIT_TYPES.put("MB", ONE_MB);
        UNIT_TYPES.put("GB", ONE_GB);
        UNIT_TYPES.put("TB", ONE_TB);
        UNIT_TYPES.put("PB", ONE_PB);
        UNIT_TYPES.put("EB", ONE_EB);
    }

    /**
     * parse the data from file data url
     *
     * @param dataUrl
     * @return
     */
    public static Map<String, Object> parseDataUrl(String dataUrl) {
        Map<String, Object> result = null;
        if (StringUtil.isNotBlank(dataUrl)) {
            result = new HashMap<>();
            result.put("fileName", System.nanoTime() + "." + dataUrl.substring(dataUrl.indexOf('/') + 1, dataUrl.indexOf(';')));
            byte[] data = SecurityUtil.decodeBase64(dataUrl.substring(dataUrl.indexOf("base64,") + 7));
            result.put("fileData", data);
            if (data != null) {
                result.put("fileSize", data.length);
            }
            result.put("mimeType", dataUrl.substring(dataUrl.indexOf("data:") + 5, dataUrl.indexOf(";base64,")));
        }
        return result;
    }

    /**
     * Save file to the given path (local path)
     *
     * @param filePath
     * @param fileData
     */
    public static void saveFile(String filePath, byte[] fileData) throws IOException {
        File file = new File(filePath);
        FileUtils.writeByteArrayToFile(file, fileData);
        logger.info("File is saved to {}.", filePath);
    }

    public static String readFile(String filePath, String encoding) throws IOException {
        return FileUtils.readFileToString(new File(filePath), encoding);
    }

    public static String readFile(String filePath) throws IOException {
        return readFile(filePath, "UTF-8");
    }

    public static String getExtension(String fileName) {
        return FilenameUtils.getExtension(fileName);
    }

    public static String getMimeType(String fileName) {
        Path path = Paths.get(fileName);
        try {
            return Files.probeContentType(path);
        } catch (IOException x) {
            throw new SysException("The mime type is not recognized.", x);
        }
    }

    /**
     * get file size for specific unit type
     *
     * @param size
     * @param unitType acceptable values: KB, MB, GB, TB, PB, EB
     * @return
     */
    public static double getFileSize(long size, String unitType) {
        BigInteger biSize = BigInteger.valueOf(size);
        BigInteger unit = UNIT_TYPES.get(unitType);
        if (unit == null) {
            throw new SysException(String.format("The given unit type [%s] is undefined.", unitType));
        }

        return biSize.divide(unit).doubleValue();
    }

    public static String getFileDisplaySize(long size) {
        BigInteger biSize = BigInteger.valueOf(size);
        String displaySize;
        if (biSize.divide(UNIT_TYPES.get("EB")).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(biSize.divide(UNIT_TYPES.get("EB"))) + " EB";
        } else if (biSize.divide(UNIT_TYPES.get("PB")).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(biSize.divide(UNIT_TYPES.get("PB"))) + " PB";
        } else if (biSize.divide(UNIT_TYPES.get("TB")).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(biSize.divide(UNIT_TYPES.get("TB"))) + " TB";
        } else if (biSize.divide(UNIT_TYPES.get("GB")).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(biSize.divide(UNIT_TYPES.get("GB"))) + " GB";
        } else if (biSize.divide(UNIT_TYPES.get("MB")).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(biSize.divide(UNIT_TYPES.get("MB"))) + " MB";
        } else if (biSize.divide(UNIT_TYPES.get("KB")).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(biSize.divide(UNIT_TYPES.get("KB"))) + " KB";
        } else {
            displaySize = String.valueOf(biSize) + " bytes";
        }
        return displaySize;
    }

    public static String genMd5FileChecksum(byte[] fileData) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return genFileChecksum(fileData, md);
        } catch (Exception x) {
            throw new SysException("Failed to generate MD5 file checksum.", x);
        }
    }

    public static String genSha256FileChecksum(byte[] fileData) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA256");
            return genFileChecksum(fileData, md);
        } catch (Exception x) {
            logger.error("Failed to generate SHA256 file checksum.", x);
            throw new BaseRuntimeException(x);
        }
    }

    private static String genFileChecksum(byte[] fileData, MessageDigest md) {
        InputStream is = new ByteArrayInputStream(fileData);
        DigestInputStream dis = new DigestInputStream(is, md);
        try {
            while (dis.read() != -1) {
            }
            is.close();
            dis.close();
        } catch (Exception x) {
            throw new SysException("Failed to close resources.", x);
        }
        // get the hash's bytes
        byte[] hashInBytes = md.digest();

        // convert bytes to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * @param filePath started with "/", and from context path
     * @return
     * @throws IOException
     */
    public static byte[] readFileAsBytes(String filePath) {
        try (InputStream input = CommonUtil.getInstance().getServletContext().getResourceAsStream(filePath);) {
            return IOUtils.toByteArray(input);

        } catch (IOException e) {
            throw new BaseRuntimeException(e);
        }
    }

    /**
     * @param filePath started with "/", and from context path
     * @return
     * @throws IOException
     */
    public static void readFileToOutputStream(String filePath, OutputStream outputStream) {
        try (InputStream input = CommonUtil.getInstance().getServletContext().getResourceAsStream(filePath);) {
            IOUtils.copy(input, outputStream);

        } catch (IOException e) {
            throw new BaseRuntimeException(e);
        }
    }

    public static byte[] readFileAsBytes(File file) {
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            logger.error("File not found", e);
        } catch (IOException e) {
            logger.error("IO error while reading file", e);
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(bos);
        }
        return buffer;
    }

    public static void fetchFrame(File file, OutputStream fos) {
        FFmpegFrameGrabber ff = null;
        try {
            ff = new FFmpegFrameGrabber(file);
            ff.start();
            int lenght = ff.getLengthInFrames();
            int i = 0;
            Frame f = null;
            while (i < lenght) {
                // 过滤前5帧，避免出现全黑的图片，依自己情况而定
                f = ff.grabFrame();
                if ((i > 5) && (f.image != null)) {
                    break;
                }
                i++;
            }

            BufferedImage bufferedImage = frameToBufferedImage(f);
            ImageIO.write(bufferedImage, "jpg", fos);
        } catch (Exception e) {
            logger.error("Error fetching frame from video", e);
        } finally {
            IOUtils.closeQuietly(ff);
        }
    }

    /**
     * 获取视频文件的封面 - 放弃前5帧
     *
     * @param video
     * @return
     */
    public static byte[] fetchFrame(File video) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            fetchFrame(video, bos);
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (Exception e) {
            logger.error("Error fetching frame from video file", e);
        } finally {
            IOUtils.closeQuietly(bos);
        }
        return new byte[0];
    }

    public static BufferedImage frameToBufferedImage(Frame frame) {
        //创建BufferedImage对象
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }

    public static String md5HashCode(InputStream fis) {
        try {
            //拿到一个MD5转换器,如果想使用SHA-1或SHA-256，则传入SHA-1,SHA-256
            MessageDigest md = MessageDigest.getInstance("MD5");

            //分多次将一个文件读入，对于大型文件而言，比较推荐这种方式，占用内存比较少。
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            fis.close();
            //转换并返回包含16个元素字节数组,返回数值范围为-128到127
            byte[] resultByte = md.digest();
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < resultByte.length; ++i) {
                int v = 0xFF & resultByte[i];
                if (v < 16) {
                    result.append("0");
                }
                result.append(Integer.toHexString(v));
            }

            return result.toString();//转换为16进制
        } catch (Exception e) {
            logger.error("Error calculating MD5 hash", e);
            return "";
        }
    }

    public static String readFileAsBase64(File file) {
        byte[] bytes = readFileAsBytes(file);
        String base64 = new String(Base64.encodeBase64(bytes));
        return base64;
    }

    /**
     * @param fileName
     */
    public static void deleteFile(String fileName) {
        String uploadPath = PUBLIC_FILE_DIR;
        if (uploadPath.startsWith("file:")) {
            uploadPath = uploadPath.substring(uploadPath.indexOf(":") + 1);
        }

        if (StringUtils.isEmpty(fileName)) {
            return;
        } else if (fileName.startsWith("/upload")) {
            fileName = fileName.substring("/upload".length());
        } else if (fileName.startsWith("upload")) {
            fileName = fileName.substring("upload".length());
        }
        String path = uploadPath + fileName;
        File f = new File(path);
        if (f.exists()) {
            FileUtils.deleteQuietly(f);
        }
    }

    public static String makePrivateFileDir(String dir) {
        //构造新的保存目录
        String path = null;
        String privatePath = FileUtil.PRIVATE_FILE_DIR;
        if (privatePath.startsWith("file:")) {
            path = privatePath.substring(privatePath.indexOf(":") + 1) + dir;
        } else if (privatePath.startsWith("classpath:")) {
            String tempPath = FileUtil.class.getClassLoader().getResource("").getPath();
            path = tempPath + privatePath.substring(privatePath.indexOf(":") + 1) + dir;
        }
        //如果目录不存在
        if (StringUtils.isNotEmpty(path)) {
            File file = new File(path);
            if (!file.exists()) {
                //创建目录
                boolean mkdirsFlag = new File(path).mkdirs();
                if (!mkdirsFlag) {
                    throw new RuntimeException("Can not make dir " + path);
                }
            }
        }
        return path;
    }

    /**
     * 构造新的存储目录
     *
     * @return
     */
    public static String makePublicFileDir(String dir) {
        //构造新的保存目录
        String path = null;
        String publicPath = FileUtil.PUBLIC_FILE_DIR;
        if (publicPath.startsWith("file:")) {
            path = publicPath.substring(publicPath.indexOf(":") + 1) + dir;
        } else if (publicPath.startsWith("classpath:")) {
            String tempPath = FileUtil.class.getClassLoader().getResource("").getPath();
            path = tempPath + publicPath.substring(publicPath.indexOf(":") + 1) + dir;
        }
        //如果目录不存在
        if (StringUtils.isNotEmpty(path)) {
            File file = new File(path);
            if (!file.exists()) {
                //创建目录
                boolean mkdirsFlag = new File(path).mkdirs();
                if (!mkdirsFlag) {
                    throw new RuntimeException("Can not make dir " + path);
                }
            }
        }
        return path;
    }

    /**
     * 获取文件根目录绝对地址
     *
     * @return
     */
    public static String getPublicFileDir() {
        String path = null;
        String publicPath = FileUtil.PUBLIC_FILE_DIR;
        if (publicPath.startsWith("file:")) {
            path = publicPath.substring(publicPath.indexOf(":") + 1);
        } else if (publicPath.startsWith("classpath:")) {
            path = FileUtil.class.getClassLoader().getResource("/").getPath() + publicPath.substring(publicPath.indexOf(":") + 1);
        }
        return path;
    }

    public static String getPrivateFileDir() {
        String path = null;
        String privatePath = FileUtil.PRIVATE_FILE_DIR;
        if (privatePath.startsWith("file:")) {
            path = privatePath.substring(privatePath.indexOf(":") + 1);
        } else if (privatePath.startsWith("classpath:")) {
            path = FileUtil.class.getClassLoader().getResource("/").getPath() + privatePath.substring(privatePath.indexOf(":") + 1);
        }
        return path;
    }

    public static File getPublicFile(String relativeName) {
        String path = getPublicFileDir() + relativeName;
        File f = new File(path);
        return f;
    }

    public static File getPrivateFile(String relativeName) {
        String path = getPrivateFileDir() + relativeName;
        File f = new File(path);
        return f;
    }

    /**
     * 下载文件
     *
     * @param url           例：WEB-INF\resources\appVersion\2017-08-09_10-10-10_v1.0.apk
     * @param request
     * @param response
     * @param messageBundle
     */
    public static void download(String url, HttpServletRequest request, HttpServletResponse response, MessageBundle messageBundle) throws IOException {
        //得到要下载的文件
        File file = new File(url);
        //如果文件不存在
        if (!file.exists()) {
            messageBundle.addErrorMessage(null, "您要下载的资源已被删除。");
            return;
        }
        //处理文件名
        String realName = url.substring(url.lastIndexOf("/") + 1);
        String fileName = realName.substring(20);
        //设置响应头，控制浏览器下载该文件
        response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        //读取要下载的文件，保存到文件输入流
        FileInputStream in = new FileInputStream(file);
        //创建输出流
        OutputStream out = response.getOutputStream();
        //创建缓冲区
        byte buffer[] = new byte[1024];
        int len = 0;
        //循环将输入流中的内容读取到缓冲区当中
        while ((len = in.read(buffer)) > 0) {
            //输出缓冲区的内容到浏览器，实现文件下载
            out.write(buffer, 0, len);
        }
        //关闭文件输入流
        in.close();
        //关闭输出流
        out.close();
    }

    /**
     * 创建缩略图
     *
     * @param dir
     * @param fileName
     * @return
     */
    public static File createThumbnailFile(String dir, String fileName) {
        fileName = makePublicFileDir(fileName);
        String path = makePublicFileDir(dir);
        File f = new File(path + fileName);
        return f;
    }

    /**
     * @param category
     * @return
     */
    public static String getTodayFileDir(String category) {
        String dir = DateUtil.formatDate(new Date(), "yyyyMMdd");
        StringBuffer fileName = new StringBuffer();
        String sep = File.separator;
        fileName.append(category).append(sep).append(dir);
        String fileDir = makePublicFileDir(fileName.toString());
        return fileDir;
    }

    public static boolean isImage(String fileName) {
        String suffix = getSuffix(fileName);
        if (StringUtils.isEmpty(suffix)) {
            return false;
        } else {
            suffix = suffix.substring(1);
            return StringUtils.equalsIgnoreCase(suffix, "png") || StringUtils.equalsIgnoreCase(suffix, "jpg") || StringUtils.equalsIgnoreCase(suffix, "jpeg");
        }
    }

    public static String getSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            return fileName.substring(index);
        } else {
            return null;
        }
    }

    public static String getFileName(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            return fileName.substring(0, index);
        } else {
            return null;
        }
    }

    public static String getFileType(String fileName) {
        String suffix = getSuffix(fileName);
        if (StringUtils.isNotEmpty(suffix)) {
            return suffix.substring(1);
        } else {
            return null;
        }
    }

    /**
     * 读取类路径下文件内容
     *
     * @param fileName 文件名（包括相对路径）
     * @return 文件内容的字符串表示形式，如果文件不存在或读取失败，则返回null
     */
    public static String readFromClasspath(String fileName) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            // 使用类加载器获取输入流
            InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        contentBuilder.append(line).append("\n");
                    }
                }
            } else {
                // 文件不存在
                logger.error("File not found: " + fileName);
                return null;
            }
        } catch (IOException e) {
            // 读取失败
            logger.error("读取失败: ", e);
            return null;
        }
        return contentBuilder.toString();
    }

    private FileUtil() {
    }

}
