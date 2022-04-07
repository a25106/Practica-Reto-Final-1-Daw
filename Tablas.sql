Create table Person (
ID_Person varchar2(10) Constraint PK_ID_Person Primary Key,
Name varchar2(50),
Surnames varchar2(50),
Email varchar2(50),
Adress varchar2(50),
Phone number(15),
Username varchar2(30),
Password varchar2(30),
Birth_Date date,
Gender varchar2(10)
);

Create table App (
ID_App varchar2(10) Constraint PK_ID_App Primary Key,
Name varchar2(50),
Volume varchar2(10),
Description varchar2(1024),
Price number(14 , 2),
Category varchar2(30)
);

Create table Buy (
ID_Person varchar2(10) Constraint FK_ID_Person_Buy References Person (ID_Person),
ID_App varchar2(10) Constraint FK_ID_App_Buy References App (ID_App)
);

Create table Upload (
ID_Person varchar2(10) Constraint FK_ID_Person_Upload References Person (ID_Person),
ID_App varchar2(10) Constraint FK_ID_App_Upload References App (ID_App)
);

Create table Opinions (
ID_Opinions varchar2(10) Constraint PK_ID_Opinions Primary Key,
ID_Person varchar2(10) Constraint FK_ID_Person_Opinions References Person (ID_Person),
ID_App varchar2(10) Constraint FK_ID_App_Opinions References App (ID_App),
Rating number(4,2),
Commentary varchar2(10)
);