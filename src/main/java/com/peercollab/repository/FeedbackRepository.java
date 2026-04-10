package com.peercollab.repository;

import com.peercollab.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findBySubmissionId(Long submissionId);
    List<Feedback> findByReviewerId(Long reviewerId);
}
