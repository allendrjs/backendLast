
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

