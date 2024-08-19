INSERT INTO authors (first_name, last_name) VALUES
('James', 'Gosling'),
('Joshua', 'Bloch'),
('Brian', 'Goetz'),
('Kathy', 'Sierra'),
('Herbert', 'Schildt'),
('Venkat', 'Subramaniam');

INSERT INTO documents (title, body) VALUES
('Effective Java', 'This document covers best practices in Java programming...'),
('Java Concurrency in Practice', 'A comprehensive guide to writing concurrent programs in Java...'),
('Head First Java', 'An introductory guide to Java programming, filled with engaging examples...'),
('Java: The Complete Reference', 'A comprehensive reference book covering all aspects of Java...'),
('Java Performance Tuning', 'This document discusses performance optimization techniques in Java...'),
('Refactoring to Patterns', 'How to refactor your Java code to make use of design patterns...'),
('Modern Java in Action', 'A guide to modern Java features including lambdas and streams...'),
('Java 8 in Action', 'A deep dive into the new features introduced in Java 8...');

INSERT INTO document_authors (document_id, author_id) VALUES
(1, 2), -- Joshua Bloch wrote "Effective Java"
(2, 3), -- Brian Goetz wrote "Java Concurrency in Practice"
(3, 4), -- Kathy Sierra wrote "Head First Java"
(4, 5), -- Herbert Schildt wrote "Java: The Complete Reference"
(5, 3), -- Brian Goetz wrote "Java Performance Tuning"
(6, 2), -- Joshua Bloch co-wrote "Refactoring to Patterns"
(7, 6), -- Venkat Subramaniam wrote "Modern Java in Action"
(8, 6), -- Venkat Subramaniam co-wrote "Java 8 in Action"
(8, 3); -- Brian Goetz co-wrote "Java 8 in Action"

INSERT INTO document_references (document_id, reference_id) VALUES
(2, 1), -- "Java Concurrency in Practice" references "Effective Java"
(5, 1), -- "Java Performance Tuning" references "Effective Java"
(6, 4), -- "Refactoring to Patterns" references "Java: The Complete Reference"
(7, 8), -- "Modern Java in Action" references "Java 8 in Action"
(8, 1), -- "Java 8 in Action" references "Effective Java"
(8, 4); -- "Java 8 in Action" references "Java: The Complete Reference"
