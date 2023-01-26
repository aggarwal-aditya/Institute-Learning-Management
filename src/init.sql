DROP SCHEMA PUBLIC CASCADE;
CREATE SCHEMA PUBLIC;


CREATE TABLE instructors
(
    instructor_id   INTEGER PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    phone_number    VARCHAR(20)  NOT NULL,
    email_id        VARCHAR(255) NOT NULL,
    dept            VARCHAR(127),
    date_of_joining DATE         NOT NULL,
    password        VARCHAR(255) NOT NULL
);

CREATE TABLE students
(
    student_id   INTEGER PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20)  NOT NULL,
    email_id     VARCHAR(255) NOT NULL,
    dept         VARCHAR(127),
    batch        DATE         NOT NULL,
    password     VARCHAR(255) NOT NULL
);


CREATE TABLE course_catalog
(
    course_code  VARCHAR(6) primary key,
    course_name  VARCHAR(255)  NOT NULL,
    credit_str   NUMERIC array NOT NULL,
    dept         VARCHAR(127),
    prerequisite VARCHAR(6) array DEFAULT NULL
);


CREATE TABLE course_offerings
(
    course_code    VARCHAR(6),
    session        VARCHAR(8),
    instructor_id  INTEGER NOT NULL,
    qualify        NUMERIC default 0,
    enrollment_num INTEGER default 0,
    primary key (course_code, session),
    foreign key (course_code) references course_catalog (course_code),
    foreign key (instructor_id) references instructors (instructor_id)
);

CREATE TABLE course_enrollments
(
    enrollment_id INTEGER PRIMARY KEY,
    course_code   VARCHAR(6) NOT NULL,
    session       VARCHAR(8) NOT NULL,
    student_id    INTEGER    NOT NULL,
    grade         VARCHAR(3) DEFAULT NULL,
    foreign key (course_code, session) references course_offerings (course_code, session),
    foreign key (student_id) references students (student_id)
);

CREATE TABLE grade_mapping
(
    grade VARCHAR(3) PRIMARY KEY,
    value NUMERIC NOT NULL
);


CREATE OR REPLACE FUNCTION enroll_student(p_course_code VARCHAR(6), p_session VARCHAR(8), p_student_id INTEGER)
    RETURNS VOID AS
$$
BEGIN
    -- check if the course is available for the given session
    IF NOT EXISTS(SELECT 1 FROM course_offerings WHERE course_code = p_course_code AND session = p_session) THEN
        RAISE EXCEPTION 'The course is not available for the given session.';
    END IF;

    -- check if the student is already enrolled in the course
    IF EXISTS(SELECT 1
              FROM course_enrollments
              WHERE course_code = p_course_code AND session = p_session AND student_id = p_student_id) THEN
        RAISE EXCEPTION 'The student is already enrolled in the course.';
    END IF;

    -- insert the enrollment into the course_enrollments table
    INSERT INTO course_enrollments (course_code, session, student_id)
    VALUES (p_course_code, p_session, p_student_id);

    -- increment the enrollment_num of the course 
    UPDATE course_offerings
    SET enrollment_num = enrollment_num + 1
    WHERE course_code = p_course_code
      AND session = p_session;

END;
$$
    LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION deregister_student(p_course_code VARCHAR(6), p_session VARCHAR(8), p_student_id INTEGER)
    RETURNS VOID AS
$$
BEGIN
    -- check if the student is not enrolled in the course
    IF NOT EXISTS(SELECT 1
                  FROM course_enrollments
                  WHERE course_code = p_course_code AND session = p_session AND student_id = p_student_id) THEN
        RAISE EXCEPTION 'The student is not enrolled in the course.';
    END IF;

    -- delete the enrollment from the course_enrollments table
    DELETE
    FROM course_enrollments
    WHERE course_code = p_course_code
      AND session = p_session
      AND student_id = p_student_id;

    -- decrement the enrollment_num of the course
    UPDATE course_offerings
    SET enrollment_num = enrollment_num - 1
    WHERE course_code = p_course_code
      AND session = p_session;

END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION add_grades(p_course_code VARCHAR(6), p_session VARCHAR(8), p_student_ids INTEGER[],
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
                            AND session = p_session
                            AND student_id = p_student_ids[i]) THEN
                RAISE EXCEPTION 'The student is not enrolled in the course.';
            END IF;

            -- update the grade in the course_enrollments table
            UPDATE course_enrollments
            SET grade = p_grades[i]
            WHERE course_code = p_course_code
              AND session = p_session
              AND student_id = p_student_ids[i];
        END LOOP;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION offer_course(p_course_code VARCHAR(6), p_session VARCHAR(8), p_instructor_id INTEGER,
                                        p_qualify NUMERIC DEFAULT NULL)
    RETURNS VOID AS
