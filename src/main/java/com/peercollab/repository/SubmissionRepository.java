package com.peercollab.repository;

import com.peercollab.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByProjectId(Long projectId);
    List<Submission> findByStudentId(Long studentId);
    Optional<Submission> findByProjectIdAndStudentId(Long projectId, Long studentId);
}
