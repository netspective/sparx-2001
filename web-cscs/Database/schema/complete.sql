

create table Record_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index Record_Status_abbrev_unq on Record_Status(abbrev);
alter table Record_Status modify (id constraint Record_Status_id_REQ NOT NULL);
alter table Record_Status modify (caption constraint Record_Status_caption_REQ NOT NULL);
alter table Record_Status add (constraint Record_Status_PK PRIMARY KEY (id));


create table Lookup_Result_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index LkResTy_abbrev_unq on Lookup_Result_Type(abbrev);
alter table Lookup_Result_Type modify (id constraint LkResTy_id_REQ NOT NULL);
alter table Lookup_Result_Type modify (caption constraint LkResTy_caption_REQ NOT NULL);
alter table Lookup_Result_Type add (constraint Lookup_Result_Type_PK PRIMARY KEY (id));


create table Contact_Method_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index CntMthTy_abbrev_unq on Contact_Method_Type(abbrev);
alter table Contact_Method_Type modify (id constraint CntMthTy_id_REQ NOT NULL);
alter table Contact_Method_Type modify (caption constraint CntMthTy_caption_REQ NOT NULL);
alter table Contact_Method_Type add (constraint Contact_Method_Type_PK PRIMARY KEY (id));


create table Contact_Address_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index CntAdrTy_abbrev_unq on Contact_Address_Type(abbrev);
alter table Contact_Address_Type modify (id constraint CntAdrTy_id_REQ NOT NULL);
alter table Contact_Address_Type modify (caption constraint CntAdrTy_caption_REQ NOT NULL);
alter table Contact_Address_Type add (constraint Contact_Address_Type_PK PRIMARY KEY (id));


create table Contact_Telephone_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index CntTelTy_abbrev_unq on Contact_Telephone_Type(abbrev);
alter table Contact_Telephone_Type modify (id constraint CntTelTy_id_REQ NOT NULL);
alter table Contact_Telephone_Type modify (caption constraint CntTelTy_caption_REQ NOT NULL);
alter table Contact_Telephone_Type add (constraint Contact_Telephone_Type_PK PRIMARY KEY (id));


create table Contact_Email_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index CntEMTy_abbrev_unq on Contact_Email_Type(abbrev);
alter table Contact_Email_Type modify (id constraint CntEMTy_id_REQ NOT NULL);
alter table Contact_Email_Type modify (caption constraint CntEMTy_caption_REQ NOT NULL);
alter table Contact_Email_Type add (constraint Contact_Email_Type_PK PRIMARY KEY (id));


create table Project_Org_Rel_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PrjORTy_abbrev_unq on Project_Org_Rel_Type(abbrev);
alter table Project_Org_Rel_Type modify (id constraint PrjORTy_id_REQ NOT NULL);
alter table Project_Org_Rel_Type modify (caption constraint PrjORTy_caption_REQ NOT NULL);
alter table Project_Org_Rel_Type add (constraint Project_Org_Rel_Type_PK PRIMARY KEY (id));


create table Project_Person_Rel_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PrjPRTy_abbrev_unq on Project_Person_Rel_Type(abbrev);
alter table Project_Person_Rel_Type modify (id constraint PrjPRTy_id_REQ NOT NULL);
alter table Project_Person_Rel_Type modify (caption constraint PrjPRTy_caption_REQ NOT NULL);
alter table Project_Person_Rel_Type add (constraint Project_Person_Rel_Type_PK PRIMARY KEY (id));


create table Project_Event_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PrjETy_abbrev_unq on Project_Event_Type(abbrev);
alter table Project_Event_Type modify (id constraint PrjETy_id_REQ NOT NULL);
alter table Project_Event_Type modify (caption constraint PrjETy_caption_REQ NOT NULL);
alter table Project_Event_Type add (constraint Project_Event_Type_PK PRIMARY KEY (id));


create table Project_Event_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PrjESt_abbrev_unq on Project_Event_Status(abbrev);
alter table Project_Event_Status modify (id constraint PrjESt_id_REQ NOT NULL);
alter table Project_Event_Status modify (caption constraint PrjESt_caption_REQ NOT NULL);
alter table Project_Event_Status add (constraint Project_Event_Status_PK PRIMARY KEY (id));


