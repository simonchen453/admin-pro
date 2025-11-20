import React, { useState, useEffect } from 'react';
import {
  Card,
  Descriptions,
  Tag,
  Avatar,
  Button,
  Space,
  message,
  Spin,
  Row,
  Col,
  Typography,
  Divider
} from 'antd';
import {
  EditOutlined,
  UserOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import { useParams, useNavigate } from 'react-router-dom';
import { getUserDetailApi, getUserPrepareDataApi } from '../../api/user';
import type {
  UserDetailResponse,
  DeptEntity,
  RoleEntity,
  PostEntity
} from '../../types';
import { UserStatus } from '../../types';

const { Title } = Typography;

const UserDetail: React.FC = () => {
  const { userDomain, userId } = useParams<{ userDomain: string; userId: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [userDetail, setUserDetail] = useState<UserDetailResponse | null>(null);
  const [deptList, setDeptList] = useState<DeptEntity[]>([]);
  const [roleList, setRoleList] = useState<RoleEntity[]>([]);
  const [postList, setPostList] = useState<PostEntity[]>([]);

  // 获取用户详情
  const fetchUserDetail = async () => {
    if (!userDomain || !userId) return;
    
    setLoading(true);
    try {
      const detail = await getUserDetailApi(userDomain, userId);
      setUserDetail(detail);
    } catch (error) {
      console.error('获取用户详情失败:', error);
      message.error('获取用户详情失败');
    } finally {
      setLoading(false);
    }
  };

  // 获取准备数据
  const fetchPrepareData = async () => {
    try {
      const data = await getUserPrepareDataApi();
      setDeptList(data.depts || []);
      setRoleList(data.roles || []);
      setPostList(data.posts || []);
    } catch (error) {
      console.error('获取准备数据失败:', error);
    }
  };

  // 获取状态标签
  const getStatusTag = (status: string) => {
    const statusMap = {
      [UserStatus.ACTIVE]: { color: 'green', text: '正常' },
      [UserStatus.LOCK]: { color: 'red', text: '锁定' },
      [UserStatus.INACTIVE]: { color: 'orange', text: '停用' }
    };
    const statusInfo = statusMap[status as UserStatus] || { color: 'default', text: status };
    return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
  };

  // 获取部门名称
  const getDeptName = (deptId?: string) => {
    if (!deptId) return '-';
    const dept = deptList.find(d => d.id === deptId);
    return dept ? dept.name : '-';
  };

  // 获取角色名称列表
  const getRoleNames = (roleIds: string[]) => {
    if (!roleIds || roleIds.length === 0) return ['-'];
    return roleIds.map(roleId => {
      const role = roleList.find(r => r.id === roleId);
      return role ? role.name : roleId;
    });
  };

  // 获取岗位名称列表
  const getPostNames = (postIds: string[]) => {
    if (!postIds || postIds.length === 0) return ['-'];
    return postIds.map(postId => {
      const post = postList.find(p => p.id === postId);
      return post ? post.name : postId;
    });
  };

  // 格式化时间
  const formatTime = (time?: string) => {
    if (!time) return '-';
    return new Date(time).toLocaleString('zh-CN');
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

  if (!userDetail) {
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
            <Avatar src={userDetail.avatarUrl} icon={<UserOutlined />} size="large" />
            <span>{userDetail.realName} ({userDetail.loginName})</span>
            {getStatusTag(userDetail.status)}
          </Space>
        }
        extra={
          <Space>
            <Button
              type="primary"
              icon={<EditOutlined />}
              onClick={() => navigate(`/user/edit/${userDetail.userDomain}/${userDetail.userId}`)}
            >
              编辑用户
            </Button>
            <Button
              icon={<ReloadOutlined />}
              onClick={fetchUserDetail}
            >
              刷新
            </Button>
          </Space>
        }
      >
        <Row gutter={[24, 24]}>
          <Col span={24}>
            <Title level={5}>基本信息</Title>
            <Descriptions column={2} bordered>
              <Descriptions.Item label="用户域" span={1}>
                {userDetail.userDomain}
              </Descriptions.Item>
              <Descriptions.Item label="用户ID" span={1}>
                {userDetail.userId}
              </Descriptions.Item>
              <Descriptions.Item label="登录名" span={1}>
                {userDetail.loginName}
              </Descriptions.Item>
              <Descriptions.Item label="真实姓名" span={1}>
                {userDetail.realName}
              </Descriptions.Item>
              <Descriptions.Item label="手机号" span={1}>
                {userDetail.mobileNo || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="邮箱" span={1}>
                {userDetail.email || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="性别" span={1}>
                {userDetail.sex || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="状态" span={1}>
                {getStatusTag(userDetail.status)}
              </Descriptions.Item>
              <Descriptions.Item label="部门" span={2}>
                {getDeptName(userDetail.deptId)}
              </Descriptions.Item>
              <Descriptions.Item label="描述" span={2}>
                {userDetail.description || '-'}
              </Descriptions.Item>
            </Descriptions>
          </Col>

          <Col span={24}>
            <Divider />
            <Title level={5}>登录信息</Title>
            <Descriptions column={2} bordered>
              <Descriptions.Item label="最后登录时间" span={2}>
                {formatTime(userDetail.latestLoginTime)}
              </Descriptions.Item>
            </Descriptions>
          </Col>

          <Col span={24}>
            <Divider />
            <Title level={5}>权限信息</Title>
            <Descriptions column={1} bordered>
              <Descriptions.Item label="角色">
                <Space wrap>
                  {getRoleNames(userDetail.roleIds).map((roleName, index) => (
                    <Tag key={index} color="blue">
                      {roleName}
                    </Tag>
                  ))}
                </Space>
              </Descriptions.Item>
              <Descriptions.Item label="岗位">
                <Space wrap>
                  {getPostNames(userDetail.postIds).map((postName, index) => (
                    <Tag key={index} color="green">
                      {postName}
                    </Tag>
                  ))}
                </Space>
              </Descriptions.Item>
            </Descriptions>
          </Col>
        </Row>
      </Card>
    </div>
  );
};

export default UserDetail;
