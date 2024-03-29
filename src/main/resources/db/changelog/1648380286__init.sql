CREATE TABLE BRANCH
(
    BRANCH_ID         SERIAL PRIMARY KEY,
    BRANCH_SHORT_NAME varchar(45)  NOT NULL,
    BRANCH_NAME       varchar(100) NOT NULL,
    DESCRIPTION       varchar(200) DEFAULT NULL
);

CREATE TABLE SUBJECT
(
    SUBJECT_ID   SERIAL PRIMARY KEY,
    SUBJECT_NAME varchar(100) NOT NULL,
    SUBJECT_DESC varchar(200) DEFAULT NULL
);

CREATE TABLE BRANCH_SUBJECT
(
    BRANCH_ID  int REFERENCES BRANCH (BRANCH_ID),
    SUBJECT_ID int REFERENCES SUBJECT (SUBJECT_ID)
);
