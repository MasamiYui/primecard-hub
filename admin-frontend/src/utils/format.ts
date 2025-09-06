/**
 * 格式化日期时间
 * @param dateTime 日期时间字符串或Date对象
 * @param format 格式化模板，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns 格式化后的日期时间字符串
 */
export function formatDateTime(dateTime: string | Date | null | undefined, format: string = 'YYYY-MM-DD HH:mm:ss'): string {
  if (!dateTime) return '';
  
  const date = typeof dateTime === 'string' ? new Date(dateTime) : dateTime;
  
  if (isNaN(date.getTime())) return '';
  
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hours = date.getHours();
  const minutes = date.getMinutes();
  const seconds = date.getSeconds();
  
  return format
    .replace('YYYY', year.toString())
    .replace('MM', month.toString().padStart(2, '0'))
    .replace('DD', day.toString().padStart(2, '0'))
    .replace('HH', hours.toString().padStart(2, '0'))
    .replace('mm', minutes.toString().padStart(2, '0'))
    .replace('ss', seconds.toString().padStart(2, '0'));
}

/**
 * 格式化日期
 * @param date 日期字符串或Date对象
 * @returns 格式化后的日期字符串 (YYYY-MM-DD)
 */
export function formatDate(date: string | Date | null | undefined): string {
  return formatDateTime(date, 'YYYY-MM-DD');
}

/**
 * 格式化金额
 * @param amount 金额数值
 * @param currency 货币符号，默认为 '¥'
 * @param decimals 小数位数，默认为 2
 * @returns 格式化后的金额字符串
 */
export function formatAmount(amount: number | string | null | undefined, currency: string = '¥', decimals: number = 2): string {
  if (amount === null || amount === undefined) return '';
  
  const num = typeof amount === 'string' ? parseFloat(amount) : amount;
  
  if (isNaN(num)) return '';
  
  return `${currency}${num.toFixed(decimals)}`;
}