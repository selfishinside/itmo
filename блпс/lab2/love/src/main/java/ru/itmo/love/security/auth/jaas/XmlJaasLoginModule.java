package ru.itmo.love.security.auth.jaas;

import ru.itmo.love.security.model.RolePrincipal;
import ru.itmo.love.security.model.UserPrincipal;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** jaas модуль входа загружающий пользователей из xml-файла */
public class XmlJaasLoginModule implements LoginModule {

    /** ключ опции пути к xml-файлу пользователей */
    public static final String OPTION_USERS_XML_PATH = "usersXmlPath";

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> options;
    private UserPrincipal authenticatedUser;
    private final Set<RolePrincipal> rolePrincipals = new HashSet<>();
    private boolean loginSucceeded;

    /** инициализирует модуль переданными данными от jaas контейнера */
    @Override
    public void initialize(
            Subject subject,
            CallbackHandler callbackHandler,
            Map<String, ?> sharedState,
            Map<String, ?> options
    ) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.options = options;
    }

    /** выполняет аутентификацию пользователя по логину и паролю */
    @Override
    public boolean login() throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException("Missing callback handler");
        }

        NameCallback nameCallback = new NameCallback("username");
        PasswordCallback passwordCallback = new PasswordCallback("password", false);

        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Failed to read credentials: " + e.getMessage());
        }

        String username = nameCallback.getName();
        String password = passwordCallback.getPassword() == null
                ? ""
                : new String(passwordCallback.getPassword());
        passwordCallback.clearPassword();

        XmlUserRecord userRecord = findUser(username);
        if (userRecord == null || !Objects.equals(userRecord.password(), password)) {
            throw new FailedLoginException("Invalid username or password");
        }

        authenticatedUser = new UserPrincipal(userRecord.username());
        userRecord.roles().forEach(role -> rolePrincipals.add(new RolePrincipal(role)));
        loginSucceeded = true;
        return true;
    }

    /** добавляет principals в subject при успешной аутентификации */
    @Override
    public boolean commit() {
        if (!loginSucceeded) {
            return false;
        }

        Set<Principal> principals = subject.getPrincipals();
        principals.add(authenticatedUser);
        principals.addAll(rolePrincipals);
        return true;
    }

    /** отменяет попытку входа и очищает состояние */
    @Override
    public boolean abort() {
        clearState();
        return true;
    }

    /** удаляет principals из subject и очищает состояние */
    @Override
    public boolean logout() {
        if (authenticatedUser != null) {
            subject.getPrincipals().remove(authenticatedUser);
        }
        subject.getPrincipals().removeAll(rolePrincipals);
        clearState();
        return true;
    }

    /** загружает запись пользователя из xml-файла по имени */
    private XmlUserRecord findUser(String username) {
        Object usersXmlPathOption = options.get(OPTION_USERS_XML_PATH);
        String usersXmlPath = usersXmlPathOption == null
                ? "classpath:jaas-users.xml"
                : String.valueOf(usersXmlPathOption);
        String classpathLocation = usersXmlPath.replaceFirst("^classpath:", "");

        try (InputStream stream = XmlJaasLoginModule.class.getResourceAsStream(classpathLocation.startsWith("/")
                ? classpathLocation
                : "/" + classpathLocation)) {
            if (stream == null) {
                throw new IllegalStateException("Users XML not found: " + usersXmlPath);
            }

            return XmlUserStore.load(stream).get(username);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read users XML file", e);
        }
    }

    /** очищает внутреннее состояние модуля */
    private void clearState() {
        loginSucceeded = false;
        authenticatedUser = null;
        rolePrincipals.clear();
    }
}
