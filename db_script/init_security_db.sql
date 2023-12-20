drop sequence spring_db.SECURITY_SEQ;
drop table spring_db.users_roles;
drop table spring_db.roles_permissions;
drop table spring_db.users;
drop table spring_db.roles;
drop table spring_db.permissions;

create table spring_db.users
             ( USER_ID                NUMBER(19)                      NOT NULL
             , USER_NAME              VARCHAR2(255 CHAR)              NOT NULL
             , NICK_NAME              VARCHAR2(255 CHAR)              NOT NULL
             , PASSWORD               VARCHAR2(255 CHAR)              NOT NULL
             , PASSWORD_EXPIRATION    TIMESTAMP(6)                    NOT NULL
             , USER_BLOCKED           CHAR(1 CHAR) DEFAULT 'N'        NOT NULL
             , INSERT_DATE            TIMESTAMP(6)                    NOT NULL
             , UPDATE_DATE            TIMESTAMP(6)                    NOT NULL
             )
             tablespace users;

create index spring_db.SYS_USER_ID on spring_db.users
                   ( USER_ID
                   ) tablespace users;

alter table spring_db.users add constraint SYS_USER_ID
       primary key ( USER_ID
                   ) using index;

create index spring_db.SYS_USER_NAME on spring_db.users
                   ( USER_NAME
                   ) tablespace users;

alter table spring_db.users add constraint SYS_USER_NAME
            unique ( USER_NAME
                   ) using index;

create index spring_db.SYS_NICK_NAME on spring_db.users
                   ( NICK_NAME
                   ) tablespace users;

alter table spring_db.users add constraint SYS_NICK_NAME
            unique ( NICK_NAME
                   ) using index;


create table spring_db.roles
             ( ROLE_ID                NUMBER(19)                      NOT NULL
             , ROLE_NAME              VARCHAR2(255 CHAR)              NOT NULL
             , ROLE_DESCRIPTION       VARCHAR2(1024 CHAR)             NOT NULL
             , INSERT_DATE            TIMESTAMP(6)                    NOT NULL
             , UPDATE_DATE            TIMESTAMP(6)                    NOT NULL
             )
             tablespace users;

create index spring_db.SYS_ROLE_ID on spring_db.roles
                   ( ROLE_ID
                   ) tablespace users;

alter table spring_db.roles add constraint SYS_ROLE_ID
       primary key ( ROLE_ID
                   ) using index;

create index spring_db.SYS_ROLE_NAME on spring_db.roles
                   ( ROLE_NAME
                   ) tablespace users;

alter table spring_db.roles add constraint SYS_ROLE_NAME
            unique ( ROLE_NAME
                   ) using index;


create table spring_db.permissions
             ( PERMISSION_ID                NUMBER(19)                      NOT NULL
             , PERMISSION_NAME              VARCHAR2(255 CHAR)              NOT NULL
             , PERMISSION_DESCRIPTION       VARCHAR2(1024 CHAR)             NOT NULL
             , INSERT_DATE                  TIMESTAMP(6)                    NOT NULL
             , UPDATE_DATE                  TIMESTAMP(6)                    NOT NULL
             )
             tablespace users;

create index spring_db.SYS_PERMISSION_ID on spring_db.permissions
                   ( PERMISSION_ID
                   ) tablespace users;

alter table spring_db.permissions add constraint SYS_PERMISSION_ID
       primary key ( PERMISSION_ID
                   ) using index;

create index spring_db.SYS_PERMISSION_NAME on spring_db.permissions
                   ( PERMISSION_NAME
                   ) tablespace users;

alter table spring_db.permissions add constraint SYS_PERMISSION_NAME
            unique ( PERMISSION_NAME
                   ) using index;


create table spring_db.users_roles
             ( USER_ID                NUMBER(19)                      NOT NULL
             , ROLE_ID                NUMBER(19)                      NOT NULL
             )
             tablespace users;

alter table spring_db.users_roles   add (
     constraint FK_USERS foreign key
       ( USER_ID
       ) references spring_db.users
       ( USER_ID ));

alter table spring_db.users_roles   add (
     constraint FK_ROLES1 foreign key
       ( ROLE_ID
       ) references spring_db.roles
       ( ROLE_ID ));


