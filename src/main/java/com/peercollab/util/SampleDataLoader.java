package com.peercollab.util;

import com.peercollab.model.*;
import com.peercollab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

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
    public void run(String... args) throws Exception {
        System.out.println("Checking and fixing user passwords...");
        
        // 5. Ensure no plain-text passwords exist in DB:
        // Remove or overwrite any incorrect entries
        for (User user : userRepository.findAll()) {
            if (!user.getPassword().startsWith("$2a$") && 
                !user.getPassword().startsWith("$2b$") && 
                !user.getPassword().startsWith("$2y$")) {
                System.out.println("Encoding plain-text password for user: " + user.getUsername());
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
            }
        }

        // Reinsert or overwrite teacher user with encoded password
        Optional<User> teacherOpt = userRepository.findByUsername("teacher");
        User teacher;
        if (teacherOpt.isPresent()) {
            teacher = teacherOpt.get();
        } else {
            teacher = new User();
        }
        teacher.setUsername("teacher");
        teacher.setPassword(passwordEncoder.encode("password"));
        teacher.setRole("ROLE_TEACHER");
        teacher.setFullName("Teacher User");
        teacher.setEmail("teacher@peercollab.edu");
        userRepository.save(teacher);

        if (projectRepository.count() == 0) {
            System.out.println("Loading sample data...");

            // Create Users (teacher created above safely)

            User student1 = new User();
            student1.setUsername("student1");
            student1.setPassword(passwordEncoder.encode("password"));
            student1.setRole("ROLE_STUDENT");
            student1.setFullName("Alice Smith");
            student1.setEmail("alice@student.edu");

            User student2 = new User();
            student2.setUsername("student2");
            student2.setPassword(passwordEncoder.encode("password"));
            student2.setRole("ROLE_STUDENT");
            student2.setFullName("Bob Jones");
            student2.setEmail("bob@student.edu");

            userRepository.save(teacher);
            userRepository.save(student1);
            userRepository.save(student2);

            // Create Project
            Project project = new Project();
            project.setTitle("Final React Project");
            project.setDescription("Build a fully responsive React application.");
            project.setDeadline(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)));
            project.setCreatedBy(teacher);
            projectRepository.save(project);

            // Create Rubrics
            Rubric r1 = new Rubric();
            r1.setProject(project);
            r1.setCriteria("UI/UX Design");
            r1.setMaxScore(10);

            Rubric r2 = new Rubric();
            r2.setProject(project);
            r2.setCriteria("Code Quality");
            r2.setMaxScore(10);

            Rubric r3 = new Rubric();
            r3.setProject(project);
            r3.setCriteria("Functionality");
            r3.setMaxScore(15);

            rubricRepository.save(r1);
            rubricRepository.save(r2);
            rubricRepository.save(r3);

            // Create Submissions
            Submission sub1 = new Submission();
            sub1.setProject(project);
            sub1.setStudent(student1);
            sub1.setContent("Here is my final project containing all requirements.");
            sub1.setFileUrl("https://github.com/alicesmith/react-final");
            sub1.setSubmittedAt(new Date());

            Submission sub2 = new Submission();
            sub2.setProject(project);
            sub2.setStudent(student2);
            sub2.setContent("My submission for the React final project.");
            sub2.setFileUrl("https://github.com/bobjones/react-final");
            sub2.setSubmittedAt(new Date());
            
            submissionRepository.save(sub1);
            submissionRepository.save(sub2);

            // Create Feedback
            Feedback fb1 = new Feedback();
            fb1.setSubmission(sub1);
            fb1.setReviewer(student2);
            fb1.setComments("Great project, very well designed. Some minor issues in code structure.");
            fb1.setTotalScore(30);
            fb1.setRubricScores(new java.util.ArrayList<>());
            fb1.setCreatedAt(new Date());

            FeedbackRubricScore s1 = new FeedbackRubricScore();
            s1.setFeedback(fb1);
            s1.setRubric(r1);
            s1.setScore(9);

            FeedbackRubricScore s2 = new FeedbackRubricScore();
            s2.setFeedback(fb1);
            s2.setRubric(r2);
            s2.setScore(6);

            FeedbackRubricScore s3 = new FeedbackRubricScore();
            s3.setFeedback(fb1);
            s3.setRubric(r3);
            s3.setScore(15);

            fb1.getRubricScores().add(s1);
            fb1.getRubricScores().add(s2);
            fb1.getRubricScores().add(s3);
            
            Feedback fb2 = new Feedback();
            fb2.setSubmission(sub2);
            fb2.setReviewer(student1);
            fb2.setComments("Good functionality but the UI needs a lot of work.");
            fb2.setTotalScore(22);
            fb2.setRubricScores(new java.util.ArrayList<>());
            fb2.setCreatedAt(new Date());

            FeedbackRubricScore s4 = new FeedbackRubricScore();
            s4.setFeedback(fb2);
            s4.setRubric(r1);
            s4.setScore(5);

            FeedbackRubricScore s5 = new FeedbackRubricScore();
            s5.setFeedback(fb2);
            s5.setRubric(r2);
            s5.setScore(7);

            FeedbackRubricScore s6 = new FeedbackRubricScore();
            s6.setFeedback(fb2);
            s6.setRubric(r3);
            s6.setScore(10);

            fb2.getRubricScores().add(s4);
            fb2.getRubricScores().add(s5);
            fb2.getRubricScores().add(s6);

            feedbackRepository.save(fb1);
            feedbackRepository.save(fb2);

            System.out.println("Sample data loaded.");
        }
    }
}
