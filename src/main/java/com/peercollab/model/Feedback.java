package com.peercollab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    @JsonIgnoreProperties({"feedbacks", "hibernateLazyInitializer", "handler"})
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User reviewer;

    @Column(columnDefinition = "TEXT")
    private String comments;

    private Integer totalScore;

    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("feedback")
    private List<FeedbackRubricScore> rubricScores;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public List<FeedbackRubricScore> getRubricScores() {
        return rubricScores;
    }

    public void setRubricScores(List<FeedbackRubricScore> rubricScores) {
        this.rubricScores = rubricScores;
    }
}
