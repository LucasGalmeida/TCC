CREATE TABLE IF NOT EXISTS document_vector_store (
    document_id INTEGER NOT NULL,
    vector_store_id UUID NOT NULL,
    PRIMARY KEY (document_id, vector_store_id),
    FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE,
    FOREIGN KEY (vector_store_id) REFERENCES vector_store(id) ON DELETE CASCADE
);