$$
BEGIN

    IF EXISTS(SELECT 1
              FROM course_offerings
              WHERE course_code = p_course_code AND session = p_session AND instructor_id = p_instructor_id) THEN
        RAISE EXCEPTION 'The course is already offered by the instructor in the same session.';
    END IF;

    -- check if the course is already offered by another instructor in the same session
    IF EXISTS(SELECT 1 FROM course_offerings WHERE course_code = p_course_code AND session = p_session) THEN
        RAISE EXCEPTION 'The course is already offered by another instructor in the same session.';
    END IF;

    -- insert the course offering into the course_offerings table
    INSERT INTO course_offerings (course_code, session, instructor_id, qualify)
    VALUES (p_course_code, p_session, p_instructor_id, COALESCE(p_qualify, 0));

END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION deregister_course(p_course_code VARCHAR(6), p_session VARCHAR(8), p_instructor_id INTEGER)
    RETURNS VOID AS
$$
BEGIN
    -- check if the course is not offered by the instructor in the same session
    IF NOT EXISTS(SELECT 1
                  FROM course_offerings
                  WHERE course_code = p_course_code AND session = p_session AND instructor_id = p_instructor_id) THEN
        RAISE EXCEPTION 'The course is not offered by the instructor in the same session.';
    END IF;

    -- deregister the course offering from the course_offerings table
    DELETE
    FROM course_offerings
    WHERE course_code = p_course_code
      AND session = p_session
      AND instructor_id = p_instructor_id;

END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION calculate_cgpa(p_student_id INTEGER)
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
    FOR course_enrollment IN (SELECT course_code, session, grade, credit_str
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
        RETURN earned_credits / total_credits;
    END IF;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION generate_transcript(p_student_id INTEGER, p_session VARCHAR(8))
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
                  AND course_enrollments.session = p_session),
               calculate_cgpa(p_student_id)
        FROM course_enrollments
                 JOIN course_catalog ON course_enrollments.course_code = course_catalog.course_code
        WHERE course_enrollments.student_id = p_student_id
          AND course_enrollments.session = p_session;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION check_prerequisites()
    RETURNS TRIGGER AS
$$
DECLARE
    prerequisite RECORD;
BEGIN
    -- Get the prerequisites for the course being enrolled in
    FOR prerequisite IN (SELECT prerequisite FROM course_catalog WHERE course_code = NEW.course_code)
        LOOP
            -- Check if the student has completed the prerequisite course and obtained a grade other than E or F
            IF NOT EXISTS(SELECT 1
                          FROM course_enrollments
                          WHERE student_id = NEW.student_id
                            AND course_code = prerequisite.prerequisite
                            AND grade NOT IN ('E', 'F')) THEN
                RAISE EXCEPTION 'Student has not cleared the prerequisite course(s)';
            END IF;
        END LOOP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER prerequisites_check
    BEFORE INSERT
    ON course_enrollments
    FOR EACH ROW
EXECUTE FUNCTION check_prerequisites();



CREATE OR REPLACE FUNCTION check_credit_limit()
    RETURNS TRIGGER AS
$$
DECLARE
    credit_limit    NUMERIC;
    count           INTEGER;
    current_credits NUMERIC;
BEGIN
    -- Get the credit limit for the student based on previous semesters
    SELECT SUM(credits)
    INTO credit_limit
    FROM (SELECT SUM(credit_str[5]) as credits
          FROM course_enrollments
                   JOIN course_catalog ON course_enrollments.course_code = course_catalog.course_code
          WHERE student_id = NEW.student_id
            AND session < NEW.session
          ORDER BY session DESC
          LIMIT 2) as previous_semesters;
    IF credit_limit IS NOT NULL THEN
        SELECT COUNT(*)
        INTO count
        FROM (SELECT SUM(credit_str[5]) as credits
              FROM course_enrollments
                       JOIN course_catalog ON course_enrollments.course_code = course_catalog.course_code
              WHERE student_id = NEW.student_id
                AND session < NEW.session
              ORDER BY session DESC
              LIMIT 2) as previous_semesters;
        IF count < 2 THEN
            credit_limit := 24;
        ELSE
            credit_limit := 1.25 * (credit_limit / count);
        END IF;
    ELSE
        credit_limit := 24;
    END IF;


    -- Get the total credits for the current semester
    SELECT SUM(credit_str[5])
    INTO current_credits
    FROM course_enrollments
             JOIN course_catalog ON course_enrollments.course_code = course_catalog.course_code
    WHERE student_id = NEW.student_id
      AND session = NEW.session;

    -- Check if the student will exceed the credit limit
    IF (current_credits + (SELECT credit_str[5] FROM course_catalog WHERE course_code = NEW.course_code)) >
       credit_limit THEN
        RAISE EXCEPTION 'The student will exceed the credit limit for this semester';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER credit_limit_check
    BEFORE INSERT
    ON course_enrollments
    FOR EACH ROW
EXECUTE FUNCTION check_credit_limit();



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

