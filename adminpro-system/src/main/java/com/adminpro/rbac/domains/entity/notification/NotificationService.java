package com.adminpro.rbac.domains.entity.notification;

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
 * 通知 服务层实现
 *
 * @author simon
 * @date 2018-09-25
 */
@Service
public class NotificationService extends BaseService<NotificationEntity, String> {

    private NotificationDao dao;

    @Autowired
    public NotificationService(NotificationDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static NotificationService getInstance() {
        return SpringUtil.getBean(NotificationService.class);
    }

    public QueryResultSet<NotificationEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<NotificationEntity> latest() {
        return dao.latest();
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

    public List<NotificationEntity> findByDomain(String domain, int length) {
        return dao.findByDomain(domain, length);
    }
}
