package ru.itmo.soa.hr;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.time.Duration;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = HrController.class)
public class HrConfiguration implements WebMvcConfigurer {
    @Bean
    public WorkersClient workersClient() throws Exception {
        URI base = URI.create(setting("workers.base.url", "WORKERS_BASE_URL", "https://localhost:8181/api/v1"));
        if (!"https".equals(base.getScheme())) {
            throw new IllegalArgumentException("workers.base.url must use HTTPS");
        }
        HttpClient.Builder builder = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER);
        String storePath = System.getProperty("workers.truststore", "");
        if (!storePath.isBlank()) {
            KeyStore store = KeyStore.getInstance("PKCS12");
            char[] password = System.getProperty("workers.truststore.password", "").toCharArray();
            try (InputStream input = Files.newInputStream(Path.of(storePath))) {
                store.load(input, password);
            } finally {
                Arrays.fill(password, '\0');
            }
            TrustManagerFactory trust = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trust.init(store);
            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(null, trust.getTrustManagers(), null);
            builder.sslContext(ssl);
        }
        String token = setting("workers.internal.token", "WORKERS_INTERNAL_TOKEN", "");
        if (token.isBlank()) throw new IllegalStateException("Internal token is required");
        return new WorkersClient(builder.build(), base, token);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = Arrays.stream(setting("cors.allowed.origins", "CORS_ALLOWED_ORIGINS",
                        "https://localhost:8443").split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
        registry.addMapping("/**").allowedOrigins(origins).allowedMethods("POST", "OPTIONS")
                .allowedHeaders("Content-Type", "Accept").maxAge(3600);
    }

    private static String setting(String property, String environment, String fallback) {
        String value = System.getProperty(property);
        if (value == null || value.isBlank()) {
            value = System.getenv(environment);
        }
        return value == null || value.isBlank() ? fallback : value;
    }
}
