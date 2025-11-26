package com.adminpro.rbac.domains.entity.dept;

import com.adminpro.core.base.entity.BaseService;
import com.adminpro.core.base.util.IdGenerator;
import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.core.jdbc.SearchParam;
import com.adminpro.core.jdbc.query.QueryResultSet;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.rbac.common.RbacCacheConstants;
import com.adminpro.rbac.common.RbacConstants;
import com.adminpro.rbac.domains.vo.tree.TreeSelect;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门 服务层实现
 *
 * @author simon
 * @date 2020-05-24
 */
@Service
public class DeptService extends BaseService<DeptEntity, String> {

    private DeptDao dao;

    @Autowired
    public DeptService(DeptDao dao) {
        super(dao);
        this.dao = dao;
    }

    public static DeptService getInstance() {
        return SpringUtil.getBean(DeptService.class);
    }

    public QueryResultSet<DeptEntity> search(SearchParam param) {
        return dao.search(param);
    }

    public List<DeptEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    @Cacheable(value = RbacCacheConstants.DEPT_CACHE, key = "'no_'+#no")
    public DeptEntity findByNo(String no) {
        return dao.findByNo(no);
    }

    public List<DeptEntity> findAll() {
        return dao.findAll();
    }

    public List<DeptEntity> findByParentId(String parentId) {
        List<DeptEntity> l = new ArrayList<>();
        List<DeptEntity> list = dao.findByParentId(parentId);
        for (int i = 0; i < list.size(); i++) {
            DeptEntity deptEntity = list.get(i);
            List<DeptEntity> list1 = findByParentId(deptEntity.getId());
            l.add(deptEntity);
            if (list1 != null && list1.size() > 0) {
                l.addAll(list1);
            }
        }
        return l;
    }

    @Cacheable(value = RbacCacheConstants.DEPT_CACHE, key = "'dept_parent_no_'+#parentNo")
    public List<DeptEntity> findByParentNo(String parentNo) {
        DeptEntity entity = dao.findByNo(parentNo);
        return findByParentId(entity.getId());
    }

    @Cacheable(value = RbacCacheConstants.DEPT_CACHE, key = "'tree_select_'+#parentNo")
    public List<TreeSelect> buildDeptTreeSelectByParentId(String parentNo) {
        DeptEntity entity = dao.findByNo(parentNo);
        List<DeptEntity> list = findByParentId(entity.getId());
        list.add(entity);
        return buildDeptTreeSelect(list);
    }

    public List<TreeSelect> buildDeptTreeSelect(List<DeptEntity> depts) {
        List<DeptEntity> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public List<DeptEntity> buildDeptTree(List<DeptEntity> depts) {
        List<DeptEntity> returnList = new ArrayList<DeptEntity>();
        List<String> tempList = new ArrayList<String>();
        for (DeptEntity dept : depts) {
            tempList.add(dept.getId());
        }
        for (Iterator<DeptEntity> iterator = depts.iterator(); iterator.hasNext(); ) {
            DeptEntity dept = (DeptEntity) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<DeptEntity> list, DeptEntity t) {
        // 得到子节点列表
        List<DeptEntity> childList = getChildList(list, t);
        t.setChildren(childList);
        for (DeptEntity tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<DeptEntity> it = childList.iterator();
                while (it.hasNext()) {
                    DeptEntity n = (DeptEntity) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<DeptEntity> getChildList(List<DeptEntity> list, DeptEntity t) {
        List<DeptEntity> tList = new ArrayList<DeptEntity>();
        Iterator<DeptEntity> it = list.iterator();
        while (it.hasNext()) {
            DeptEntity n = (DeptEntity) it.next();
            if (StringHelper.isNotNull(n.getParentId()) && StringHelper.equals(n.getParentId(), t.getId())) {
                tList.add(n);
            }
        }
        return tList;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<DeptEntity> list, DeptEntity t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    /**
     * 创建 DeptEntity
     *
     * @param entity
     */
    @Override
    @Transactional
    @CacheEvict(value = RbacCacheConstants.DEPT_CACHE, allEntries = true)
    public void create(DeptEntity entity) {
        entity.setId(IdGenerator.getInstance().nextStringId());
        DeptEntity parent = findById(entity.getParentId());
        if (parent != null) {
            entity.setAncestors(parent.getAncestors() + "," + entity.getParentId());
        } else {
            entity.setAncestors(RbacConstants.getDeptSuperParentId());
        }
        super.create(entity);
    }

    @Override
    @Transactional
    @CacheEvict(value = RbacCacheConstants.DEPT_CACHE, allEntries = true)
    public void update(DeptEntity entity) {
        DeptEntity parent = findById(entity.getParentId());
        if (parent != null) {
            entity.setAncestors(parent.getAncestors() + "," + entity.getParentId());
        } else {
            entity.setAncestors("0");
        }
        super.update(entity);
    }

    @Transactional
    @CacheEvict(value = RbacCacheConstants.DEPT_CACHE, allEntries = true)
    public void deleteByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return;
        }
        String[] split = ids.split(",");
        for (int i = 0; i < split.length; i++) {
            DeptEntity entity = findById(split[i]);
            if (entity != null) {
                deleteEntityAndChildren(entity);
            }
        }
    }

    private void deleteEntityAndChildren(DeptEntity deptEntity) {
        if (deptEntity == null) {
            return;
        }
        List<DeptEntity> children = dao.findByParentId(deptEntity.getId());
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                DeptEntity child = children.get(i);
                deleteEntityAndChildren(child);
            }
        }
        dao.delete(deptEntity.getId());
    }
}
