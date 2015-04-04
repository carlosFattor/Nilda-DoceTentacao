package service;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;
import models.EmailNews;
import DAO.GenericDao;
import DAO.NewsService;

public class NewsServiceImp extends GenericDao<EmailNews, Long> implements NewsService{

	public NewsServiceImp(Class<EmailNews> entityClass) {
		super(entityClass);
	}

	@Override
	protected EntityManager getEntityManager() {
		return JPA.em();
	}

}
