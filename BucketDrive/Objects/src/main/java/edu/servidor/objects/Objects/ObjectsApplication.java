package edu.servidor.objects.Objects;

import edu.servidor.objects.Objects.interceptors.NeedToBeLoggedInterceptor;
import edu.servidor.objects.Objects.interceptors.NeedToBeUnloggedInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ObjectsApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(ObjectsApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new NeedToBeLoggedInterceptor())
                .addPathPatterns("/objects/**")
                .addPathPatterns("/settings")
                .addPathPatterns("/download/**")
                .addPathPatterns("/deletebucket/**")
                .addPathPatterns("/object/**")
                .addPathPatterns("/deleteobj/**");

        registry.addInterceptor(new NeedToBeUnloggedInterceptor())
                .addPathPatterns("/index")
                .addPathPatterns("/login")
                .addPathPatterns("/signup")
                .addPathPatterns("/");
    }

}
