import React, { useState, useEffect } from 'react';
import {
  Card,
  Button,
  Space,
  message,
  Spin,
  Typography
} from 'antd';
import {
  ArrowLeftOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import { useParams, useNavigate } from 'react-router-dom';
import { getUserDetailApi, getUserPrepareDataApi, getDeptTreeSelectApi } from '../../api/user';
import UserForm from './UserForm';
import type {
  UserEntity,
  RoleEntity,
  PostEntity
} from '../../types';

const { Title } = Typography;

interface DeptTreeNode {
  key: string;
  title: string;
  value: string;
  children?: DeptTreeNode[];
}

const UserEdit: React.FC = () => {
  const { userDomain, userId } = useParams<{ userDomain: string; userId: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [user, setUser] = useState<UserEntity | null>(null);
  const [deptTreeData, setDeptTreeData] = useState<DeptTreeNode[]>([]);
  const [roleList, setRoleList] = useState<RoleEntity[]>([]);
  const [postList, setPostList] = useState<PostEntity[]>([]);

  // 获取用户详情
  const fetchUserDetail = async () => {
    if (!userDomain || !userId) return;

    setLoading(true);
    try {
      const detail = await getUserDetailApi(userId);
      // 转换为UserEntity格式
      const userEntity: UserEntity = {
        userDomain: detail.userDomain,
        userId: detail.userId,
        userIden: {
          userDomain: detail.userDomain,
          userId: detail.userId
        },
        loginName: detail.loginName,
        realName: detail.realName,
        mobileNo: detail.mobileNo,
        email: detail.email,
        avatarUrl: detail.avatarUrl,
        status: detail.status,
        sex: detail.sex,
        description: detail.description,
        deptNo: detail.deptNo,
        latestLoginTime: detail.latestLoginTime
      };
      setUser(userEntity);
    } catch (error) {
      console.error('获取用户详情失败:', error);
      message.error('获取用户详情失败');
    } finally {
      setLoading(false);
    }
  };

  // 转换部门树数据
  const convertDeptTree = (data: Array<{ id: string; label: string; children?: any[] }>): DeptTreeNode[] => {
    if (!data || data.length === 0) {
      return [];
    }
    return data.map(item => ({
      key: item.id,
      title: item.label,
      value: item.id,
      children: item.children ? convertDeptTree(item.children) : undefined
    }));
  };

  // 获取准备数据
  const fetchPrepareData = async () => {
    try {
      const [deptData, prepareData] = await Promise.all([
        getDeptTreeSelectApi(),
        getUserPrepareDataApi()
      ]);
      const treeData = convertDeptTree(deptData as Array<{ id: string; label: string; children?: any[] }>);
      setDeptTreeData(treeData);
      setRoleList(prepareData.roles || []);
      setPostList(prepareData.posts || []);
    } catch (error) {
      console.error('获取准备数据失败:', error);
    }
  };

  // 处理成功回调
  const handleSuccess = () => {
    message.success('用户更新成功');
    navigate(`/user/detail/${userDomain}/${userId}`);
  };

  // 处理取消回调
  const handleCancel = () => {
    navigate(`/user/detail/${userDomain}/${userId}`);
  };

  useEffect(() => {
    fetchPrepareData();
    fetchUserDetail();
  }, [userDomain, userId]);

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!user) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Title level={4}>用户不存在</Title>
        <Button type="primary" onClick={() => navigate('/user/list')}>
          返回用户列表
        </Button>
      </div>
    );
  }

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={
          <Space>
            <Button
              type="text"
              icon={<ArrowLeftOutlined />}
              onClick={() => navigate(`/user/detail/${userDomain}/${userId}`)}
            >
              返回详情
            </Button>
            <span>编辑用户 - {user.realName}</span>
          </Space>
        }
        extra={
          <Button
            icon={<ReloadOutlined />}
            onClick={fetchUserDetail}
          >
            刷新
          </Button>
        }
      >
        <UserForm
          user={user}
          deptTreeData={deptTreeData}
          roleList={roleList}
          postList={postList}
          onSuccess={handleSuccess}
          onCancel={handleCancel}
        />
      </Card>
    </div>
  );
};

export default UserEdit;
