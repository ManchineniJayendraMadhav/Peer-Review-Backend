package com.peercollab.controller;

import com.peercollab.model.Project;
import com.peercollab.model.Submission;
import com.peercollab.model.User;
import com.peercollab.repository.ProjectRepository;
import com.peercollab.repository.SubmissionRepository;
import com.peercollab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "https://peer-review-frontend-three.vercel.app", maxAge = 3600)
@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public List<Submission> getAllSubmissions() {
        System.out.println("=== GET /api/submissions called ===");
        List<Submission> submissions = submissionRepository.findAll();
        System.out.println("Found " + submissions.size() + " submissions");
        return submissions;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Submission> getSubmissionById(@PathVariable Long id) {
        return submissionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createSubmission(@Valid @RequestBody Submission submission) {
        if (submission.getProject() == null || submission.getProject().getId() == null ||
            submission.getStudent() == null || submission.getStudent().getId() == null) {
            return ResponseEntity.badRequest().body("Project and Student are required.");
        }

        Project project = projectRepository.findById(submission.getProject().getId()).orElse(null);
        User student = userRepository.findById(submission.getStudent().getId()).orElse(null);

        if (project == null || student == null) {
            return ResponseEntity.badRequest().body("Invalid Project or Student.");
        }

        submission.setProject(project);
        submission.setStudent(student);

        return ResponseEntity.ok(submissionRepository.save(submission));
    }
}
