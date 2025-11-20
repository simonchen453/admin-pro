import React, { useState, useEffect } from 'react';
import {
  Button,
  Card,
  Breadcrumb,
  Divider,
  Descriptions,
  message,
  Spin
} from 'antd';
import {
  HomeOutlined,
  ArrowLeftOutlined
} from '@ant-design/icons';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { getSysLogDetailApi } from '../../api/syslog';
import type { SysLogEntity } from '../../types';
import { formatDate } from '../../utils';

const SysLogView: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [loading, setLoading] = useState(false);
  const [sysLog, setSysLog] = useState<SysLogEntity | null>(null);

  const fetchSysLogDetail = async () => {
    const id = searchParams.get('id');
    if (!id) {
      message.error('缺少日志ID参数');
      navigate('/admin/syslog');
      return;
    }

    setLoading(true);
    try {
      const response = await getSysLogDetailApi(id);
      const responseData = response as any;
      const logData = responseData?.data || responseData;
      setSysLog(logData);
    } catch (error) {
      console.error('获取系统日志详情失败:', error);
      message.error('获取系统日志详情失败');
      navigate('/admin/syslog');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSysLogDetail();
  }, []);

  const formatJson = (jsonStr?: string, indent = 2) => {
    if (!jsonStr) return '-';
    try {
      const obj = typeof jsonStr === 'string' ? JSON.parse(jsonStr) : jsonStr;
      return JSON.stringify(obj, null, indent);
    } catch {
      return jsonStr;
    }
  };

  const handleCancel = () => {
    navigate('/admin/syslog');
  };

  if (loading) {
    return (
      <div style={{ padding: '24px', display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!sysLog) {
    return null;
  }

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center' }}>
        <Breadcrumb
          items={[
            {
              title: (
                <Button
                  type="link"
                  icon={<HomeOutlined />}
                  onClick={() => navigate('/')}
                  style={{ padding: 0, height: 'auto', lineHeight: 1 }}
                >
                  首页
                </Button>
              )
            },
            {
              title: (
                <Button
                  type="link"
                  onClick={handleCancel}
                  style={{ padding: 0, height: 'auto', lineHeight: 1 }}
                >
                  系统日志管理
                </Button>
              )
            },
            {
              title: '查看系统日志'
            }
          ]}
        />
      </div>
      
      <Divider />

      <Card>
        <Descriptions column={1} labelStyle={{ width: '200px' }} bordered>
          <Descriptions.Item label="用户域">
            {sysLog.userDomain || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="用户ID">
            {sysLog.userId || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="用户IP">
            {sysLog.ip || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="用户浏览器">
            {sysLog.browser || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="访问方法">
            {sysLog.method || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="操作">
            {sysLog.operation || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="访问参数">
            <pre style={{ 
              margin: 0, 
              padding: '8px', 
              background: '#f5f5f5', 
              borderRadius: '4px',
              maxHeight: '300px',
              overflow: 'auto',
              whiteSpace: 'pre-wrap',
              wordBreak: 'break-word'
            }}>
              {formatJson(sysLog.params, 3)}
            </pre>
          </Descriptions.Item>
          <Descriptions.Item label="响应">
            <pre style={{ 
              margin: 0, 
              padding: '8px', 
              background: '#f5f5f5', 
              borderRadius: '4px',
              maxHeight: '300px',
              overflow: 'auto',
              whiteSpace: 'pre-wrap',
              wordBreak: 'break-word'
            }}>
              {formatJson(sysLog.response, 3)}
            </pre>
          </Descriptions.Item>
          <Descriptions.Item label="消耗时间">
            {sysLog.time !== undefined && sysLog.time !== null ? `${sysLog.time} (ms)` : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {sysLog.createdDate ? formatDate(sysLog.createdDate) : '-'}
          </Descriptions.Item>
        </Descriptions>

        <div style={{ marginTop: 24, textAlign: 'center' }}>
          <Button type="default" icon={<ArrowLeftOutlined />} onClick={handleCancel}>
            返回
          </Button>
        </div>
      </Card>
    </div>
  );
};

export default SysLogView;

