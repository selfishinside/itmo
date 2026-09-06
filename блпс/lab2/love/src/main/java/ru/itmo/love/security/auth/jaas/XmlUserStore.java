package ru.itmo.love.security.auth.jaas;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * утилитный класс для загрузки пользователей из xml-файла
 * парсит xml-документ и возвращает словарь username → XmlUserRecord
 * не инстанциируется: содержит только статические методы
 */
public final class XmlUserStore {

    /** скрытый конструктор — класс не предназначен для создания экземпляров */
    private XmlUserStore() {
    }

    /**
     * загружает пользователей из xml-потока и возвращает неизменяемую карту
     * ожидаемый формат xml:
     * <pre>{@code
     * <users>
     *   <user username="alice" password="secret">
     *     <role>ROLE_USER</role>
     *   </user>
     * </users>
     * }</pre>
     *
     * @param xmlInputStream входной поток с xml-содержимым
     * @return неизменяемая карта username → XmlUserRecord
     * @throws IllegalStateException если xml не может быть разобран
     */
    public static Map<String, XmlUserRecord> load(InputStream xmlInputStream) {
        try {
            Element root = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(xmlInputStream)
                    .getDocumentElement();

            NodeList users = root.getElementsByTagName("user");
            Map<String, XmlUserRecord> result = new HashMap<>();

            for (int i = 0; i < users.getLength(); i++) {
                Element userElement = (Element) users.item(i);
                String username = userElement.getAttribute("username");
                String password = userElement.getAttribute("password");

                // собираем список ролей из дочерних элементов <role>
                NodeList roleNodes = userElement.getElementsByTagName("role");
                Set<String> roles = new HashSet<>();
                for (int j = 0; j < roleNodes.getLength(); j++) {
                    String role = roleNodes.item(j).getTextContent().trim();
                    if (!role.isEmpty()) {
                        roles.add(role);
                    }
                }

                result.put(username, new XmlUserRecord(username, password, Set.copyOf(roles)));
            }

            return Map.copyOf(result);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new IllegalStateException("Failed to parse XML users file", e);
        }
    }
}
