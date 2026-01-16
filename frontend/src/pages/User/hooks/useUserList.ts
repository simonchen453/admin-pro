import { useState, useEffect, useCallback } from 'react';
import { message } from 'antd';
import {
    getUserListApi,
    getUserPrepareDataApi,
    getDeptTreeSelectApi,
    getDomainListApi,
    activeUserApi,
    inactiveUserApi,
    deleteUserApi
} from '../../../api/user';
import type { UserEntity, UserSearchForm, RoleEntity, PostEntity } from '../../../types';

/**
 * 部门树节点类型
 */
interface DeptTreeNode {
    key: string;
    title: string;
    value: string;
    children?: DeptTreeNode[];
}

/**
 * useUserList Hook 返回值
 */
export interface UseUserListReturn {
    // 数据状态
    userList: UserEntity[];
    loading: boolean;
    total: number;
    currentPage: number;
    pageSize: number;

    // 下拉数据
    deptTreeData: DeptTreeNode[];
    roleList: RoleEntity[];
    postList: PostEntity[];
    domainList: Array<{ id: string; name: string; display: string }>;

    // 选择状态
    selectedRowKeys: React.Key[];
    selectedUsers: UserEntity[];

    // 操作方法
    fetchUserList: (params?: UserSearchForm) => Promise<void>;
    setCurrentPage: (page: number) => void;
    setPageSize: (size: number) => void;
    handleSearch: (values: UserSearchForm) => void;
    handleReset: () => void;
    handlePageChange: (page: number, size?: number) => void;
    handleSelectChange: (keys: React.Key[], users: UserEntity[]) => void;
    handleActive: (userId: string) => Promise<boolean>;
    handleInactive: (userId: string) => Promise<boolean>;
    handleDelete: (userIds: string) => Promise<boolean>;
    refresh: () => void;
}

/**
 * 用户列表数据管理 Hook
 * 
 * 将 UserList 组件中的数据获取和状态管理逻辑提取出来
 * 
 * @example
 * ```tsx
 * const { userList, loading, fetchUserList, handleSearch } = useUserList();
 * 
 * return (
 *   <UserSearchForm onSearch={handleSearch} />
 *   <Table dataSource={userList} loading={loading} />
 * );
 * ```
 */
export function useUserList(): UseUserListReturn {
    // 列表数据状态
    const [userList, setUserList] = useState<UserEntity[]>([]);
    const [loading, setLoading] = useState(false);
    const [total, setTotal] = useState(0);
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [searchForm, setSearchForm] = useState<UserSearchForm>({});

    // 下拉数据
    const [deptTreeData, setDeptTreeData] = useState<DeptTreeNode[]>([]);
    const [roleList, setRoleList] = useState<RoleEntity[]>([]);
    const [postList, setPostList] = useState<PostEntity[]>([]);
    const [domainList, setDomainList] = useState<Array<{ id: string; name: string; display: string }>>([]);

    // 选择状态
    const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
    const [selectedUsers, setSelectedUsers] = useState<UserEntity[]>([]);

    // 转换部门树数据
    const convertDeptTree = (data: any[]): DeptTreeNode[] => {
        if (!data || data.length === 0) return [];
        return data.map(item => ({
            key: item.id,
            title: item.label,
            value: item.id,
            children: item.children ? convertDeptTree(item.children) : undefined
        }));
    };

    // 获取准备数据
    const fetchPrepareData = useCallback(async () => {
        try {
            const [deptData, prepareData, domains] = await Promise.all([
                getDeptTreeSelectApi(),
                getUserPrepareDataApi(),
                getDomainListApi()
            ]);

            setDeptTreeData(convertDeptTree(deptData as any[]));
            setRoleList(prepareData.roles || []);
            setPostList(prepareData.posts || []);
            setDomainList(domains || []);
        } catch (error) {
            console.error('获取准备数据失败:', error);
        }
    }, []);

    // 获取用户列表
    const fetchUserList = useCallback(async (params: UserSearchForm = {}) => {
        setLoading(true);
        try {
            const response = await getUserListApi({
                ...searchForm,
                ...params,
                page: params.page ?? currentPage,
                pageSize: params.pageSize ?? pageSize
            });

            const responseData = response as any;
            const list = responseData?.list || responseData?.records || responseData?.data?.list || [];
            const totalCount = responseData?.pagination?.total || responseData?.totalCount || 0;

            setUserList(Array.isArray(list) ? list.map((item: any, index: number) => ({ ...item, index })) : []);
            setTotal(totalCount);
        } catch (error) {
            console.error('获取用户列表失败:', error);
            setUserList([]);
            setTotal(0);
        } finally {
            setLoading(false);
        }
    }, [searchForm, currentPage, pageSize]);

    // 搜索
    const handleSearch = useCallback((values: UserSearchForm) => {
        setSearchForm(values);
        setCurrentPage(1);
        fetchUserList({ ...values, page: 1 });
    }, [fetchUserList]);

    // 重置
    const handleReset = useCallback(() => {
        setSearchForm({});
        setCurrentPage(1);
        fetchUserList({ page: 1 });
    }, [fetchUserList]);

    // 分页变化
    const handlePageChange = useCallback((page: number, size?: number) => {
        setCurrentPage(page);
        if (size) setPageSize(size);
        fetchUserList({ page, pageSize: size || pageSize });
    }, [fetchUserList, pageSize]);

    // 选择变化
    const handleSelectChange = useCallback((keys: React.Key[], users: UserEntity[]) => {
        setSelectedRowKeys(keys);
        setSelectedUsers(users);
    }, []);

    // 启用用户
    const handleActive = useCallback(async (userId: string): Promise<boolean> => {
        try {
            const result = await activeUserApi(userId);
            if (result.success) {
                message.success('用户启用成功');
                fetchUserList();
                return true;
            }
            message.error(result.message || '用户启用失败');
            return false;
        } catch {
            message.error('用户启用失败');
            return false;
        }
    }, [fetchUserList]);

    // 停用用户
    const handleInactive = useCallback(async (userId: string): Promise<boolean> => {
        try {
            const result = await inactiveUserApi(userId);
            if (result.success) {
                message.success('用户停用成功');
                fetchUserList();
                return true;
            }
            message.error(result.message || '用户停用失败');
            return false;
        } catch {
            message.error('用户停用失败');
            return false;
        }
    }, [fetchUserList]);

    // 删除用户
    const handleDelete = useCallback(async (userIds: string): Promise<boolean> => {
        try {
            const result = await deleteUserApi(userIds);
            if (result.restCode === '200') {
                message.success('用户删除成功');
                fetchUserList();
                setSelectedRowKeys([]);
                setSelectedUsers([]);
                return true;
            }
            message.error(result.message || '用户删除失败');
            return false;
        } catch {
            message.error('用户删除失败');
            return false;
        }
    }, [fetchUserList]);

    // 刷新
    const refresh = useCallback(() => {
        fetchUserList();
    }, [fetchUserList]);

    // 初始化
    useEffect(() => {
        fetchPrepareData();
        fetchUserList();
    }, []);

    return {
        userList,
        loading,
        total,
        currentPage,
        pageSize,
        deptTreeData,
        roleList,
        postList,
        domainList,
        selectedRowKeys,
        selectedUsers,
        fetchUserList,
        setCurrentPage,
        setPageSize,
        handleSearch,
        handleReset,
        handlePageChange,
        handleSelectChange,
        handleActive,
        handleInactive,
        handleDelete,
        refresh
    };
}

export default useUserList;
