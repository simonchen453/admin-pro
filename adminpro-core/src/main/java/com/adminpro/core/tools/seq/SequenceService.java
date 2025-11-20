package com.adminpro.core.tools.seq;

import com.adminpro.core.base.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 获取自增长序列
 *
 * @author simon
 */
@Service
@Transactional
public class SequenceService {

    @Autowired
    private SequenceDao dao;

    public static SequenceService getInstance() {
        return SpringUtil.getBean(SequenceService.class);
    }

    /**
     * 不要求连续
     *
     * @param seqName
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long getNextLong(String seqName) {
        return dao.getNextLong(seqName);
    }

    /**
     * 要求连续，上一级事务回滚会跟着回滚
     *
     * @param seqName
     * @return
     */
    @Transactional
    public long getNextLongUninterrupted(String seqName) {
        return dao.getNextLong(seqName);
    }

    public void delete(String seqName) {
        dao.delete(seqName);
    }
}
