package com.adminpro.rbac.domains.entity.post;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.core.base.validator.IValidatorGroup;
import com.adminpro.framework.common.helper.StringHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 职位 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
public class PostUpdateValidator extends BaseValidator<PostEntity> {

    @Autowired
    private PostService postService;

    /**
     * 校验更新职位
     */
    @Override
    public void validate(PostEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle, IValidatorGroup.Update.class);
        if (!msgBundle.hasErrorMessage("id")) {
            if (!StringUtils.isEmpty(entity.getId())) {
                PostEntity postEntity = postService.findById(entity.getId());
                if (postEntity == null) {
                    msgBundle.addErrorMessage("id", "职位不存在");
                }
            }
        }

        if (!msgBundle.hasErrorMessage("code")) {
            PostEntity byCode = postService.findByCode(entity.getCode());
            if (byCode != null && !StringHelper.equals(byCode.getId(), entity.getId())) {
                msgBundle.addErrorMessage("code", "职位编码不能重复");
            }
        }

        if (!msgBundle.hasErrorMessage("name")) {
            PostEntity byName = postService.findByName(entity.getName());
            if (byName != null && !StringHelper.equals(byName.getId(), entity.getId())) {
                msgBundle.addErrorMessage("name", "职位名称不能重复");
            }
        }
    }
}
