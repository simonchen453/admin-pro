package com.adminpro.tools.domains.entity.oss;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.helper.UploadDownloadHelper;
import com.adminpro.rbac.domains.vo.oss.ListOssDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OSSService extends BaseService<OSSEntity, String> {

    private OSSDao dao;

    @Autowired
    protected OSSService(OSSDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static OSSService getInstance() {
        return SpringUtil.getBean(OSSService.class);
    }

    public QueryResultSet<ListOssDto> search(SearchParam param) {
        return dao.search(param);
    }

    @Transactional
    public void delete(OSSEntity entity) {
        dao.delete(entity.getId());
        UploadDownloadHelper.getInstance().delete(entity.getKey());
        if (StringUtils.isNotEmpty(entity.getCoverKey())) {
            UploadDownloadHelper.getInstance().delete(entity.getCoverKey());
        }
    }

    public List<OSSEntity> findByBatchId(String batchId) {
        return dao.findByBatchId(batchId);
    }

    public List<OSSDTO> findDTOByBatchId(String batchId) {
        List<OSSEntity> ossEntityList = findByBatchId(batchId);

        List<OSSDTO> vs = new ArrayList<>();
        for (OSSEntity ossEntity : ossEntityList) {
            vs.add(OSSDTO.from(ossEntity));
        }
        return vs;
    }

    public OSSDTO findDTOById(String id) {
        OSSEntity ossEntity = findById(id);
        return OSSDTO.from(ossEntity);
    }

    @Transactional
    public void deleteByBatchId(String batchId) {
        try {
            List<OSSEntity> ossEntities = findByBatchId(batchId);
            for (int i = 0; i < ossEntities.size(); i++) {
                delete(ossEntities.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int active(String batchId) {
        return dao.active(batchId);
    }
}
