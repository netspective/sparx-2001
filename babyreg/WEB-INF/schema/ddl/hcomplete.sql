

create table Lookup_Result_Type
(
	id integer,
	caption varchar(64) NOT NULL,
	abbrev varchar(32)
);
create uniqueindex Lookup_Result_Type_abbrev_unq on Lookup_Result_Type(abbrev);


create table Status
(
	id integer,
	caption varchar(64) NOT NULL,
	abbrev varchar(32),
	group_name varchar(32),
	sort_seq integer,
	result integer
);
create uniqueindex Status_abbrev_unq on Status(abbrev);


create table Category
(
	id integer,
	caption varchar(64) NOT NULL,
	abbrev varchar(32),
	group_name varchar(32),
	sort_seq integer,
	result integer
);
create uniqueindex Category_abbrev_unq on Category(abbrev);


create table Relationship
(
	id integer,
	caption varchar(64) NOT NULL,
	abbrev varchar(32),
	group_name varchar(32),
	sort_seq integer,
	result integer
);
create uniqueindex Relationship_abbrev_unq on Relationship(abbrev);


create table Age_Range_Unit
(
	id integer,
	caption varchar(64) NOT NULL,
	abbrev varchar(32)
);
create uniqueindex Age_Range_Unit_abbrev_unq on Age_Range_Unit(abbrev);


create table Gift
(
	created_stamp date ,
	modified_stamp date ,
	id integer IDENTITY PRIMARY KEY NOT NULL ,
	active BIT ,
	name varchar(32) NOT NULL,
	category_id integer NOT NULL,
	status_id integer NOT NULL,
	user_id ,
	picture_path ,
	url ,
	decription ,
	age_range ,
	price currency,
	brand varchar(32),
	reference varchar(32)
);


create table User
(
	created_stamp date ,
	modified_stamp date ,
	id integer IDENTITY PRIMARY KEY NOT NULL ,
	active BIT ,
	name ,
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
