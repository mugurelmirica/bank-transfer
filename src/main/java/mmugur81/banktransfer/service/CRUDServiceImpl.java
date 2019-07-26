package mmugur81.banktransfer.service;

import mmugur81.banktransfer.repository.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public class CRUDServiceImpl<T> implements CRUDService<T> {
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    private final String typeName;

    public CRUDServiceImpl() {
        String classname;
        try {
            classname = ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0]
                    .getTypeName();
        } catch (ClassCastException e) {
            classname = getClass().getGenericSuperclass().getTypeName();

        }
        typeName = classname;
    }

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
    public Optional<T> get(long id) {
        T result;
        try (Session session = sessionFactory.openSession()) {
            result = (T) session.get(typeName, id);
        }

        return Optional.ofNullable(result);
    }

    @Override
    public List<T> list() {
        List<T> list;

        try (Session session = sessionFactory.openSession()) {
            list = session.createQuery("FROM " + typeName).list();
        }

        return list;
    }

    @Override
    public void update(T t) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.getTransaction();
            try {
                tx.begin();
                session.update(t);
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
                tx.rollback();
            }
        }
    }
}
