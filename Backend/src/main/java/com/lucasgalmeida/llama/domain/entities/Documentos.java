package com.lucasgalmeida.llama.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Entity
@Table(name = "documents")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Documentos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "original_file_name")
    private String originalFileName;
    private String name;
    private String description;
    private String type;
    @Column(name = "date_upload")
    private LocalDateTime dateUpload;
    private boolean processed;
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_documents_user_id"))
    private User user;

    @PrePersist
    @PreUpdate
    private void truncar(){
        this.name = this.name.substring(0, Math.min(255, this.name.length()));
        if(StringUtils.isNotEmpty(this.description)){
            this.description = this.description.substring(0, Math.min(255, this.description.length()));
        } else {
            this.description = null;
        }
    }

    @JsonBackReference("document-vector-store-vector")
    @ManyToMany
    @JoinTable(
            name = "document_vector_store",
            joinColumns = @JoinColumn(name = "document_id"),
            inverseJoinColumns = @JoinColumn(name = "vector_store_id")
    )
    private Set<VectorStoreEntity> vetores;

    @JsonIgnore
    public String getFileNameWithTimeStamp(){
        String date = this.dateUpload.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int lastDotIndex = this.name.lastIndexOf('.');
        String extension = (lastDotIndex != -1 && lastDotIndex < name.length() - 1) ? name.substring(lastDotIndex).toLowerCase() : "";
        String baseName = this.name.replace(extension, "");
        return baseName + "_" + date + extension;
    }
}
