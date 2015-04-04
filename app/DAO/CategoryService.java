package DAO;

import java.util.List;
import java.util.Optional;

import models.Category;

public interface CategoryService {

	public Category save(Category product);
	
	public void remove(Category product);
	
	public Optional<Category> find(Long id);
	
	public List<Category> findAll();
}