create table spring_db.roles_permissions
             ( ROLE_ID                NUMBER(19)                      NOT NULL
             , PERMISSION_ID          NUMBER(19)                      NOT NULL
             )
             tablespace users;

alter table spring_db.roles_permissions   add (
     constraint FK_ROLES2 foreign key
       ( ROLE_ID
       ) references spring_db.roles
       ( ROLE_ID ));

alter table spring_db.roles_permissions   add (
     constraint FK_PERMISSIONS foreign key
       ( PERMISSION_ID
       ) references spring_db.permissions
       ( PERMISSION_ID ));



create sequence spring_db.SECURITY_SEQ
  minvalue 1
  maxvalue 9999999999999999999999999999
  increment by 1
  nocycle
  noorder
  cache 20
  start with 1;

declare

id_role_admin      number := spring_db.SECURITY_SEQ.nextval;
id_role_operator   number := spring_db.SECURITY_SEQ.nextval;
id_role_supervisor number := spring_db.SECURITY_SEQ.nextval;

id_company_read    number := spring_db.SECURITY_SEQ.nextval;
id_company_add     number := spring_db.SECURITY_SEQ.nextval;
id_employee_read   number := spring_db.SECURITY_SEQ.nextval;
id_employee_add    number := spring_db.SECURITY_SEQ.nextval;

begin

INSERT INTO spring_db.roles (ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION, INSERT_DATE, UPDATE_DATE)
VALUES (id_role_admin, 'admin', 'System administrator', TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));
INSERT INTO spring_db.roles (ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION, INSERT_DATE, UPDATE_DATE)
VALUES (id_role_operator, 'operator', 'Operator can only read data', TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));
INSERT INTO spring_db.roles (ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION, INSERT_DATE, UPDATE_DATE)
VALUES (id_role_supervisor, 'supervisor', 'Supervisor can read and write data', TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));

INSERT INTO spring_db.permissions (PERMISSION_ID, PERMISSION_NAME, PERMISSION_DESCRIPTION, INSERT_DATE, UPDATE_DATE)
VALUES (id_company_read, 'AUTH_COMPANY_READ', 'Read company data', TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));
INSERT INTO spring_db.permissions (PERMISSION_ID, PERMISSION_NAME, PERMISSION_DESCRIPTION, INSERT_DATE, UPDATE_DATE)
VALUES (id_company_add, 'AUTH_COMPANY_ADD', 'Update company data', TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));
INSERT INTO spring_db.permissions (PERMISSION_ID, PERMISSION_NAME, PERMISSION_DESCRIPTION, INSERT_DATE, UPDATE_DATE)
VALUES (id_employee_read, 'AUTH_EMPLOYEE_READ', 'Read employee data', TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));
INSERT INTO spring_db.permissions (PERMISSION_ID, PERMISSION_NAME, PERMISSION_DESCRIPTION, INSERT_DATE, UPDATE_DATE)
VALUES (id_employee_add, 'AUTH_EMPLOYEE_ADD', 'Update company data', TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'), TO_DATE(to_char(sysdate,'YYYY-mm-dd'),'YYYY-mm-dd'));

INSERT INTO spring_db.roles_permissions (ROLE_ID, PERMISSION_ID)
VALUES (id_role_operator, id_company_read);
INSERT INTO spring_db.roles_permissions (ROLE_ID, PERMISSION_ID)
VALUES (id_role_operator, id_employee_read);
INSERT INTO spring_db.roles_permissions (ROLE_ID, PERMISSION_ID)
VALUES (id_role_supervisor, id_company_add);
INSERT INTO spring_db.roles_permissions (ROLE_ID, PERMISSION_ID)
VALUES (id_role_supervisor, id_employee_add);
INSERT INTO spring_db.roles_permissions (ROLE_ID, PERMISSION_ID)
VALUES (id_role_admin, id_company_read);
INSERT INTO spring_db.roles_permissions (ROLE_ID, PERMISSION_ID)
VALUES (id_role_admin, id_employee_read);
INSERT INTO spring_db.roles_permissions (ROLE_ID, PERMISSION_ID)
VALUES (id_role_admin, id_company_add);
INSERT INTO spring_db.roles_permissions (ROLE_ID, PERMISSION_ID)
VALUES (id_role_admin, id_employee_add);

commit;

end;
/