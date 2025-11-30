import React, { useEffect } from 'react';
import {
  Form,
  Input,
  Select,
  Button,
  Row,
  Col,
  message,
  Space
} from 'antd';
import { create${className}Api, update${className}Api, get${className}DetailApi } from '../../api/${classname}';
import type {
  ${className}Entity,
  ${className}CreateRequest,
  ${className}UpdateRequest
} from '../../types';

const { Option } = Select;
const { TextArea } = Input;

interface ${className}FormProps {
  record?: ${className}Entity | null;
  onSuccess: () => void;
  onCancel: () => void;
}

const ${className}Form: React.FC<${className}FormProps> = ({
  record,
  onSuccess,
  onCancel
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);

  const isEdit = !!record;

  useEffect(() => {
    if (!record) {
      form.resetFields();
    }
  }, [record, form]);

  useEffect(() => {
    if (isEdit && record) {
      const fetchDetail = async () => {
        try {
          const id = record.${primaryKey.attrname} || (record as any).id;
          const detail = await get${className}DetailApi(id);
          form.setFieldsValue(detail);
        } catch (error) {
          console.error('获取详情失败:', error);
          message.error('获取详情失败');
        }
      };
      fetchDetail();
    }
  }, [isEdit, record, form]);

  const handleSubmit = async (values: any) => {
    setLoading(true);
    try {
      if (isEdit) {
        const updateData: ${className}UpdateRequest = {
          ${primaryKey.attrname}: record!.${primaryKey.attrname} || (record as any).id,
<#list entityColumns as column>
<#if column.attrname != primaryKey.attrname>
          ${column.attrname}: values.${column.attrname},
</#if>
</#list>
        };
        await update${className}Api(updateData);
        message.success('更新成功');
      } else {
        const createData: ${className}CreateRequest = {
<#list entityColumns as column>
<#if column.attrname != primaryKey.attrname>
          ${column.attrname}: values.${column.attrname},
</#if>
</#list>
        };
        await create${className}Api(createData);
        message.success('创建成功');
      }
      onSuccess();
    } catch (error: unknown) {
      console.error('保存失败:', error);
      
      let errorMessage = '保存失败';
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
    <Form autoComplete="off"
      form={form}
      layout="vertical"
      onFinish={handleSubmit}
    >
      <Row gutter={16}>
<#list entityColumns as column>
<#if column.attrname != primaryKey.attrname>
        <Col span={12}>
          <Form.Item
            name="${column.attrname}"
            label="${column.columnComment}"
<#if column.canNull == 'NO'>
            rules={[{ required: true, message: '请输入${column.columnComment}' }]}
</#if>
<#if column.attrname == 'status'>
            rules={[{ required: true, message: '请选择状态' }]}
</#if>
<#if column.dataType == 'varchar' && column.maxLength??>
            rules={[
<#if column.canNull == 'NO'>
              { required: true, message: '请输入${column.columnComment}' },
</#if>
              { max: ${column.maxLength}, message: '${column.columnComment}最多${column.maxLength}个字符' }
            ]}
</#if>
          >
<#if column.attrname == 'status'>
            <Select placeholder="请选择状态" allowClear>
              <Option value="ACTIVE">正常</Option>
              <Option value="LOCK">锁定</Option>
            </Select>
<#elseif column.dataType == 'text' || column.dataType == 'longtext' || column.dataType == 'mediumtext'>
            <TextArea
              rows={3}
              placeholder="请输入${column.columnComment}"
<#if column.maxLength??>
              maxLength={${column.maxLength}}
</#if>
            />
<#else>
            <Input 
              placeholder="请输入${column.columnComment}"
<#if column.maxLength??>
              maxLength={${column.maxLength}}
</#if>
            />
</#if>
          </Form.Item>
        </Col>
</#if>
</#list>
      </Row>

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

export default ${className}Form;

