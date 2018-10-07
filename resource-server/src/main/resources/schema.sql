DROP TABLE IF EXISTS todo;
DROP SEQUENCE IF EXISTS seq_todo_id;

CREATE SEQUENCE seq_todo_id START WITH 1 INCREMENT BY 1;

CREATE TABLE todo(
  id INTEGER DEFAULT nextval('seq_todo_id') PRIMARY KEY,
  description VARCHAR(256),
  created_at TIMESTAMP,
  deadline DATE,
  done BOOLEAN
);