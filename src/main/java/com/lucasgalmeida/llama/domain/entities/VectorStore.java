package com.lucasgalmeida.llama.domain.entities;


import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vector_store")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VectorStore {

    @Id
    @GeneratedValue
    private UUID id;
    private String content;
    @Type(JsonBinaryType.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;
    @Type(JsonType.class)
    @Column(name = "embedding", columnDefinition = "vector")
    private List<Double> embedding; //    ou talvez - https://github.com/pgvector/pgvector-java#hibernate
}
