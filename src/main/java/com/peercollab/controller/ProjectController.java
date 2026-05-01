package com.peercollab.controller;

import com.peercollab.model.Project;
import com.peercollab.model.User;
import com.peercollab.repository.ProjectRepository;
import com.peercollab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "https://peer-review-frontend-three.vercel.app", maxAge = 3600)
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping
    // TEMPORARILY REMOVED @PreAuthorize FOR TESTING - ADD BACK LATER
    public List<Project> getAllProjects() {
        System.out.println("=== GET /api/projects called ===");
        List<Project> projects = projectRepository.findAll();
        System.out.println("Found " + projects.size() + " projects");
        return projects;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Optional<Project> project = projectRepository.findById(id);
        return project.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public Project createProject(@Valid @RequestBody Project project) {
        if (project.getCreatedBy() != null && project.getCreatedBy().getId() != null) {
            User creator = userRepository.findById(project.getCreatedBy().getId()).orElse(null);
            project.setCreatedBy(creator);
        }
        if (project.getRubrics() != null) {
            project.getRubrics().forEach(r -> r.setProject(project));
        }
        return projectRepository.save(project);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @Valid @RequestBody Project projectDetails) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            project.setTitle(projectDetails.getTitle());
            project.setDescription(projectDetails.getDescription());
            project.setDeadline(projectDetails.getDeadline());
            
            if (projectDetails.getRubrics() != null) {
                project.getRubrics().clear();
                projectDetails.getRubrics().forEach(r -> {
                    r.setProject(project);
                    project.getRubrics().add(r);
                });
            }
            return ResponseEntity.ok(projectRepository.save(project));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        return projectRepository.findById(id)
                .map(project -> {
                    projectRepository.delete(project);
                    return ResponseEntity.ok().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
