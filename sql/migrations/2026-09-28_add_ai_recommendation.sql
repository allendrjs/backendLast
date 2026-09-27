-- Adds AI recommendation columns for the new deskPrefect appeal/request review feature.
-- appeal previously had no AI columns at all; request already had AIRESPONSE (informational
-- summary) and now also gets AIRECOMMENDATION (APPROVABLE / DENIABLE / UNCERTAIN).

ALTER TABLE appeal ADD AIRECOMMENDATION VARCHAR2(20);
ALTER TABLE appeal ADD AIREASONING CLOB;
ALTER TABLE request ADD AIRECOMMENDATION VARCHAR2(20);
ALTER TABLE request ADD AIREASONING CLOB;

COMMIT;
