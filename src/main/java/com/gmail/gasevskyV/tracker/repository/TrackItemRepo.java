package com.gmail.gasevskyV.tracker.repository;

import com.gmail.gasevskyV.tracker.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface TrackItemRepo extends JpaRepository<Item, Long> {
    Optional<Item> findById(Long id);
    List<Item> findAllByActive(Boolean active);
}
