package service;

import javax.persistence.EntityManager;

import models.User;
import play.db.jpa.JPA;
import DAO.GenericDao;
import DAO.UserService;

public class UserServiceImp extends GenericDao<User, Long> implements UserService{

	public UserServiceImp(Class<User> entityClass) {
		super(entityClass);
	}

	@Override
	protected EntityManager getEntityManager() {
		return JPA.em();
	}
}
