package org.canopyplatform.canopy.reportservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "user_login")
@NoArgsConstructor
public class UserLogin {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "login_at")
    private OffsetDateTime loginAt;
}
