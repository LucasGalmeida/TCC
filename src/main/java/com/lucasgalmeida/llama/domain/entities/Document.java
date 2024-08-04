package com.lucasgalmeida.llama.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Entity
@Table(name = "documents")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String type;
    @Column(name = "date_upload")
    private LocalDateTime dateUpload;
    private boolean processed;
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_docs_remetentes_pagador_frete_id"))
    private User user;

    @JsonBackReference("document-vector-store-vector")
    @ManyToMany
    @JoinTable(
            name = "document_vector_store",
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "vector_store_id")
    )
    private Set<VectorStore> vectorStores;

    @JsonIgnore
    public String getFileNameWithTimeStamp(){
        String date = this.dateUpload.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int lastDotIndex = this.name.lastIndexOf('.');
        String extension = (lastDotIndex != -1 && lastDotIndex < name.length() - 1) ? name.substring(lastDotIndex).toLowerCase() : "";
        String baseName = this.name.replace(extension, "");
        return baseName + "_" + date + extension;
    }
}
