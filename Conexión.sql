conn sys as sysdba

alter system set local_listener = '127.0.0.1:1521' scope=memory;

create user toskan identified by user;

grant connect to user;

grant resource to user;