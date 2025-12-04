package com.adminpro.rbac.domains.entity.user;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户标签 服务层实现
 *
 * @author simon
 * @date 2020-05-21
 */
@Service
public class UserTagService extends BaseService<UserTagEntity, String> {

    private UserTagDao dao;

    @Autowired
    public UserTagService(UserTagDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static UserTagService getInstance() {
        return SpringUtil.getBean(UserTagService.class);
    }

    public QueryResultSet<UserTagEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<UserTagEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    public List<String> findTagByUserIden(UserIden userIden) {
        List<UserTagEntity> list = dao.findByUserIden(userIden);
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            tags.add(list.get(i).getTag());
        }
        return tags;
    }

    public List<UserTagEntity> findByUserIden(UserIden userIden) {
        List<UserTagEntity> list = dao.findByUserIden(userIden);
        return list;
    }

    @Transactional
    public void deleteByUserIden(UserIden userIden) {
        dao.deleteByUserIden(userIden);
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
}
