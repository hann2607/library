package net.sparkminds.library.service;

import java.util.Optional;

import net.sparkminds.library.entity.Session;

public interface SessionService {
	void create(Session session);
	
	void update(Session session);
	
	Optional<Session> findByJti(String JTI);
}
