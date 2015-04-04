package service;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;
import models.Gallery;
import DAO.GalleryService;
import DAO.GenericDao;

public class GalleryServiceImp extends GenericDao<Gallery, Long> implements GalleryService{

	public GalleryServiceImp(Class<Gallery> entityClass) {
		super(entityClass);
	}

	@Override
	protected EntityManager getEntityManager() {
		return JPA.em();
	}

}
