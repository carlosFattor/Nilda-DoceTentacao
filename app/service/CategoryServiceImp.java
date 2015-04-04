package service;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;
import models.Category;
import DAO.CategoryService;
import DAO.GenericDao;

public class CategoryServiceImp extends GenericDao<Category, Long> implements CategoryService{

	public CategoryServiceImp(Class<Category> entityClass) {
		super(entityClass);
	}

	@Override
	protected EntityManager getEntityManager() {
		return JPA.em();
	}
}
