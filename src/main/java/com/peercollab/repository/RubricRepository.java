package com.peercollab.repository;

import com.peercollab.model.Rubric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RubricRepository extends JpaRepository<Rubric, Long> {
    List<Rubric> findByProjectId(Long projectId);
}
