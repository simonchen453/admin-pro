package com.adminpro.web;

import com.adminpro.core.base.entity.R;
import com.adminpro.core.base.enums.UploadImageType;
import com.adminpro.core.base.util.DateUtil;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.constants.WebConstants;
import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.framework.common.helper.UploadDownloadHelper;
import com.adminpro.rbac.api.LoginHelper;
import com.adminpro.rbac.common.RbacCacheConstants;
import com.adminpro.rbac.common.RbacConstants;
import com.adminpro.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.rbac.domains.entity.dept.DeptService;
import com.adminpro.rbac.domains.entity.domain.DomainEntity;
import com.adminpro.rbac.domains.entity.domain.DomainService;
import com.adminpro.rbac.domains.entity.menu.MenuEntity;
import com.adminpro.rbac.domains.entity.menu.MenuService;
import com.adminpro.rbac.domains.entity.role.RoleService;
import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.adminpro.rbac.domains.entity.user.UserIden;
import com.adminpro.rbac.domains.entity.user.UserService;
import com.adminpro.rbac.domains.vo.menu.MenuTreeVo;
import com.adminpro.rbac.domains.vo.oss.FileUploadVo;
import com.adminpro.rbac.domains.vo.tree.TreeSelect;
import com.adminpro.rbac.domains.vo.user.ChangePwdVo;
import com.adminpro.tools.domains.entity.session.SessionService;
import com.adminpro.tools.domains.entity.syslog.SysLogDTO;
import com.adminpro.tools.domains.entity.syslog.SysLogService;
import com.adminpro.tools.domains.enums.SessionStatus;
import com.adminpro.tools.domains.entity.oss.OSSEntity;
import com.adminpro.tools.domains.entity.oss.OSSService;
import com.adminpro.web.vo.ServerInfo;
import com.adminpro.web.vo.RecentActivityVO;
import com.adminpro.framework.common.annotation.SysLog;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;

@Controller
@RequestMapping(value = "/common")
public class CommonController extends BaseRoutingController {

    @Autowired
    private DeptService deptService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private UploadDownloadHelper uploadDownloadHelper;

    @Autowired
    private OSSService ossService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SysLogService sysLogService;

