package DAO;

import java.util.List;
import java.util.Optional;

import models.User;

public interface UserService {

	public User save(User product);
	
	public void remove(User product);
	
	public Optional<User> find(Long id);
	
	public List<User> findAll();
}
