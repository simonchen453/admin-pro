package com.adminpro.system.rbac.domains.entity.post;

import com.adminpro.framework.base.message.MessageBundle;
import com.adminpro.framework.base.validator.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 职位 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
@RequiredArgsConstructor
public class PostCreateValidator extends BaseValidator<PostEntity> {

    private final PostService postService;

    /**
     * 校验创建职位
     */
    @Override
    public void validate(PostEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
        if (!msgBundle.hasErrorMessage("code")) {
            PostEntity byCode = postService.findByCode(entity.getCode());
            if (byCode != null) {
                msgBundle.addErrorMessage("code", "职位编码不能重复");
            }
        }

        if (!msgBundle.hasErrorMessage("name")) {
            PostEntity byName = postService.findByName(entity.getName());
            if (byName != null) {
                msgBundle.addErrorMessage("name", "职位名称不能重复");
            }
        }
    }
}
