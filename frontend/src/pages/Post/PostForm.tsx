import React, { useEffect } from 'react';
import {
  Form,
  Input,
  InputNumber,
  Radio,
  Button,
  Space,
  message
} from 'antd';
import { createPostApi, updatePostApi, getPostDetailApi } from '../../api/post';
import { PostStatus, type PostEntity } from '../../types';

const { TextArea } = Input;

interface PostFormProps {
  post?: PostEntity | null;
  onSuccess: () => void;
  onCancel: () => void;
}

const PostForm: React.FC<PostFormProps> = ({ post, onSuccess, onCancel }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);

  const isEdit = !!post?.id;

  useEffect(() => {
    if (isEdit && post?.id) {
      fetchPostDetail(post.id);
    } else {
      form.resetFields();
      form.setFieldsValue({
        sort: 0,
        status: PostStatus.ACTIVE
      });
    }
  }, [post, form, isEdit]);

  const fetchPostDetail = async (id: string) => {
    try {
      const response = await getPostDetailApi(id);
      if (response.restCode === '200') {
        form.setFieldsValue({
          code: response.data.code,
          name: response.data.name,
          sort: response.data.sort || 0,
          status: response.data.status,
          remark: response.data.remark
        });
      } else {
        message.error(response.message || '获取岗位详情失败');
      }
    } catch (error) {
      console.error('获取岗位详情失败:', error);
      message.error('获取岗位详情失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      const formData: PostEntity = {
        id: post?.id,
        code: values.code,
        name: values.name,
        sort: values.sort || 0,
        status: values.status,
        remark: values.remark
      };

      let response;
      if (isEdit) {
        response = await updatePostApi(formData);
        if (response.restCode === '200') {
          message.success('岗位更新成功');
          onSuccess();
        } else {
          if (response.errorsMap) {
            Object.keys(response.errorsMap).forEach(key => {
              form.setFields([{ name: key, errors: [response.errorsMap![key]] }]);
            });
          }
          message.error(response.message || '岗位更新失败');
        }
      } else {
        response = await createPostApi(formData);
        if (response.restCode === '200') {
          message.success('岗位创建成功');
          onSuccess();
        } else {
          if (response.errorsMap) {
            Object.keys(response.errorsMap).forEach(key => {
              form.setFields([{ name: key, errors: [response.errorsMap![key]] }]);
            });
          }
          message.error(response.message || '岗位创建失败');
        }
      }
    } catch (error) {
      console.error('提交失败:', error);
      message.error(isEdit ? '岗位更新失败' : '岗位创建失败');
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
          label="岗位编码"
          name="code"
          rules={[
            { required: true, message: '岗位编码不能为空' }
          ]}
        >
          <Input placeholder="请输入岗位编码" allowClear />
        </Form.Item>

        <Form.Item
          label="岗位名称"
          name="name"
          rules={[
            { required: true, message: '岗位名称不能为空' }
          ]}
        >
          <Input placeholder="请输入岗位名称" allowClear />
        </Form.Item>

        <Form.Item
          label="岗位顺序"
          name="sort"
          rules={[
            { required: true, message: '岗位顺序不能为空' }
          ]}
        >
          <InputNumber
            placeholder="请输入岗位顺序"
            min={0}
            style={{ width: '100%' }}
            controls={{ position: 'right' }}
          />
        </Form.Item>

        <Form.Item
          label="岗位状态"
          name="status"
          rules={[
            { required: true, message: '请选择状态' }
          ]}
        >
          <Radio.Group>
            <Radio value={PostStatus.ACTIVE}>正常</Radio>
            <Radio value={PostStatus.INACTIVE}>停用</Radio>
          </Radio.Group>
        </Form.Item>

        <Form.Item
          label="备注"
          name="remark"
        >
          <TextArea
            placeholder="请输入备注"
            rows={3}
            allowClear
          />
        </Form.Item>
      </Form>

      <div style={{ textAlign: 'right', paddingTop: '16px', borderTop: '1px solid #f0f0f0' }}>
        <Space>
          <Button onClick={onCancel}>
            取消
          </Button>
          <Button type="primary" loading={loading} onClick={handleSubmit}>
            {isEdit ? '更新' : '创建'}
          </Button>
        </Space>
      </div>
    </div>
  );
};

export default PostForm;

