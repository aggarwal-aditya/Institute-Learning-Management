DROP SCHEMA PUBLIC CASCADE;
CREATE SCHEMA PUBLIC;
-- CREATE TYPE course_type AS ENUM ('core', 'humanities_elective', 'programme_elective', 'science_math_elective','internship','btech_project');


CREATE TABLE users
(
    email_id VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(255) NOT NULL
);
CREATE TABLE departments
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);
CREATE TABLE grade_mapping
(
    grade VARCHAR(3) PRIMARY KEY,
    value NUMERIC NOT NULL
);
CREATE TABLE semester
(
    year                         INTEGER NOT NULL,
    semester_number              INTEGER NOT NULL,
    start_date                   DATE    NOT NULL,
    end_date                     DATE    NOT NULL,
    grades_submission_start_date DATE    NOT NULL,
    grades_submission_end_date   DATE    NOT NULL,
    course_float_start_date      DATE    NOT NULL,
    course_float_end_date        DATE    NOT NULL,
    course_add_drop_start_date   DATE    NOT NULL,
    course_add_drop_end_date     DATE    NOT NULL,
    PRIMARY KEY (year, semester_number)
);

CREATE TABLE instructors
(
    instructor_id   SERIAL4 PRIMARY KEY,
    email_id        VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    phone_number    VARCHAR(20)  NOT NULL,
    department_id   INTEGER      NOT NULL REFERENCES departments (id),
    date_of_joining DATE         NOT NULL,
    foreign key (email_id) references users (email_id)
);
create unique index instructor_unique_email_idx on instructors (email_id);

CREATE TABLE students
(
    student_id    VARCHAR(255) PRIMARY KEY,
    email_id      VARCHAR(255) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(20)  NOT NULL,
    department_id INTEGER      NOT NULL REFERENCES departments (id),
    batch         INTEGER      NOT NULL,
    foreign key (email_id) references users (email_id)
);
create unique index student_unique_email_idx on students (email_id);


CREATE TABLE course_catalog
(
    course_code  VARCHAR(6) primary key,
    course_name  VARCHAR(255)  NOT NULL,
    credit_str   NUMERIC array NOT NULL,
--     dept         VARCHAR(127),
    prerequisite TEXT[] DEFAULT NULL
);


CREATE TABLE course_offerings
(
    course_code      VARCHAR(6),
    semester         VARCHAR(8),
    instructor_id    INTEGER NOT NULL,
    qualify          NUMERIC DEFAULT 0,
    enrollment_count INTEGER default 0,
    prerequisite     TEXT[]  DEFAULT NULL,
    primary key (course_code, semester),
    foreign key (course_code) references course_catalog (course_code),
    foreign key (instructor_id) references instructors (instructor_id)
);


CREATE TABLE course_mappings
(
    course_code   VARCHAR(6)  NOT NULL,
    semester      VARCHAR(8)  NOT NULL,
    department_id INTEGER     NOT NULL REFERENCES departments (id) ON DELETE CASCADE,
    batch         INTEGER     NOT NULL,
    course_type   VARCHAR(32) NOT NULL,
    primary key (course_code, semester, department_id, batch),
    foreign key (course_code, semester) references course_offerings (course_code, semester) on delete cascade
);


CREATE TABLE course_enrollments
(
    enrollment_id SERIAL4 PRIMARY KEY,
    course_code   VARCHAR(6)   NOT NULL,
    semester      VARCHAR(8)   NOT NULL,
    student_id    VARCHAR(255) NOT NULL,
    grade         VARCHAR(3) DEFAULT 'NA',
    foreign key (course_code, semester) references course_offerings (course_code, semester) ON DELETE CASCADE,
    foreign key (student_id) references students (student_id)
);

CREATE TABLE ug_curriculum
(
    year                INTEGER NOT NULL,
    department_id       INTEGER NOT NULL REFERENCES departments (id),
    core_count          NUMERIC NOT NULL,
    hs_elect_count      NUMERIC NOT NULL,
    pc_elect_count      NUMERIC NOT NULL,
    sm_elect_count      NUMERIC NOT NULL,
    oe_elect_count      NUMERIC NOT NULL,
    internship_count    NUMERIC NOT NULL,
    btech_project_count NUMERIC NOT NULL,
    PRIMARY KEY (year, department_id)
);