create table Person_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PerType_abbrev_unq on Person_Type(abbrev);
alter table Person_Type modify (id constraint PerType_id_REQ NOT NULL);
alter table Person_Type modify (caption constraint PerType_caption_REQ NOT NULL);
alter table Person_Type add (constraint Person_Type_PK PRIMARY KEY (id));


create table Drug_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index Drug_abbrev_unq on Drug_Type(abbrev);
alter table Drug_Type modify (id constraint Drug_id_REQ NOT NULL);
alter table Drug_Type modify (caption constraint Drug_caption_REQ NOT NULL);
alter table Drug_Type add (constraint Drug_Type_PK PRIMARY KEY (id));


create table Dosage_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index Dose_abbrev_unq on Dosage_Type(abbrev);
alter table Dosage_Type modify (id constraint Dose_id_REQ NOT NULL);
alter table Dosage_Type modify (caption constraint Dose_caption_REQ NOT NULL);
alter table Dosage_Type add (constraint Dosage_Type_PK PRIMARY KEY (id));


create table Quantity_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index Quantity_abbrev_unq on Quantity_Type(abbrev);
alter table Quantity_Type modify (id constraint Quantity_id_REQ NOT NULL);
alter table Quantity_Type modify (caption constraint Quantity_caption_REQ NOT NULL);
alter table Quantity_Type add (constraint Quantity_Type_PK PRIMARY KEY (id));


