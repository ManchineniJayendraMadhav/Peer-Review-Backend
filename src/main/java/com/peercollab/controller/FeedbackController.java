package com.peercollab.controller;

import com.peercollab.model.Feedback;
import com.peercollab.model.Submission;
import com.peercollab.model.User;
import com.peercollab.repository.FeedbackRepository;
import com.peercollab.repository.SubmissionRepository;
import com.peercollab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.peercollab.model.FeedbackRubricScore;
import com.peercollab.model.Rubric;
import com.peercollab.repository.RubricRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RubricRepository rubricRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public List<Feedback> getFeedbacks(@RequestParam(required = false) Long submissionId) {
        if (submissionId != null) {
            return feedbackRepository.findBySubmissionId(submissionId);
        }
        return feedbackRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Long id) {
        return feedbackRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<?> createFeedback(@Valid @RequestBody Feedback feedback) {
        if (feedback.getSubmission() == null || feedback.getSubmission().getId() == null ||
            feedback.getReviewer() == null || feedback.getReviewer().getId() == null) {
            return ResponseEntity.badRequest().body("Submission and Reviewer are required.");
        }

        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isTeacher = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));

        if (!isTeacher && feedback.getRubricScores() != null && !feedback.getRubricScores().isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).body("Only TEACHER can assign marks/scores.");
        }

        Submission submission = submissionRepository.findById(feedback.getSubmission().getId()).orElse(null);
        User reviewer = userRepository.findById(feedback.getReviewer().getId()).orElse(null);

        if (submission == null || reviewer == null) {
            return ResponseEntity.badRequest().body("Invalid Submission or Reviewer.");
        }

        feedback.setSubmission(submission);
        feedback.setReviewer(reviewer);

        int computedTotal = 0;
        if (feedback.getRubricScores() != null) {
            for (FeedbackRubricScore frs : feedback.getRubricScores()) {
                if (frs.getRubric() == null || frs.getRubric().getId() == null) {
                    throw new IllegalArgumentException("Rubric ID is required for each score.");
                }
                Rubric r = rubricRepository.findById(frs.getRubric().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Rubric ID: " + frs.getRubric().getId()));
                
                if (frs.getScore() > r.getMaxScore()) {
                    throw new IllegalArgumentException("Score " + frs.getScore() + " exceeds maximum allowed (" + r.getMaxScore() + ") for rubric: " + r.getCriteria());
                }
                computedTotal += frs.getScore();
                frs.setRubric(r);
                frs.setFeedback(feedback);
            }
        }
        feedback.setTotalScore(computedTotal);

        return ResponseEntity.ok(feedbackRepository.save(feedback));
    }
}