CREATE OR REPLACE FUNCTION calculate_cgpa(p_student_id VARCHAR(255))
    RETURNS NUMERIC AS
$$
DECLARE
    total_credits     NUMERIC;
    earned_credits    NUMERIC;
    grade_value       NUMERIC;
    course_enrollment RECORD;
BEGIN
    total_credits := 0;
    earned_credits := 0;

    -- calculate total credits and earned credits
    FOR course_enrollment IN (SELECT course_enrollments.course_code, semester, grade, credit_str
                              FROM course_enrollments
                                       JOIN course_catalog
                                            ON course_enrollments.course_code = course_catalog.course_code
                              WHERE student_id = p_student_id)
        LOOP
            -- check if the grade is not null
            IF course_enrollment.grade IS NOT NULL THEN
                total_credits := total_credits + course_enrollment.credit_str[5];
                -- get grade value from grade_mapping
                SELECT value INTO grade_value FROM grade_mapping WHERE grade = course_enrollment.grade;
                earned_credits := earned_credits + (course_enrollment.credit_str[5] * grade_value);
            END IF;
        END LOOP;

    -- calculate CGPA
    IF total_credits = 0 THEN
        RETURN 0;
    ELSE
        RETURN earned_credits * (1.0) / (total_credits * 1.0);
    END IF;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION generate_transcript(p_student_id VARCHAR(255), p_semester VARCHAR(8))
    RETURNS TABLE
            (
                course_code    VARCHAR(6),
                course_name    VARCHAR(255),
                grade          VARCHAR(3),
                credits        INTEGER,
                semester_gpa   NUMERIC,
                cumulative_gpa NUMERIC
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT course_enrollments.course_code,
               course_catalog.course_name,
               course_enrollments.grade,
               course_catalog.credit_str[5],
               (SELECT SUM(grade_mapping.value * course_catalog.credit_str[5]) / SUM(course_catalog.credit_str[5])
                FROM course_enrollments
                         JOIN course_catalog ON course_enrollments.course_code = course_catalog.course_code
                         JOIN grade_mapping ON course_enrollments.grade = grade_mapping.grade
                WHERE course_enrollments.student_id = p_student_id
                  AND course_enrollments.semester = p_semester),
               calculate_cgpa(p_student_id)
        FROM course_enrollments
                 JOIN course_catalog ON course_enrollments.course_code = course_catalog.course_code
        WHERE course_enrollments.student_id = p_student_id
          AND course_enrollments.semester = p_semester;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION update_course_enrollment_count() RETURNS TRIGGER AS
$$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        UPDATE course_offerings
        SET enrollment_count = enrollment_count - 1
        WHERE course_code = OLD.course_code
          AND semester = OLD.semester;
    ELSIF (TG_OP = 'INSERT') THEN
        UPDATE course_offerings
        SET enrollment_count = enrollment_count + 1
        WHERE course_code = NEW.course_code
          AND semester = NEW.semester;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_course_enrollment_trigger
    AFTER INSERT OR DELETE
    ON course_enrollments
    FOR EACH ROW
EXECUTE FUNCTION update_course_enrollment_count();


CREATE OR REPLACE FUNCTION check_already_enrolled() RETURNS TRIGGER AS
$$

BEGIN
    IF EXISTS(SELECT 1
              FROM course_enrollments
              WHERE course_code = new.course_code
                AND semester = new.semester
                AND student_id = new.student_id) THEN
        RAISE EXCEPTION 'The student is already enrolled in the course.';
    END IF;

    IF EXISTS(SELECT 1
              FROM course_enrollments
              WHERE course_code = new.course_code
                AND grade != 'F'
                AND student_id = new.student_id) THEN
        RAISE EXCEPTION 'The student has already completed the course earlier.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_already_enrolled_trigger
    BEFORE INSERT
    ON course_enrollments
    FOR EACH ROW
EXECUTE FUNCTION check_already_enrolled();


CREATE OR REPLACE FUNCTION check_student_requirements(enrollment_id INTEGER) RETURNS BOOLEAN AS
$$
DECLARE
    dept_id             INTEGER;
    grad_year           INTEGER;
    core_count          INTEGER;
    hs_elect_count      INTEGER;
    pc_elect_count      INTEGER;
    sm_elect_count      INTEGER;
    oe_elect_count      INTEGER;
    internship_count    INTEGER;
    btech_project_count INTEGER;
    total_credits       NUMERIC;
    current_credits     NUMERIC;
    course_type         VARCHAR(32);
    current_course      RECORD;
BEGIN
    -- Get the department ID and year for the student
    SELECT department_id, batch INTO dept_id, grad_year FROM students WHERE student_id = $1;

    -- Get the required number of courses for the student's department and year
    SELECT core_count,
           hs_elect_count,
           pc_elect_count,
           sm_elect_count,
           oe_elect_count,
           internship_count,
           btech_project_count
    INTO core_count, hs_elect_count, pc_elect_count, sm_elect_count, oe_elect_count, internship_count, btech_project_count
    FROM ug_curriculum
    WHERE department_id = dept_id
      AND year = grad_year;

    FOR current_course IN (SELECT course_enrollments.course_code, course_enrollments.grade, course_type
                           FROM course_enrollments
                                    JOIN course_mappings
                                         ON course_mappings.course_code = course_enrollments.course_code AND
                                            course_mappings.semester = course_enrollments.semester
                                    JOIN course_catalog ON course_catalog.course_code = course_enrollments.course_code
                           WHERE course_enrollments.student_id = $1
                             AND course_enrollments.grade != 'F')
        LOOP

        end loop;
    Return TRUE;
END;
$$ LANGUAGE plpgsql;


-- CREATE OR REPLACE FUNCTION check_credit_limit()
--     RETURNS TRIGGER AS
--
-- $$
-- DECLARE
--     credit_limit    NUMERIC;
--     count           INTEGER;
--     current_credits NUMERIC;
-- BEGIN
--     -- Get the credit limit for the student based on previous semesters
--     SELECT SUM(credits)
--     INTO credit_limit
--     FROM (SELECT SUM(credit_str[5]) as credits
--           FROM course_enrollments
--                    JOIN course_catalog ON course_enrollments.course_code = course_catalog.course_code
--           WHERE student_id = NEW.student_id
--           GROUP BY semester
--             AND semester < NEW.semester
--           ORDER BY semester DESC
--           LIMIT 2) as previous_semesters;
--     IF credit_limit IS NOT NULL THEN
--         SELECT COUNT(*)
--         INTO count
--         FROM (SELECT SUM(credit_str[5]) as credits
--               FROM course_enrollments
--                        JOIN course_catalog ON course_enrollments.course_code = course_catalog.course_code
--               WHERE student_id = NEW.student_id
--                 AND semester < NEW.semester
--               ORDER BY semester DESC
--               LIMIT 2) as previous_semesters;
--         IF count < 2 THEN
--             credit_limit := 24;
--         ELSE
--             credit_limit := 1.25 * (credit_limit / count);
--         END IF;
--     ELSE
--         credit_limit := 24;
--     END IF;
--
--
--     -- Get the total credits for the current semester
--     SELECT SUM(credit_str[5])
--     INTO current_credits
--     FROM course_enrollments
--              JOIN course_catalog ON course_enrollments.course_code = course_catalog.course_code
--     WHERE student_id = NEW.student_id
--       AND semester = NEW.semester;
--
--     -- Check if the student will exceed the credit limit
--     IF (current_credits + (SELECT credit_str[5] FROM course_catalog WHERE course_code = NEW.course_code)) >
--        credit_limit THEN
--         RAISE EXCEPTION 'The student will exceed the credit limit for this semester';
--     END IF;
--
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER credit_limit_check
--     BEFORE INSERT
--     ON course_enrollments
--     FOR EACH ROW
-- EXECUTE FUNCTION check_credit_limit();


INSERT INTO grade_mapping (grade, value)
VALUES ('A', 10);
INSERT INTO grade_mapping (grade, value)
VALUES ('A-', 9);
INSERT INTO grade_mapping (grade, value)
VALUES ('B', 8);
INSERT INTO grade_mapping (grade, value)
VALUES ('B-', 7);
INSERT INTO grade_mapping (grade, value)
VALUES ('C', 6);
INSERT INTO grade_mapping (grade, value)
VALUES ('C-', 5);
INSERT INTO grade_mapping (grade, value)
VALUES ('D', 4);
INSERT INTO grade_mapping (grade, value)
VALUES ('E', 2);
INSERT INTO grade_mapping (grade, value)
VALUES ('F', 0);


INSERT INTO users
VALUES ('2020csb1066@iitrpr.ac.in', 'aditya', 'student');
INSERT INTO users
VALUES ('mudgal@yopmail.com', 'aditya', 'instructor');
INSERT INTO departments (name)
VALUES ('Computer Science');
INSERT INTO departments (name)
VALUES ('Electrical Engineering');
INSERT INTO departments (name)
VALUES ('Mechanical Engineering');
INSERT INTO departments (name)
VALUES ('Chemical Engineering');
INSERT INTO departments (name)
VALUES ('Civil Engineering');
INSERT INTO departments (name)
VALUES ('Materials Science');
INSERT INTO departments (name)
VALUES ('Mathematics');
INSERT into instructors (email_id, name, phone_number, department_id, date_of_joining)
VALUES ('mudgal@yopmail.com', 'Apurva Mudgal', '8989872980', 1, now());
INSERT into students
VALUES ('2020CSB1066', '2020csb1066@iitrpr.ac.in', 'Aditya Aggarwal', '8989872980', 1, '2024');
INSERT into semester
VALUES (2022, 2, '2020-02-25', '2024-02-25', '2020-02-25', '2024-02-25', '2020-02-25', '2024-02-25', '2020-02-25',
        '2024-02-25');
INSERT INTO course_catalog (course_code, course_name, credit_str)
VALUES ('CS101', 'Introduction to Computer Science', ARRAY [3,0,0,0,3]);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS102', 'Introduction to Programming', ARRAY [3,0,0,0,3], ARRAY ['CS101(B-)']);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS103', 'Introduction to Data Structures', ARRAY [3,0,0,0,3], ARRAY ['CS102(C)']);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS104', 'Introduction to Algorithms', ARRAY [3,0,0,0,3], ARRAY ['CS103(B-)|CS101(A)','CS102(E)']);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS105', 'Introduction to Computer Architecture', ARRAY [3,0,0,0,3], ARRAY ['CS102(C)']);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS201', 'Introduction to Operating Systems', ARRAY [3,0,0,0,3], ARRAY ['CS103(B-)|CS101(A)','CS102(E)']);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS202', 'Introduction to Computer Networks', ARRAY [3,0,0,0,3], ARRAY ['CS104(C)','CS105(B)']);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS203', 'Introduction to Database Systems', ARRAY [3,0,0,0,3], ARRAY ['CS102(E)','CS104(C)']);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS204', 'Introduction to Software Engineering', ARRAY [3,0,0,0,3], ARRAY ['CS103(B-)|CS101(A)','CS102(E)']);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS205', 'Introduction to Artificial Intelligence', ARRAY [3,0,0,0,3], ARRAY ['CS104(C)','CS105(B)']);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS301', 'Introduction to Computer Graphics', ARRAY [3,0,0,0,3], ARRAY ['CS104(C)','CS105(B)']);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS302', 'Introduction to Compiler Design', ARRAY [3,0,0,0,3], ARRAY ['CS104(C)','CS105(B)']);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS303', 'Introduction to Computer Security', ARRAY [3,0,0,0,3], ARRAY ['CS104(C)','CS105(B)']);
INSERT INTO course_catalog (course_code, course_name, credit_str, prerequisite)
VALUES ('CS304', 'Introduction to Computer Vision', ARRAY [3,0,0,0,3], ARRAY ['CS104(C)','CS105(B)']);