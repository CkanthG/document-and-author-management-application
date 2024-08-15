-- create table schema for authors.
CREATE TABLE IF NOT EXISTS authors (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);
-- create table schema for documents.
CREATE TABLE IF NOT EXISTS documents (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body TEXT
);
-- document authors many to many mapping table.
CREATE TABLE IF NOT EXISTS document_authors (
    document_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (document_id, author_id),
    FOREIGN KEY (document_id) REFERENCES documents(id),
    FOREIGN KEY (author_id) REFERENCES authors(id)
);
-- document references many to many mapping table.
CREATE TABLE IF NOT EXISTS document_references (
    document_id BIGINT NOT NULL,
    reference_id BIGINT NOT NULL,
    PRIMARY KEY (document_id, reference_id),
    FOREIGN KEY (document_id) REFERENCES documents(id),
    FOREIGN KEY (reference_id) REFERENCES documents(id)
);
