drop sequence test_spring_db.EMPLOYEE_SEQ;
drop sequence test_spring_db.COMPANY_SEQ;
drop table test_spring_db.employees;
drop table test_spring_db.companies;

create table test_spring_db.companies
             ( COMPANY_ID                NUMBER(19)                      NOT NULL
             , NAME				         VARCHAR2(255 CHAR)              NOT NULL
             , COMPANY_CODE				 VARCHAR2(255 CHAR)              NOT NULL
             , ADDRESS				     VARCHAR2(255 CHAR)              NOT NULL
             , COMPANY_TYPE              VARCHAR2(255 CHAR)              NOT NULL
             , INSERT_DATE               TIMESTAMP(6)                    NOT NULL
             , UPDATE_DATE               TIMESTAMP(6)                    NOT NULL
             )
             tablespace users;

create index test_spring_db.SYS_COMPANY_ID on test_spring_db.companies
                   ( COMPANY_ID
                   ) tablespace users;

alter table test_spring_db.companies add constraint SYS_COMPANY_ID
       primary key ( COMPANY_ID
                   ) using index;

create index test_spring_db.SYS_COMPANY_CODE on test_spring_db.companies
                   ( COMPANY_CODE
                   ) tablespace users;

alter table test_spring_db.companies add constraint SYS_COMPANY_CODE
            unique ( COMPANY_CODE
                   ) using index;


create table test_spring_db.employees
             ( EMPLOYEE_ID               NUMBER(19)                      NOT NULL
             , NAME				         VARCHAR2(255 CHAR)              NOT NULL
             , SURNAME				     VARCHAR2(255 CHAR)              NOT NULL
             , BIRTHDAY_DATE             TIMESTAMP(6)                    NOT NULL
             , FISCAL_CODE               VARCHAR2(255 CHAR)              NOT NULL
             , ROLE                      VARCHAR2(255 CHAR)              NOT NULL
             , COMPANY_ID                NUMBER(19)                      NOT NULL
             , INSERT_DATE               TIMESTAMP(6)                    NOT NULL
             , UPDATE_DATE               TIMESTAMP(6)                    NOT NULL
             )
             tablespace users;

create index test_spring_db.SYS_EMPLOYEE_ID on test_spring_db.employees
                   ( EMPLOYEE_ID
                   ) tablespace users;

alter table test_spring_db.employees add constraint SYS_EMPLOYEE_ID
       primary key ( EMPLOYEE_ID
                   ) using index;

create index test_spring_db.SYS_FISCAL_CODE on test_spring_db.employees
                   ( FISCAL_CODE
                   ) tablespace users;

alter table test_spring_db.employees add constraint SYS_FISCAL_CODE
            unique ( FISCAL_CODE
                   ) using index;

alter table test_spring_db.employees   add (
     constraint FK_COMPANY foreign key
       ( COMPANY_ID
       ) references test_spring_db.companies
       ( COMPANY_ID ));


create sequence test_spring_db.EMPLOYEE_SEQ
  minvalue 1
  maxvalue 9999999999999999999999999999
  increment by 1
  nocycle
  noorder
  cache 20
  start with 1;

create sequence test_spring_db.COMPANY_SEQ
  minvalue 1
  maxvalue 9999999999999999999999999999
  increment by 1
  nocycle
  noorder
  cache 20
  start with 1;

declare

id_leonardo number := test_spring_db.COMPANY_SEQ.nextval;
id_thales number := test_spring_db.COMPANY_SEQ.nextval;

begin

INSERT INTO test_spring_db.companies (COMPANY_ID, NAME, COMPANY_CODE, ADDRESS, COMPANY_TYPE, INSERT_DATE, UPDATE_DATE)
VALUES (id_leonardo, 'Leonardo s.p.a.', '0003465', 'Via Puccini 2, 16154 Genova', 'HIGH_TECH', TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));
INSERT INTO test_spring_db.companies (COMPANY_ID, NAME, COMPANY_CODE, ADDRESS, COMPANY_TYPE, INSERT_DATE, UPDATE_DATE)
VALUES (id_thales, 'Thales s.p.a.', '0005690', 'Roue The Artist 18, 25729 Tolosa', 'HIGH_TECH', TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));

INSERT INTO test_spring_db.employees (EMPLOYEE_ID, NAME, SURNAME, BIRTHDAY_DATE, FISCAL_CODE, ROLE, COMPANY_ID, INSERT_DATE, UPDATE_DATE)
VALUES (test_spring_db.EMPLOYEE_SEQ.nextval, 'Mario', 'Rossi', TO_DATE('1972-07-23','YYYY-mm-dd'), 'ROSMAR72E23E488K', 'WORKER', id_leonardo, TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));
INSERT INTO test_spring_db.employees (EMPLOYEE_ID, NAME, SURNAME, BIRTHDAY_DATE, FISCAL_CODE, ROLE, COMPANY_ID, INSERT_DATE, UPDATE_DATE)
VALUES (test_spring_db.EMPLOYEE_SEQ.nextval, 'Sandro', 'Bolchi', TO_DATE('1978-02-04','YYYY-mm-dd'), 'BOLSAN78E04E512K', 'EMPLOYEE', id_leonardo, TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));
INSERT INTO test_spring_db.employees (EMPLOYEE_ID, NAME, SURNAME, BIRTHDAY_DATE, FISCAL_CODE, ROLE, COMPANY_ID, INSERT_DATE, UPDATE_DATE)
VALUES (test_spring_db.EMPLOYEE_SEQ.nextval, 'Giorgio', 'Neri', TO_DATE('1981-11-09','YYYY-mm-dd'), 'NERGIO81E09E829K', 'MANAGER', id_leonardo, TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));
INSERT INTO test_spring_db.employees (EMPLOYEE_ID, NAME, SURNAME, BIRTHDAY_DATE, FISCAL_CODE, ROLE, COMPANY_ID, INSERT_DATE, UPDATE_DATE)
VALUES (test_spring_db.EMPLOYEE_SEQ.nextval, 'Gianni', 'Verdi', TO_DATE('1985-08-14','YYYY-mm-dd'), 'VERGIA85E08E278K', 'WORKER', id_thales, TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));
INSERT INTO test_spring_db.employees (EMPLOYEE_ID, NAME, SURNAME, BIRTHDAY_DATE, FISCAL_CODE, ROLE, COMPANY_ID, INSERT_DATE, UPDATE_DATE)
VALUES (test_spring_db.EMPLOYEE_SEQ.nextval, 'Gianni', 'Vattimo', TO_DATE('1966-10-17','YYYY-mm-dd'), 'VATGIA66E17E209K', 'EMPLOYEE', id_thales, TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));
INSERT INTO test_spring_db.employees (EMPLOYEE_ID, NAME, SURNAME, BIRTHDAY_DATE, FISCAL_CODE, ROLE, COMPANY_ID, INSERT_DATE, UPDATE_DATE)
VALUES (test_spring_db.EMPLOYEE_SEQ.nextval, 'Roberto', 'Abbo', TO_DATE('1964-07-22','YYYY-mm-dd'), 'ABBROB64E22E720K', 'EMPLOYEE', id_thales, TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));

commit;

end;
/