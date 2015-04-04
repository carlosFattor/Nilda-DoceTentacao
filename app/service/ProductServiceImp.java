package service;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import models.Product;
import play.db.jpa.JPA;
import DAO.GenericDao;
import DAO.ProductService;

public class ProductServiceImp extends GenericDao<Product, Long> implements ProductService{

	public ProductServiceImp(Class<Product> entityClass) {
		super(entityClass);
	}
	
	public Optional<List<Product>> findLikeString(String key, String value){
		
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Product>cq = builder.createQuery(Product.class);
		Root<Product> p = cq.from(Product.class);
		cq.select(p);
		cq.where(builder.like(p.<String>get(key), value));
		cq.where(builder.isTrue(p.get(key)));
		return Optional.ofNullable(getEntityManager().createQuery(cq).getResultList());
	}
	
public Optional<List<Product>> findBooleanCond(String key, boolean value){
		
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Product>cq = builder.createQuery(Product.class);
		Root<Product> p = cq.from(Product.class);
		cq.select(p);
		cq.where(builder.equal(p.get(key), value));
		return Optional.ofNullable(getEntityManager().createQuery(cq).getResultList());
	}

	@Override
	protected EntityManager getEntityManager() {
		return JPA.em();
	}
}
