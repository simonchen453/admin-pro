import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Button,
  Space,
  message,
  Select
} from 'antd';
import { getDomainEnvDetailApi, createDomainEnvApi, updateDomainEnvApi } from '../../api/domainEnv';
import type { DomainEnvEntity } from '../../types';

interface DomainEnvFormProps {
  domainEnv?: DomainEnvEntity | null;
  domainList: Array<{ id: string; name: string; display: string }>;
  onSuccess: () => void;
  onCancel: () => void;
}

const DomainEnvForm: React.FC<DomainEnvFormProps> = ({ domainEnv, domainList, onSuccess, onCancel }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const rules = {
    userDomain: [
      { required: true, message: '用户域不能为空' }
    ],
    commonRole: [
      { required: true, message: '基础角色不能为空' }
    ],
    homePageUrl: [
      { required: true, message: '首页地址不能为空' }
    ],
    loginUrl: [
      { required: true, message: '登录地址不能为空' }
    ]
  };

  useEffect(() => {
    if (domainEnv?.id) {
      fetchDomainEnvDetail(domainEnv.id);
    } else {
      form.resetFields();
    }
  }, [domainEnv, form]);

  const fetchDomainEnvDetail = async (id: string) => {
    try {
      const response = await getDomainEnvDetailApi(id);
      if (response.restCode === '200') {
        const domainEnvData = response.data;
        form.setFieldsValue({
          userDomain: domainEnvData.userDomain,
          commonRole: domainEnvData.commonRole,
          homePageUrl: domainEnvData.homePageUrl,
          loginUrl: domainEnvData.loginUrl,
          description: domainEnvData.description
        });
      } else {
        message.error(response.message || '获取用户域环境配置详情失败');
      }
    } catch (error) {
      console.error('获取用户域环境配置详情失败:', error);
      message.error('获取用户域环境配置详情失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      const formData: DomainEnvEntity = {
        id: domainEnv?.id || undefined,
        userDomain: values.userDomain || '',
        commonRole: values.commonRole || '',
        homePageUrl: values.homePageUrl || '',
        loginUrl: values.loginUrl || '',
        description: values.description
      };

      let response;
      if (domainEnv?.id) {
        response = await updateDomainEnvApi(formData);
        if (response.restCode === '200') {
          message.success('修改成功');
          onSuccess();
        } else {
          if (response.errorsMap) {
            Object.keys(response.errorsMap).forEach(field => {
              form.setFields([{ name: field, errors: [response.errorsMap![field]] }]);
            });
          }
          message.error(response.message || '修改失败');
        }
      } else {
        response = await createDomainEnvApi(formData);
        if (response.restCode === '200') {
          message.success('新增成功');
          onSuccess();
        } else {
          if (response.errorsMap) {
            Object.keys(response.errorsMap).forEach(field => {
              form.setFields([{ name: field, errors: [response.errorsMap![field]] }]);
            });
          }
          message.error(response.message || '创建失败');
        }
      }
    } catch (error) {
      if (error && typeof error === 'object' && 'errorFields' in error) {
        return;
      }
      console.error('提交失败:', error);
      message.error(domainEnv?.id ? '修改失败' : '创建失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Form
        form={form}
        layout="vertical"
      >
        <Form.Item
          label="用户域"
          name="userDomain"
          rules={rules.userDomain}
        >
          <Select
            placeholder="请选择用户域"
            allowClear
            disabled={!!domainEnv?.id}
          >
            {domainList.map(domain => (
              <Select.Option key={domain.name} value={domain.name}>
                {domain.display}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="公共角色"
          name="commonRole"
          rules={rules.commonRole}
        >
          <Input placeholder="请输入公共角色" allowClear />
        </Form.Item>

        <Form.Item
          label="首页地址"
          name="homePageUrl"
          rules={rules.homePageUrl}
        >
          <Input placeholder="请输入首页地址" allowClear />
        </Form.Item>

        <Form.Item
          label="登录地址"
          name="loginUrl"
          rules={rules.loginUrl}
        >
          <Input placeholder="请输入登录地址" allowClear />
        </Form.Item>

        <Form.Item
          label="描述"
          name="description"
        >
          <Input.TextArea placeholder="请输入描述" rows={4} allowClear />
        </Form.Item>
      </Form>

      <div style={{ textAlign: 'right', paddingTop: '16px', borderTop: '1px solid #f0f0f0' }}>
        <Space>
          <Button onClick={onCancel}>
            取消
          </Button>
          <Button type="primary" loading={loading} onClick={handleSubmit}>
            确定
          </Button>
        </Space>
      </div>
    </div>
  );
};

export default DomainEnvForm;

