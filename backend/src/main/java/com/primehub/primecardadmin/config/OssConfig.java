package com.primehub.primecardadmin.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置类
 * 
 * @author PrimeCardHub
 * @since 2024-09-06
 */
@Configuration
public class OssConfig {

    @Bean
    public OSS ossClient(OssProperties ossProperties) {
        return new OSSClientBuilder().build(
            ossProperties.getEndpoint(), 
            ossProperties.getAccessKeyId(), 
            ossProperties.getAccessKeySecret()
        );
    }

    /**
     * OSS配置属性类
     */
    @Data
    @Component
    @ConfigurationProperties(prefix = "oss")
    public static class OssProperties {
        /**
         * OSS服务端点
         */
        private String endpoint;
        
        /**
         * 访问密钥ID
         */
        private String accessKeyId;
        
        /**
         * 访问密钥Secret
         */
        private String accessKeySecret;
        
        /**
         * 存储桶名称
         */
        private String bucketName;
        
        /**
         * URL前缀
         */
        private String urlPrefix;
        
        /**
         * 路径前缀配置
         */
        private PathPrefix pathPrefix = new PathPrefix();
        
        @Data
        public static class PathPrefix {
            /**
             * 轮播图路径前缀
             */
            private String banner = "banner/";
            
            /**
             * 新闻图片路径前缀
             */
            private String news = "news/";
            
            /**
             * 头像路径前缀
             */
            private String avatar = "avatar/";
        }
    }
}