create table Drug_Quota
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	record_status_id number(8) DEFAULT 0,
	drug number(8) DEFAULT 0,
	max_prescription number(8)
);
create sequence Per_person_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Person
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	record_status_id number(8) DEFAULT 0,
	person_id number(16),
	name_first varchar(32),
	name_last varchar(32),
	simple_name varchar(96),
	ssn varchar(11),
	date_of_birth date,
	person_type number(8) DEFAULT 0,
	address varchar(256),
	city varchar(128),
	state varchar(128),
	zip varchar(128),
	dea_number number(8)
);
create index Per_ssn on Person(ssn);
alter table Person modify (person_id constraint Per_person_id_REQ NOT NULL);
alter table Person modify (name_first constraint Per_name_first_REQ NOT NULL);
alter table Person modify (name_last constraint Per_name_last_REQ NOT NULL);
alter table Person add (constraint Person_PK PRIMARY KEY (person_id));
create sequence PerLg_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Person_Login
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	person_id number(16),
	user_id varchar(32),
	password varchar(16),
	quantity number(8) DEFAULT 1
);
create index PerLg_person_id on Person_Login(person_id);
create unique index PerLg_user_id_unq on Person_Login(user_id);
alter table Person_Login modify (system_id constraint PerLg_system_id_REQ NOT NULL);
alter table Person_Login add (constraint Person_Login_PK PRIMARY KEY (system_id));
create sequence Prescr_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Prescription
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	person_id number(16),
	drug number(8) DEFAULT 0,
	dosage number(8) DEFAULT 10,
	quantity number(8) DEFAULT 10,
	instructions varchar(255),
	refills number(8),
	genallowed number(1)
);
create index Prescr_person_id on Prescription(person_id);
alter table Prescription modify (system_id constraint Prescr_system_id_REQ NOT NULL);
alter table Prescription modify (person_id constraint Prescr_person_id_REQ NOT NULL);
alter table Prescription add (constraint Prescription_PK PRIMARY KEY (system_id));
alter table Drug_Quota add (constraint Drug_Quota_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Drug_Quota add (constraint Drug_Quota_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Drug_Quota add (constraint Drug_Quota_drug_FK FOREIGN KEY (drug) REFERENCES Drug_Type(id));
alter table Person add (constraint Per_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Person add (constraint Per_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Person add (constraint Per_person_type_FK FOREIGN KEY (person_type) REFERENCES Person_Type(id));
alter table Person_Login add (constraint PerLg_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Person_Login add (constraint PerLg_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Person_Login add (constraint PerLg_person_id_FK FOREIGN KEY (person_id) REFERENCES Person(person_id));
alter table Prescription add (constraint Prescr_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Prescription add (constraint Prescr_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Prescription add (constraint Prescr_person_id_FK FOREIGN KEY (person_id) REFERENCES Person(person_id));
alter table Prescription add (constraint Prescr_drug_FK FOREIGN KEY (drug) REFERENCES Drug_Type(id));
alter table Prescription add (constraint Prescr_dosage_FK FOREIGN KEY (dosage) REFERENCES Dosage_Type(id));
alter table Prescription add (constraint Prescr_quantity_FK FOREIGN KEY (quantity) REFERENCES Quantity_Type(id));
insert into Record_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Record_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Record_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Lookup_Result_Type(id, caption) values (0, 'ID');
insert into Lookup_Result_Type(id, caption) values (1, 'Caption');
insert into Lookup_Result_Type(id, caption) values (2, 'Abbreviation');
insert into Contact_Method_Type(id, caption) values (0, 'Physical Address');
insert into Contact_Method_Type(id, caption) values (1, 'Telephone/Fax as text (stored in method_value only)');
insert into Contact_Method_Type(id, caption) values (2, 'Telephone/Fax as text+numbers (stored in method_value and phone_* columns)');
insert into Contact_Method_Type(id, caption) values (3, 'E-mail');
insert into Contact_Method_Type(id, caption) values (4, 'URL');
insert into Contact_Address_Type(id, caption) values (0, 'Business');
insert into Contact_Address_Type(id, caption) values (1, 'Home');
insert into Contact_Address_Type(id, caption) values (2, 'Other');
insert into Contact_Telephone_Type(id, caption) values (0, 'Assistant');
insert into Contact_Telephone_Type(id, caption) values (1, 'Business');
insert into Contact_Telephone_Type(id, caption) values (2, 'Business 2');
insert into Contact_Telephone_Type(id, caption) values (3, 'Business Fax');
insert into Contact_Telephone_Type(id, caption) values (4, 'Callback');
insert into Contact_Telephone_Type(id, caption) values (5, 'Car');
insert into Contact_Telephone_Type(id, caption) values (6, 'Company');
insert into Contact_Telephone_Type(id, caption) values (7, 'Home');
insert into Contact_Telephone_Type(id, caption) values (8, 'Home 2');
insert into Contact_Telephone_Type(id, caption) values (9, 'Home Fax');
insert into Contact_Telephone_Type(id, caption) values (10, 'ISDN');
insert into Contact_Telephone_Type(id, caption) values (11, 'Mobile');
insert into Contact_Telephone_Type(id, caption) values (12, 'Other');
insert into Contact_Telephone_Type(id, caption) values (13, 'Other Fax');
insert into Contact_Telephone_Type(id, caption) values (14, 'Pager');
insert into Contact_Telephone_Type(id, caption) values (15, 'Primary');
insert into Contact_Telephone_Type(id, caption) values (16, 'Radio');
insert into Contact_Telephone_Type(id, caption) values (17, 'Telex');
insert into Contact_Telephone_Type(id, caption) values (18, 'TTY/TDD');
insert into Contact_Email_Type(id, caption) values (0, 'Business');
insert into Contact_Email_Type(id, caption) values (1, 'Home');
insert into Contact_Email_Type(id, caption) values (2, 'Other');
insert into Contact_Email_Type(id, caption) values (3, 'Primary');
insert into Project_Event_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Project_Event_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Project_Event_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Person_Type(id, caption) values (0, 'Admin');
insert into Person_Type(id, caption) values (1, 'Physician');
insert into Person_Type(id, caption) values (2, 'Patient');
insert into Drug_Type(id, caption) values (0, 'Codeine');
insert into Drug_Type(id, caption) values (1, 'Percoset');
insert into Drug_Type(id, caption) values (2, 'Xanax');
insert into Drug_Type(id, caption) values (3, 'Lithium');
insert into Drug_Type(id, caption) values (4, 'Cipro');
insert into Drug_Type(id, caption) values (5, 'Keflex');
insert into Dosage_Type(id, caption) values (50, 'tab 50 mg');
insert into Dosage_Type(id, caption) values (100, 'tab 100 mg');
insert into Dosage_Type(id, caption) values (200, 'tab 200 mg');
insert into Quantity_Type(id, caption) values (10, 'tablets 10');
insert into Quantity_Type(id, caption) values (20, 'tablets 20');
insert into Quantity_Type(id, caption) values (30, 'tablets 30');
