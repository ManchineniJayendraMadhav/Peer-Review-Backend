package com.peercollab.util;

import com.peercollab.model.*;
import com.peercollab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class SampleDataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RubricRepository rubricRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        System.out.println("🚀 Running SampleDataLoader...");

        // 🔹 CREATE TEACHER ONLY IF NOT EXISTS
        User teacher = userRepository.findByUsername("teacher")
                .orElseGet(() -> {
                    User t = new User();
                    t.setUsername("teacher");
                    t.setPassword(passwordEncoder.encode("password"));
                    t.setRole("ROLE_TEACHER");
                    t.setFullName("Teacher User");
                    t.setEmail("teacher@peercollab.edu");
                    return userRepository.save(t);
                });

        // 🔹 CREATE STUDENTS ONLY IF NOT EXISTS
        User student1 = userRepository.findByUsername("student1")
                .orElseGet(() -> {
                    User s = new User();
                    s.setUsername("student1");
                    s.setPassword(passwordEncoder.encode("password"));
                    s.setRole("ROLE_STUDENT");
                    s.setFullName("Alice Smith");
                    s.setEmail("alice@student.edu");
                    return userRepository.save(s);
                });

        User student2 = userRepository.findByUsername("student2")
                .orElseGet(() -> {
                    User s = new User();
                    s.setUsername("student2");
                    s.setPassword(passwordEncoder.encode("password"));
                    s.setRole("ROLE_STUDENT");
                    s.setFullName("Bob Jones");
                    s.setEmail("bob@student.edu");
                    return userRepository.save(s);
                });

        // 🔹 ONLY LOAD PROJECT DATA IF EMPTY
        if (projectRepository.count() == 0) {

            System.out.println("📦 Loading sample project data...");

            // Project
            Project project = new Project();
            project.setTitle("Final React Project");
            project.setDescription("Build a fully responsive React application.");
            project.setDeadline(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)));
            project.setCreatedBy(teacher);
            projectRepository.save(project);

            // Rubrics
            Rubric r1 = new Rubric(null, project, "UI/UX Design", 10);
            Rubric r2 = new Rubric(null, project, "Code Quality", 10);
            Rubric r3 = new Rubric(null, project, "Functionality", 15);

            rubricRepository.saveAll(Arrays.asList(r1, r2, r3));

            // Submissions
            Submission sub1 = new Submission();
            sub1.setProject(project);
            sub1.setStudent(student1);
            sub1.setContent("Here is my final project.");
            sub1.setFileUrl("https://github.com/alicesmith/react-final");
            sub1.setSubmittedAt(new Date());

            Submission sub2 = new Submission();
            sub2.setProject(project);
            sub2.setStudent(student2);
            sub2.setContent("My submission.");
            sub2.setFileUrl("https://github.com/bobjones/react-final");
            sub2.setSubmittedAt(new Date());

            submissionRepository.saveAll(Arrays.asList(sub1, sub2));

            // Feedback 1
            Feedback fb1 = new Feedback();
            fb1.setSubmission(sub1);
            fb1.setReviewer(student2);
            fb1.setComments("Great work!");
            fb1.setTotalScore(30);
            fb1.setCreatedAt(new Date());
            fb1.setRubricScores(new ArrayList<>());

            // Feedback scores
            FeedbackRubricScore s1 = new FeedbackRubricScore(null, fb1, r1, 9);
            FeedbackRubricScore s2 = new FeedbackRubricScore(null, fb1, r2, 6);
            FeedbackRubricScore s3 = new FeedbackRubricScore(null, fb1, r3, 15);

            fb1.getRubricScores().addAll(Arrays.asList(s1, s2, s3));

            // Feedback 2
            Feedback fb2 = new Feedback();
            fb2.setSubmission(sub2);
            fb2.setReviewer(student1);
            fb2.setComments("Needs UI improvement.");
            fb2.setTotalScore(22);
            fb2.setCreatedAt(new Date());
            fb2.setRubricScores(new ArrayList<>());

            FeedbackRubricScore s4 = new FeedbackRubricScore(null, fb2, r1, 5);
            FeedbackRubricScore s5 = new FeedbackRubricScore(null, fb2, r2, 7);
            FeedbackRubricScore s6 = new FeedbackRubricScore(null, fb2, r3, 10);

            fb2.getRubricScores().addAll(Arrays.asList(s4, s5, s6));

            feedbackRepository.saveAll(Arrays.asList(fb1, fb2));

            System.out.println("✅ Sample data loaded successfully");
        }
    }
}
