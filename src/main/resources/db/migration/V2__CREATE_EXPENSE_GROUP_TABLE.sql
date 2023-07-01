CREATE TABLE EXPENSE_GROUP (
   ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   GROUP_NAME VARCHAR(30) NOT NULL CHECK(GROUP_NAME <> ''),
   IS_ACTIVE BOOLEAN NOT NULL DEFAULT TRUE,
   CREATED_BY BIGINT NOT NULL,
   CREATED_AT TIMESTAMP NOT NULL,
   UPDATED_AT TIMESTAMP NOT NULL,
   FOREIGN KEY (CREATED_BY) REFERENCES PERSON (ID)
);