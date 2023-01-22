package edu.servidor.objects.Objects;

import edu.servidor.objects.Objects.interceptors.MyAuthInterceptor;
import edu.servidor.objects.Objects.interceptors.MyLogInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@SpringBootApplication
public class ObjectsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ObjectsApplication.class, args);
	}
	public void addInterceptors(InterceptorRegistry registry){
		registry.addInterceptor(new MyAuthInterceptor())
				.addPathPatterns("/objects/**")
				.addPathPatterns("/settings/");

		registry.addInterceptor(new MyLogInterceptor())
				.addPathPatterns("/**");
	}
}
