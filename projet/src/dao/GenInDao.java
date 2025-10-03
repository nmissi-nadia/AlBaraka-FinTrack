package dao;
import java.util.List;
import java.util.UUID;

public interface GenInDao<T> {
    void create(T entity);
    T findById (UUID id);
    List<T> findAll();
    void update(T entity);
    void delete(T entity);
}
