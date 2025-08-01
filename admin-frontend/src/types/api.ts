// 通用API响应类型
export interface ApiResponse<T = any> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

// 分页响应类型
export interface PageResponse<T = any> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

// 用户相关类型
export interface User {
  id: number
  username: string
  email: string
  role: string
  status: string
  avatar?: string
  lastLoginAt?: string
  remark?: string
  createdAt: string
  updatedAt: string
}

export interface UserCreate {
  username: string
  email: string
  password: string
  role: string
  status: string
  remark?: string
}

export interface UserUpdate {
  email?: string
  role?: string
  status?: string
  remark?: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  refreshToken: string
  user: User
}

// 银行相关类型
export interface Bank {
  id: number
  name: string
  code: string
  logoUrl?: string
  description?: string
  createdAt: string
  updatedAt: string
}

// 信用卡相关类型
export interface Card {
  id: number
  name: string
  subtitle?: string
  bank?: Bank
  category?: Category
  tags?: Tag[]
  annualFee: number
  status: string
  imageUrl?: string
  description?: string
  createdAt: string
  updatedAt: string
}

export interface CardCreate {
  name: string
  subtitle?: string
  bankId: number
  categoryId: number
  annualFee: number
  status: string
  tagIds?: number[]
  imageUrl?: string
  description?: string
}

export interface CardUpdate {
  name?: string
  subtitle?: string
  bankId?: number
  categoryId?: number
  annualFee?: number
  status?: string
  tagIds?: number[]
  imageUrl?: string
  description?: string
}

export interface CreditCard {
  id: number
  name: string
  bankName: string
  cardType: string
  annualFee: number
  description: string
  imageUrl: string
  isActive: boolean
  createdAt: string
  updatedAt: string
  categories: Category[]
  tags: Tag[]
}

export interface CreditCardCreate {
  name: string
  bankName: string
  cardType: string
  annualFee: number
  description: string
  imageUrl?: string
  isActive: boolean
  categoryIds: number[]
  tagIds: number[]
}

// 分类相关类型
export interface Category {
  id: number
  name: string
  description: string
  createdAt: string
  updatedAt: string
}

export interface CategoryCreate {
  name: string
  description: string
}

// 标签相关类型
export interface Tag {
  id: number
  name: string
  color: string
  createdAt: string
  updatedAt: string
}

export interface TagCreate {
  name: string
  color: string
}

// 新闻相关类型
export interface News {
  id: number
  title: string
  content: string
  summary: string
  imageUrl?: string
  status: string // DRAFT, PUBLISHED, ARCHIVED
  publishedAt?: string
  viewCount?: number
  author: string
  category?: Category
  tags?: Tag[]
  createdAt: string
  updatedAt: string
}

export interface NewsCreate {
  title: string
  content: string
  summary: string
  imageUrl?: string
  status: string
  categoryId?: number
  tagIds?: number[]
}

export interface NewsUpdate {
  title?: string
  content?: string
  summary?: string
  imageUrl?: string
  status?: string
  categoryId?: number
  tagIds?: number[]
}

// 统计数据类型
export interface Statistics {
  totalUsers: number
  totalCreditCards: number
  totalNews: number
  totalCategories: number
  recentUsers: User[]
  recentNews: News[]
}

// 查询参数类型
export interface QueryParams {
  page?: number
  size?: number
  sort?: string
  search?: string
  [key: string]: any
}