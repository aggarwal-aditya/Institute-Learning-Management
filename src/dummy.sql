INSERT INTO users VALUES ('2020csb1066@iitrpr.ac.in','aditya','student');
INSERT INTO users VALUES ('mudgal@yopmail.com','aditya','instructor');
INSERT into instructors (email_id,name,phone_number,dept,date_of_joining) VALUES ('mudgal@yopmail.com','Apurva Mudgal','8989872980','CSE',now());
INSERT into students VALUES ('2020CSB1066','2020csb1066@iitrpr.ac.in','Aditya Aggarwal','8989872980','CSE','2024');
INSERT into semester VALUES (2022,2,'2020-02-25','2024-02-25');
INSERT INTO course_catalog VALUES ('CS201','Data Structures',ARRAY[3,1,2,2,4],'CSE');
INSERT into course_offerings VALUES ('CS201','2022-2',1);




INSERT INTO users VALUES ('peter.johnson@example.com', 'password', 'student');
INSERT INTO users VALUES ('kate.williams@example.com', 'password', 'student');
INSERT INTO users VALUES ('steve.miller@example.com', 'password', 'instructor');
INSERT INTO users VALUES ('emily.davis@example.com', 'password', 'instructor');
INSERT INTO users VALUES ('david.lee@example.com', 'password', 'student');
INSERT INTO users VALUES ('matt.wilson@example.com', 'password', 'student');
INSERT INTO users VALUES ('lisa.brown@example.com', 'password', 'instructor');
INSERT INTO users VALUES ('jennifer.johnson@example.com', 'password', 'instructor');
INSERT INTO users VALUES ('chris.white@example.com', 'password', 'student');
INSERT INTO users VALUES ('jessica.green@example.com', 'password', 'student');


INSERT into instructors (email_id,name,phone_number,dept,date_of_joining) VALUES ('steve.miller@example.com', 'Steve Miller', '1111111111', 'CSE', '2022-01-01');
INSERT into instructors (email_id,name,phone_number,dept,date_of_joining) VALUES ('emily.davis@example.com', 'Emily Davis', '2222222222', 'IT', '2022-03-01');
INSERT into instructors (email_id,name,phone_number,dept,date_of_joining) VALUES ('lisa.brown@example.com', 'Lisa Brown', '3333333333', 'MCE', '2021-11-01');
INSERT into instructors (email_id,name,phone_number,dept,date_of_joining) VALUES ('jennifer.johnson@example.com', 'Jennifer Johnson', '4444444444', 'CSE', '2021-07-01');















CREATE OR REPLACE FUNCTION check_credit_limit(id VARCHAR(255),sem VARCHAR(255), code VARCHAR(255))
    RETURNS VOID AS
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
          WHERE student_id = id
            AND semester < sem
           GROUP BY semester
          ORDER BY semester DESC
          LIMIT 2) as previous_semesters;
    IF credit_limit IS NOT NULL THEN
        SELECT COUNT(*)
        INTO count
        FROM (SELECT SUM(credit_str[5]) as credits
              FROM course_enrollments
                       JOIN course_catalog ON course_enrollments.course_code = course_catalog.course_code
              WHERE student_id =id
                AND semester < sem
              ORDER BY semester DESC
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
    WHERE student_id = id
      AND semester = sem;

    -- Check if the student will exceed the credit limit
    IF (current_credits + (SELECT credit_str[5] FROM course_catalog WHERE course_code =code)) >
       credit_limit THEN
        RAISE EXCEPTION 'The student will exceed the credit limit for this semester';
    ELSE 
    	RAISE EXCEPTION 'OK';
    END IF;
END;
$$ LANGUAGE plpgsql;