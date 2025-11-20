import React, { useEffect } from 'react';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Space,
  message
} from 'antd';
import { createJobApi, updateJobApi } from '../../api/job';
import type {
  JobEntity
} from '../../types';

const { TextArea } = Input;

interface JobFormProps {
  job?: JobEntity | null;
  onSuccess: () => void;
  onCancel: () => void;
}

const JobForm: React.FC<JobFormProps> = ({
  job,
  onSuccess,
  onCancel
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);

  const isEdit = !!job;

  useEffect(() => {
    if (isEdit && job) {
      form.setFieldsValue({
        beanName: job.beanName,
        methodName: job.methodName,
        params: job.params,
        cronExpression: job.cronExpression,
        remark: job.remark
      });
    } else {
      form.resetFields();
    }
  }, [isEdit, job, form]);

  interface JobFormData {
    beanName: string;
    methodName: string;
    params?: string;
    cronExpression: string;
    remark?: string;
  }

  const handleSubmit = async (values: JobFormData) => {
    setLoading(true);
    try {
      if (isEdit) {
        const updateData: JobEntity = {
          id: job!.id,
          beanName: values.beanName,
          methodName: values.methodName,
          params: values.params,
          cronExpression: values.cronExpression,
          remark: values.remark,
          status: job!.status
        };
        const response = await updateJobApi(updateData);
        if (response.restCode === '200' || response.restCode === 200) {
          message.success('定时任务编辑成功');
          onSuccess();
        } else {
          if (response.errorsMap) {
            Object.keys(response.errorsMap).forEach(field => {
              form.setFields([{
                name: field,
                errors: [response.errorsMap![field]]
              }]);
            });
          }
          message.error(response.message || '定时任务编辑失败');
        }
      } else {
        const createData: JobEntity = {
          beanName: values.beanName,
          methodName: values.methodName,
          params: values.params,
          cronExpression: values.cronExpression,
          remark: values.remark,
          status: '1'
        };
        const response = await createJobApi(createData);
        if (response.restCode === '200' || response.restCode === 200) {
          message.success('定时任务创建成功');
          onSuccess();
        } else {
          if (response.errorsMap) {
            Object.keys(response.errorsMap).forEach(field => {
              form.setFields([{
                name: field,
                errors: [response.errorsMap![field]]
              }]);
            });
          }
          message.error(response.message || '定时任务创建失败');
        }
      }
    } catch (error: unknown) {
      console.error('保存定时任务失败:', error);
      
      let errorMessage = '保存定时任务失败';
      let fieldErrors: Record<string, string> = {};
      
      if (error && typeof error === 'object' && 'response' in error) {
        const errorResponse = error as { 
          response?: { 
            data?: { 
              message?: string;
              errorsMap?: Record<string, string>;
            } 
          } 
        };
        
        if (errorResponse.response?.data?.message) {
          errorMessage = errorResponse.response.data.message;
        }
        
        if (errorResponse.response?.data?.errorsMap) {
          fieldErrors = errorResponse.response.data.errorsMap;
        }
      }
      
      if (Object.keys(fieldErrors).length > 0) {
        Object.keys(fieldErrors).forEach(field => {
          form.setFields([{
            name: field,
            errors: [fieldErrors[field]]
          }]);
        });
      }
      
      message.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleSubmit}
    >
      <Form.Item
        name="beanName"
        label="Bean名称"
        rules={[{ required: true, message: 'Bean名称不能为空' }]}
      >
        <Input placeholder="请输入Bean名称" disabled={isEdit} />
      </Form.Item>

      <Form.Item
        name="methodName"
        label="方法名称"
        rules={[{ required: true, message: '方法名称不能为空' }]}
      >
        <Input placeholder="请输入方法名称" />
      </Form.Item>

      <Form.Item
        name="params"
        label="参数"
      >
        <Input placeholder="请输入参数" />
      </Form.Item>

      <Form.Item
        name="cronExpression"
        label="Cron表达式"
        rules={[{ required: true, message: 'Cron表达式不能为空' }]}
      >
        <Input placeholder="请输入Cron表达式" />
      </Form.Item>

      <Form.Item
        name="remark"
        label="备注"
      >
        <TextArea
          rows={3}
          placeholder="请输入备注"
          maxLength={200}
        />
      </Form.Item>

      <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
        <Space>
          <Button onClick={onCancel}>
            取消
          </Button>
          <Button type="primary" htmlType="submit" loading={loading}>
            {isEdit ? '更新' : '创建'}
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default JobForm;

