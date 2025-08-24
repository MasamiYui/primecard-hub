/**
 * 通用工具函数
 */

// 格式化时间
const formatTime = (date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hour = date.getHours();
  const minute = date.getMinutes();
  const second = date.getSeconds();

  return [
    [year, month, day].map(formatNumber).join('-'),
    [hour, minute, second].map(formatNumber).join(':')
  ].join(' ');
};

// 格式化日期
const formatDate = (date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();

  return [year, month, day].map(formatNumber).join('-');
};

// 格式化数字
const formatNumber = (n) => {
  n = n.toString();
  return n[1] ? n : `0${n}`;
};

// 格式化金额
const formatAmount = (amount, decimal = 2) => {
  if (typeof amount !== 'number') {
    amount = parseFloat(amount) || 0;
  }
  return amount.toFixed(decimal).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

// 获取相对时间
const getRelativeTime = (dateString) => {
  const date = new Date(dateString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  // 转换为秒
  const seconds = Math.floor(diff / 1000);
  
  // 小于1分钟
  if (seconds < 60) {
    return '刚刚';
  }
  
  // 小于1小时
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) {
    return `${minutes}分钟前`;
  }
  
  // 小于1天
  const hours = Math.floor(minutes / 60);
  if (hours < 24) {
    return `${hours}小时前`;
  }
  
  // 小于30天
  const days = Math.floor(hours / 24);
  if (days < 30) {
    return `${days}天前`;
  }
  
  // 小于12个月
  const months = Math.floor(days / 30);
  if (months < 12) {
    return `${months}个月前`;
  }
  
  // 大于等于12个月
  const years = Math.floor(months / 12);
  return `${years}年前`;
};

// 防抖函数
const debounce = (fn, delay = 500) => {
  let timer = null;
  return function(...args) {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
};

// 节流函数
const throttle = (fn, interval = 500) => {
  let last = 0;
  return function(...args) {
    const now = Date.now();
    if (now - last >= interval) {
      last = now;
      fn.apply(this, args);
    }
  };
};

// 生成随机字符串
const randomString = (length = 16) => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
};

// 深拷贝
const deepClone = (obj) => {
  if (obj === null || typeof obj !== 'object') return obj;
  const clone = Array.isArray(obj) ? [] : {};
  for (let key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      clone[key] = deepClone(obj[key]);
    }
  }
  return clone;
};

// 检查对象是否为空
const isEmpty = (obj) => {
  if (obj === null || obj === undefined) return true;
  if (typeof obj === 'string') return obj.trim() === '';
  if (Array.isArray(obj)) return obj.length === 0;
  if (typeof obj === 'object') return Object.keys(obj).length === 0;
  return false;
};

// 导出工具函数
module.exports = {
  formatTime,
  formatDate,
  formatNumber,
  formatAmount,
  getRelativeTime,
  debounce,
  throttle,
  randomString,
  deepClone,
  isEmpty
};