package ru.itmo.soa.hr;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class HttpsOnlyFilter implements Filter {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!((HttpServletRequest) request).isSecure()) {
            HttpServletResponse http = (HttpServletResponse) response;
            http.setStatus(403);
            http.setContentType("application/json");
            http.setCharacterEncoding("UTF-8");
            mapper.writeValue(http.getOutputStream(), new ApiErrors.Error(403, "HTTPS is required"));
            return;
        }
        chain.doFilter(request, response);
    }
}
