package com.tripmate.sns.repository;

import com.tripmate.sns.domain.Sns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnsRepository extends JpaRepository<Sns, Long> {
    List<Sns> findAllByLocation(String location);
}
