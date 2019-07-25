package mmugur81.banktransfer.service;

import java.util.List;
import java.util.Optional;

public interface CRUDService<T> {

    T create(T t);

    Optional<T> get(long id);

    List<T> list();

    void update(T t);
}
