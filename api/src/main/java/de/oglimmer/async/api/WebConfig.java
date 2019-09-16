package de.oglimmer.async.api;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import de.oglimmer.async.api.service.AsyncPost;

@EnableWebMvc
@Configuration
@ServletComponentScan(basePackageClasses = AsyncPost.class)
public class WebConfig implements WebMvcConfigurer {

	private int MAX_UPLOAD_SIZE = 5 * 1024 * 1024;

	@Bean
	public ViewResolver viewResolver() {
		final InternalResourceViewResolver bean = new InternalResourceViewResolver();
		bean.setViewClass(JstlView.class);
		bean.setPrefix("/WEB-INF/view/");
		bean.setSuffix(".jsp");
		bean.setOrder(0);
		return bean;
	}

	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();        
    }    

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver2() {
	    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
	    multipartResolver.setMaxUploadSize(MAX_UPLOAD_SIZE);
	    return multipartResolver;
	}

//	@Bean
//	public StandardServletMultipartResolver multipartResolver3() {
//	    return new StandardServletMultipartResolver();
//	}

}
