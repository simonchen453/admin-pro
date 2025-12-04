package com.adminpro.system.rbac.domains.vo.user;

import com.adminpro.framework.base.entity.BaseVO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileVo extends BaseVO {
    @Size(max = 255)
    private String realName;

    @Size(max = 255)
    private String mobileNo;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String avatarUrl;

    @Size(max = 12)
    private String sex;

    @Size(max = 255)
    private String description;
}

