package org.ruslan.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.ruslan.models.Point;

import java.util.List;

public class PointDao {

    private static PointDao instance;

    private SessionFactory sessionFactory;

    public static synchronized PointDao getInstance() {
        if (instance == null) {
            instance = new PointDao();
        }
        return instance;
    }

    private PointDao() {
        setUp();
    }

    public void setUp() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy( registry );
            e.printStackTrace();
            throw e;
        }
    }

    public void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public void addPoint(Point point) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.persist(point);

            session.getTransaction().commit();
        }
    }

    public List<Point> getPoints() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            List<Point> points = session.createQuery( "select p from Point p" , Point.class).list();
            session.getTransaction().commit();
            return points;
        }
    }

    public int clear() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            int deletedCount = session.createQuery("delete from Point p").executeUpdate();

            session.getTransaction().commit();
            return deletedCount;
        }
    }
}
