-- Columns the current entities map that were previously only added by hand / by
-- sql/RC-OSD_Full_DB_Script.sql. Run on an EXISTING database; skip any line that
-- fails with ORA-01430 ("column being added already exists in table").

-- Login lockout (upstream PR #56)
ALTER TABLE login ADD failed_login_attempts NUMBER(10) DEFAULT 0 NOT NULL;

-- Student active/inactive status
ALTER TABLE student ADD isActive NUMBER(1) DEFAULT 1 NOT NULL;

-- Offense active flag and request AI summary (from deployment_fixes_combined.sql)
ALTER TABLE offense ADD ISACTIVE NUMBER(1) DEFAULT 1 NOT NULL;
ALTER TABLE request ADD AIRESPONSE CLOB;

COMMIT;
