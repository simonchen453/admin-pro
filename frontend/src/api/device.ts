import request from './request';

// 获取在线设备列表 (User)
export const getDeviceListApi = () => {
  return request.get('/auth/devices');
};

// 踢出设备 (User)
export const kickoutDeviceApi = (deviceId: string) => {
  return request.delete(`/auth/devices/${deviceId}`);
};

// --- Admin APIs ---

// 获取所有设备列表 (Admin)
export const getAllDevicesApi = (params: any) => {
  return request.get('/devices/all', { params });
};

// 踢出指定用户的设备 (Admin)
export const kickoutAnyDeviceApi = (userId: string, deviceId: string) => {
  return request.delete(`/devices/${userId}/${deviceId}`);
};
