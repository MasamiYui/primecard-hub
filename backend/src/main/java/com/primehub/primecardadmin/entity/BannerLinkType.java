package com.primehub.primecardadmin.entity;

public enum BannerLinkType {
    NONE,       // 无链接，纯展示
    CARD,       // 跳转到信用卡详情
    NEWS,       // 跳转到资讯详情
    EXTERNAL,   // 跳转到外部网页
    MINIPROGRAM // 跳转到其他小程序
}