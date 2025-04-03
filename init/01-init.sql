-- 기본 샘플 데이터 넣기
USE ecc;

INSERT INTO subject (name, template, prompt)
VALUES ('AI 회화', '토론 중심 수업', '다음 문장을 영어로 바꾸시오.');

INSERT INTO major (name)
VALUES ('산업공학과'), ('컴퓨터공학과');

INSERT INTO member (kakao_uuid, user_id, password, tel, name, student_no, email, level, rate, subject_id)
VALUES
    ('kakao123', 'user01', 'pass01', '01012345678', '홍길동', '20201234', 'hong@example.com', 1, 4.3, 1),
    ('kakao124', 'user02', 'pass02', '01098765432', '김철수', '20205678', 'kim@example.com', 2, 3.8, 1);

INSERT INTO team (subject_id, name, score, year, semester)
VALUES (1, '1조', 0, 2025, 1);

INSERT INTO team_members (member_id, subject_id)
VALUES (1, 1), (2, 1);

INSERT INTO report (contents, grade, week, subject_id, team_id)
VALUES ('주차별 보고서입니다.', 2, 1, 1, 1);

INSERT INTO study (team_id, report_id, subject_id, status)
VALUES (1, 1, 1, 'ongoing');

INSERT INTO notification (contents, checked, member_id)
VALUES ('스터디 일정이 업데이트되었습니다.', false, 1);
