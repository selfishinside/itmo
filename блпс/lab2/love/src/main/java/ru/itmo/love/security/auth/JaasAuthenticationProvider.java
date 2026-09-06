package ru.itmo.love.security.auth;

import ru.itmo.love.security.auth.jaas.XmlJaasLoginModule;
import ru.itmo.love.security.model.RolePrincipal;
import ru.itmo.love.security.policy.RolePrivileges;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** jaas провайдер аутентификации загружающий пользователей из xml-файла */
@Component
public class JaasAuthenticationProvider implements AuthenticationProvider {

    /** имя контекста входа */
    private static final String LOGIN_CONTEXT_NAME = "LoveJaas";

    /** конфигурация jaas */
    private final Configuration jaasConfiguration;

    /** создаёт провайдер и инициализирует конфигурацию jaas */
    public JaasAuthenticationProvider(@Value("${security.jaas.users-xml:classpath:jaas-users.xml}") String usersXmlPath) {
        this.jaasConfiguration = new InMemoryJaasConfiguration(usersXmlPath);
    }

    /** аутентифицирует пользователя и возвращает токен с набором привилегий */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials() == null ? "" : authentication.getCredentials().toString();

        try {
            LoginContext loginContext = new LoginContext(
                    LOGIN_CONTEXT_NAME,
                    null,
                    new UsernamePasswordHandler(username, password),
                    jaasConfiguration
            );
            loginContext.login();

            Subject subject = loginContext.getSubject();
            Set<GrantedAuthority> authorities = new HashSet<>();

            subject.getPrincipals(RolePrincipal.class)
                    .forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role.getName()));
                        Set<String> privileges = RolePrivileges.ROLE_TO_PRIVILEGES.getOrDefault(role.getName(), Set.of());
                        privileges.forEach(privilege -> authorities.add(new SimpleGrantedAuthority(privilege)));
                    });

            if (authorities.isEmpty()) {
                throw new BadCredentialsException("User has no granted authorities");
            }

            return new UsernamePasswordAuthenticationToken(username, null, authorities);
        } catch (LoginException e) {
            throw new BadCredentialsException("Authentication failed", e);
        }
    }

    /** проверяет поддержку типа токена */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /** конфигурация jaas хранящая параметры в памяти */
    private static final class InMemoryJaasConfiguration extends Configuration {

        /** путь к xml-файлу пользователей */
        private final String usersXmlPath;

        /** создаёт конфигурацию с указанным путём к xml */
        private InMemoryJaasConfiguration(String usersXmlPath) {
            this.usersXmlPath = usersXmlPath;
        }

        /** возвращает конфигурацию модуля входа для указанного приложения */
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            if (!LOGIN_CONTEXT_NAME.equals(name)) {
                return new AppConfigurationEntry[0];
            }

            Map<String, String> options = Map.of(
                    XmlJaasLoginModule.OPTION_USERS_XML_PATH,
                    usersXmlPath
            );

            return new AppConfigurationEntry[]{new AppConfigurationEntry(
                    XmlJaasLoginModule.class.getName(),
                    AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                    options
            )};
        }
    }

    /** обработчик обратных вызовов для передачи логина и пароля */
    private static final class UsernamePasswordHandler implements CallbackHandler {

        private final String username;
        private final String password;

        /** создаёт обработчик с заданными логином и паролем */
        private UsernamePasswordHandler(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /** обрабатывает обратные вызовы передавая имя пользователя и пароль */
        @Override
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback nameCallback) {
                    nameCallback.setName(username);
                } else if (callback instanceof PasswordCallback passwordCallback) {
                    passwordCallback.setPassword(password.toCharArray());
                } else {
                    throw new UnsupportedCallbackException(callback, "Unsupported callback type");
                }
            }
        }
    }
}
