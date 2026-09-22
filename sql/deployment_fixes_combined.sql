ALTER SESSION SET current_schema = rcosd;

ALTER TABLE OFFENSE ADD ISACTIVE NUMBER(1) DEFAULT 1 NOT NULL;

ALTER TABLE REQUEST ADD AIRESPONSE CLOB;

INSERT INTO person (lastName, firstName, middleName, dateOfBirth)
VALUES ('Admin', 'System', NULL, NULL);

UPDATE login
SET personID = (SELECT personID FROM person WHERE lastName = 'Admin' AND firstName = 'System')
WHERE username = 'admin';

UPDATE login
SET personID = 60
WHERE username = 'prefect';

insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (59, 'prefectCollege', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_PREFECT', 'user:read,user:create,user:update,user:delete', 1, 0);

insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (9, 'CT23-0009', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (10, 'CT23-0010', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (11, 'CT23-0011', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (12, 'CT23-0012', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (13, 'CT23-0013', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (14, 'CT23-0014', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (15, 'CT23-0015', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (16, 'CT23-0016', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (17, 'CT23-0017', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (18, 'CT23-0018', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (19, 'JHS-0019', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (20, 'JHS-0020', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (21, 'JHS-0021', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (22, 'JHS-0022', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (23, 'JHS-0023', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (24, 'JHS-0024', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (25, 'JHS-0025', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (26, 'JHS-0026', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (27, 'JHS-0027', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (28, 'JHS-0028', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (29, 'SHS-0029', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (30, 'SHS-0030', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (31, 'SHS-0031', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (32, 'SHS-0032', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (33, 'SHS-0033', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (34, 'SHS-0034', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (35, 'SHS-0035', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (36, 'SHS-0036', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (37, 'SHS-0037', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (38, 'SHS-0038', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (39, 'JHS-0039', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (40, 'JHS-0040', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (41, 'JHS-0041', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (42, 'JHS-0042', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (43, 'JHS-0043', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (44, 'JHS-0044', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (45, 'JHS-0045', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (46, 'JHS-0046', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (47, 'JHS-0047', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (48, 'JHS-0048', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (49, 'SHS-0049', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (50, 'SHS-0050', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (51, 'SHS-0051', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (52, 'SHS-0052', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (53, 'SHS-0053', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (54, 'SHS-0054', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (55, 'SHS-0055', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (56, 'SHS-0056', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (57, 'SHS-0057', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);
insert into login (personID, username, password, join_date, last_login_date, role, authorities, is_active, is_locked)
values (58, 'SHS-0058', '$2b$10$T8VCsavGpS/.Wz/cfDVjHuM4bfnVAUuCnSRXzST2bAPdoRJNVujF.', to_timestamp('2024-01-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), to_timestamp('2024-10-01 00:00:00.00', 'yyyy-mm-dd hh24:mi:ss:ff'), 'ROLE_USER', 'user:read,user:create,user:update', 1, 0);

COMMIT;
