import React, { useEffect, useState } from 'react';
import {
  Form,
  Input,
  Button,
  Space,
  message,
  Select,
  Table,
  Modal
} from 'antd';
import {
  EditOutlined,
  DeleteOutlined,
  PlusOutlined
} from '@ant-design/icons';
import { createDictApi, updateDictApi, getDictDetailApi } from '../../api/dict';
import type { DictEntity, DictDataEntity } from '../../types';
import type { ColumnsType } from 'antd/es/table';

const { TextArea } = Input;
const { Option } = Select;

interface DictFormProps {
  dict?: DictEntity | null;
  onSuccess: () => void;
  onCancel: () => void;
}

const DictForm: React.FC<DictFormProps> = ({
  dict,
  onSuccess,
  onCancel
}) => {
  const [form] = Form.useForm();
  const [dataForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [dataList, setDataList] = useState<DictDataEntity[]>([]);
  const [dataModalVisible, setDataModalVisible] = useState(false);
  const [editingDataIndex, setEditingDataIndex] = useState<number | null>(null);

  const isEdit = !!dict?.id;

  useEffect(() => {
    if (isEdit && dict?.id) {
      fetchDictDetail(dict.id);
    } else {
      form.resetFields();
      form.setFieldsValue({
        status: 'active'
      });
      setDataList([]);
    }
  }, [dict, form, isEdit]);

  const fetchDictDetail = async (id: string) => {
    try {
      const response = await getDictDetailApi(id);
      if (response.restCode === '200') {
        form.setFieldsValue({
          name: response.data.name,
          key: response.data.key,
          status: response.data.status,
          remark: response.data.remark
        });
        setDataList(response.data.data || []);
      } else {
        message.error(response.message || '获取字典详情失败');
      }
    } catch (error) {
      console.error('获取字典详情失败:', error);
      message.error('获取字典详情失败');
    }
  };

  const handleAddData = () => {
    if (!form.getFieldValue('key')) {
      message.warning('请先输入字典键值，再增加配置项');
      return;
    }
    setEditingDataIndex(null);
    dataForm.resetFields();
    dataForm.setFieldsValue({
      key: form.getFieldValue('key'),
      order: '1',
      status: 'active'
    });
    setDataModalVisible(true);
  };

  const handleEditData = (index: number, record: DictDataEntity) => {
    setEditingDataIndex(index);
    dataForm.setFieldsValue({
      key: record.key || form.getFieldValue('key'),
      value: record.value,
      label: record.label,
      order: record.order,
      cssClass: record.cssClass,
      status: record.status
    });
    setDataModalVisible(true);
  };

  const handleDeleteData = (index: number) => {
    const newDataList = dataList.filter((_, i) => i !== index);
    setDataList(newDataList);
  };

  const handleConfirmData = async () => {
    try {
      const values = await dataForm.validateFields();
      const key = form.getFieldValue('key');
      
      const dataItem: DictDataEntity = {
        key: key,
        value: values.value,
        label: values.label,
        order: values.order,
        cssClass: values.cssClass,
        status: values.status
      };

      if (editingDataIndex !== null) {
        const newDataList = [...dataList];
        newDataList[editingDataIndex] = dataItem;
        setDataList(newDataList);
      } else {
        setDataList([...dataList, dataItem]);
      }
      
      setDataModalVisible(false);
      setEditingDataIndex(null);
      dataForm.resetFields();
    } catch (error) {
      console.error('配置项验证失败:', error);
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      const formData: DictEntity = {
        id: dict?.id,
        name: values.name,
        key: values.key,
        status: values.status,
        remark: values.remark,
        data: dataList
      };

      let response;
      if (isEdit) {
        response = await updateDictApi(formData);
        if (response.restCode === '200') {
          message.success('字典更新成功');
          onSuccess();
        } else {
          if (response.errorsMap) {
            Object.keys(response.errorsMap).forEach(key => {
              form.setFields([{ name: key, errors: [response.errorsMap![key]] }]);
            });
          }
          message.error(response.message || '字典更新失败');
        }
      } else {
        response = await createDictApi(formData);
        if (response.restCode === '200') {
          message.success('字典创建成功');
          onSuccess();
        } else {
          if (response.errorsMap) {
            Object.keys(response.errorsMap).forEach(key => {
              form.setFields([{ name: key, errors: [response.errorsMap![key]] }]);
            });
          }
          message.error(response.message || '字典创建失败');
        }
      }
    } catch (error) {
      console.error('提交失败:', error);
      message.error(isEdit ? '字典更新失败' : '字典创建失败');
    } finally {
      setLoading(false);
    }
  };

  const dataColumns: ColumnsType<DictDataEntity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 50,
      render: (_, __, index) => index + 1
    },
    {
      title: '配置值',
      dataIndex: 'value',
      key: 'value',
    },
    {
      title: '显示值',
      dataIndex: 'label',
      key: 'label',
    },
    {
      title: '顺序',
      dataIndex: 'order',
      key: 'order',
    },
    {
      title: '样式',
      dataIndex: 'cssClass',
      key: 'cssClass',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => (
        <span>{status === 'active' ? '启用' : '停用'}</span>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record, index) => (
        <Space size="small">
          <Button
            size="small"
            type="primary"
            icon={<EditOutlined />}
            onClick={() => handleEditData(index, record)}
          >
            修改
          </Button>
          <Button
            size="small"
            type="primary"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleDeleteData(index)}
          >
            删除
          </Button>
        </Space>
      )
    }
  ];

  return (
    <>
      <Form
        form={form}
        layout="vertical"
      >
        <Form.Item
          name="name"
          label="字典名称"
          rules={[{ required: true, message: '字典名称不能为空' }]}
        >
          <Input placeholder="请输入字典名称" />
        </Form.Item>

        <Form.Item
          name="key"
          label="字典键值"
          rules={[{ required: true, message: '字典键值不能为空' }]}
        >
          <Input placeholder="请输入字典键值" disabled={isEdit} />
        </Form.Item>

        <Form.Item
          name="status"
          label="状态"
          rules={[{ required: true, message: '状态不能为空' }]}
        >
          <Select placeholder="请选择状态">
            <Option value="active">启用</Option>
            <Option value="inactive">停用</Option>
          </Select>
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

        <Form.Item label="配置项">
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAddData}>
            新增
          </Button>
        </Form.Item>

        <Form.Item>
          <Table
            columns={dataColumns}
            dataSource={dataList}
            rowKey={(record, index) => `data-${index}`}
            pagination={false}
            size="small"
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

      <Modal
        title={editingDataIndex !== null ? '修改配置项' : '新增配置项'}
        open={dataModalVisible}
        onOk={handleConfirmData}
        onCancel={() => {
          setDataModalVisible(false);
          setEditingDataIndex(null);
          dataForm.resetFields();
        }}
        width={600}
      >
        <Form
          form={dataForm}
          layout="vertical"
        >
          <Form.Item
            name="key"
            label="唯一键"
          >
            <Input disabled />
          </Form.Item>

          <Form.Item
            name="value"
            label="配置值"
            rules={[{ required: true, message: '配置值不能为空' }]}
          >
            <Input placeholder="请输入配置值" />
          </Form.Item>

          <Form.Item
            name="label"
            label="显示值"
            rules={[{ required: true, message: '显示值不能为空' }]}
          >
            <Input placeholder="请输入显示值" />
          </Form.Item>

          <Form.Item
            name="order"
            label="顺序"
            rules={[{ required: true, message: '顺序不能为空' }]}
          >
            <Input placeholder="请输入顺序" />
          </Form.Item>

          <Form.Item
            name="cssClass"
            label="样式"
          >
            <Input placeholder="请输入样式" />
          </Form.Item>

          <Form.Item
            name="status"
            label="状态"
            rules={[{ required: true, message: '状态不能为空' }]}
          >
            <Select placeholder="请选择状态">
              <Option value="active">启用</Option>
              <Option value="inactive">停用</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default DictForm;

