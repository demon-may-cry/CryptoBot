package com.skillbox.cryptobot.repositories;

import com.skillbox.cryptobot.entity.SubscribersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscribersRepository extends JpaRepository<SubscribersEntity, UUID> {

    boolean existsById(String id);

    SubscribersEntity findById(String id);

    @Query("SELECT s.price FROM SubscribersEntity s WHERE s.id = :id")
    Integer findPriceById(String id);

}
