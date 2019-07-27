package mmugur81.banktransfer.service;

import com.google.inject.ImplementedBy;

import java.util.List;
import java.util.Optional;

@ImplementedBy(CRUDServiceImpl.class)
public interface CRUDService<T> {

    T create(T t);

    Optional<T> get(long id);

    Object getWithClass(long id, String className);

    List<T> list();

    void update(T t);
}
