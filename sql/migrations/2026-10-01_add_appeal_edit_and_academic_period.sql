-- Workstream B (requirements 6-9): appeal edit/lock, edited badge, academic
-- appeal window.

ALTER TABLE appeal ADD EDITED NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE appeal ADD EDITEDAT DATE;

CREATE TABLE appeal_edit_history (
    historyId NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    appealId NUMBER NOT NULL,
    oldMessage CLOB,
    newMessage CLOB,
    editedAt TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT fk_appeal_edit_history_appeal
        FOREIGN KEY (appealId) REFERENCES appeal(appealId)
);

CREATE TABLE academic_period (
    periodId NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    label VARCHAR2(100) NOT NULL,
    startDate DATE,
    endDate DATE,
    appealDeadline DATE,
    active NUMBER(1) DEFAULT 0 NOT NULL
);

COMMIT;
