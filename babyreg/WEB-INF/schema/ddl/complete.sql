

create table Lookup_Result_Type
(
	id integer,
	caption varchar(64) NOT NULL,
	abbrev varchar(32)
);


create table Status
(
	id integer,
	caption varchar(64) NOT NULL,
	abbrev varchar(32),
	group_name varchar(32),
	sort_seq integer,
	result integer
);


create table Category
(
	id integer,
	caption varchar(64) NOT NULL,
	abbrev varchar(32),
	group_name varchar(32),
	sort_seq integer,
	result integer
);


create table Relationship
(
	id integer,
	caption varchar(64) NOT NULL,
	abbrev varchar(32),
	group_name varchar(32),
	sort_seq integer,
	result integer
);


create table Age_Range_Unit
(
	id integer,
	caption varchar(64) NOT NULL,
	abbrev varchar(32)
);


create table Gift
(
	created_stamp date ,
	modified_stamp date ,
	id integer NOT NULL IDENTITY PRIMARY KEY ,
	active BIT ,
	name varchar(32) NOT NULL,
	category_id integer NOT NULL,
	status_id integer NOT NULL,
	user_id integer,
	picture_path varchar(256),
	url varchar(256),
	decription varchar(256),
	begin_age_range integer,
	end_age_range integer,
	unit_age_range integer,
	price float,
	brand varchar(32),
	description varchar(256),
	reference varchar(32)
);
create unique index Gift_name_category_id_unq on Gift(name,category_id);

create table User
(
	created_stamp date ,
	modified_stamp date ,
	id integer NOT NULL IDENTITY PRIMARY KEY ,
	active BIT ,
	first_name varchar(32) NOT NULL,
	middle_name varchar(32),
	last_name varchar(32) NOT NULL,
	relationship_id integer NOT NULL,
	email_addr varchar(32),
	phone varchar(32)
);


create table Baby_data
(
	name varchar(32) NOT NULL,
	value varchar(32) NOT NULL
);
insert into Lookup_Result_Type(id, caption) values (0, 'ID');
insert into Lookup_Result_Type(id, caption) values (1, 'Caption');
insert into Lookup_Result_Type(id, caption) values (2, 'Abbreviation');
insert into Status(id, caption) values (0, 'Bought');
insert into Status(id, caption) values (1, 'Picked');
insert into Status(id, caption) values (2, 'Needed');
insert into Status(id, caption) values (3, 'Desired');
insert into Status(id, caption) values (4, 'Suggested');
insert into Category(id, caption) values (0, 'Clouthing');
insert into Category(id, caption) values (1, 'Toys');
insert into Category(id, caption) values (2, 'Furniture');
insert into Category(id, caption) values (3, 'Supplies');
insert into Category(id, caption) values (4, 'Bedding');
insert into Category(id, caption) values (5, 'Bathing');
insert into Category(id, caption) values (6, 'Feeding');
insert into Category(id, caption) values (7, 'Diapering');
insert into Category(id, caption) values (8, 'Nursery');
insert into Category(id, caption) values (9, 'Gear');
insert into Category(id, caption) values (10, 'Baby Care');
insert into Category(id, caption) values (11, 'Safety');
insert into Category(id, caption) values (12, 'Activity');
insert into Relationship(id, caption) values (-1, 'No Relation');
insert into Relationship(id, caption) values (0, 'Dad');
insert into Relationship(id, caption) values (1, 'Mom');
insert into Relationship(id, caption) values (2, 'Grandmom');
insert into Relationship(id, caption) values (3, 'Granddad');
insert into Relationship(id, caption) values (4, 'Uncle');
insert into Relationship(id, caption) values (5, 'Aunt');
insert into Relationship(id, caption) values (6, 'Friend');
insert into Age_Range_Unit(id, caption) values (0, 'Days');
insert into Age_Range_Unit(id, caption) values (1, 'Weeks');
insert into Age_Range_Unit(id, caption) values (2, 'Months');
insert into Age_Range_Unit(id, caption) values (3, 'Years');

INSERT INTO USER VALUES(NULL,NULL,-1,NULL,'nobody',NULL,'nobody',-1,NULL,NULL)
INSERT INTO USER VALUES(NULL,NULL,0,NULL,'Roque',NULL,'Hernandez',0,NULL,NULL)
INSERT INTO USER VALUES(NULL,NULL,1,NULL,'Karina',NULL,'Hernandez',1,NULL,NULL)