    /**
     * 获取首页统计数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/statistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<Map<String, Long>> statistics() {
        Map<String, Long> stats = new HashMap<>();
        
        SearchParam userParam = startPaging();
        long userCount = userService.search(userParam).getTotalCount();
        stats.put("userCount", userCount);
        
        SearchParam roleParam = startPaging();
        long roleCount = roleService.findAll().size();
        stats.put("roleCount", roleCount);
        
        List<DeptEntity> deptList = deptService.findAll();
        stats.put("deptCount", (long) deptList.size());
        
        SearchParam sessionParam = startPaging();
        sessionParam.addFilter("status", SessionStatus.ACTIVE.getCode());
        long sessionCount = sessionService.search(sessionParam).getTotalCount();
        stats.put("sessionCount", sessionCount);
        
        return R.ok(stats);
    }

    /**
     * 获取最近活动
     * 
     * @param limit 返回的记录数，默认 10
     * @return 最近活动列表
     */
    @ResponseBody
    @RequestMapping(value = "/recent-activities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<List<RecentActivityVO>> recentActivities(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        try {
            List<SysLogDTO> recentLogs = sysLogService.findRecentLogs(limit);
            List<RecentActivityVO> activities = new ArrayList<>();
            
            for (SysLogDTO log : recentLogs) {
                RecentActivityVO activity = new RecentActivityVO();
                activity.setId(log.getId());
                activity.setType(determineActivityType(log.getDescription(), log.getMethod()));
                activity.setTitle(determineActivityTitle(log.getDescription()));
                activity.setDescription(buildActivityDescription(log));
                activity.setTime(log.getCreatedDate());
                activity.setUser(log.getLoginName());
                activities.add(activity);
            }
            
            return R.ok(activities);
        } catch (Exception e) {
            logger.error("获取最近活动失败", e);
            return R.error("获取最近活动失败: " + e.getMessage());
        }
    }

    /**
     * 确定活动类型
     */
    private String determineActivityType(String operation, String method) {
        if (operation != null) {
            String op = operation.toLowerCase();
            if (op.contains("登录") || op.contains("login")) {
                return "login";
            } else if (op.contains("系统") || (method != null && method.toLowerCase().contains("system"))) {
                return "system";
            }
        }
        return "operation";
    }

    /**
     * 确定活动标题
     */
    private String determineActivityTitle(String operation) {
        if (operation == null) {
            return "系统操作";
        }
        if (operation.contains("登录") || operation.contains("login")) {
            return "用户登录";
        } else if (operation.contains("系统")) {
            return "系统操作";
        }
        return operation;
    }

    /**
     * 构建活动描述
     */
    private String buildActivityDescription(SysLogDTO log) {
        StringBuilder desc = new StringBuilder();
        if (log.getDescription() != null) {
            desc.append(log.getDescription());
        }
        if (log.getParams() != null && !log.getParams().isEmpty()) {
            if (desc.length() > 0) {
                desc.append(" - ");
            }
            desc.append(log.getParams());
        }
        return desc.length() > 0 ? desc.toString() : "系统操作";
    }

    /**
     * 修改密码
     *
     * @param vo
     * @return
     */
    @SysLog("修改密码")
    @PreAuthorize("@ss.hasPermission('common:changepwd')")
    @ResponseBody
    @RequestMapping(value = "/changepwd", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<List<UserEntity>> changepwd(@RequestBody ChangePwdVo vo) {
        UserIden userIden = LoginHelper.getInstance().getLoginUserIden();

        String confirmNewPwd = vo.getConfirmNewPwd();
        String newPwd = vo.getNewPwd();
        if (!StringUtils.equals(confirmNewPwd, newPwd)) {
            return R.error("两次输入的新密码不一致");
        }

        // 校验密码规则
        List<String> passwordErrors = com.adminpro.rbac.api.PasswordValidator.validatePassword(newPwd);
        if (passwordErrors != null && !passwordErrors.isEmpty()) {
            return R.error(String.join("；", passwordErrors));
        }

        String oldPwd = vo.getOldPwd();
        UserEntity entity = UserService.getInstance().changePwd(userIden, oldPwd, newPwd);
        return R.ok(entity);
    }

    /**
     * 获取所有domains
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermission('common:domains')")
    @ResponseBody
    @RequestMapping(value = "/domains", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<List<DomainEntity>> domains() {
        List<DomainEntity> list = DomainService.getInstance().findAll();
        return R.ok(list);
    }

    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<ServerInfo> info() {
        String releaseVersion = ConfigHelper.getString(BaseConstants.SERVER_RELEASE_VERSION_KEY);
        String platformName = ConfigHelper.getString(BaseConstants.PLATFORM_NAME_KEY);
        String platformShortName = ConfigHelper.getString(BaseConstants.PLATFORM_SHORT_NAME_KEY);
        String buildVersion = ConfigHelper.getString(BaseConstants.SERVER_BUILD_VERSION_KEY);
        String copyRight = ConfigHelper.getString(BaseConstants.COPY_RIGHT_KEY);

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setReleaseVersion(releaseVersion);
        serverInfo.setBuildVersion(buildVersion);
        serverInfo.setPlatformName(platformName);
        serverInfo.setPlatformShortName(platformShortName);

        serverInfo.setCopyRight(copyRight);

        return R.ok(serverInfo);
    }

    /**
     * 获取部门下拉树列表
     */
    @PreAuthorize("@ss.hasPermission('common:dept:treeselect')")
    @GetMapping("/dept/treeselect")
    @ResponseBody
    public R deptTreeSelect() {
        SearchParam param = startPaging();
        List<DeptEntity> list = deptService.findByParam(param);
        List<TreeSelect> treeSelects = deptService.buildDeptTreeSelect(list);
        return R.ok(treeSelects);
    }

    /**
     * 获取菜单下拉树列表
     */
    @PreAuthorize("@ss.hasPermission('common:menu:treeselect')")
    @GetMapping("/menu/treeselect")
    @ResponseBody
    public R menuTreeSelect() {
        UserIden loginUserIden = LoginHelper.getInstance().getLoginUserIden();
        SearchParam param = startPaging();
//        param.addFilter("userIden", loginUserIden);
        List<MenuEntity> list = menuService.findByParam(param);
        List<TreeSelect> treeSelects = menuService.buildMenuTreeSelect(list);
        return R.ok(treeSelects);
    }

    @PreAuthorize("@ss.hasPermission('common:changepwd')")
    @GetMapping("/menus")
    @ResponseBody
    public R menus() {
        HttpSession session = request.getSession();
        UserEntity userEntity = LoginHelper.getInstance().getUserEntity();
        if (userEntity != null) {
            List<MenuTreeVo> res = (List<MenuTreeVo>) session.getAttribute(RbacCacheConstants.SESSION_MENU_CACHE);
            List<String> parentMenuIds = (List<String>) session.getAttribute(RbacCacheConstants.SESSION_MENU_PARENT_CACHE);

            if (res == null || parentMenuIds == null) {
                List<MenuEntity> menuEntityList = menuService.findMenuTreeByUserIden(userEntity.getUserIden());
                res = menuService.buildMenus(menuEntityList);
                parentMenuIds = menuService.getParentMenuIds(menuEntityList);
                session.setAttribute(RbacCacheConstants.SESSION_MENU_CACHE, res);
                session.setAttribute(RbacCacheConstants.SESSION_MENU_PARENT_CACHE, parentMenuIds);
            }
            String avatarUrl = "";
            if (userEntity != null) {
                avatarUrl = userEntity.getAvatarUrl();
            }
            if (StringUtils.isEmpty(avatarUrl)) {
                avatarUrl = request.getContextPath() + "/img/avatar.png";
            }
            request.setAttribute("menus", res);
            request.setAttribute("avatarUrl", avatarUrl);
            String activeMenuId = (String) request.getSession().getAttribute(RbacConstants.MENU_SESSION_KEY);
            request.setAttribute("default_opened", new String[]{getTopParentMenuId(activeMenuId, res)});
            if (userEntity != null) {
                if (userEntity.getLatestLoginTime() != null) {
                    request.setAttribute("latest_login_time", DateUtil.formatDateTime(userEntity.getLatestLoginTime()));
                }
                String deptNo = userEntity.getDeptNo();
                DeptEntity deptEntity = DeptService.getInstance().findByNo(deptNo);
                if (deptEntity != null) {
                    request.setAttribute("dept", deptEntity);
                }
            }

            return R.ok(res);
        } else {
            return R.ok();
        }
    }

    private String getTopParentMenuId(String menuId, List<MenuTreeVo> res) {
        for (int i = 0; i < res.size(); i++) {
            MenuTreeVo menuTreeVo = res.get(i);
            List<MenuTreeVo> subs = menuTreeVo.getSubs();
            if (subs != null && subs.size() > 0) {
                boolean included = isIncluded(menuId, subs);
                if (included) {
                    return menuTreeVo.getId();
                }
            }
        }
        return "";
    }

    private boolean isIncluded(String menuId, List<MenuTreeVo> res) {
        for (int i = 0; i < res.size(); i++) {
            MenuTreeVo menuTreeVo = res.get(i);
            if (StringHelper.equals(menuTreeVo.getId(), menuId)) {
                return true;
            } else {
                List<MenuTreeVo> subs = menuTreeVo.getSubs();
                if (subs != null && subs.size() > 0) {
                    return isIncluded(menuId, subs);
                }
            }
        }
        return false;
    }

    /**
     * 加载对应角色菜单列表树
     */
    @PreAuthorize("@ss.hasPermission('common:menu:roleMenuTreeSelect')")
    @GetMapping(value = "/menu/roleMenuTreeSelect/{roleName}")
    @ResponseBody
    public R roleMenuTreeSelect(@PathVariable("roleName") String roleName) {
        SearchParam param = startPaging();
        List<MenuEntity> list = menuService.findByParam(param);

        SearchParam param2 = startPaging();
        param2.addFilter("roleName", roleName);
        List<MenuEntity> list2 = menuService.findByParam(param2);
        List<String> names = new ArrayList<>();
        for (int i = 0; i < list2.size(); i++) {
            names.add(list2.get(i).getName());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("checkedKeys", names);
        map.put("menus", menuService.buildMenuTreeSelect(list));
        return R.ok(map);
    }

    /**
     * OSS文件上传
     */
    @SysLog("OSS文件上传")
    @PreAuthorize("@ss.hasPermission('common:oss:upload')")
    @RequestMapping(value = "/oss/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public R ossUploadFile(@RequestParam("file") MultipartFile file) throws Exception {
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

    /**
     * OSS文件删除
     */
    @SysLog("OSS文件删除")
    @PreAuthorize("@ss.hasPermission('common:oss:upload')")
    @RequestMapping(value = "/oss/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public R ossDeleteFile(@PathVariable String id) throws Exception {
        OSSEntity entity = ossService.findById(id);
        UserIden loginUserIden = LoginHelper.getInstance().getLoginUserIden();
        if (entity != null && entity.isOwner(loginUserIden)) {
            ossService.delete(entity);
            return R.ok();
        }
        return R.error("删除文件失败");
    }

    /**
     * 本地文件上传
     */
    @SysLog("本地文件上传")
    @PreAuthorize("@ss.hasPermission('common:file:upload')")
    @RequestMapping(value = "/file/upload/{type}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public R uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String type) throws Exception {
        if (file.isEmpty()) {
            throw new BaseRuntimeException("上传文件不能为空");
        }
        StringBuffer url = new StringBuffer();
        String dir = DateUtil.formatDate(new Date(), "yyyyMMdd");
        String sep = "/";
        url.append("/file").append(sep).append(dir);
        String fileDir = uploadDownloadHelper.makePublicFileDir(url.toString());
        String originalFilename = file.getOriginalFilename();
        String suffix = uploadDownloadHelper.getSuffix(originalFilename);
        String fileType = uploadDownloadHelper.getFileType(originalFilename);
        String fileName = IdGenerator.getInstance().nextStringId() + suffix;
        String filePath = fileDir + sep + fileName;
        url.append(sep).append(fileName);

        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
        File f = new File(filePath);
        if (StringHelper.equals(type, UploadImageType.ORIGINAL.getCode())) {
            FileUtils.copyInputStreamToFile(bis, f);
        } else {
            Thumbnails.of(bis).size(900, 900).outputFormat(fileType).toFile(f);
        }
        return R.ok(WebConstants.getServerAddress() + request.getContextPath() + "/upload" + url);
    }

    /**
     * 本地文件上传
     */
    @SysLog("本地文件上传")
    @PreAuthorize("@ss.hasPermission('common:file:upload')")
    @RequestMapping(value = "/file/upload2/{type}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public R uploadFile2(@RequestParam("file") MultipartFile file, @PathVariable String type) throws Exception {
        if (file.isEmpty()) {
            throw new BaseRuntimeException("上传文件不能为空");
        }
        StringBuffer url = new StringBuffer();
        String dir = DateUtil.formatDate(new Date(), "yyyyMMdd");
        String sep = "/";
        url.append("/file").append(sep).append(dir);
        String fileDir = uploadDownloadHelper.makePublicFileDir(url.toString());
        String originalFilename = file.getOriginalFilename();
        String suffix = uploadDownloadHelper.getSuffix(originalFilename);
        String fileType = uploadDownloadHelper.getFileType(originalFilename);
        String fileName = IdGenerator.getInstance().nextStringId() + suffix;
        String filePath = fileDir + sep + fileName;
        url.append(sep).append(fileName);

        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
        File f = new File(filePath);
        if (StringHelper.equals(type, UploadImageType.ORIGINAL.getCode())) {
            FileUtils.copyInputStreamToFile(bis, f);
        } else {
            Thumbnails.of(bis).size(900, 900).outputFormat(fileType).toFile(f);
        }
        FileUploadVo vo = new FileUploadVo();
        vo.setRelativePath("/upload" + url);
        vo.setAbsolutePath(WebConstants.getServerAddress() + request.getContextPath() + "/upload" + url);
        return R.ok(vo);
    }
}
