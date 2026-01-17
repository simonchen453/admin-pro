package com.adminpro.system.rbac.domains.entity.dept;

import com.adminpro.framework.base.entity.BaseService;
import com.adminpro.framework.base.util.IdGenerator;
import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.framework.jdbc.SearchParam;
import com.adminpro.framework.jdbc.query.QueryResultSet;
import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.rbac.common.RbacCacheConstants;
import com.adminpro.system.rbac.common.RbacConstants;
import com.adminpro.system.rbac.domains.vo.tree.TreeSelect;
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
 * 部门服务类
 * <p>
 * 提供部门管理的核心业务功能，包括：
 * <ul>
 * <li>部门基本操作：创建、更新、删除、查询</li>
 * <li>部门树构建：构建部门树形结构供前端展示</li>
 * <li>层级管理：维护部门的祖级路径（ancestors）</li>
 * <li>级联删除：删除部门时自动删除所有子部门</li>
 * <li>缓存管理：使用Spring Cache缓存部门数据</li>
 * </ul>
 * </p>
 * <p>
 * 部门层级关系：
 * <ul>
 * <li>每个部门记录其父部门ID和祖级路径</li>
 * <li>祖级路径格式：0,祖部门ID,父部门ID</li>
 * <li>支持无限层级嵌套</li>
 * </ul>
 * </p>
 *
 * @author simon
 * @date 2020-05-24
 * @version 1.0
 * @see DeptEntity
 * @see DeptDao
 */
@Service
public class DeptService extends BaseService<DeptEntity, String> {

    private DeptDao dao;

    @Autowired
    public DeptService(DeptDao dao) {
        super(dao);
        this.dao = dao;
    }

    /**
     * 获取DeptService实例
     * <p>
     * 通过Spring容器获取Service实例，用于在非Spring管理的类中调用服务
     * </p>
     *
     * @return DeptService实例
     */
    public static DeptService getInstance() {
        return SpringUtil.getBean(DeptService.class);
    }

    /**
     * 搜索部门（分页）
     * <p>
     * 根据搜索参数进行分页查询，支持多种条件过滤
     * </p>
     *
     * @param param 搜索参数对象，包含分页信息和过滤条件
     * @return 分页查询结果集
     */
    public QueryResultSet<DeptEntity> search(SearchParam param) {
        return dao.search(param);
    }

    /**
     * 根据参数查询部门列表
     * <p>
     * 根据搜索参数查询符合条件的部门列表，不分页
     * </p>
     *
     * @param param 搜索参数对象，包含过滤条件
     * @return 部门实体列表
     */
    public List<DeptEntity> findByParam(SearchParam param) {
        return dao.findByParam(param);
    }

    /**
     * 根据部门编号查询部门
     * <p>
     * 支持缓存，使用部门编号作为缓存键
     * </p>
     *
     * @param no 部门编号
     * @return 部门实体对象，不存在返回null
     */
    @Cacheable(value = RbacCacheConstants.DEPT_CACHE, key = "'no_'+#no")
    public DeptEntity findByNo(String no) {
        return dao.findByNo(no);
    }

    /**
     * 查询所有部门
     *
     * @return 部门实体列表
     */
    public List<DeptEntity> findAll() {
        return dao.findAll();
    }

    /**
     * 根据父部门ID查询部门及其所有子部门
     * <p>
     * 递归查询指定父部门下的所有子部门（包含多级子部门）
     * </p>
     *
     * @param parentId 父部门ID
     * @return 部门实体列表（包含父部门和所有子部门）
     */
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

    /**
     * 根据父部门编号查询部门及其所有子部门
     * <p>
     * 支持缓存，使用父部门编号作为缓存键
     * </p>
     *
     * @param parentNo 父部门编号
     * @return 部门实体列表（包含父部门和所有子部门）
     */
    @Cacheable(value = RbacCacheConstants.DEPT_CACHE, key = "'dept_parent_no_'+#parentNo")
    public List<DeptEntity> findByParentNo(String parentNo) {
        DeptEntity entity = dao.findByNo(parentNo);
        return findByParentId(entity.getId());
    }

    /**
     * 根据父部门编号构建部门树选择列表
     * <p>
     * 查询指定父部门及其所有子部门，构建为树形结构，并转换为TreeSelect对象供前端下拉选择使用
     * 支持缓存
     * </p>
     *
     * @param parentNo 父部门编号
     * @return 树形选择对象列表
     */
    @Cacheable(value = RbacCacheConstants.DEPT_CACHE, key = "'tree_select_'+#parentNo")
    public List<TreeSelect> buildDeptTreeSelectByParentId(String parentNo) {
        DeptEntity entity = dao.findByNo(parentNo);
        List<DeptEntity> list = findByParentId(entity.getId());
        list.add(entity);
        return buildDeptTreeSelect(list);
    }

    /**
     * 构建部门树选择列表
     * <p>
     * 将部门列表构建为树形结构，并转换为TreeSelect对象供前端下拉选择使用
     * </p>
     *
     * @param depts 部门实体列表
     * @return 树形选择对象列表
     */
    public List<TreeSelect> buildDeptTreeSelect(List<DeptEntity> depts) {
        List<DeptEntity> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 构建部门树
     * <p>
     * 将扁平的部门列表构建为树形结构，自动识别顶级节点并递归构建子树
     * </p>
     *
     * @param depts 部门实体列表
     * @return 树形结构的部门列表
     */
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
     * 递归构建部门树
     * <p>
     * 递归查找并设置部门的所有子节点
     * </p>
     *
     * @param list 部门列表
     * @param t 当前部门节点
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
     * 获取子节点列表
     * <p>
     * 从部门列表中查找指定父节点的所有直接子节点
     * </p>
     *
     * @param list 部门列表
     * @param t 父部门节点
     * @return 直接子部门列表
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
     * <p>
     * 判断指定部门节点是否存在子节点
     * </p>
     *
     * @param list 部门列表
     * @param t 部门节点
     * @return 存在子节点返回true，否则返回false
     */
    private boolean hasChild(List<DeptEntity> list, DeptEntity t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    /**
     * 创建部门
     * <p>
     * 创建新部门，自动生成ID并设置祖级路径。
     * 祖级路径由父部门的祖级路径加上父部门ID组成。
     * 创建成功后清除部门缓存。
     * </p>
     *
     * @param entity 部门实体对象
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

    /**
     * 更新部门
     * <p>
     * 更新部门信息，重新计算并设置祖级路径。
     * 更新成功后清除部门缓存。
     * </p>
     *
     * @param entity 部门实体对象
     */
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

    /**
     * 批量删除部门
     * <p>
     * 根据部门ID字符串批量删除部门，级联删除所有子部门。
     * 删除成功后清除部门缓存。
     * </p>
     *
     * @param ids 部门ID字符串，格式：id1,id2,id3
     */
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

    /**
     * 递归删除部门及其所有子部门
     * <p>
     * 先递归删除所有子部门，最后删除当前部门
     * </p>
     *
     * @param deptEntity 要删除的部门实体对象
     */
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
