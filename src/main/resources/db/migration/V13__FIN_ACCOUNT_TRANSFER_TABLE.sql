CREATE TABLE FIN_ACCOUNT_TRANSFER (
  ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  INPUT_MOVEMENT BIGINT NOT NULL,
  OUTPUT_MOVEMENT BIGINT NOT NULL,
  CREATED_BY BIGINT NOT NULL,
  CREATED_AT TIMESTAMP NOT NULL,
  UPDATED_AT TIMESTAMP NOT NULL,
  FOREIGN KEY (CREATED_BY) REFERENCES PERSON (ID),
  FOREIGN KEY (INPUT_MOVEMENT) REFERENCES ACCOUNT_MOVEMENT (ID),
  FOREIGN KEY (OUTPUT_MOVEMENT) REFERENCES ACCOUNT_MOVEMENT (ID),
  UNIQUE(INPUT_MOVEMENT, OUTPUT_MOVEMENT)
);