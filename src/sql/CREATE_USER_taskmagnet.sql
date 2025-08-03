-- Connect to PDB first
ALTER SESSION SET CONTAINER = XEPDB1;

-- Create the user
CREATE USER taskmagnet IDENTIFIED BY "mahedee.net";

-- Grant basic connection privileges
GRANT CONNECT TO taskmagnet;
GRANT RESOURCE TO taskmagnet;
GRANT CREATE SESSION TO taskmagnet;

-- Grant additional privileges for full database operations
GRANT CREATE TABLE TO taskmagnet;
GRANT CREATE VIEW TO taskmagnet;
GRANT CREATE SEQUENCE TO taskmagnet;
GRANT CREATE PROCEDURE TO taskmagnet;

-- Grant unlimited tablespace (for development)
GRANT UNLIMITED TABLESPACE TO taskmagnet;

-- Verify user creation (compatible with all Oracle versions)
SELECT username, account_status FROM dba_users WHERE username = 'TASKMAGNET';

-- Show current container
SELECT SYS_CONTEXT('USERENV', 'CON_NAME') FROM DUAL;