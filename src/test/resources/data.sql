----------------- //  privilege // -----------------
INSERT INTO privilege (id, name) VALUES (1, 'READ_PRIVILEGE');
INSERT INTO privilege (id, name) VALUES (2, 'WRITE_PRIVILEGE');
INSERT INTO privilege (id, name) VALUES (3, 'EDIT_PRIVILEGE');


----------------- //  role // -----------------
INSERT INTO role (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO role (id, name) VALUES (2, 'ROLE_USER');

----------------- //  roles_privileges // -----------------
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 1);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (2, 1);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 2);

----------------- //  user_account // -----------------
