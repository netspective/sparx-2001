

create table Lookup_Result_Type
(
idinteger PRIMARY KEY,captionvarchar(96) NOT NULL,abbrevvarchar(32));
create uniqueindex LkResTy_abbrev_unq on Lookup_Result_Type(abbrev);


create table Book_Type
(
idinteger PRIMARY KEY,captionvarchar(96) NOT NULL,abbrevvarchar(32));
create uniqueindex bkT_abbrev_unq on Book_Type(abbrev);


create table Book_Info
(
cr_stampdate,idinteger IDENTITY PRIMARY KEY,namevarchar(64),typeinteger,authorvarchar(64),publishervarchar(64),isbnvarchar(10));
insert into Lookup_Result_Type(id, caption) values (0, 'ID');
insert into Lookup_Result_Type(id, caption) values (1, 'Caption');
insert into Lookup_Result_Type(id, caption) values (2, 'Abbreviation');
insert into Book_Type(id, caption) values (0, 'Science Fiction');
insert into Book_Type(id, caption) values (1, 'Mystery');
insert into Book_Type(id, caption) values (2, 'Business');
insert into Book_Type(id, caption) values (3, 'Information Technology');
insert into Book_Type(id, caption) values (4, 'Nuclear Physics');
insert into Book_Type(id, caption) values (5, 'Chemistry');
