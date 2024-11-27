package org.example.flowday.domain.post.tag.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.flowday.domain.post.post.entity.Post;

import java.util.List;

@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Post> posts;

}