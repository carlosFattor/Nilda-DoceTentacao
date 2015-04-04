package DAO;

import java.util.List;
import java.util.Optional;

import models.Gallery;

public interface GalleryService {

public Gallery save(Gallery product);
	
	public void remove(Gallery product);
	
	public Optional<Gallery> find(Long id);
	
	public List<Gallery> findAll();
}
