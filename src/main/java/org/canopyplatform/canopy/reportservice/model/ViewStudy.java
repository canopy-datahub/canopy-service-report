package org.canopyplatform.canopy.reportservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "view_study")
@NoArgsConstructor
@AllArgsConstructor
public class ViewStudy {

    @Id
    @Column(name = "study_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studyId;

    @Column(name = "title")
    private String title;
}
