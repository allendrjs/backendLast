-- Aligns the `offense` catalog with the actual Student Handbook (JHS/SHS/College)
-- Minor vs. Major classifications. Safe to run against the live production
-- database: updates existing rows in place (preserving offenseID, so any
-- already-logged `record` rows referencing them are unaffected) and adds one
-- new row for the previously-conflated "Technology Violation" case.
--
-- Run as the rcosd schema user, e.g.:
--   sqlplus rcosd/<password>@localhost:1539/FREEPDB1 @2026-09-26_fix_offense_severity.sql
--
-- NOTE: this does not touch the pre-existing duplicate rows for
-- 'Use/Possession of Drugs' and 'Dress Code' (two offenseIDs each with
-- identical text). Removing a duplicate could break a foreign key on any
-- `record` row already logged against that specific offenseID -- if you want
-- those merged, check `record` for references to the duplicate offenseID
-- first and repoint them before deleting the extra row.

-- Tardiness: was Major, should be Minor (Handbook Sec. 1.1)
UPDATE offense
SET type = 'Minor Offense',
    description = 'Student is late to class (Student Handbook Sec. 1.1, Minor Offenses)'
WHERE offense = 'Tardiness';

-- Disrespect: was Minor, should be Major (Handbook Sec. 2.1.10 / 2.2.15)
UPDATE offense
SET type = 'Major Offense',
    description = 'Student shows disrespect toward classmates, schoolmates, school authorities, personnel, or visitors (Student Handbook Sec. 2.1.10 / 2.2.15, Major Offenses)'
WHERE offense = 'Disrespect';

-- Inappropriate Language: was Minor, should be Major (Handbook Sec. 2.2.3)
UPDATE offense
SET type = 'Major Offense',
    description = 'Student uses vulgar, malicious, or offensive words or gestures (Student Handbook Sec. 2.2.3, Major Offenses)'
WHERE offense = 'Inappropriate Language';

-- Technology Violation: was a single ambiguous Major entry. Handbook actually
-- splits this into a Minor case (bringing a gadget) and a Major case (using
-- it during class, or unauthorized computer access). Repurpose the existing
-- row as the Minor case, and add the Major case as a new offense.
UPDATE offense
SET offense = 'Technology Violation (Unauthorized Gadget)',
    type = 'Minor Offense',
    description = 'Student brings an unnecessary electronic device/gadget to school without authorization (Student Handbook Sec. 1.16, Minor Offenses)'
WHERE offense = 'Technology Violation';

INSERT INTO offense (offense, type, description)
VALUES (
    'Technology Violation (Unauthorized Use)',
    'Major Offense',
    'Student uses an electronic device during class, programs, or Mass, or accesses/alters school computer data without authorization (Student Handbook Sec. 2.1.5 / 2.2.11, Major Offenses)'
);

COMMIT;
