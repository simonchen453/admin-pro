import React, { useEffect } from 'react';
import {
  Form,
  Input,
  InputNumber,
  Select,
  TreeSelect,
  Button,
  Row,
  Col,
  Upload,
  Radio,
  message,
  Space
} from 'antd';
import {
  UploadOutlined
} from '@ant-design/icons';
import { createDeptApi, updateDeptApi, uploadDeptLogoApi } from '../../api/dept';
import type {
  DeptEntity
} from '../../types';
import { DeptStatus, SystemConfig } from '../../types';

const { Option } = Select;

interface DeptFormProps {
  dept?: DeptEntity | null;
  deptTreeOptions: any[];
  onSuccess: () => void;
  onCancel: () => void;
}

const DeptForm: React.FC<DeptFormProps> = ({
  dept,
  deptTreeOptions,
  onSuccess,
  onCancel
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const [logoPath, setLogoPath] = React.useState<string>('');

  const isEdit = !!(dept && dept.id);

  useEffect(() => {
    if (dept) {
      form.setFieldsValue({
        ...dept,
        parentId: dept.parentId || '0',
        status: dept.status || DeptStatus.ACTIVE,
        customLogin: dept.customLogin || SystemConfig.NO,
        orderNum: dept.orderNum || 1
      });
      setLogoPath(dept.logoPath || '');
    } else {
      form.resetFields();
      form.setFieldsValue({
        parentId: '0',
        status: DeptStatus.ACTIVE,
        customLogin: SystemConfig.NO,
        orderNum: 1
      });
      setLogoPath('');
    }
  }, [dept, form]);

  const handleUpload = async (file: File) => {
    try {
      const response = await uploadDeptLogoApi(file);
      if (response.restCode === '200' || response.restCode === '0') {
        const path = response.data;
        setLogoPath(path);
        form.setFieldsValue({ logoPath: path });
        message.success('上传成功');
      } else {
        message.error(response.message || '上传失败');
      }
      return false;
    } catch (error) {
      console.error('上传失败:', error);
      message.error('上传失败');
      return false;
    }
  };

  const handleSubmit = async (values: any) => {
    setLoading(true);
    try {
      const formData: DeptEntity = {
        ...values,
        logoPath: logoPath || values.logoPath
      };

      if (isEdit && dept?.id) {
        formData.id = dept.id;
        const response = await updateDeptApi(formData);
        if (response.restCode === '200' || response.restCode === '0') {
          message.success('修改成功');
          onSuccess();
        } else {
          if (response.errorsMap) {
            Object.keys(response.errorsMap).forEach(field => {
              form.setFields([{
                name: field,
                errors: [response.errorsMap[field]]
              }]);
            });
          }
          message.error(response.message || '修改失败');
        }
      } else {
        const response = await createDeptApi(formData);
        if (response.restCode === '200' || response.restCode === '0') {
          message.success('新增成功');
          onSuccess();
        } else {
          if (response.errorsMap) {
            Object.keys(response.errorsMap).forEach(field => {
              form.setFields([{
                name: field,
                errors: [response.errorsMap[field]]
              }]);
            });
          }
          message.error(response.message || '创建失败');
        }
      }
    } catch (error: unknown) {
      console.error('保存部门失败:', error);
      let errorMessage = '保存部门失败';
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

  const getImageUrl = (path?: string) => {
    if (!path) return '';
    if (path.startsWith('http')) return path;
    return `${import.meta.env.VITE_API_BASE || '/api'}${path}`;
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleSubmit}
    >
      <Row gutter={16}>
        <Col span={24}>
          <Form.Item
            name="parentId"
            label="上级部门"
            rules={[{ required: true, message: '上级部门不能为空' }]}
          >
            <TreeSelect
              placeholder="选择上级部门"
              treeData={deptTreeOptions}
              treeDefaultExpandAll
              showSearch
              allowClear
              treeNodeFilterProp="title"
            />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="name"
            label="部门名称"
            rules={[{ required: true, message: '部门名称不能为空' }]}
          >
            <Input placeholder="请输入部门名称" />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="no"
            label="部门编号"
            rules={[{ required: true, message: '部门编号不能为空' }]}
          >
            <Input placeholder="请输入部门编号" />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="orderNum"
            label="显示排序"
            rules={[{ required: true, message: '显示顺序不能为空' }]}
          >
            <InputNumber
              placeholder="显示排序"
              min={0}
              style={{ width: '100%' }}
              controls={{ position: 'right' }}
            />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="linkman"
            label="联系人"
          >
            <Input placeholder="请输入联系人" maxLength={20} />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="phone"
            label="联系电话"
            rules={[
              {
                pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
                message: '请输入正确的手机号码'
              }
            ]}
          >
            <Input placeholder="请输入联系电话" maxLength={11} />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="email"
            label="邮箱"
            rules={[
              {
                type: 'email',
                message: '请输入正确的邮箱地址'
              }
            ]}
          >
            <Input placeholder="请输入邮箱" maxLength={50} />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="status"
            label="部门状态"
            rules={[{ required: true, message: '状态不能为空' }]}
          >
            <Radio.Group>
              <Radio value={DeptStatus.ACTIVE}>正常</Radio>
              <Radio value={DeptStatus.INACTIVE}>停用</Radio>
            </Radio.Group>
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="customLogin"
            label="定制登录"
          >
            <Radio.Group>
              <Radio value={SystemConfig.YES}>是</Radio>
              <Radio value={SystemConfig.NO}>否</Radio>
            </Radio.Group>
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={24}>
          <Form.Item
            name="logoPath"
            label="部门图标"
          >
            <div>
              {logoPath && (
                <img
                  src={getImageUrl(logoPath)}
                  alt="部门图标"
                  style={{ height: '80px', marginRight: '20px', cursor: 'pointer' }}
                  onClick={() => {
                    window.open(getImageUrl(logoPath), '_blank');
                  }}
                />
              )}
              <Upload
                beforeUpload={handleUpload}
                showUploadList={false}
              >
                <Button icon={<UploadOutlined />}>点击上传</Button>
              </Upload>
            </div>
          </Form.Item>
        </Col>
      </Row>

      <Form.Item style={{ marginBottom: 0, textAlign: 'right', marginTop: 16 }}>
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

export default DeptForm;

