package mmugur81.banktransfer.service;

import mmugur81.banktransfer.repository.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public class CRUDServiceImpl<T> implements CRUDService<T> {
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    private String typeName = ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[0]
            .getTypeName();

    @Override
    public T create(T t) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(t);
            session.getTransaction().commit();
        }

        return t;
    }

    @Override
    public T get(long id) {
        T result;
        try (Session session = sessionFactory.openSession()) {
            result = (T) session.get(typeName, id);
        }

        return result;
    }

    @Override
    public List<T> list() {
        List<T> list;

        try (Session session = sessionFactory.openSession()) {
            list = session.createQuery("FROM " + typeName).list();
        }

        return list;
    }
}
