package com.sijie.blogweb.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "blog", schema = "blog_web")
@Data
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // business logic id
    @Column(name = "bid", length = 200, nullable = false, unique = true)
    private String bid;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    // longtext
    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    // true: pin to the top
    @Column(name = "pin", nullable = false)
    private boolean pin;

    @Column(name = "views", nullable = false)
    private long views;

    @Column(name = "author_id", length = 200, nullable = false)
    private String authorId;

    @Column(name = "author_avatar", length = 500, nullable = false)
    private String authorAvatar;

    @Column(name = "category_id", length = 200, nullable = false)
    private String categoryId;

    @Column(name = "category_name", length = 200, nullable = false)
    private String categoryName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "gmt_create", nullable = false)
    private Date gmtCreate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "gmt_update", nullable = false)
    private Date gmtUpdate;

}
