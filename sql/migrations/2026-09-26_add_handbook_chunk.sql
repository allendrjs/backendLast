-- Adds the handbook_chunk table used by the Student Handbook RAG feature.
-- Safe to run against the live production database: additive only, no
-- existing tables/data are touched.
--
-- Run as the rcosd schema user, e.g.:
--   sqlplus rcosd/<password>@localhost:1539/FREEPDB1 @2026-09-26_add_handbook_chunk.sql

CREATE TABLE handbook_chunk (
   chunk_id number(20,0) generated as identity
       constraint HANDBOOK_CHUNK_NOT_NULL not null,
   department VARCHAR2(20) not null,
   section_title VARCHAR2(200),
   content CLOB,
   embedding VECTOR(768, FLOAT32),
   primary key (chunk_id)
);

ALTER TABLE handbook_chunk ADD CONSTRAINT CHK_HANDBOOK_DEPT
    CHECK (department IN ('JHS', 'SHS', 'COLLEGE'));

COMMIT;
