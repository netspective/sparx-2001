

create table Lookup_Result_Type
(
	id integer PRIMARY KEY,
	caption varchar(64) NOT NULL,
	abbrev varchar(32)
);
create uniqueindex Lookup_Result_Type_abbrev_unq on Lookup_Result_Type(abbrev);


create table Status
(
	id integer PRIMARY KEY,
	caption varchar(64) NOT NULL,
	abbrev varchar(32),
	group_name varchar(32),
	sort_seq integer,
	result integer
);
create uniqueindex Status_abbrev_unq on Status(abbrev);


create table Category
(
	id integer PRIMARY KEY,
	caption varchar(64) NOT NULL,
	abbrev varchar(32),
	group_name varchar(32),
	sort_seq integer,
	result integer
);
create uniqueindex Category_abbrev_unq on Category(abbrev);


create table Relationship
(
	id integer PRIMARY KEY,
	caption varchar(64) NOT NULL,
	abbrev varchar(32),
	group_name varchar(32),
	sort_seq integer,
	result integer
);
create uniqueindex Relationship_abbrev_unq on Relationship(abbrev);


create table Age_Range_Unit
(
	id integer PRIMARY KEY,
	caption varchar(64) NOT NULL,
	abbrev varchar(32)
);
create uniqueindex Age_Range_Unit_abbrev_unq on Age_Range_Unit(abbrev);


create table Gift
(
	created_stamp date DEFAULT getdate(),
	modified_stamp date DEFAULT getdate(),
	id integer PRIMARY KEY NOT NULL DEFAULT 0,
	active boolean DEFAULT 0,
	name varchar(32) NOT NULL,
	category_id integer NOT NULL,
	status_id integer NOT NULL,
	user_id ,
	picture_path ,
	url ,
	decription ,
	age_range ,
	price money,
	brand varchar(32),
	reference varchar(32)
);


create table User
(
	created_stamp date DEFAULT getdate(),
	modified_stamp date DEFAULT getdate(),
	id integer PRIMARY KEY NOT NULL DEFAULT 0,
	active boolean DEFAULT 0,
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
alter table Status add constraint Status_result_FK FOREIGN KEY (result) REFERENCES Lookup_Result_Type(id);
alter table Category add constraint Category_result_FK FOREIGN KEY (result) REFERENCES Lookup_Result_Type(id);
alter table Relationship add constraint Relationship_result_FK FOREIGN KEY (result) REFERENCES Lookup_Result_Type(id);
alter table Gift add constraint Gift_category_id_FK FOREIGN KEY (category_id) REFERENCES Category(id);
alter table Gift add constraint Gift_status_id_FK FOREIGN KEY (status_id) REFERENCES Status(id);
alter table User add constraint User_relationship_id_FK FOREIGN KEY (relationship_id) REFERENCES Relationship(id);
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
