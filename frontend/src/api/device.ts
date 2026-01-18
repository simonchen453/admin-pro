import { request } from './request';

// 获取在线设备列表
export const getDeviceListApi = () => {
  return request.get('/auth/devices');
};

// 踢出设备
export const kickoutDeviceApi = (deviceId: string) => {
  return request.delete(`/auth/devices/${deviceId}`);
};
