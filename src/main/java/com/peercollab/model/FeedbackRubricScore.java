package com.peercollab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "feedback_rubric_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRubricScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id", nullable = false)
    @JsonIgnoreProperties({"rubricScores", "hibernateLazyInitializer", "handler"})
    private Feedback feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubric_id", nullable = false)
    @JsonIgnoreProperties({"project", "hibernateLazyInitializer", "handler"})
    private Rubric rubric;

    @NotNull
    @Min(0)
    private Integer score;
}
