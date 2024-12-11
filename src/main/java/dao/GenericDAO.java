package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class GenericDAO<T, ID> {
    private Class<T> entityClass;
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("BiblioEjemplares");
    EntityManager em = emf.createEntityManager();
    EntityTransaction et = em.getTransaction();
    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;

    }
    public T read(ID id) {
        return em.find(entityClass, id);
    }
    public void create(T entity) {
        et.begin();
        em.persist(entity);
        et.commit();
    }
    public void update(T entity) {
        et.begin();
        em.merge(entity);
        et.commit();
    }
    public void delete(T entity) {
        et.begin();
        if (entity != null) {
            em.remove(entity);
        }
        et.commit();
    }
    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).getResultList();
    }

}
