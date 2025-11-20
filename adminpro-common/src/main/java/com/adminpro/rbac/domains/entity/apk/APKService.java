package com.adminpro.rbac.domains.entity.apk;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * APK版本管理 服务层实现
 *
 * @author simon
 * @date 2021-02-03
 */
@Service
public class APKService extends BaseService<APKEntity, String> {

    private APKDao dao;

    @Autowired
    public APKService(APKDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static APKService getInstance() {
        return SpringUtil.getBean(APKService.class);
    }

    public QueryResultSet<APKEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<APKEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
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

    public List<APKEntity> findByTypeAndVerCode(String type, Integer verCode) {
        return dao.findByTypeAndVerCode(type, verCode);
    }

    public APKEntity findLatestVersion(String type) {
        return dao.findLatestVersion(type);
    }
}
