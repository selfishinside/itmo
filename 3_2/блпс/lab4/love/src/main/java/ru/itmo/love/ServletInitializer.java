package ru.itmo.love;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

 // инициализатор для war деплоя
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
 // настраивает источник приложения
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(LoveApplication.class);
    }

}
