package com.softawii.hermes.repository;

import com.softawii.hermes.entity.PlayerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerModel, String> {
}
