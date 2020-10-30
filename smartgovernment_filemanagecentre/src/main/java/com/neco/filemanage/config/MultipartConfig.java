package com.neco.filemanage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfig {
		
	@Value("${spring.multipart.maxFileSize}")
	private String maxFileSize;
	
	@Value("${spring.multipart.maxRequestSize}")
	private String maxRequestSize;
	
	
	/**  
     * 文件上传配置  
     * @return  
     */  
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大  
        //factory.setMaxFileSize(maxFileSize); //KB,MB
        long maxFileSizeLong = Long.parseLong(maxFileSize);
        DataSize fileSizeDataSize = DataSize.ofBytes(maxFileSizeLong);
        factory.setMaxFileSize(fileSizeDataSize);
        //设置总上传数据总大小
        long maxRequestSizeLong = Long.parseLong(maxRequestSize);
        DataSize requestSizeDataSize = DataSize.ofBytes(maxFileSizeLong);
        factory.setMaxRequestSize(requestSizeDataSize);
       // factory.setMaxRequestSize(maxRequestSize);
        return factory.createMultipartConfig();  
    }  
    
    
    
    
}
