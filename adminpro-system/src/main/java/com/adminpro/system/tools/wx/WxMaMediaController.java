package com.adminpro.system.tools.wx;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import com.adminpro.framework.base.entity.R;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * <pre>
 *  小程序临时素材接口
 *  Created by BinaryWang on 2017/6/16.
 * </pre>
 */
@RestController
@RequestMapping("/api/wechat/media")
@Tag(name = "微信小程序素材管理", description = "微信小程序临时素材上传和下载接口")
public class WxMaMediaController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WxMaService service;

    /**
     * 上传临时素材
     *
     * @return 素材的media_id列表，实际上如果有的话，只会有一个
     */
    @PostMapping("/upload")
    @Operation(summary = "上传微信小程序临时素材", description = "上传图片等临时素材到微信服务器，返回素材的media_id列表")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 上传成功，返回素材的media_id列表
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=500: 微信接口错误或服务器内部错误
                """,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = R.class))
    )
    public List<String> uploadMedia(HttpServletRequest request) throws WxErrorException {
        // 使用 Spring Boot 内置的 multipart 支持
        if (!(request instanceof MultipartHttpServletRequest)) {
            return Lists.newArrayList();
        }

        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        Iterator<String> it = multiRequest.getFileNames();
        List<String> result = Lists.newArrayList();
        while (it.hasNext()) {
            try {
                MultipartFile file = multiRequest.getFile(it.next());
                File newFile = new File(Files.createTempDir(), file.getOriginalFilename());
                this.logger.info("filePath is ：" + newFile.toString());
                file.transferTo(newFile);
                WxMediaUploadResult uploadResult = this.service.getMediaService().uploadMedia(WxMaConstants.KefuMsgType.IMAGE, newFile);
                this.logger.info("media_id ： " + uploadResult.getMediaId());
                result.add(uploadResult.getMediaId());
            } catch (IOException e) {
                this.logger.error(e.getMessage(), e);
            }
        }

        return result;
    }

    /**
     * 下载临时素材
     */
    @GetMapping("/download/{mediaId}")
    @Operation(summary = "下载微信小程序临时素材", description = "根据media_id从微信服务器下载临时素材文件")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 下载成功，返回文件流
                - restCode=400: 参数错误
                - restCode=401: 未授权，需要登录
                - restCode=500: 微信接口错误或服务器内部错误
                """,
        content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = @Schema(implementation = R.class))
    )
    public File getMedia(@Parameter(description = "微信素材ID", required = true) @PathVariable String mediaId) throws WxErrorException {
        return this.service.getMediaService().getMedia(mediaId);
    }
}
