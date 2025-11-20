package com.adminpro.rbac.domains.entity.post;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.rbac.common.RbacCacheConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 职位 服务层实现
 *
 * @author simon
 * @date 2020-05-21
 */
@Service
public class PostService extends BaseService<PostEntity, String> {

    private PostDao dao;

    @Autowired
    public PostService(PostDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static PostService getInstance() {
        return SpringUtil.getBean(PostService.class);
    }

    public QueryResultSet<PostEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<PostEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    /**
     * 创建 PostEntity
     *
     * @param entity
     */
    @Override
    @Transactional
    public void create(PostEntity entity) {
        entity.setId(IdGenerator.getInstance().nextStringId());
        super.create(entity);
    }

    @Transactional
    public void deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return;
        }
        String[] split = ids.split(",");
        for (int i = 0; i < split.length; i++) {
            dao.delete(split[i]);
        }
    }

    /**
     * 根据职位编号查找对象
     *
     * @param code
     * @return
     */
    @Cacheable(value = RbacCacheConstants.POST_CACHE, key = "'code_'+#code")
    public PostEntity findByCode(String code) {
        return dao.findByCode(code);
    }

    /**
     * 根据职位名称查找对象
     *
     * @param name
     * @return
     */
    public PostEntity findByName(String name) {
        return dao.findByName(name);
    }
}
