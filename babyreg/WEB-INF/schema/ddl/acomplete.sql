

create table Lookup_Result_Type
(
	id integer,
	caption varchar(64),
	abbrev varchar(32)
);
create uniqueindex Lookup_Result_Type_abbrev_unq on Lookup_Result_Type(abbrev);
alter table Lookup_Result_Type modify (id constraint Lookup_Result_Type_id_REQ NOT NULL);
alter table Lookup_Result_Type modify (caption constraint Lookup_Result_Type_caption_REQ NOT NULL);
alter table Lookup_Result_Type add (constraint Lookup_Result_Type_PK PRIMARY KEY (id));


create table Status
(
	id integer,
	caption varchar(64),
	abbrev varchar(32),
	group_name varchar(32),
	sort_seq integer,
	result integer
);
create uniqueindex Status_abbrev_unq on Status(abbrev);
alter table Status modify (id constraint Status_id_REQ NOT NULL);
alter table Status modify (caption constraint Status_caption_REQ NOT NULL);
alter table Status add (constraint Status_PK PRIMARY KEY (id));


create table Category
(
	id integer,
	caption varchar(64),
	abbrev varchar(32),
	group_name varchar(32),
	sort_seq integer,
	result integer
);
create uniqueindex Category_abbrev_unq on Category(abbrev);
alter table Category modify (id constraint Category_id_REQ NOT NULL);
alter table Category modify (caption constraint Category_caption_REQ NOT NULL);
alter table Category add (constraint Category_PK PRIMARY KEY (id));


create table Relationship
(
	id integer,
	caption varchar(64),
	abbrev varchar(32),
	group_name varchar(32),
	sort_seq integer,
	result integer
);
create uniqueindex Relationship_abbrev_unq on Relationship(abbrev);
alter table Relationship modify (id constraint Relationship_id_REQ NOT NULL);
alter table Relationship modify (caption constraint Relationship_caption_REQ NOT NULL);
alter table Relationship add (constraint Relationship_PK PRIMARY KEY (id));


create table Age_Range_Unit
(
	id integer,
	caption varchar(64),
	abbrev varchar(32)
);
create uniqueindex Age_Range_Unit_abbrev_unq on Age_Range_Unit(abbrev);
alter table Age_Range_Unit modify (id constraint Age_Range_Unit_id_REQ NOT NULL);
alter table Age_Range_Unit modify (caption constraint Age_Range_Unit_caption_REQ NOT NULL);
alter table Age_Range_Unit add (constraint Age_Range_Unit_PK PRIMARY KEY (id));
create sequence Gift_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Gift
(
	created_stamp date DEFAULT sysdate,
	modified_stamp date DEFAULT sysdate,
	id integer DEFAULT 0,
	active boolean DEFAULT 0,
	name varchar(32),
	category_id integer,
	status_id integer,
	user_id ,
	picture_path ,
	url ,
	decription ,
	age_range ,
	price currency,
	brand varchar(32),
	reference varchar(32)
);
alter table Gift modify (name constraint Gift_name_REQ NOT NULL);
alter table Gift modify (category_id constraint Gift_category_id_REQ NOT NULL);
alter table Gift modify (status_id constraint Gift_status_id_REQ NOT NULL);
alter table Gift add (constraint Gift_PK PRIMARY KEY (id));
create sequence User_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table User
(
	created_stamp date DEFAULT sysdate,
	modified_stamp date DEFAULT sysdate,
	id integer DEFAULT 0,
	active boolean DEFAULT 0,
	name ,
	relationship_id integer,
	email_addr varchar(32),
	phone varchar(32)
);
alter table User modify (relationship_id constraint User_relationship_id_REQ NOT NULL);
alter table User add (constraint User_PK PRIMARY KEY (id));


create table Baby_data
(
	name varchar(32),
	value varchar(32)
);
alter table Baby_data modify (name constraint Baby_data_name_REQ NOT NULL);
alter table Baby_data modify (value constraint Baby_data_value_REQ NOT NULL);
alter table Status add (constraint Status_result_FK FOREIGN KEY (result) REFERENCES Lookup_Result_Type(id));
alter table Category add (constraint Category_result_FK FOREIGN KEY (result) REFERENCES Lookup_Result_Type(id));
alter table Relationship add (constraint Relationship_result_FK FOREIGN KEY (result) REFERENCES Lookup_Result_Type(id));
alter table Gift add (constraint Gift_category_id_FK FOREIGN KEY (category_id) REFERENCES Category(id));
alter table Gift add (constraint Gift_status_id_FK FOREIGN KEY (status_id) REFERENCES Status(id));
alter table User add (constraint User_relationship_id_FK FOREIGN KEY (relationship_id) REFERENCES Relationship(id));
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
