DROP SCHEMA PUBLIC CASCADE;
CREATE SCHEMA PUBLIC;



CREATE TABLE users
(
    email_id VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(255) NOT NULL
);

CREATE TABLE instructors
(
    instructor_id   SERIAL4 PRIMARY KEY,
    email_id        VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    phone_number    VARCHAR(20)  NOT NULL,
    dept            VARCHAR(127),
    date_of_joining DATE         NOT NULL,
    foreign key (email_id) references users (email_id)
);
create unique index instructor_unique_email_idx on instructors (email_id);

CREATE TABLE students
(
    student_id   VARCHAR(255) PRIMARY KEY,
    email_id     VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20)  NOT NULL,
    dept         VARCHAR(127),
    batch        INTEGER      NOT NULL,
    foreign key (email_id) references users (email_id)
);
create unique index student_unique_email_idx on students (email_id);

CREATE TABLE semester
(
    year            INTEGER NOT NULL,
    semester_number INTEGER NOT NULL,
    start_date      DATE    NOT NULL,
    end_date        DATE    NOT NULL,
    PRIMARY KEY (year, semester_number)
);


CREATE TABLE course_catalog
(
    course_code  VARCHAR(6) primary key,
    course_name  VARCHAR(255)  NOT NULL,
    credit_str   NUMERIC array NOT NULL,
    dept         VARCHAR(127),
    prerequisite VARCHAR(6)[] DEFAULT NULL
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

CREATE TABLE grade_mapping
(
    grade VARCHAR(3) PRIMARY KEY,
    value NUMERIC NOT NULL
);

CREATE TABLE graduation_requirements
(
    year        INTEGER NOT NULL,
    dept        VARCHAR(127) NOT NULL,
    core_count  NUMERIC NOT NULL,
    elect_count NUMERIC NOT NULL,
    PRIMARY KEY (year, dept)
);


-- CREATE TABLE course_category(
--     course_code VARCHAR(6) NOT NULL,
--     year INTEGER NOT NULL,
--     dept VARCHAR (127) NOT NULL,
--
-- );


CREATE OR REPLACE FUNCTION ug_curriculum(p_core_elective_dept VARCHAR(10)[], p_core_elective_year INTEGER[],
                                         p_course_code VARCHAR(6), p_semester VARCHAR(8),
                                         p_is_elective boolean default false)
    RETURNS VOID AS
$$
DECLARE
    i INTEGER;
BEGIN
    EXECUTE FORMAT('CREATE TABLE %I (dept VARCHAR(10), INTEGER, is_elective boolean default false);',
                   p_course_code || '_' || p_semester);
    i := 1;
    WHILE i <= array_length(p_core_elective_dept, 1)
        LOOP
            EXECUTE FORMAT('INSERT INTO %I (dept, year, is_elective) VALUES ($1, $2, $3);',
                           p_course_code || '_' || p_semester)
                USING p_core_elective_dept[i], p_core_elective_year[i], p_is_elective;
            i := i + 1;
        END LOOP;
END;
$$
    LANGUAGE plpgsql;





CREATE OR REPLACE FUNCTION enroll_student(p_course_code VARCHAR(6), p_semester VARCHAR(8), p_student_id VARCHAR(255))
    RETURNS VOID AS
$$
BEGIN
    -- check if the course is available for the given semester
    -- IF NOT EXISTS(SELECT 1 FROM course_offerings WHERE course_code = p_course_code AND semester = p_semester) THEN
    --     RAISE EXCEPTION 'The course is not available for the given semester.';
    -- END IF;

    -- check if the student is already enrolled in the course
    IF EXISTS(SELECT 1
              FROM course_enrollments
              WHERE course_code = p_course_code
                AND semester = p_semester
                AND student_id = p_student_id) THEN
        RAISE EXCEPTION 'The student is already enrolled in the course.';
    END IF;

    IF EXISTS(SELECT 1
              FROM course_enrollments
              WHERE course_code = p_course_code
                AND grade != 'F'
                AND student_id = p_student_id) THEN
        RAISE EXCEPTION 'The student has already completed the course earlier.';
    END IF;

    -- insert the enrollment into the course_enrollments table
    INSERT INTO course_enrollments (course_code, semester, student_id)
    VALUES (p_course_code, p_semester, p_student_id);

    -- increment the enrollment_num of the course 
    UPDATE course_offerings
    SET enrollment_count = enrollment_count + 1
    WHERE course_code = p_course_code
      AND semester = p_semester;

END;
$$
    LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION deregister_student(p_course_code VARCHAR(6), p_semester VARCHAR(8),
                                              p_student_id VARCHAR(255))
    RETURNS VOID AS
$$
BEGIN
    -- check if the student is not enrolled in the course
    IF NOT EXISTS(SELECT 1
                  FROM course_enrollments
                  WHERE course_code = p_course_code
                    AND semester = p_semester
                    AND student_id = p_student_id) THEN
        RAISE EXCEPTION 'The student is not enrolled in the course.';
    END IF;

    -- delete the enrollment from the course_enrollments table
    DELETE
    FROM course_enrollments
    WHERE course_code = p_course_code
      AND semester = p_semester
      AND student_id = p_student_id;

    -- decrement the enrollment_num of the course
    UPDATE course_offerings
    SET enrollment_count = enrollment_count - 1
    WHERE course_code = p_course_code
      AND semester = p_semester;

END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION add_grades(p_course_code VARCHAR(6), p_semester VARCHAR(8), p_student_ids VARCHAR(255)[],
                                      p_grades VARCHAR(3)[])
    RETURNS VOID AS
$$
BEGIN
    FOR i in 1..array_length(p_student_ids, 1)
        LOOP
            -- check if the student is enrolled in the course
            IF NOT EXISTS(SELECT 1
                          FROM course_enrollments
                          WHERE course_code = p_course_code
                            AND semester = p_semester
                            AND student_id = p_student_ids[i]) THEN
                RAISE EXCEPTION 'The student is not enrolled in the course.';
            END IF;

            -- update the grade in the course_enrollments table
            UPDATE course_enrollments
            SET grade = p_grades[i]
            WHERE course_code = p_course_code
              AND semester = p_semester
              AND student_id = p_student_ids[i];
        END LOOP;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION offer_course(p_course_code VARCHAR(6), p_semester VARCHAR(8), p_instructor_id INTEGER,
                                        p_qualify NUMERIC DEFAULT NULL)
    RETURNS VOID AS
$$
BEGIN

    IF EXISTS(SELECT 1
              FROM course_offerings
              WHERE course_code = p_course_code
                AND semester = p_semester
                AND instructor_id = p_instructor_id) THEN
        RAISE EXCEPTION 'The course is already offered by the instructor in the same semester.';
    END IF;

    -- check if the course is already offered by another instructor in the same semester
    IF EXISTS(SELECT 1 FROM course_offerings WHERE course_code = p_course_code AND semester = p_semester) THEN
        RAISE EXCEPTION 'The course is already offered by another instructor in the same semester.';
    END IF;

    -- insert the course offering into the course_offerings table
    INSERT INTO course_offerings (course_code, semester, instructor_id, qualify)
    VALUES (p_course_code, p_semester, p_instructor_id, COALESCE(p_qualify, 0));

END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION delist_course(p_course_code VARCHAR(6), p_semester VARCHAR(8), p_instructor_id INTEGER)
    RETURNS VOID AS
$$
BEGIN
    -- check if the course is not offered by the instructor in the same semester
    IF NOT EXISTS(SELECT 1
                  FROM course_offerings
                  WHERE course_code = p_course_code
                    AND semester = p_semester
                    AND instructor_id = p_instructor_id) THEN
        RAISE EXCEPTION 'The course is not offered by the instructor in the same semester.';
    END IF;

    -- deregister the course offering from the course_offerings table
    DELETE
    FROM course_offerings
    WHERE course_code = p_course_code
      AND semester = p_semester
      AND instructor_id = p_instructor_id;

END;
$$ LANGUAGE plpgsql;




CREATE OR REPLACE FUNCTION get_instructor_courses(p_instructor_id INTEGER)
    RETURNS TABLE
            (
                course_code    VARCHAR(6),
                course_name    VARCHAR(100),
                semester       VARCHAR(8),
                qualify        NUMERIC,
                enrollment_num INTEGER
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT course_offerings.course_code,
               course_catalog.course_name,
               course_offerings.semester,
               course_offerings.qualify,
               course_offerings.enrollment_count
        FROM course_offerings
                 JOIN course_catalog ON course_offerings.course_code = course_catalog.course_code
        WHERE instructor_id = p_instructor_id;
END;
$$ LANGUAGE plpgsql;



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


CREATE OR REPLACE FUNCTION student_history(p_student_id VARCHAR(255))
    RETURNS TABLE
            (
                course_code VARCHAR(6),
                course_name VARCHAR(255),
                grade       VARCHAR(3)
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT course_catalog.course_code, course_catalog.course_name, course_enrollments.grade
        FROM course_catalog
                 JOIN course_enrollments ON course_catalog.course_code = course_enrollments.course_code
        WHERE course_enrollments.student_id = p_student_id;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION graduation_check(p_student_id VARCHAR(255))
    RETURNS BOOLEAN AS
$$
DECLARE
    batch INTEGER;
    dept VARCHAR(4);
    core_req  NUMERIC;
    elect_req INTEGER;
    completed record;
--     elect_completed record;
    BEGIN
    SELECT substring(p_student_id, 1, 11) INTO batch;
    SELECT substring(p_student_id, 5, 7) INTO dept;
    SELECT core_count, elect_count INTO core_req, elect_req FROM graduation_requirements WHERE graduation_requirements.dept == dept AND graduation_requirements.year == batch;
--     EXECUTE FORMAT ('SELECT credit_str[5], ce.course_code, ce.semester FROM course_catalog JOIN course_enrollments ce on course_catalog.course_code = ce.course_code JOIN (SELECT column_name, is_elective FROM information_schema.columns WHERE table_name = %I AND column_name = %L) AS pc ON TRUE WHERE ce.student_id = p_student_id AND ce.grade != %L INTO completed;', ce.course_code||'_');




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




CREATE OR REPLACE FUNCTION update_course_enrollment_count() RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        UPDATE course_offerings
        SET enrollment_count = enrollment_count - 1
        WHERE course_code = OLD.course_code AND semester = OLD.semester;
    ELSIF (TG_OP = 'INSERT') THEN
        UPDATE course_offerings
        SET enrollment_count = enrollment_count + 1
        WHERE course_code = NEW.course_code AND semester = NEW.semester;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_course_enrollment_trigger
    AFTER INSERT OR DELETE ON course_enrollments
    FOR EACH ROW
EXECUTE FUNCTION update_course_enrollment_count();




-- CREATE OR REPLACE FUNCTION check_prerequisites()
--     RETURNS TRIGGER AS
-- $$
-- DECLARE
--     prerequisite RECORD;
-- BEGIN
--     -- Get the prerequisites for the course being enrolled in
--     FOR prerequisite IN (SELECT prerequisite FROM course_catalog WHERE course_code = NEW.course_code)
--         LOOP
--             -- Check if the student has completed the prerequisite course and obtained a grade other than E or F
--             IF NOT EXISTS(SELECT 1
--                           FROM course_enrollments
--                           WHERE student_id = NEW.student_id
--                             AND course_code = prerequisite.prerequisite
--                             AND grade NOT IN ('E', 'F')) THEN
--                 RAISE EXCEPTION 'Student has not cleared the prerequisite course(s)';
--             END IF;
--         END LOOP;
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER prerequisites_check
--     BEFORE INSERT
--     ON course_enrollments
--     FOR EACH ROW
-- EXECUTE FUNCTION check_prerequisites();
--
--


-- CREATE OR REPLACE FUNCTION update_course_enrollment()
--     RETURNS TRIGGER AS
-- $$
--     BEGIN
--         DELETE FROM course_enrollments
--         WHERE course_code = OLD.course_code
--           AND semester = OLD.semester;
--         RETURN OLD;
--     END;
-- $$ LANGUAGE plpgsql;

-- CREATE TRIGGER delete_course_enrollment
--     AFTER DELETE
--     ON course_offerings
--     FOR EACH ROW
-- EXECUTE FUNCTION update_course_enrollment();



-- CREATE OR REPLACE FUNCTION check_credit_limit()
--     RETURNS TRIGGER AS
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
INSERT into instructors (email_id, name, phone_number, dept, date_of_joining)
VALUES ('mudgal@yopmail.com', 'Apurva Mudgal', '8989872980', 'CSE', now());
INSERT into students
VALUES ('2020CSB1066', '2020csb1066@iitrpr.ac.in', 'Aditya Aggarwal', '8989872980', 'CSE', '2024');
INSERT into semester
VALUES (2022, 2, '2020-02-25', '2024-02-25');
INSERT INTO course_catalog
VALUES ('CS201', 'Data Structures', ARRAY [3,1,2,2,4], 'CSE');
INSERT INTO course_catalog
VALUES ('CS202', 'Algorithms', ARRAY [3,1,2,2,4], 'CSE');
INSERT into course_offerings VALUES ('CS201','2022-2',1);
INSERT into course_offerings VALUES ('CS202','2022-1',1);
-- INSERT INTO course_enrollments (course_code, semester,student_id,grade) values ('CS201','2022-2','2020CSB1066','A');
-- INSERT INTO course_enrollments (course_code, semester,student_id,grade) values ('CS202','2022-1','2020CSB1066','D');