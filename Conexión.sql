conn sys as sysdba

alter system set local_listener = '127.0.0.1:1521' scope=memory;

create user usuario identified by usuario;

grant connect to usuario;

grant resource to usuario;