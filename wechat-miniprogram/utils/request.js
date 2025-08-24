/**
 * 网络请求工具类
 */

const app = getApp();

// 请求方法
const request = (url, method, data, header = {}) => {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token');
    
    // 合并请求头
    const requestHeader = {
      'Content-Type': 'application/json',
      ...header
    };
    
    // 如果有token，添加到请求头
    if (token) {
      requestHeader['Authorization'] = `Bearer ${token}`;
    }
    
    // 显示加载中
    wx.showLoading({
      title: '加载中',
      mask: true
    });
    
    wx.request({
      url: url.startsWith('http') ? url : `${app.globalData.baseUrl}${url}`,
      method,
      data,
      header: requestHeader,
      success: (res) => {
        // 隐藏加载中
        wx.hideLoading();
        
        // 请求成功
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(res.data);
        } else if (res.statusCode === 401) {
          // 未授权，清除登录状态
          app.clearLoginInfo();
          
          // 跳转到登录页
          wx.navigateTo({
            url: '/pages/profile/profile'
          });
          
          reject({
            code: 401,
            message: '登录已过期，请重新登录'
          });
        } else {
          // 其他错误
          reject({
            code: res.statusCode,
            message: res.data.message || '请求失败'
          });
        }
      },
      fail: (err) => {
        // 隐藏加载中
        wx.hideLoading();
        
        // 网络错误
        reject({
          code: -1,
          message: '网络请求失败，请检查网络连接'
        });
      }
    });
  });
};

// 导出请求方法
module.exports = {
  // GET请求
  get: (url, data = {}, header = {}) => {
    return request(url, 'GET', data, header);
  },
  
  // POST请求
  post: (url, data = {}, header = {}) => {
    return request(url, 'POST', data, header);
  },
  
  // PUT请求
  put: (url, data = {}, header = {}) => {
    return request(url, 'PUT', data, header);
  },
  
  // DELETE请求
  delete: (url, data = {}, header = {}) => {
    return request(url, 'DELETE', data, header);
  },
  
  // 上传文件
  upload: (url, filePath, name = 'file', formData = {}) => {
    return new Promise((resolve, reject) => {
      const token = wx.getStorageSync('token');
      
      wx.uploadFile({
        url: url.startsWith('http') ? url : `${app.globalData.baseUrl}${url}`,
        filePath,
        name,
        formData,
        header: {
          'Authorization': token ? `Bearer ${token}` : ''
        },
        success: (res) => {
          if (res.statusCode >= 200 && res.statusCode < 300) {
            // 上传成功
            let data = res.data;
            try {
              // 尝试解析JSON
              data = JSON.parse(data);
            } catch (e) {
              // 解析失败，使用原始数据
            }
            resolve(data);
          } else if (res.statusCode === 401) {
            // 未授权，清除登录状态
            app.clearLoginInfo();
            
            // 跳转到登录页
            wx.navigateTo({
              url: '/pages/profile/profile'
            });
            
            reject({
              code: 401,
              message: '登录已过期，请重新登录'
            });
          } else {
            // 其他错误
            reject({
              code: res.statusCode,
              message: '上传失败'
            });
          }
        },
        fail: () => {
          // 上传失败
          reject({
            code: -1,
            message: '网络请求失败，请检查网络连接'
          });
        }
      });
    });
  }
};