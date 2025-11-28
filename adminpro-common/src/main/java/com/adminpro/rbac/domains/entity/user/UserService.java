package com.adminpro.rbac.domains.entity.user;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.exceptions.BaseRuntimeException;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.cache.AppCache;
import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.rbac.domains.vo.user.UserExportVo;
import com.adminpro.rbac.domains.vo.user.UserImportVo;
import com.adminpro.rbac.enums.UserStatus;
import com.adminpro.framework.security.auth.TokenGenerator;
import com.adminpro.rbac.api.PasswordHelper;
import com.adminpro.rbac.common.RbacCacheConstants;
import com.adminpro.rbac.common.RbacConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import org.apache.poi.ss.usermodel.Workbook;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.List;

@Service
public class UserService extends BaseService<UserEntity, UserIden> {

    @Autowired
    protected UserService(UserDao dao) {
        super(dao);
    }

    public static UserService getInstance() {
        return SpringUtil.getBean(UserService.class);
    }

    @Autowired
    private UserDao dao;

    public QueryResultSet<UserEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<UserEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    @Transactional
    public void update(UserEntity entity) {
        dao.update(entity);
        AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, entity.getUserIden().toSecurityUsername());
    }

    @Transactional
    public UserEntity resetPwd(UserIden userIden, String newPassword) {
        UserEntity entity = UserService.getInstance().findById(userIden);
        String newPwd = PasswordHelper.encryptPwd(entity.getUserIden(), newPassword);
        entity.setPassword(newPwd);
        dao.update(entity);
        AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, entity.getUserIden().toSecurityUsername());
        return entity;
    }

    public String authLogin(UserIden userIden, String password) {
        UserEntity entity = UserService.getInstance().findById(userIden);
        String newPwd = PasswordHelper.encryptPwd(userIden, password);
        boolean restRequest = WebHelper.isRestRequest();
        if (restRequest) {
            if (StringUtils.equals(newPwd, entity.getPassword())) {
                String token = TokenGenerator.generateValue();
                return token;
            }
        } else {
            if (StringUtils.equals(newPwd, entity.getPassword())) {
                return "success";
            }
        }
        return null;
    }

    @Transactional
    public UserEntity changePwd(UserIden userIden, String oldPwd, String newPassword) {
        UserEntity entity = UserService.getInstance().findById(userIden);

        if (entity == null) {
            throw new BaseRuntimeException("用户不存在");
        }

        String encryptPwd = PasswordHelper.encryptPayPwd(userIden, oldPwd);
        String newPwd = PasswordHelper.encryptPwd(entity.getUserIden(), newPassword);
        if (StringUtils.equals(encryptPwd, entity.getPassword())) {
            entity.setPassword(newPwd);
            dao.update(entity);
            AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, entity.getUserIden().toSecurityUsername());
            return entity;
        } else {
            throw new BaseRuntimeException("原密码不正确");
        }
    }

    @Transactional
    public void create(UserEntity entity) {
        String password = entity.getPassword();
        if (StringUtils.isEmpty(password)) {
            entity.setPassword(ConfigHelper.getString(RbacConstants.USER_DEFAULT_PASSWORD));
        }
        String encryptPwd = PasswordHelper.encryptPwd(entity.getUserIden(), entity.getPassword());

        entity.setPassword(encryptPwd);
        dao.create(entity);
        AppCache.getInstance().delete(RbacCacheConstants.AUTH_USER_DETAIL_CACHE, entity.getUserIden().toSecurityUsername());
    }

    public UserEntity findById(UserIden userIden) {
        return dao.findById(userIden);
    }

    public List<UserEntity> findByDomain(String domain) {
        return dao.findByDomain(domain);
    }

    public UserEntity findByUserDomainAndUserId(String userDomain, String userId) {
        return dao.findById(new UserIden(userDomain, userId));
    }

    public UserEntity findByUserDomainAndLoginName(String userDomain, String loginName) {
        return dao.findByUserDomainAndLoginName(userDomain, loginName);
    }

    public UserEntity findByDomainAndDisplay(String domain, String display) {
        return dao.findByDomainAndDisplay(domain, display);
    }

    public UserEntity findByExtUserId(String extUserId) {
        return dao.findByExtUserId(extUserId);
    }

    public UserEntity findByDomainAndEmail(String domain, String email) {
        return dao.findByDomainAndEmail(domain, email);
    }

    public UserEntity findByDomainAndMobileNo(String domain, String mobileNo) {
        return dao.findByDomainAndMobileNo(domain, mobileNo);
    }

    public List<UserEntity> findByUserDomainAndLikeLoginName(String userDomain, String loginNameLike) {
        return dao.findByUserDomainAndLikeLoginName(userDomain, loginNameLike);
    }

    public List<UserEntity> findByDomainAndLikeMobileNo(String domain, String mobileNoLike) {
        return dao.findByDomainAndLikeMobileNo(domain, mobileNoLike);
    }

    public List<UserEntity> findByDomainAndLikeUserId(String domain, String userIdLike) {
        return dao.findByDomainAndLikeUserId(domain, userIdLike);
    }

    /**
     * @param users
     */
    @Transactional
    public void deleteMany(String users) {
        String[] userDomainIdArray = StringUtils.split(users, ",");
        for (String userDomainId : userDomainIdArray) {
            String[] split = userDomainId.split("_");
            UserIden userIden = new UserIden(split[0], split[1]);
            dao.delete(userIden);
        }
    }

    @Transactional
    public void delete(UserIden userIden) {
        dao.delete(userIden);
    }

    /**
     * 验证密码是否正确
     *
     * @param userIden
     * @param password
     * @return
     */
    public boolean authenticate(UserIden userIden, String password) {
        UserEntity userEntity = UserService.getInstance().findById(userIden);
        String encryptPwd = PasswordHelper.encryptPwd(userIden, password);
        return StringUtils.equals(encryptPwd, userEntity.getPassword());
    }

    @Transactional
    public void importExcel(List<UserImportVo> importList) {
        for (UserImportVo importVo : importList) {
            if (StringUtils.isEmpty(importVo.getUserDomain()) || StringUtils.isEmpty(importVo.getLoginName())) {
                continue;
            }
            UserEntity existingUser = findByUserDomainAndLoginName(importVo.getUserDomain(), importVo.getLoginName());
            if (existingUser == null) {
                UserEntity user = new UserEntity();
                user.setUserIden(new UserIden(importVo.getUserDomain(), IdGenerator.getInstance().nextStringId()));
                user.setUserDomain(importVo.getUserDomain());
                user.setLoginName(importVo.getLoginName());
                user.setRealName(importVo.getRealName());
                user.setDisplay(StringUtils.isNotEmpty(importVo.getDisplay()) ? importVo.getDisplay() : importVo.getRealName());
                user.setEmail(importVo.getEmail());
                user.setMobileNo(importVo.getMobileNo());
                user.setStatus(StringUtils.isNotEmpty(importVo.getStatus()) ? importVo.getStatus() : UserStatus.ACTIVE.getCode());
                user.setSex(importVo.getSex());
                user.setDescription(importVo.getDescription());
                user.setDeptNo(importVo.getDeptNo());
                user.setJobNo(importVo.getJobNo());
                user.setAddress(importVo.getAddress());
                user.setBirthday(importVo.getBirthday());
                user.setIdNo(importVo.getIdNo());
                
                if (StringUtils.isNotEmpty(importVo.getPassword())) {
                    user.setPassword(importVo.getPassword());
                } else {
                    user.setPassword(ConfigHelper.getString(RbacConstants.USER_DEFAULT_PASSWORD));
                }
                create(user);
            } else {
                if (StringUtils.isNotEmpty(importVo.getRealName())) {
                    existingUser.setRealName(importVo.getRealName());
                }
                if (StringUtils.isNotEmpty(importVo.getDisplay())) {
                    existingUser.setDisplay(importVo.getDisplay());
                }
                if (StringUtils.isNotEmpty(importVo.getEmail())) {
                    existingUser.setEmail(importVo.getEmail());
                }
                if (StringUtils.isNotEmpty(importVo.getMobileNo())) {
                    existingUser.setMobileNo(importVo.getMobileNo());
                }
                if (StringUtils.isNotEmpty(importVo.getStatus())) {
                    existingUser.setStatus(importVo.getStatus());
                }
                if (StringUtils.isNotEmpty(importVo.getDescription())) {
                    existingUser.setDescription(importVo.getDescription());
                }
                if (StringUtils.isNotEmpty(importVo.getSex())) {
                    existingUser.setSex(importVo.getSex());
                }
                if (StringUtils.isNotEmpty(importVo.getDeptNo())) {
                    existingUser.setDeptNo(importVo.getDeptNo());
                }
                if (StringUtils.isNotEmpty(importVo.getJobNo())) {
                    existingUser.setJobNo(importVo.getJobNo());
                }
                if (StringUtils.isNotEmpty(importVo.getAddress())) {
                    existingUser.setAddress(importVo.getAddress());
                }
                if (importVo.getBirthday() != null) {
                    existingUser.setBirthday(importVo.getBirthday());
                }
                if (StringUtils.isNotEmpty(importVo.getIdNo())) {
                    existingUser.setIdNo(importVo.getIdNo());
                }
                if (StringUtils.isNotEmpty(importVo.getPassword())) {
                    String encryptPwd = PasswordHelper.encryptPwd(existingUser.getUserIden(), importVo.getPassword());
                    existingUser.setPassword(encryptPwd);
                }
                update(existingUser);
            }
        }
    }

    public void exportExcel(HttpServletResponse response, List<UserEntity> list) throws Exception {
        Workbook book = generateWorkbook(list);

        response.reset();
        response.setContentType("application/x-msdownload");
        String fileName = "用户数据";
        fileName = fileName + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("gb2312"), "ISO-8859-1") + ".xls");
        ServletOutputStream outStream = null;
        try {
            outStream = response.getOutputStream();
            book.write(outStream);
        } finally {
            book.close();
            if (outStream != null) {
                outStream.close();
            }
        }
    }

    private Workbook generateWorkbook(List<UserEntity> list) {
        List<UserExportVo> exportList = convertToExportVo(list);
        ExportParams params = new ExportParams();
        params.setSheetName("用户数据");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("title", params);
        dataMap.put("entity", UserExportVo.class);
        dataMap.put("data", exportList);
        List<Map<String, Object>> sheetsList = new ArrayList<>();
        sheetsList.add(dataMap);
        return ExcelExportUtil.exportExcel(sheetsList, ExcelType.HSSF);
    }

    private List<UserExportVo> convertToExportVo(List<UserEntity> list) {
        List<UserExportVo> exportList = new ArrayList<>();
        for (UserEntity user : list) {
            UserExportVo vo = new UserExportVo();
            vo.setUserDomain(user.getUserDomain());
            vo.setLoginName(user.getLoginName());
            vo.setRealName(user.getRealName());
            vo.setDisplay(user.getDisplay());
            vo.setMobileNo(user.getMobileNo());
            vo.setEmail(user.getEmail());
            vo.setStatus(user.getStatus());
            vo.setSex(user.getSex());
            vo.setDescription(user.getDescription());
            vo.setDeptNo(user.getDeptNo());
            vo.setJobNo(user.getJobNo());
            vo.setAddress(user.getAddress());
            vo.setBirthday(user.getBirthday());
            vo.setIdNo(user.getIdNo());
            vo.setLatestLoginTime(user.getLatestLoginTime());
            vo.setCreatedDate(user.getCreatedDate());
            exportList.add(vo);
        }
        return exportList;
    }
}
