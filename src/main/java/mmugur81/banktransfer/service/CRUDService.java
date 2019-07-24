package mmugur81.banktransfer.service;

import java.util.List;

public interface CRUDService<T> {

    T create(T t);

    T get(long id);

    List<T> list();
}
