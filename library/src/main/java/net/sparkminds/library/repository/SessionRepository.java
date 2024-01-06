package net.sparkminds.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.sparkminds.library.entity.Session;

public interface SessionRepository extends JpaRepository<Session, Long>{

}
