

create table Lookup_Result_Type
(
	id integer PRIMARY KEY,
	caption varchar(96) NOT NULL,
	abbrev varchar(32)
);
create unique index LkResTy_abbrev_unq on Lookup_Result_Type(abbrev);


create table Book_Type
(
	id integer PRIMARY KEY,
	caption varchar(96) NOT NULL,
	abbrev varchar(32)
);
create unique index bkT_abbrev_unq on Book_Type(abbrev);


create table Book_Info
(
	cr_stamp date,
	id integer IDENTITY PRIMARY KEY,
	name varchar(64),
	type integer,
	author varchar(64),
	publisher varchar(64),
	isbn varchar(10)
);

insert into Lookup_Result_Type(id, caption) values (0, 'ID');
insert into Lookup_Result_Type(id, caption) values (1, 'Caption');
insert into Lookup_Result_Type(id, caption) values (2, 'Abbreviation');
insert into Book_Type(id, caption) values (0, 'Science Fiction');
insert into Book_Type(id, caption) values (1, 'Mystery');
insert into Book_Type(id, caption) values (2, 'Business');
insert into Book_Type(id, caption) values (3, 'Information Technology');
insert into Book_Type(id, caption) values (4, 'Nuclear Physics');
insert into Book_Type(id, caption) values (5, 'Chemistry');
