package mmugur81.banktransfer.service;

import com.google.inject.Singleton;
import mmugur81.banktransfer.domain.Holder;
import mmugur81.banktransfer.repository.HibernateUtil;
import org.hibernate.Session;

@Singleton
public class HolderServiceImpl implements HolderService {

    @Override
    public Holder create(Holder holder) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();
        session.save(holder);
        session.getTransaction().commit();

        return holder;
    }
}
