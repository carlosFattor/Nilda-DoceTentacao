package DAO;

import java.util.List;
import java.util.Optional;

import models.Product;

public interface ProductService {

	public Product save(Product product);
	
	public void remove(Product product);
	
	public Optional<Product> find(Long id);
	
	public List<Product> findAll();	
}
