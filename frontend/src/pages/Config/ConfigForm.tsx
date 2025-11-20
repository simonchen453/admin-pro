import React, { useEffect } from 'react';
import {
  Form,
  Input,
  Button,
  Space,
  message
} from 'antd';
import { createConfigApi, updateConfigApi, getConfigDetailApi } from '../../api/config';
import type { ConfigEntity } from '../../types';

const { TextArea } = Input;

interface ConfigFormProps {
  config?: ConfigEntity | null;
  onSuccess: () => void;
  onCancel: () => void;
}

const ConfigForm: React.FC<ConfigFormProps> = ({
  config,
  onSuccess,
  onCancel
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);

  const isEdit = !!config?.id;

  useEffect(() => {
    if (isEdit && config?.id) {
      fetchConfigDetail(config.id);
    } else {
      form.resetFields();
    }
  }, [config, form, isEdit]);

  const fetchConfigDetail = async (id: string) => {
    try {
      const response = await getConfigDetailApi(id);
      if (response.restCode === '200') {
        form.setFieldsValue({
          name: response.data.name,
          key: response.data.key,
          value: response.data.value,
          remark: response.data.remark
        });
      } else {
        message.error(response.message || '获取配置详情失败');
      }
    } catch (error) {
      console.error('获取配置详情失败:', error);
      message.error('获取配置详情失败');
    }
  };

  interface ConfigFormData {
    name: string;
    key: string;
    value: string;
    remark?: string;
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      const formData: ConfigEntity = {
        id: config?.id,
        name: values.name,
        key: values.key,
        value: values.value,
        remark: values.remark
      };

      let response;
      if (isEdit) {
        response = await updateConfigApi(formData);
        if (response.restCode === '200') {
          message.success('配置更新成功');
          onSuccess();
        } else {
          if (response.errorsMap) {
            Object.keys(response.errorsMap).forEach(key => {
              form.setFields([{ name: key, errors: [response.errorsMap![key]] }]);
            });
          }
          message.error(response.message || '配置更新失败');
        }
      } else {
        response = await createConfigApi(formData);
        if (response.restCode === '200') {
          message.success('配置创建成功');
          onSuccess();
        } else {
          if (response.errorsMap) {
            Object.keys(response.errorsMap).forEach(key => {
              form.setFields([{ name: key, errors: [response.errorsMap![key]] }]);
            });
          }
          message.error(response.message || '配置创建失败');
        }
      }
    } catch (error) {
      console.error('提交失败:', error);
      message.error(isEdit ? '配置更新失败' : '配置创建失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
    >
      <Form.Item
        name="name"
        label="配置名称"
        rules={[{ required: true, message: '配置名称不能为空' }]}
      >
        <Input placeholder="请输入配置名称" />
      </Form.Item>

      <Form.Item
        name="key"
        label="Key"
        rules={[{ required: true, message: 'Key不能为空' }]}
      >
        <Input placeholder="请输入配置键名" disabled={isEdit} />
      </Form.Item>

      <Form.Item
        name="value"
        label="Value"
        rules={[{ required: true, message: 'Value不能为空' }]}
      >
        <TextArea
          rows={4}
          placeholder="请输入配置键值"
        />
      </Form.Item>

      <Form.Item
        name="remark"
        label="备注"
      >
        <TextArea
          rows={3}
          placeholder="请输入备注"
        />
      </Form.Item>

      <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
        <Space>
          <Button onClick={onCancel}>
            取消
          </Button>
          <Button type="primary" onClick={handleSubmit} loading={loading}>
            {isEdit ? '更新' : '创建'}
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default ConfigForm;

