package dao;
import java.util.List;
public interface GenInDao<T> {
    void create(T entity);
    T findById (int id);
    List<T> findAll();
    void update(T entity);
    void delete(T entity);
}
