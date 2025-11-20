package com.adminpro.rbac.domains.entity.user;

import com.adminpro.core.base.entity.BaseAuditEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 * 用户表 sys_user_tbl
 *
 * @author simon
 * @date 2020-05-21
 */
public class UserEntity extends BaseAuditEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 表名
     */
    public static final String TABLE_NAME = "SYS_USER_TBL";

    /**
     * 用户域
     */
    public static final String COL_USER_DOMAIN = "COL_USER_DOMAIN";
    /**
     * 用户ID
     */
    public static final String COL_USER_ID = "COL_USER_ID";
    /**
     * 用户登录名称
     */
    public static final String COL_LOGIN_NAME = "COL_LOGIN_NAME";
    /**
     * 用户显示昵称
     */
    public static final String COL_DISPLAY = "COL_DISPLAY";
    /**
     * 用户真实名称
     */
    public static final String COL_REAL_NAME = "COL_REAL_NAME";
    /**
     * 用户身份证号
     */
    public static final String COL_ID_NO = "COL_ID_NO";
    /**
     * 用户邮箱
     */
    public static final String COL_EMAIL = "COL_EMAIL";
    /**
     * 用户状态
     */
    public static final String COL_STATUS = "COL_STATUS";
    /**
     * 是否实名认证
     */
    public static final String COL_AUTHENTICATED = "COL_AUTHENTICATED";
    /**
     * 手机号码
     */
    public static final String COL_MOBILE_NO = "COL_MOBILE_NO";
    /**
     * 是否系统配置
     */
    public static final String COL_IS_SYSTEM = "COL_IS_SYSTEM";
    /**
     * 地址
     */
    public static final String COL_ADDRESS = "COL_ADDRESS";
    /**
     * 密码
     */
    public static final String COL_PWD = "COL_PWD";
    /**
     * 生日
     */
    public static final String COL_BIRTHDAY = "COL_BIRTHDAY";
    /**
     * 性别
     */
    public static final String COL_SEX = "COL_SEX";
    /**
     * 婚姻状态
     */
    public static final String COL_MARITAL = "COL_MARITAL";
    /**
     * 民族
     */
    public static final String COL_NATION = "COL_NATION";
    /**
     * 是否免密支付
     */
    public static final String COL_PAY_NO_PWD = "COL_PAY_NO_PWD";
    /**
     * 支付密码
     */
    public static final String COL_PAY_PWD = "COL_PAY_PWD";
    /**
     * 免密支付阀值
     */
    public static final String COL_PAY_NO_PWD_POINT = "COL_PAY_NO_PWD_POINT";
    /**
     * 头像地址
     */
    public static final String COL_AVATAR_URL = "COL_AVATAR_URL";
    /**
     * 职位
     */
    public static final String COL_POST = "COL_POST";
    /**
     * 职业
     */
    public static final String COL_PROFESSION = "COL_PROFESSION";
    /**
     * 星座
     */
    public static final String COL_CONSTELLATION = "COL_CONSTELLATION";
    /**
     * 第三方账号
     */
    public static final String COL_THIRD_PARTY_USER_NAME = "COL_THIRD_PARTY_USER_NAME";
    /**
     * 第三方密码
     */
    public static final String COL_THIRD_PARTY_PWD = "COL_THIRD_PARTY_PWD";
    /**
     * 工号
     */
    public static final String COL_JOB_NO = "COL_JOB_NO";
    /**
     * 外部用户ID
     */
    public static final String COL_EXT_USER_ID = "COL_EXT_USER_ID";
    /**
     * 实名认证时间
     */
    public static final String COL_AUTHENTICATE_DATE = "COL_AUTHENTICATE_DATE";
    /**
     * 最后登录时间
     */
    public static final String COL_LATEST_LOGIN_TIME = "COL_LATEST_LOGIN_TIME";
    /**
     * 最后修改支付密码时间
     */
    public static final String COL_LATEST_CHANGE_PWD_TIME = "COL_LATEST_CHANGE_PWD_TIME";
    /**
     * 用户描述
     */
    public static final String COL_DESCRIPTION = "COL_DESCRIPTION";
    /**
     * 部门号
     */
    public static final String COL_DEPT_NO = "COL_DEPT_NO";
    /**
     * 分享码
     */
    public static final String COL_SHARE_CODE = "COL_SHARE_CODE";
    /**
     * 上级分享码
     */
    public static final String COL_PARENT_SHARE_CODE = "COL_PARENT_SHARE_CODE";
    /**
     * 用户积分
     */
    public static final String COL_INTEGRAL = "COL_INTEGRAL";
    /**
     * 省
     */
    public static final String COL_PROVINCE = "COL_PROVINCE";
    /**
     * 市
     */
    public static final String COL_CITY = "COL_CITY";
    /**
     * 区
     */
    public static final String COL_DISTRICT = "COL_DISTRICT";

    /**
     * 用户域
     */
    @NotBlank
    @Size(max = 64)
    private String userDomain;
    /**
     * 用户ID
     */
    @NotBlank
    @Size(max = 64)
    private String userId;
    /**
     * 用户登录名称
     */
    @NotBlank
    @Size(max = 255)
    private String loginName;
    /**
     * 用户显示昵称
     */
    @NotBlank
    @Size(max = 255)
    private String display;
    /**
     * 用户真实名称
     */
    @NotBlank
    @Size(max = 255)
    private String realName;
    /**
     * 用户身份证号
     */
    @Size(max = 255)
    private String idNo;
    /**
     * 用户邮箱
     */
    @Email
    @Size(max = 255)
    private String email;
    /**
     * 用户状态
     */
    @Size(max = 11)
    private String status;
    /**
     * 是否实名认证
     */
    private boolean authenticated;
    /**
     * 手机号码
     */
    @Size(max = 255)
    private String mobileNo;
    /**
     * 是否系统配置
     */
    private boolean system;
    /**
     * 地址
     */
    @Size(max = 255)
    private String address;
    /**
     * 密码
     */
    private String password;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 性别
     */
    @Size(max = 12)
    private String sex;
    /**
     * 婚姻状态
     */
    @Size(max = 12)
    private String marital;
    /**
     * 民族
     */
    @Size(max = 255)
    private String nation;
    /**
     * 是否免密支付
     */
    private boolean payNoPwd;
    /**
     * 支付密码
     */
    private String payPwd;
    /**
     * 免密支付阀值
     */
    private Integer payNoPwdPoint;
    /**
     * 头像地址
     */
    @Size(max = 255)
    private String avatarUrl;
    /**
     * 职位
     */
    @Size(max = 255)
    private String post;
    /**
     * 职业
     */
    @Size(max = 255)
    private String profession;
    /**
     * 星座
     */
    @Size(max = 255)
    private String constellation;
    /**
     * 第三方账号
     */
    @Size(max = 255)
    private String thirdPartyUserName;
    /**
     * 第三方密码
     */
    @Size(max = 255)
    private String thirdPartyPwd;
    /**
     * 工号
     */
    @Size(max = 255)
    private String jobNo;
    /**
     * 外部用户ID
     */
    @Size(max = 255)
    private String extUserId;
    /**
     * 实名认证时间
     */
    private Date authenticateDate;
    /**
     * 最后登录时间
     */
    private Date latestLoginTime;
    /**
     * 最后修改支付密码时间
     */
    private Date latestChangePwdTime;
    /**
     * 用户描述
     */
    @Size(max = 255)
    private String description;
    /**
     * 部门号
     */
    @Size(max = 255)
    private String deptNo;
    /**
     * 分享码
     */
    @Size(max = 255)
    private String shareCode;
    /**
     * 上级分享码
     */
    @Size(max = 255)
    private String parentShareCode;
    /**
     * 用户积分
     */
    private Long integral;
    /**
     * 省
     */
    @Size(max = 64)
    private String province;
    /**
     * 市
     */
    @Size(max = 64)
    private String city;
    /**
     * 区
     */
    @Size(max = 64)
    private String district;

    public UserIden userIden;

    public void setUserDomain(String userDomain) {
        this.userDomain = userDomain;
    }

    public String getUserDomain() {
        return userDomain;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }


    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getNation() {
        return nation;
    }

    public void setPayNoPwd(boolean payNoPwd) {
        this.payNoPwd = payNoPwd;
    }

    public boolean getPayNoPwd() {
        return payNoPwd;
    }

    public void setPayPwd(String payPwd) {
        this.payPwd = payPwd;
    }

    public String getPayPwd() {
        return payPwd;
    }

    public void setPayNoPwdPoint(Integer payNoPwdPoint) {
        this.payNoPwdPoint = payNoPwdPoint;
    }

    public Integer getPayNoPwdPoint() {
        return payNoPwdPoint;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPost() {
        return post;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getProfession() {
        return profession;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setThirdPartyUserName(String thirdPartyUserName) {
        this.thirdPartyUserName = thirdPartyUserName;
    }

    public String getThirdPartyUserName() {
        return thirdPartyUserName;
    }

    public void setThirdPartyPwd(String thirdPartyPwd) {
        this.thirdPartyPwd = thirdPartyPwd;
    }

    public String getThirdPartyPwd() {
        return thirdPartyPwd;
    }

    public void setJobNo(String jobNo) {
        this.jobNo = jobNo;
    }

    public String getJobNo() {
        return jobNo;
    }

    public void setExtUserId(String extUserId) {
        this.extUserId = extUserId;
    }

    public String getExtUserId() {
        return extUserId;
    }

    public void setAuthenticateDate(Date authenticateDate) {
        this.authenticateDate = authenticateDate;
    }

    public Date getAuthenticateDate() {
        return authenticateDate;
    }

    public void setLatestLoginTime(Date latestLoginTime) {
        this.latestLoginTime = latestLoginTime;
    }

    public Date getLatestLoginTime() {
        return latestLoginTime;
    }

    public void setLatestChangePwdTime(Date latestChangePwdTime) {
        this.latestChangePwdTime = latestChangePwdTime;
    }

    public Date getLatestChangePwdTime() {
        return latestChangePwdTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setParentShareCode(String parentShareCode) {
        this.parentShareCode = parentShareCode;
    }

    public String getParentShareCode() {
        return parentShareCode;
    }

    public void setIntegral(Long integral) {
        this.integral = integral;
    }

    public Long getIntegral() {
        return integral;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvince() {
        return province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDistrict() {
        return district;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMarital() {
        return marital;
    }

    public void setMarital(String marital) {
        this.marital = marital;
    }

    public boolean isPayNoPwd() {
        return payNoPwd;
    }

    public UserIden getUserIden() {
        return userIden;
    }

    public void setUserIden(UserIden userIden) {
        this.userIden = userIden;
        if (userIden != null) {
            this.userId = userIden.getUserId();
            this.userDomain = userIden.getUserDomain();
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("userDomain", getUserDomain())
                .append("userId", getUserId())
                .append("loginName", getLoginName())
                .append("display", getDisplay())
                .append("realName", getRealName())
                .append("idNo", getIdNo())
                .append("email", getEmail())
                .append("status", getStatus())
                .append("authenticated", getAuthenticated())
                .append("mobileNo", getMobileNo())
                .append("isSystem", isSystem())
                .append("address", getAddress())
                .append("pwd", getPassword())
                .append("birthday", getBirthday())
                .append("sex", getSex())
                .append("marital", getMarital())
                .append("nation", getNation())
                .append("payNoPwd", getPayNoPwd())
                .append("payPwd", getPayPwd())
                .append("payNoPwdPoint", getPayNoPwdPoint())
                .append("avatarUrl", getAvatarUrl())
                .append("post", getPost())
                .append("profession", getProfession())
                .append("constellation", getConstellation())
                .append("thirdPartyUserName", getThirdPartyUserName())
                .append("thirdPartyPwd", getThirdPartyPwd())
                .append("jobNo", getJobNo())
                .append("extUserId", getExtUserId())
                .append("authenticateDate", getAuthenticateDate())
                .append("latestLoginTime", getLatestLoginTime())
                .append("latestChangePwdTime", getLatestChangePwdTime())
                .append("description", getDescription())
                .append("deptNo", getDeptNo())
                .append("shareCode", getShareCode())
                .append("parentShareCode", getParentShareCode())
                .append("integral", getIntegral())
                .append("province", getProvince())
                .append("city", getCity())
                .append("district", getDistrict())
                .append("createdByUserDomain", getCreatedByUserDomain())
                .append("createdByUserId", getCreatedByUserId())
                .append("createdDate", getCreatedDate())
                .append("updatedByUserDomain", getUpdatedByUserDomain())
                .append("updatedByUserId", getUpdatedByUserId())
                .append("updatedDate", getUpdatedDate())
                .toString();
    }
}
