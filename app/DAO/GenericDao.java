package DAO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public abstract class GenericDao<T, PK extends Number> {

	private Class<T> entityClass;

	public GenericDao(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	protected abstract EntityManager getEntityManager();
	
	public T save(T e){
		e = getEntityManager().merge(e);
		return e;
	}
	
	public void remove(T e){
		getEntityManager().remove(getEntityManager().merge(e));
	}
	
	public Optional<T> find(Long id){
		return Optional.ofNullable(getEntityManager().find(entityClass, id));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findAll(){
		CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		return getEntityManager().createQuery(cq).getResultList();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<T> findRange(int[] range){
		CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		Query q = getEntityManager().createQuery(cq);
		q.setMaxResults(range[1] - range[0]);
		q.setFirstResult(range[0]);
		return q.getResultList();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public long count(){
		CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		Root<T> rt = cq.from(entityClass);
		cq.select(getEntityManager().getCriteriaBuilder().count(rt));
		Query q = getEntityManager().createQuery(cq);
		return ((Long)q.getSingleResult()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByNamedQuery(String namedQuery, Map<String, Object>param) throws Exception{
		Query q = getEntityManager().createNamedQuery(namedQuery);

		for (Map.Entry<String, Object> p : param.entrySet()) {
            q.setParameter(p.getKey(), p.getValue());
        }
		
		try {
			return q.getResultList();
		} catch (Exception e) {
			throw new Exception("Can't find value",e.getCause());
		}
	}
}
