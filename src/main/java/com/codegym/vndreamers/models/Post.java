package com.codegym.vndreamers.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "post", schema = "vndreamers")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "image")
    private String image;

    @Basic
    @Column(name = "content", nullable = false, length = 100)
    private String content;

    @Basic
    @Column(name = "create_date", nullable = false)
    private Timestamp createDate = Timestamp.valueOf(LocalDateTime.now());

    @Basic
    @Column(name = "modify_date", nullable = false)
    private Timestamp modifyDate = Timestamp.valueOf(LocalDateTime.now());

    @Basic
    @Column(name = "status", nullable = false)
    private int status;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "post")
    @JsonIgnore
    private Set<PostReaction> likes;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
//    @JsonIgnore
    private User user;
}
