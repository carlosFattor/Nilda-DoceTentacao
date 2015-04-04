package DAO;

import java.util.List;
import java.util.Optional;

import models.EmailNews;

public interface NewsService {

public EmailNews save(EmailNews news);
	
	public void remove(EmailNews news);
	
	public Optional<EmailNews> find(Long id);
	
	public List<EmailNews> findAll();
}
