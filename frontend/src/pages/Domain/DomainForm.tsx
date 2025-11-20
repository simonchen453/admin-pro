import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Button,
  Space,
  message
} from 'antd';
import { createDomainApi, updateDomainApi, getDomainDetailApi } from '../../api/domain';
import type { DomainEntity } from '../../types';

interface DomainFormProps {
  domain?: DomainEntity | null;
  onSuccess: () => void;
  onCancel: () => void;
}

const DomainForm: React.FC<DomainFormProps> = ({ domain, onSuccess, onCancel }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const rules = {
    name: [
      { required: true, message: '名称不能为空' }
    ],
    display: [
      { required: true, message: '显示名称不能为空' }
    ]
  };

  useEffect(() => {
    if (domain?.id) {
      fetchDomainDetail(domain.id);
    } else {
      form.resetFields();
    }
  }, [domain, form]);

  const fetchDomainDetail = async (id: string) => {
    try {
      const response = await getDomainDetailApi(id);
      if (response.restCode === '200') {
        const domainData = response.data;
        form.setFieldsValue({
          name: domainData.name,
          display: domainData.display
        });
      } else {
        message.error(response.message || '获取用户域详情失败');
      }
    } catch (error) {
      console.error('获取用户域详情失败:', error);
      message.error('获取用户域详情失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      const formData: DomainEntity = {
        id: domain?.id || undefined,
        name: values.name || '',
        display: values.display || ''
      };

      let response;
      if (domain?.id) {
        response = await updateDomainApi(formData);
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
        response = await createDomainApi(formData);
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
      message.error(domain?.id ? '修改失败' : '创建失败');
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
          label="名称"
          name="name"
          rules={rules.name}
        >
          <Input 
            placeholder="请输入名称" 
            allowClear 
            disabled={!!domain?.id}
          />
        </Form.Item>

        <Form.Item
          label="显示名称"
          name="display"
          rules={rules.display}
        >
          <Input placeholder="请输入显示名称" allowClear />
        </Form.Item>
      </Form>

      <div style={{ textAlign: 'right', paddingTop: '16px', borderTop: '1px solid #f0f0f0' }}>
        <Space>
          <Button onClick={onCancel}>
            取消
          </Button>
          <Button type="primary" loading={loading} onClick={handleSubmit}>
            {domain?.id ? '确定' : '确定'}
          </Button>
        </Space>
      </div>
    </div>
  );
};

export default DomainForm;

