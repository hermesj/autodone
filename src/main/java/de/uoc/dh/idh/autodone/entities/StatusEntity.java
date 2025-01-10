package de.uoc.dh.idh.autodone.entities;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.UUID;

@Data()
@Entity()
@JsonNaming(SnakeCaseStrategy.class)
public class StatusEntity {

    @Id()
    @GeneratedValue(strategy = UUID)
    public UUID uuid;

    //

    @ManyToOne(optional = false)
    public GroupEntity group;

    @OneToMany(cascade = ALL, fetch = LAZY, mappedBy = "status")
    public List<MediaEntity> media;

    //

    @Column(nullable = false)
    public Instant date;

    @Column()
    public String id;

    @Column(nullable = false, length = 500)
    public String status;

    //

    @Transient()
    public List<Exception> exceptions;

}
