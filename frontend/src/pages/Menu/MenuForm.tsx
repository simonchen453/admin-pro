import React, { useEffect } from 'react';
import {
  Form,
  Input,
  Select,
  TreeSelect,
  Radio,
  InputNumber,
  Button,
  Space,
  Row,
  Col,
  message
} from 'antd';
import { createMenuApi, updateMenuApi } from '../../api/menu';
import type {
  MenuEntity,
  MenuTreeSelectNode
} from '../../types';
import { MenuType, MenuStatus, MenuVisible } from '../../types';

const { Option } = Select;

interface MenuFormProps {
  menu?: MenuEntity | null;
  menuOptions: MenuTreeSelectNode[];
  onSuccess: () => void;
  onCancel: () => void;
}

const MenuForm: React.FC<MenuFormProps> = ({
  menu,
  menuOptions,
  onSuccess,
  onCancel
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const [menuType, setMenuType] = React.useState<string>(MenuType.DIRECTORY);

  const isEdit = !!(menu && menu.id);

  useEffect(() => {
    if (menu) {
      form.setFieldsValue({
        ...menu,
        parentId: menu.parentId || 0,
        type: menu.type || MenuType.DIRECTORY,
        status: menu.status || MenuStatus.ACTIVE,
        visible: menu.visible || MenuVisible.SHOW,
        orderNum: menu.orderNum || 0
      });
      setMenuType(menu.type || MenuType.DIRECTORY);
    } else {
      form.resetFields();
      form.setFieldsValue({
        parentId: 0,
        type: MenuType.DIRECTORY,
        status: MenuStatus.ACTIVE,
        visible: MenuVisible.SHOW,
        orderNum: 0
      });
      setMenuType(MenuType.DIRECTORY);
    }
  }, [menu, form]);

  const convertMenuTreeToTreeSelect = (nodes: MenuTreeSelectNode[]): any[] => {
    return nodes.map(node => ({
      title: node.display,
      value: node.id,
      key: node.id,
      children: node.children ? convertMenuTreeToTreeSelect(node.children) : undefined
    }));
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      const formData: MenuEntity = {
        id: menu?.id,
        parentId: values.parentId || 0,
        display: values.display,
        name: values.name,
        icon: values.icon,
        type: values.type,
        orderNum: values.orderNum || 0,
        permission: values.permission,
        url: values.url,
        status: values.status,
        visible: values.visible
      };

      let response;
      if (isEdit) {
        response = await updateMenuApi(formData);
        if (response.restCode === '200') {
          message.success('修改成功');
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
          message.error(response.message || '修改失败');
        }
      } else {
        response = await createMenuApi(formData);
        if (response.restCode === '200') {
          message.success('新增成功');
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
          message.error(response.message || '新增失败');
        }
      }
    } catch (error: unknown) {
      if (error && typeof error === 'object' && 'errorFields' in error) {
        return;
      }
      console.error('保存菜单失败:', error);
      
      let errorMessage = '保存菜单失败';
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

  const treeSelectData = convertMenuTreeToTreeSelect(menuOptions);

  return (
    <Form
      form={form}
      layout="vertical"
      initialValues={{
        parentId: 0,
        type: MenuType.DIRECTORY,
        status: MenuStatus.ACTIVE,
        visible: MenuVisible.SHOW,
        orderNum: 0
      }}
    >
      <Row gutter={16}>
        <Col span={24}>
          <Form.Item
            name="parentId"
            label="上级菜单"
            rules={[{ required: true, message: '请选择上级菜单' }]}
          >
            <TreeSelect
              placeholder="选择上级菜单"
              treeData={treeSelectData}
              treeDefaultExpandAll
              showSearch
              allowClear
              treeNodeFilterProp="title"
              style={{ width: '100%' }}
            />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={24}>
          <Form.Item
            name="type"
            label="菜单类型"
            rules={[{ required: true, message: '请选择菜单类型' }]}
          >
            <Radio.Group 
              onChange={(e) => setMenuType(e.target.value)}
            >
              <Radio value={MenuType.DIRECTORY}>目录</Radio>
              <Radio value={MenuType.MENU}>菜单</Radio>
              <Radio value={MenuType.BUTTON}>按钮</Radio>
            </Radio.Group>
          </Form.Item>
        </Col>
      </Row>

      {menuType !== MenuType.BUTTON && (
        <Row gutter={16}>
          <Col span={24}>
            <Form.Item
              name="icon"
              label="菜单图标"
            >
              <Input placeholder="请输入菜单图标" />
            </Form.Item>
          </Col>
        </Row>
      )}

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="name"
            label="菜单名称"
            rules={[{ required: true, message: '请输入菜单名称' }]}
          >
            <Input placeholder="请输入菜单名称" />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="display"
            label="显示名称"
            rules={[{ required: true, message: '菜单名称不能为空' }]}
          >
            <Input placeholder="请输入显示名称" />
          </Form.Item>
        </Col>
      </Row>

      {menuType === MenuType.MENU && (
        <Row gutter={16}>
          <Col span={24}>
            <Form.Item
              name="url"
              label="链接路径"
              rules={[{ required: true, message: '请输入链接路径' }]}
            >
              <Input placeholder="请输入链接路径" />
            </Form.Item>
          </Col>
        </Row>
      )}

      <Row gutter={16}>
        <Col span={menuType === MenuType.DIRECTORY ? 24 : 12}>
          <Form.Item
            name="orderNum"
            label="显示排序"
            rules={[{ required: true, message: '菜单顺序不能为空' }]}
          >
            <InputNumber 
              controls={{ position: 'right' }}
              min={0}
              style={{ width: '100%' }}
            />
          </Form.Item>
        </Col>

        {menuType !== MenuType.DIRECTORY && (
          <Col span={12}>
            <Form.Item
              name="permission"
              label="权限标识"
              rules={[{ required: true, message: '请输入权限标识' }]}
            >
              <Input 
                placeholder="请输入权限标识" 
                maxLength={50}
                style={{ width: '100%' }}
              />
            </Form.Item>
          </Col>
        )}
      </Row>

      {menuType !== MenuType.BUTTON && (
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="visible"
              label="显示状态"
              rules={[{ required: true, message: '请选择显示状态' }]}
            >
              <Select placeholder="请选择" style={{ width: '100%' }}>
                <Option value={MenuVisible.SHOW}>显示</Option>
                <Option value={MenuVisible.HIDE}>隐藏</Option>
              </Select>
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="status"
              label="菜单状态"
              rules={[{ required: true, message: '请选择状态' }]}
            >
              <Select placeholder="请选择" style={{ width: '100%' }}>
                <Option value={MenuStatus.ACTIVE}>正常</Option>
                <Option value={MenuStatus.INACTIVE}>停用</Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>
      )}

      {menuType === MenuType.BUTTON && (
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name="status"
              label="菜单状态"
              rules={[{ required: true, message: '请选择状态' }]}
            >
              <Select placeholder="请选择" style={{ width: '100%' }}>
                <Option value={MenuStatus.ACTIVE}>正常</Option>
                <Option value={MenuStatus.INACTIVE}>停用</Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>
      )}

      <Form.Item style={{ marginBottom: 0, textAlign: 'right', marginTop: 16 }}>
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

export default MenuForm;
