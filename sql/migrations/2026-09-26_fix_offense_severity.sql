
UPDATE offense
SET type = 'Minor Offense',
    description = 'Student is late to class (Student Handbook Sec. 1.1, Minor Offenses)'
WHERE offense = 'Tardiness';

UPDATE offense
SET type = 'Major Offense',
    description = 'Student shows disrespect toward classmates, schoolmates, school authorities, personnel, or visitors (Student Handbook Sec. 2.1.10 / 2.2.15, Major Offenses)'
WHERE offense = 'Disrespect';

UPDATE offense
SET type = 'Major Offense',
    description = 'Student uses vulgar, malicious, or offensive words or gestures (Student Handbook Sec. 2.2.3, Major Offenses)'
WHERE offense = 'Inappropriate Language';

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

