package urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.Arrays;
import static urlshortener.utils.SafeBrowsing.checkURLs;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        /*System.out.println("Ejecutando comprobacion de URLs");
        checkURLs(Arrays.asList("https://developers.google.com/safe-browsing/v4/lists", "https://www.youtube.com/"));
        System.out.println("URLs comprobadas");*/
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

}