package com.gilia;

import com.gilia.config.SwaggerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API Application Init. The execution of main method of this class results in the building and deployment of the API.
 */
@SpringBootApplication
@Import(SwaggerConfiguration.class)
public class MetamodelApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(MetamodelApplication.class, args);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

    }
}