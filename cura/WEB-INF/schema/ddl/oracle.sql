

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


create table Gender
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index Gender_abbrev_unq on Gender(abbrev);
alter table Gender modify (id constraint Gender_id_REQ NOT NULL);
alter table Gender modify (caption constraint Gender_caption_REQ NOT NULL);
alter table Gender add (constraint Gender_PK PRIMARY KEY (id));


create table Marital_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index MarStat_abbrev_unq on Marital_Status(abbrev);
alter table Marital_Status modify (id constraint MarStat_id_REQ NOT NULL);
alter table Marital_Status modify (caption constraint MarStat_caption_REQ NOT NULL);
alter table Marital_Status add (constraint Marital_Status_PK PRIMARY KEY (id));
create sequence Per_person_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Person
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	person_id number(16),
	name_prefix varchar(16),
	name_first varchar(32),
	name_middle varchar(32),
	name_last varchar(32),
	name_suffix varchar(16),
	short_name varchar(42),
	simple_name varchar(96),
	complete_name varchar(128),
	short_sortable_name varchar(42),
	complete_sortable_name varchar(128),
	ssn varchar(11),
	gender number(8) DEFAULT 0,
	marital_status number(8) DEFAULT 0,
	date_of_birth date,
	age number(8)
);
create index Per_ssn on Person(ssn);
alter table Person modify (person_id constraint Per_person_id_REQ NOT NULL);
alter table Person modify (name_first constraint Per_name_first_REQ NOT NULL);
alter table Person modify (name_last constraint Per_name_last_REQ NOT NULL);
alter table Person add (constraint Person_PK PRIMARY KEY (person_id));


create table Person_Event_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PerETy_abbrev_unq on Person_Event_Type(abbrev);
alter table Person_Event_Type modify (id constraint PerETy_id_REQ NOT NULL);
alter table Person_Event_Type modify (caption constraint PerETy_caption_REQ NOT NULL);
alter table Person_Event_Type add (constraint Person_Event_Type_PK PRIMARY KEY (id));


create table Person_Event_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PerESt_abbrev_unq on Person_Event_Status(abbrev);
alter table Person_Event_Status modify (id constraint PerESt_id_REQ NOT NULL);
alter table Person_Event_Status modify (caption constraint PerESt_caption_REQ NOT NULL);
alter table Person_Event_Status add (constraint Person_Event_Status_PK PRIMARY KEY (id));
create sequence PerEvt_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Person_Event
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	parent_event_id number(16),
	event_type number(8),
	event_name varchar(128),
	event_descr varchar(4000),
	event_begin date,
	event_end date
);
alter table Person_Event modify (system_id constraint PerEvt_system_id_REQ NOT NULL);
alter table Person_Event add (constraint Person_Event_PK PRIMARY KEY (system_id));


create table Person_Flag_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PerFlTy_abbrev_unq on Person_Flag_Type(abbrev);
alter table Person_Flag_Type modify (id constraint PerFlTy_id_REQ NOT NULL);
alter table Person_Flag_Type modify (caption constraint PerFlTy_caption_REQ NOT NULL);
alter table Person_Flag_Type add (constraint Person_Flag_Type_PK PRIMARY KEY (id));


create table Person_Flag_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PerFlSt_abbrev_unq on Person_Flag_Status(abbrev);
alter table Person_Flag_Status modify (id constraint PerFlSt_id_REQ NOT NULL);
alter table Person_Flag_Status modify (caption constraint PerFlSt_caption_REQ NOT NULL);
alter table Person_Flag_Status add (constraint Person_Flag_Status_PK PRIMARY KEY (id));
create sequence PerFlg_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Person_Flag
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	flag number(8)
);
alter table Person_Flag modify (system_id constraint PerFlg_system_id_REQ NOT NULL);
alter table Person_Flag add (constraint Person_Flag_PK PRIMARY KEY (system_id));


create table Person_Identifier_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PerIDTy_abbrev_unq on Person_Identifier_Type(abbrev);
alter table Person_Identifier_Type modify (id constraint PerIDTy_id_REQ NOT NULL);
alter table Person_Identifier_Type modify (caption constraint PerIDTy_caption_REQ NOT NULL);
alter table Person_Identifier_Type add (constraint Person_Identifier_Type_PK PRIMARY KEY (id));
create sequence PerID_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Person_Identifier
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	person_id number(16),
	org_id number(16),
	id_type number(8),
	identifier varchar(64)
);
alter table Person_Identifier modify (system_id constraint PerID_system_id_REQ NOT NULL);
alter table Person_Identifier add (constraint Person_Identifier_PK PRIMARY KEY (system_id));


create table Person_Login_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PerLgSt_abbrev_unq on Person_Login_Status(abbrev);
alter table Person_Login_Status modify (id constraint PerLgSt_id_REQ NOT NULL);
alter table Person_Login_Status modify (caption constraint PerLgSt_caption_REQ NOT NULL);
alter table Person_Login_Status add (constraint Person_Login_Status_PK PRIMARY KEY (id));
create sequence PerLg_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Person_Login
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	person_id number(16),
	user_id varchar(32),
	password varchar(16),
	login_status number(8),
	quantity number(8) DEFAULT 1
);
create index PerLg_person_id on Person_Login(person_id);
create unique index PerLg_user_id_unq on Person_Login(user_id);
alter table Person_Login modify (system_id constraint PerLg_system_id_REQ NOT NULL);
alter table Person_Login modify (login_status constraint PerLg_login_status_REQ NOT NULL);
alter table Person_Login add (constraint Person_Login_PK PRIMARY KEY (system_id));


create table Person_Role_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PerRlTy_abbrev_unq on Person_Role_Type(abbrev);
alter table Person_Role_Type modify (id constraint PerRlTy_id_REQ NOT NULL);
alter table Person_Role_Type modify (caption constraint PerRlTy_caption_REQ NOT NULL);
alter table Person_Role_Type add (constraint Person_Role_Type_PK PRIMARY KEY (id));
create sequence PerRlNm_role_name_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Person_Role_Name
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	role_name_id number(16),
	role_type_id number(8),
	role_name varchar(255)
);
create unique index PerRlNm_Person_Role_Name_unq on Person_Role_Name(role_type_id,role_name);
create index PerRlNm_role_type_id on Person_Role_Name(role_type_id);
alter table Person_Role_Name modify (role_name_id constraint PerRlNm_role_name_id_REQ NOT NULL);
alter table Person_Role_Name modify (role_type_id constraint PerRlNm_role_type_id_REQ NOT NULL);
alter table Person_Role_Name modify (role_name constraint PerRlNm_role_name_REQ NOT NULL);
alter table Person_Role_Name add (constraint Person_Role_Name_PK PRIMARY KEY (role_name_id));


create table Person_Role_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PerRlSt_abbrev_unq on Person_Role_Status(abbrev);
alter table Person_Role_Status modify (id constraint PerRlSt_id_REQ NOT NULL);
alter table Person_Role_Status modify (caption constraint PerRlSt_caption_REQ NOT NULL);
alter table Person_Role_Status add (constraint Person_Role_Status_PK PRIMARY KEY (id));
create sequence PerRl_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Person_Role
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	person_id number(16),
	role_type_id number(8),
	role_name_id number(16)
);
create index PerRl_person_id on Person_Role(person_id);
create index PerRl_role_type_id on Person_Role(role_type_id);
create index PerRl_role_name_id on Person_Role(role_name_id);
alter table Person_Role modify (system_id constraint PerRl_system_id_REQ NOT NULL);
alter table Person_Role modify (person_id constraint PerRl_person_id_REQ NOT NULL);
alter table Person_Role modify (role_type_id constraint PerRl_role_type_id_REQ NOT NULL);
alter table Person_Role modify (role_name_id constraint PerRl_role_name_id_REQ NOT NULL);
alter table Person_Role add (constraint Person_Role_PK PRIMARY KEY (system_id));


create table Person_Relationship_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PerRelTy_abbrev_unq on Person_Relationship_Type(abbrev);
alter table Person_Relationship_Type modify (id constraint PerRelTy_id_REQ NOT NULL);
alter table Person_Relationship_Type modify (caption constraint PerRelTy_caption_REQ NOT NULL);
alter table Person_Relationship_Type add (constraint Person_Relationship_Type_PK PRIMARY KEY (id));


create table Person_Relationship_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PerRelSt_abbrev_unq on Person_Relationship_Status(abbrev);
alter table Person_Relationship_Status modify (id constraint PerRelSt_id_REQ NOT NULL);
alter table Person_Relationship_Status modify (caption constraint PerRelSt_caption_REQ NOT NULL);
alter table Person_Relationship_Status add (constraint Person_Relationship_Status_PK PRIMARY KEY (id));
create sequence PerRel_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Person_Relationship
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	rel_type number(8),
	rel_begin date,
	rel_end date,
	rel_descr varchar(1024),
	rel_person_id number(16)
);
alter table Person_Relationship modify (system_id constraint PerRel_system_id_REQ NOT NULL);
alter table Person_Relationship modify (rel_type constraint PerRel_rel_type_REQ NOT NULL);
alter table Person_Relationship modify (rel_person_id constraint PerRel_rel_person_id_REQ NOT NULL);
alter table Person_Relationship add (constraint Person_Relationship_PK PRIMARY KEY (system_id));


create table PersonOrg_Rel_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PeORelTy_abbrev_unq on PersonOrg_Rel_Type(abbrev);
alter table PersonOrg_Rel_Type modify (id constraint PeORelTy_id_REQ NOT NULL);
alter table PersonOrg_Rel_Type modify (caption constraint PeORelTy_caption_REQ NOT NULL);
alter table PersonOrg_Rel_Type add (constraint PersonOrg_Rel_Type_PK PRIMARY KEY (id));


create table PersonOrg_Rel_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PeORelSt_abbrev_unq on PersonOrg_Rel_Status(abbrev);
alter table PersonOrg_Rel_Status modify (id constraint PeORelSt_id_REQ NOT NULL);
alter table PersonOrg_Rel_Status modify (caption constraint PeORelSt_caption_REQ NOT NULL);
alter table PersonOrg_Rel_Status add (constraint PersonOrg_Rel_Status_PK PRIMARY KEY (id));
create sequence PeORel_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table PersonOrg_Relationship
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	rel_type number(8),
	rel_begin date,
	rel_end date,
	rel_descr varchar(1024),
	rel_org_id number(16)
);
alter table PersonOrg_Relationship modify (system_id constraint PeORel_system_id_REQ NOT NULL);
alter table PersonOrg_Relationship modify (rel_type constraint PeORel_rel_type_REQ NOT NULL);
alter table PersonOrg_Relationship modify (rel_org_id constraint PeORel_rel_org_id_REQ NOT NULL);
alter table PersonOrg_Relationship add (constraint PersonOrg_Relationship_PK PRIMARY KEY (system_id));
create sequence PerAddr_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Person_Address
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	address_name varchar(128),
	mailing number(1),
	line1 varchar(256),
	line2 varchar(256),
	city varchar(128),
	county varchar(128),
	state varchar(128),
	zip varchar(128),
	country varchar(128)
);
create unique index PerAddr_PerAddr_unq on Person_Address(parent_id,address_name);
create index PerAddr_parent_id on Person_Address(parent_id);
create index PerAddr_address_name on Person_Address(address_name);
alter table Person_Address modify (system_id constraint PerAddr_system_id_REQ NOT NULL);
alter table Person_Address add (constraint Person_Address_PK PRIMARY KEY (system_id));
create sequence PerCont_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Person_Contact
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	method_type number(8),
	method_name varchar(128),
	method_value varchar(255),
	phone_cc varchar(16) DEFAULT 1,
	phone_ac number(8),
	phone_prefix number(8),
	phone_suffix number(8)
);
create unique index PerCont_PerCont_unq on Person_Contact(parent_id,method_name);
create index PerCont_parent_id on Person_Contact(parent_id);
create index PerCont_method_type on Person_Contact(method_type);
create index PerCont_method_name on Person_Contact(method_name);
create index PerCont_method_value on Person_Contact(method_value);
create index PerCont_phone_cc on Person_Contact(phone_cc);
create index PerCont_phone_ac on Person_Contact(phone_ac);
create index PerCont_phone_prefix on Person_Contact(phone_prefix);
create index PerCont_phone_suffix on Person_Contact(phone_suffix);
alter table Person_Contact modify (system_id constraint PerCont_system_id_REQ NOT NULL);
alter table Person_Contact add (constraint Person_Contact_PK PRIMARY KEY (system_id));


create table Org_Ownership
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index OOwnE_abbrev_unq on Org_Ownership(abbrev);
alter table Org_Ownership modify (id constraint OOwnE_id_REQ NOT NULL);
alter table Org_Ownership modify (caption constraint OOwnE_caption_REQ NOT NULL);
alter table Org_Ownership add (constraint Org_Ownership_PK PRIMARY KEY (id));


create table Org_Type_Enum
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index OTypE_abbrev_unq on Org_Type_Enum(abbrev);
alter table Org_Type_Enum modify (id constraint OTypE_id_REQ NOT NULL);
alter table Org_Type_Enum modify (caption constraint OTypE_caption_REQ NOT NULL);
alter table Org_Type_Enum add (constraint Org_Type_Enum_PK PRIMARY KEY (id));


create table Org_Industry_Enum
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index OIndE_abbrev_unq on Org_Industry_Enum(abbrev);
alter table Org_Industry_Enum modify (id constraint OIndE_id_REQ NOT NULL);
alter table Org_Industry_Enum modify (caption constraint OIndE_caption_REQ NOT NULL);
alter table Org_Industry_Enum add (constraint Org_Industry_Enum_PK PRIMARY KEY (id));
create sequence Org_org_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Org
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	org_id number(16),
	org_code varchar(64),
	org_name varchar(128),
	org_abbrev varchar(24),
	ownership number(8),
	ticker_symbol varchar(24),
	sic_code varchar(24),
	employees number(8),
	time_zone varchar(10)
);
alter table Org modify (org_id constraint Org_org_id_REQ NOT NULL);
alter table Org modify (org_code constraint Org_org_code_REQ NOT NULL);
alter table Org modify (org_name constraint Org_org_name_REQ NOT NULL);
alter table Org add (constraint Org_PK PRIMARY KEY (org_id));
create sequence OTyp_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Org_Type
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	org_id number(16),
	org_type number(8)
);
alter table Org_Type modify (system_id constraint OTyp_system_id_REQ NOT NULL);
alter table Org_Type add (constraint Org_Type_PK PRIMARY KEY (system_id));
create sequence OInd_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Org_Industry
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	org_id number(16),
	org_industry number(8)
);
alter table Org_Industry modify (system_id constraint OInd_system_id_REQ NOT NULL);
alter table Org_Industry add (constraint Org_Industry_PK PRIMARY KEY (system_id));


create table Org_Identifier_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index OIdTy_abbrev_unq on Org_Identifier_Type(abbrev);
alter table Org_Identifier_Type modify (id constraint OIdTy_id_REQ NOT NULL);
alter table Org_Identifier_Type modify (caption constraint OIdTy_caption_REQ NOT NULL);
alter table Org_Identifier_Type add (constraint Org_Identifier_Type_PK PRIMARY KEY (id));
create sequence OrgID_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Org_Identifier
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	org_id number(16),
	id_type number(8),
	id varchar(32)
);
alter table Org_Identifier modify (system_id constraint OrgID_system_id_REQ NOT NULL);
alter table Org_Identifier add (constraint Org_Identifier_PK PRIMARY KEY (system_id));


create table Org_Relationship_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index ORelTy_abbrev_unq on Org_Relationship_Type(abbrev);
alter table Org_Relationship_Type modify (id constraint ORelTy_id_REQ NOT NULL);
alter table Org_Relationship_Type modify (caption constraint ORelTy_caption_REQ NOT NULL);
alter table Org_Relationship_Type add (constraint Org_Relationship_Type_PK PRIMARY KEY (id));


create table Org_Relationship_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index ORelSt_abbrev_unq on Org_Relationship_Status(abbrev);
alter table Org_Relationship_Status modify (id constraint ORelSt_id_REQ NOT NULL);
alter table Org_Relationship_Status modify (caption constraint ORelSt_caption_REQ NOT NULL);
alter table Org_Relationship_Status add (constraint Org_Relationship_Status_PK PRIMARY KEY (id));
create sequence OrgRel_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Org_Relationship
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	rel_type number(8),
	rel_begin date,
	rel_end date,
	rel_descr varchar(1024),
	rel_org_id number(16)
);
alter table Org_Relationship modify (system_id constraint OrgRel_system_id_REQ NOT NULL);
alter table Org_Relationship modify (rel_type constraint OrgRel_rel_type_REQ NOT NULL);
alter table Org_Relationship modify (rel_org_id constraint OrgRel_rel_org_id_REQ NOT NULL);
alter table Org_Relationship add (constraint Org_Relationship_PK PRIMARY KEY (system_id));
create sequence OrgAdr_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Org_Address
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	address_name varchar(128),
	mailing number(1),
	line1 varchar(256),
	line2 varchar(256),
	city varchar(128),
	county varchar(128),
	state varchar(128),
	zip varchar(128),
	country varchar(128)
);
create unique index OrgAdr_OrgAdr_unq on Org_Address(parent_id,address_name);
create index OrgAdr_parent_id on Org_Address(parent_id);
create index OrgAdr_address_name on Org_Address(address_name);
alter table Org_Address modify (system_id constraint OrgAdr_system_id_REQ NOT NULL);
alter table Org_Address add (constraint Org_Address_PK PRIMARY KEY (system_id));
create sequence OrgCnt_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Org_Contact
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	method_type number(8),
	method_name varchar(128),
	method_value varchar(255),
	phone_cc varchar(16) DEFAULT 1,
	phone_ac number(8),
	phone_prefix number(8),
	phone_suffix number(8)
);
create unique index OrgCnt_OrgCnt_unq on Org_Contact(parent_id,method_name);
create index OrgCnt_parent_id on Org_Contact(parent_id);
create index OrgCnt_method_type on Org_Contact(method_type);
create index OrgCnt_method_name on Org_Contact(method_name);
create index OrgCnt_method_value on Org_Contact(method_value);
create index OrgCnt_phone_cc on Org_Contact(phone_cc);
create index OrgCnt_phone_ac on Org_Contact(phone_ac);
create index OrgCnt_phone_prefix on Org_Contact(phone_prefix);
create index OrgCnt_phone_suffix on Org_Contact(phone_suffix);
alter table Org_Contact modify (system_id constraint OrgCnt_system_id_REQ NOT NULL);
alter table Org_Contact add (constraint Org_Contact_PK PRIMARY KEY (system_id));


create table Task_Relationship_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskRelTy_abbrev_unq on Task_Relationship_Type(abbrev);
alter table Task_Relationship_Type modify (id constraint TskRelTy_id_REQ NOT NULL);
alter table Task_Relationship_Type modify (caption constraint TskRelTy_caption_REQ NOT NULL);
alter table Task_Relationship_Type add (constraint Task_Relationship_Type_PK PRIMARY KEY (id));
create sequence Task_task_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Task
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	task_id number(16),
	owner_project_id number(16),
	owner_org_id number(16),
	owner_person_id number(16),
	task_type number(8),
	task_summary varchar(512),
	parent_task_id number(16),
	priority_id number(8),
	impact_id number(8),
	task_status number(8),
	task_resolution number(8),
	task_descr varchar(4000),
	start_date date,
	end_date date
);
alter table Task modify (task_id constraint Task_task_id_REQ NOT NULL);
alter table Task add (constraint Task_PK PRIMARY KEY (task_id));


create table Task_Priority
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskPrt_abbrev_unq on Task_Priority(abbrev);
alter table Task_Priority modify (id constraint TskPrt_id_REQ NOT NULL);
alter table Task_Priority modify (caption constraint TskPrt_caption_REQ NOT NULL);
alter table Task_Priority add (constraint Task_Priority_PK PRIMARY KEY (id));
create sequence TskPerRel_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table TaskPerson_Relation
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	rel_type number(8),
	rel_begin date,
	rel_end date,
	rel_descr varchar(1024),
	rel_person_id number(16),
	notify_email varchar(32)
);
alter table TaskPerson_Relation modify (system_id constraint TskPerRel_system_id_REQ NOT NULL);
alter table TaskPerson_Relation modify (rel_type constraint TskPerRel_rel_type_REQ NOT NULL);
alter table TaskPerson_Relation modify (rel_person_id constraint TskPerRel_rel_person_id_REQ NOT NULL);
alter table TaskPerson_Relation add (constraint TaskPerson_Relation_PK PRIMARY KEY (system_id));


create table TaskPerson_Relation_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskPerRelTy_abbrev_unq on TaskPerson_Relation_Type(abbrev);
alter table TaskPerson_Relation_Type modify (id constraint TskPerRelTy_id_REQ NOT NULL);
alter table TaskPerson_Relation_Type modify (caption constraint TskPerRelTy_caption_REQ NOT NULL);
alter table TaskPerson_Relation_Type add (constraint TaskPerson_Relation_Type_PK PRIMARY KEY (id));


create table Task_Relationship_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskRelSt_abbrev_unq on Task_Relationship_Status(abbrev);
alter table Task_Relationship_Status modify (id constraint TskRelSt_id_REQ NOT NULL);
alter table Task_Relationship_Status modify (caption constraint TskRelSt_caption_REQ NOT NULL);
alter table Task_Relationship_Status add (constraint Task_Relationship_Status_PK PRIMARY KEY (id));
create sequence TskDep_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Task_Dependency
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	dependency_type number(8),
	parent_id number(16),
	dependent_id number(16)
);
alter table Task_Dependency modify (system_id constraint TskDep_system_id_REQ NOT NULL);
alter table Task_Dependency add (constraint Task_Dependency_PK PRIMARY KEY (system_id));


create table Task_Dependency_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskDepTy_abbrev_unq on Task_Dependency_Type(abbrev);
alter table Task_Dependency_Type modify (id constraint TskDepTy_id_REQ NOT NULL);
alter table Task_Dependency_Type modify (caption constraint TskDepTy_caption_REQ NOT NULL);
alter table Task_Dependency_Type add (constraint Task_Dependency_Type_PK PRIMARY KEY (id));


create table Task_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskTy_abbrev_unq on Task_Type(abbrev);
alter table Task_Type modify (id constraint TskTy_id_REQ NOT NULL);
alter table Task_Type modify (caption constraint TskTy_caption_REQ NOT NULL);
alter table Task_Type add (constraint Task_Type_PK PRIMARY KEY (id));
create sequence TskEvt_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Task_Event
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	parent_event_id number(16),
	event_type number(8),
	event_name varchar(128),
	event_descr varchar(4000),
	event_begin date,
	event_end date
);
alter table Task_Event modify (system_id constraint TskEvt_system_id_REQ NOT NULL);
alter table Task_Event add (constraint Task_Event_PK PRIMARY KEY (system_id));


create table Task_Event_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskEvtTy_abbrev_unq on Task_Event_Type(abbrev);
alter table Task_Event_Type modify (id constraint TskEvtTy_id_REQ NOT NULL);
alter table Task_Event_Type modify (caption constraint TskEvtTy_caption_REQ NOT NULL);
alter table Task_Event_Type add (constraint Task_Event_Type_PK PRIMARY KEY (id));


create table Task_Event_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskEvtSt_abbrev_unq on Task_Event_Status(abbrev);
alter table Task_Event_Status modify (id constraint TskEvtSt_id_REQ NOT NULL);
alter table Task_Event_Status modify (caption constraint TskEvtSt_caption_REQ NOT NULL);
alter table Task_Event_Status add (constraint Task_Event_Status_PK PRIMARY KEY (id));
create sequence TskFlg_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Task_Flag
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	flag number(8)
);
alter table Task_Flag modify (system_id constraint TskFlg_system_id_REQ NOT NULL);
alter table Task_Flag add (constraint Task_Flag_PK PRIMARY KEY (system_id));


create table Task_Flag_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskFlgTy_abbrev_unq on Task_Flag_Type(abbrev);
alter table Task_Flag_Type modify (id constraint TskFlgTy_id_REQ NOT NULL);
alter table Task_Flag_Type modify (caption constraint TskFlgTy_caption_REQ NOT NULL);
alter table Task_Flag_Type add (constraint Task_Flag_Type_PK PRIMARY KEY (id));


create table Task_Flag_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskFlgSt_abbrev_unq on Task_Flag_Status(abbrev);
alter table Task_Flag_Status modify (id constraint TskFlgSt_id_REQ NOT NULL);
alter table Task_Flag_Status modify (caption constraint TskFlgSt_caption_REQ NOT NULL);
alter table Task_Flag_Status add (constraint Task_Flag_Status_PK PRIMARY KEY (id));


create table Task_Impact
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskImpt_abbrev_unq on Task_Impact(abbrev);
alter table Task_Impact modify (id constraint TskImpt_id_REQ NOT NULL);
alter table Task_Impact modify (caption constraint TskImpt_caption_REQ NOT NULL);
alter table Task_Impact add (constraint Task_Impact_PK PRIMARY KEY (id));


create table Task_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskSt_abbrev_unq on Task_Status(abbrev);
alter table Task_Status modify (id constraint TskSt_id_REQ NOT NULL);
alter table Task_Status modify (caption constraint TskSt_caption_REQ NOT NULL);
alter table Task_Status add (constraint Task_Status_PK PRIMARY KEY (id));


create table Task_Resolution
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index TskRes_abbrev_unq on Task_Resolution(abbrev);
alter table Task_Resolution modify (id constraint TskRes_id_REQ NOT NULL);
alter table Task_Resolution modify (caption constraint TskRes_caption_REQ NOT NULL);
alter table Task_Resolution add (constraint Task_Resolution_PK PRIMARY KEY (id));
create sequence TskRel_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Task_Relationship
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	rel_type number(8),
	rel_begin date,
	rel_end date,
	rel_descr varchar(1024),
	rel_task_id number(16)
);
alter table Task_Relationship modify (system_id constraint TskRel_system_id_REQ NOT NULL);
alter table Task_Relationship modify (rel_type constraint TskRel_rel_type_REQ NOT NULL);
alter table Task_Relationship modify (rel_task_id constraint TskRel_rel_task_id_REQ NOT NULL);
alter table Task_Relationship add (constraint Task_Relationship_PK PRIMARY KEY (system_id));


create table Artifact_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index ArfType_abbrev_unq on Artifact_Type(abbrev);
alter table Artifact_Type modify (id constraint ArfType_id_REQ NOT NULL);
alter table Artifact_Type modify (caption constraint ArfType_caption_REQ NOT NULL);
alter table Artifact_Type add (constraint Artifact_Type_PK PRIMARY KEY (id));


create table Artifact_Event_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index ArfEvTy_abbrev_unq on Artifact_Event_Type(abbrev);
alter table Artifact_Event_Type modify (id constraint ArfEvTy_id_REQ NOT NULL);
alter table Artifact_Event_Type modify (caption constraint ArfEvTy_caption_REQ NOT NULL);
alter table Artifact_Event_Type add (constraint Artifact_Event_Type_PK PRIMARY KEY (id));


create table Artifact_Source_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index ArfSrcTy_abbrev_unq on Artifact_Source_Type(abbrev);
alter table Artifact_Source_Type modify (id constraint ArfSrcTy_id_REQ NOT NULL);
alter table Artifact_Source_Type modify (caption constraint ArfSrcTy_caption_REQ NOT NULL);
alter table Artifact_Source_Type add (constraint Artifact_Source_Type_PK PRIMARY KEY (id));


create table Artifact_Association_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index ArfAsnTy_abbrev_unq on Artifact_Association_Type(abbrev);
alter table Artifact_Association_Type modify (id constraint ArfAsnTy_id_REQ NOT NULL);
alter table Artifact_Association_Type modify (caption constraint ArfAsnTy_caption_REQ NOT NULL);
alter table Artifact_Association_Type add (constraint Artifact_Association_Type_PK PRIMARY KEY (id));


create table Artifact_Association_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index ArfAsnSt_abbrev_unq on Artifact_Association_Status(abbrev);
alter table Artifact_Association_Status modify (id constraint ArfAsnSt_id_REQ NOT NULL);
alter table Artifact_Association_Status modify (caption constraint ArfAsnSt_caption_REQ NOT NULL);
alter table Artifact_Association_Status add (constraint Artifact_Association_Status_PK PRIMARY KEY (id));
create sequence Artf_arf_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Artifact
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	arf_id number(16),
	arf_id_alias varchar(64),
	arf_message_digest varchar(32),
	arf_mime_type varchar(128),
	arf_header varchar(4000),
	arf_spec_type number(8),
	arf_spec_subtype varchar(128),
	arf_source_id varchar(255),
	arf_source_type number(8),
	arf_source_subtype varchar(255),
	arf_source_system varchar(255),
	arf_name varchar(1024),
	arf_description varchar(4000),
	arf_orig_stamp date,
	arf_recv_stamp date,
	arf_data_a varchar(1024),
	arf_data_b varchar(1024),
	arf_data_c varchar(1024),
	arf_content_uri varchar(512),
	arf_content_small varchar(4000),
	arf_content_large clob,
	arf_dest_ids varchar(1024)
);
create index Artf_arf_spec_type on Artifact(arf_spec_type);
create index Artf_arf_spec_subtype on Artifact(arf_spec_subtype);
create index Artf_arf_source_id on Artifact(arf_source_id);
create index Artf_arf_source_type on Artifact(arf_source_type);
create index Artf_arf_source_subtype on Artifact(arf_source_subtype);
create index Artf_arf_source_system on Artifact(arf_source_system);
alter table Artifact modify (arf_id constraint Artf_arf_id_REQ NOT NULL);
alter table Artifact modify (arf_spec_type constraint Artf_arf_spec_type_REQ NOT NULL);
alter table Artifact modify (arf_source_type constraint Artf_arf_source_type_REQ NOT NULL);
alter table Artifact modify (arf_name constraint Artf_arf_name_REQ NOT NULL);
alter table Artifact add (constraint Artifact_PK PRIMARY KEY (arf_id));
create sequence ArfAssn_arf_assn_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Artifact_Association
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	arf_assn_id number(16),
	assn_status number(8),
	assn_type number(8),
	assn_sequence number(8),
	arf_id number(16),
	assoc_arf_id number(16),
	person_id number(16),
	org_id number(16),
	assn_data_a varchar(1024),
	assn_data_b varchar(1024),
	assn_data_c varchar(1024)
);
create index ArfAssn_assn_status on Artifact_Association(assn_status);
create index ArfAssn_assn_type on Artifact_Association(assn_type);
create index ArfAssn_assn_sequence on Artifact_Association(assn_sequence);
create index ArfAssn_arf_id on Artifact_Association(arf_id);
create index ArfAssn_assoc_arf_id on Artifact_Association(assoc_arf_id);
create index ArfAssn_person_id on Artifact_Association(person_id);
create index ArfAssn_org_id on Artifact_Association(org_id);
alter table Artifact_Association modify (arf_assn_id constraint ArfAssn_arf_assn_id_REQ NOT NULL);
alter table Artifact_Association modify (assn_type constraint ArfAssn_assn_type_REQ NOT NULL);
alter table Artifact_Association modify (arf_id constraint ArfAssn_arf_id_REQ NOT NULL);
alter table Artifact_Association add (constraint Artifact_Association_PK PRIMARY KEY (arf_assn_id));
create sequence ArfKeyw_arf_keyword_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Artifact_Keyword
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	arf_keyword_id number(16),
	arf_id number(16),
	keyword varchar(1024),
	person_id number(16),
	org_id number(16)
);
create index ArfKeyw_keyword on Artifact_Keyword(keyword);
create index ArfKeyw_person_id on Artifact_Keyword(person_id);
create index ArfKeyw_org_id on Artifact_Keyword(org_id);
alter table Artifact_Keyword modify (arf_keyword_id constraint ArfKeyw_arf_keyword_id_REQ NOT NULL);
alter table Artifact_Keyword modify (arf_id constraint ArfKeyw_arf_id_REQ NOT NULL);
alter table Artifact_Keyword add (constraint Artifact_Keyword_PK PRIMARY KEY (arf_keyword_id));
create sequence ArfEvent_arf_event_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Artifact_Event
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	arf_event_id number(16),
	event_type number(8),
	event_status varchar(1024),
	arf_id number(16),
	related_arf_id number(16),
	person_id number(16),
	org_id number(16),
	event_info varchar(1024),
	event_info_extra varchar(1024)
);
create index ArfEvent_event_type on Artifact_Event(event_type);
create index ArfEvent_person_id on Artifact_Event(person_id);
create index ArfEvent_org_id on Artifact_Event(org_id);
alter table Artifact_Event modify (arf_event_id constraint ArfEvent_arf_event_id_REQ NOT NULL);
alter table Artifact_Event modify (event_type constraint ArfEvent_event_type_REQ NOT NULL);
alter table Artifact_Event modify (arf_id constraint ArfEvent_arf_id_REQ NOT NULL);
alter table Artifact_Event add (constraint Artifact_Event_PK PRIMARY KEY (arf_event_id));
create sequence Prj_project_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Project
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	project_id number(16),
	parent_id number(16),
	project_code varchar(64),
	project_name varchar(256),
	project_descr varchar(4000),
	project_status number(8),
	start_date date,
	target_end_date date,
	actual_end_date date
);
create unique index Prj_project_code_unq on Project(project_code);
alter table Project modify (project_id constraint Prj_project_id_REQ NOT NULL);
alter table Project add (constraint Project_PK PRIMARY KEY (project_id));


create table Project_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PrjSt_abbrev_unq on Project_Status(abbrev);
alter table Project_Status modify (id constraint PrjSt_id_REQ NOT NULL);
alter table Project_Status modify (caption constraint PrjSt_caption_REQ NOT NULL);
alter table Project_Status add (constraint Project_Status_PK PRIMARY KEY (id));


create table ProjectOrg_Relation_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PrjOrgRelTy_abbrev_unq on ProjectOrg_Relation_Type(abbrev);
alter table ProjectOrg_Relation_Type modify (id constraint PrjOrgRelTy_id_REQ NOT NULL);
alter table ProjectOrg_Relation_Type modify (caption constraint PrjOrgRelTy_caption_REQ NOT NULL);
alter table ProjectOrg_Relation_Type add (constraint ProjectOrg_Relation_Type_PK PRIMARY KEY (id));


create table ProjectPerson_Relation_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PrjPerRelTy_abbrev_unq on ProjectPerson_Relation_Type(abbrev);
alter table ProjectPerson_Relation_Type modify (id constraint PrjPerRelTy_id_REQ NOT NULL);
alter table ProjectPerson_Relation_Type modify (caption constraint PrjPerRelTy_caption_REQ NOT NULL);
alter table ProjectPerson_Relation_Type add (constraint ProjectPerson_Relation_Type_PK PRIMARY KEY (id));


create table Project_Relationship_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PrjRelSt_abbrev_unq on Project_Relationship_Status(abbrev);
alter table Project_Relationship_Status modify (id constraint PrjRelSt_id_REQ NOT NULL);
alter table Project_Relationship_Status modify (caption constraint PrjRelSt_caption_REQ NOT NULL);
alter table Project_Relationship_Status add (constraint Project_Relationship_Status_PK PRIMARY KEY (id));
create sequence PrjOrgRel_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table ProjectOrg_Relation
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	rel_type number(8),
	rel_begin date,
	rel_end date,
	rel_descr varchar(1024),
	rel_org_id number(16),
	notify_email varchar(32)
);
alter table ProjectOrg_Relation modify (system_id constraint PrjOrgRel_system_id_REQ NOT NULL);
alter table ProjectOrg_Relation modify (rel_type constraint PrjOrgRel_rel_type_REQ NOT NULL);
alter table ProjectOrg_Relation modify (rel_org_id constraint PrjOrgRel_rel_org_id_REQ NOT NULL);
alter table ProjectOrg_Relation add (constraint ProjectOrg_Relation_PK PRIMARY KEY (system_id));
create sequence PrjPerRel_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table ProjectPerson_Relation
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	rel_type number(8),
	rel_begin date,
	rel_end date,
	rel_descr varchar(1024),
	rel_person_id number(16),
	notify_email varchar(32)
);
alter table ProjectPerson_Relation modify (system_id constraint PrjPerRel_system_id_REQ NOT NULL);
alter table ProjectPerson_Relation modify (rel_type constraint PrjPerRel_rel_type_REQ NOT NULL);
alter table ProjectPerson_Relation modify (rel_person_id constraint PrjPerRel_rel_person_id_REQ NOT NULL);
alter table ProjectPerson_Relation add (constraint ProjectPerson_Relation_PK PRIMARY KEY (system_id));
create sequence PrjEvt_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Project_Event
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	parent_event_id number(16),
	event_type number(8),
	event_name varchar(128),
	event_descr varchar(4000),
	event_begin date,
	event_end date
);
alter table Project_Event modify (system_id constraint PrjEvt_system_id_REQ NOT NULL);
alter table Project_Event add (constraint Project_Event_PK PRIMARY KEY (system_id));


create table Project_Flag_Type
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PrjFlTy_abbrev_unq on Project_Flag_Type(abbrev);
alter table Project_Flag_Type modify (id constraint PrjFlTy_id_REQ NOT NULL);
alter table Project_Flag_Type modify (caption constraint PrjFlTy_caption_REQ NOT NULL);
alter table Project_Flag_Type add (constraint Project_Flag_Type_PK PRIMARY KEY (id));


create table Project_Flag_Status
(
	id number(8),
	caption varchar(96),
	abbrev varchar(32)
);
create unique index PrjFlSt_abbrev_unq on Project_Flag_Status(abbrev);
alter table Project_Flag_Status modify (id constraint PrjFlSt_id_REQ NOT NULL);
alter table Project_Flag_Status modify (caption constraint PrjFlSt_caption_REQ NOT NULL);
alter table Project_Flag_Status add (constraint Project_Flag_Status_PK PRIMARY KEY (id));
create sequence PrjFlg_system_id_SEQ increment by 1 start with 1 nomaxvalue nocache nocycle;


create table Project_Flag
(
	cr_stamp date DEFAULT sysdate,
	cr_person_id number(16),
	cr_org_id number(16),
	record_status_id number(8) DEFAULT 0,
	system_id number(16),
	parent_id number(16),
	flag number(8)
);
alter table Project_Flag modify (system_id constraint PrjFlg_system_id_REQ NOT NULL);
alter table Project_Flag add (constraint Project_Flag_PK PRIMARY KEY (system_id));
alter table Person add (constraint Per_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Person add (constraint Per_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Person add (constraint Per_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Person add (constraint Per_gender_FK FOREIGN KEY (gender) REFERENCES Gender(id));
alter table Person add (constraint Per_marital_status_FK FOREIGN KEY (marital_status) REFERENCES Marital_Status(id));
alter table Person_Event add (constraint PerEvt_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Person_Event add (constraint PerEvt_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Person_Event add (constraint PerEvt_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Person_Event_Status(id));
alter table Person_Event add (constraint PerEvt_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Person(person_id));
alter table Person_Event add (constraint PerEvt_parent_event_id_FK FOREIGN KEY (parent_event_id) REFERENCES Person_Event(system_id));
alter table Person_Event add (constraint PerEvt_event_type_FK FOREIGN KEY (event_type) REFERENCES Person_Event_Type(id));
alter table Person_Flag add (constraint PerFlg_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Person_Flag add (constraint PerFlg_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Person_Flag add (constraint PerFlg_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Person_Flag_Status(id));
alter table Person_Flag add (constraint PerFlg_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Person(person_id));
alter table Person_Flag add (constraint PerFlg_flag_FK FOREIGN KEY (flag) REFERENCES Person_Flag_Type(id));
alter table Person_Identifier add (constraint PerID_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Person_Identifier add (constraint PerID_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Person_Identifier add (constraint PerID_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Person_Identifier add (constraint PerID_person_id_FK FOREIGN KEY (person_id) REFERENCES Person(person_id));
alter table Person_Identifier add (constraint PerID_org_id_FK FOREIGN KEY (org_id) REFERENCES Org(org_id));
alter table Person_Identifier add (constraint PerID_id_type_FK FOREIGN KEY (id_type) REFERENCES Person_Identifier_Type(id));
alter table Person_Login add (constraint PerLg_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Person_Login add (constraint PerLg_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Person_Login add (constraint PerLg_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Person_Login add (constraint PerLg_person_id_FK FOREIGN KEY (person_id) REFERENCES Person(person_id));
alter table Person_Login add (constraint PerLg_login_status_FK FOREIGN KEY (login_status) REFERENCES Person_Login_Status(id));
alter table Person_Role_Name add (constraint PerRlNm_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Person_Role_Name add (constraint PerRlNm_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Person_Role_Name add (constraint PerRlNm_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Person_Role_Name add (constraint PerRlNm_role_type_id_FK FOREIGN KEY (role_type_id) REFERENCES Person_Role_Type(id));
alter table Person_Role add (constraint PerRl_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Person_Role add (constraint PerRl_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Person_Role add (constraint PerRl_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Person_Role add (constraint PerRl_person_id_FK FOREIGN KEY (person_id) REFERENCES Person(person_id));
alter table Person_Role add (constraint PerRl_role_type_id_FK FOREIGN KEY (role_type_id) REFERENCES Person_Role_Type(id));
alter table Person_Role add (constraint PerRl_role_name_id_FK FOREIGN KEY (role_name_id) REFERENCES Person_Role_Name(role_name_id));
alter table Person_Relationship add (constraint PerRel_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Person_Relationship add (constraint PerRel_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Person_Relationship add (constraint PerRel_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Person_Relationship_Status(id));
alter table Person_Relationship add (constraint PerRel_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Person(person_id));
alter table Person_Relationship add (constraint PerRel_rel_type_FK FOREIGN KEY (rel_type) REFERENCES Person_Relationship_Type(id));
alter table Person_Relationship add (constraint PerRel_rel_person_id_FK FOREIGN KEY (rel_person_id) REFERENCES Person(person_id));
alter table PersonOrg_Relationship add (constraint PeORel_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table PersonOrg_Relationship add (constraint PeORel_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table PersonOrg_Relationship add (constraint PeORel_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES PersonOrg_Rel_Status(id));
alter table PersonOrg_Relationship add (constraint PeORel_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Person(person_id));
alter table PersonOrg_Relationship add (constraint PeORel_rel_type_FK FOREIGN KEY (rel_type) REFERENCES PersonOrg_Rel_Type(id));
alter table PersonOrg_Relationship add (constraint PeORel_rel_org_id_FK FOREIGN KEY (rel_org_id) REFERENCES Org(org_id));
alter table Person_Address add (constraint PerAddr_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Person_Address add (constraint PerAddr_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Person_Address add (constraint PerAddr_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Person_Address add (constraint PerAddr_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Person(person_id));
alter table Person_Contact add (constraint PerCont_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Person_Contact add (constraint PerCont_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Person_Contact add (constraint PerCont_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Person_Contact add (constraint PerCont_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Person(person_id));
alter table Person_Contact add (constraint PerCont_method_type_FK FOREIGN KEY (method_type) REFERENCES Contact_Method_Type(id));
alter table Org add (constraint Org_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Org add (constraint Org_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Org add (constraint Org_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Org add (constraint Org_ownership_FK FOREIGN KEY (ownership) REFERENCES Org_Ownership(id));
alter table Org_Type add (constraint OTyp_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Org_Type add (constraint OTyp_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Org_Type add (constraint OTyp_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Org_Type add (constraint OTyp_org_id_FK FOREIGN KEY (org_id) REFERENCES Org(org_id));
alter table Org_Type add (constraint OTyp_org_type_FK FOREIGN KEY (org_type) REFERENCES Org_Type_Enum(id));
alter table Org_Industry add (constraint OInd_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Org_Industry add (constraint OInd_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Org_Industry add (constraint OInd_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Org_Industry add (constraint OInd_org_id_FK FOREIGN KEY (org_id) REFERENCES Org(org_id));
alter table Org_Industry add (constraint OInd_org_industry_FK FOREIGN KEY (org_industry) REFERENCES Org_Industry_Enum(id));
alter table Org_Identifier add (constraint OrgID_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Org_Identifier add (constraint OrgID_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Org_Identifier add (constraint OrgID_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Org_Identifier add (constraint OrgID_org_id_FK FOREIGN KEY (org_id) REFERENCES Org(org_id));
alter table Org_Identifier add (constraint OrgID_id_type_FK FOREIGN KEY (id_type) REFERENCES Org_Identifier_Type(id));
alter table Org_Relationship add (constraint OrgRel_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Org_Relationship add (constraint OrgRel_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Org_Relationship add (constraint OrgRel_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Org_Relationship_Status(id));
alter table Org_Relationship add (constraint OrgRel_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Org(org_id));
alter table Org_Relationship add (constraint OrgRel_rel_type_FK FOREIGN KEY (rel_type) REFERENCES Org_Relationship_Type(id));
alter table Org_Relationship add (constraint OrgRel_rel_org_id_FK FOREIGN KEY (rel_org_id) REFERENCES Org(org_id));
alter table Org_Address add (constraint OrgAdr_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Org_Address add (constraint OrgAdr_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Org_Address add (constraint OrgAdr_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Org_Address add (constraint OrgAdr_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Org(org_id));
alter table Org_Contact add (constraint OrgCnt_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Org_Contact add (constraint OrgCnt_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Org_Contact add (constraint OrgCnt_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Org_Contact add (constraint OrgCnt_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Org(org_id));
alter table Org_Contact add (constraint OrgCnt_method_type_FK FOREIGN KEY (method_type) REFERENCES Contact_Method_Type(id));
alter table Task add (constraint Task_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Task add (constraint Task_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Task add (constraint Task_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Task add (constraint Task_owner_project_id_FK FOREIGN KEY (owner_project_id) REFERENCES Project(project_id));
alter table Task add (constraint Task_owner_org_id_FK FOREIGN KEY (owner_org_id) REFERENCES Org(org_id));
alter table Task add (constraint Task_owner_person_id_FK FOREIGN KEY (owner_person_id) REFERENCES Person(person_id));
alter table Task add (constraint Task_task_type_FK FOREIGN KEY (task_type) REFERENCES Task_Type(id));
alter table Task add (constraint Task_parent_task_id_FK FOREIGN KEY (parent_task_id) REFERENCES Task(task_id));
alter table Task add (constraint Task_priority_id_FK FOREIGN KEY (priority_id) REFERENCES Task_Priority(id));
alter table Task add (constraint Task_impact_id_FK FOREIGN KEY (impact_id) REFERENCES Task_Impact(id));
alter table Task add (constraint Task_task_status_FK FOREIGN KEY (task_status) REFERENCES Task_Status(id));
alter table Task add (constraint Task_task_resolution_FK FOREIGN KEY (task_resolution) REFERENCES Task_Resolution(id));
alter table TaskPerson_Relation add (constraint TskPerRel_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table TaskPerson_Relation add (constraint TskPerRel_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table TaskPerson_Relation add (constraint TskPerRel_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Task_Relationship_Status(id));
alter table TaskPerson_Relation add (constraint TskPerRel_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Task(task_id));
alter table TaskPerson_Relation add (constraint TskPerRel_rel_type_FK FOREIGN KEY (rel_type) REFERENCES TaskPerson_Relation_Type(id));
alter table TaskPerson_Relation add (constraint TskPerRel_rel_person_id_FK FOREIGN KEY (rel_person_id) REFERENCES Person(person_id));
alter table Task_Dependency add (constraint TskDep_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Task_Dependency add (constraint TskDep_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Task_Dependency add (constraint TskDep_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Task_Dependency add (constraint TskDep_dependency_type_FK FOREIGN KEY (dependency_type) REFERENCES Task_Dependency_Type(id));
alter table Task_Dependency add (constraint TskDep_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Task(task_id));
alter table Task_Dependency add (constraint TskDep_dependent_id_FK FOREIGN KEY (dependent_id) REFERENCES Task(task_id));
alter table Task_Event add (constraint TskEvt_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Task_Event add (constraint TskEvt_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Task_Event add (constraint TskEvt_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Task_Event_Status(id));
alter table Task_Event add (constraint TskEvt_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Task(task_id));
alter table Task_Event add (constraint TskEvt_parent_event_id_FK FOREIGN KEY (parent_event_id) REFERENCES Task_Event(system_id));
alter table Task_Event add (constraint TskEvt_event_type_FK FOREIGN KEY (event_type) REFERENCES Task_Event_Type(id));
alter table Task_Flag add (constraint TskFlg_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Task_Flag add (constraint TskFlg_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Task_Flag add (constraint TskFlg_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Task_Flag_Status(id));
alter table Task_Flag add (constraint TskFlg_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Task(task_id));
alter table Task_Flag add (constraint TskFlg_flag_FK FOREIGN KEY (flag) REFERENCES Task_Flag_Type(id));
alter table Task_Relationship add (constraint TskRel_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Task_Relationship add (constraint TskRel_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Task_Relationship add (constraint TskRel_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Task_Relationship_Status(id));
alter table Task_Relationship add (constraint TskRel_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Task(task_id));
alter table Task_Relationship add (constraint TskRel_rel_type_FK FOREIGN KEY (rel_type) REFERENCES Task_Relationship_Type(id));
alter table Task_Relationship add (constraint TskRel_rel_task_id_FK FOREIGN KEY (rel_task_id) REFERENCES Task(task_id));
alter table Artifact add (constraint Artf_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Artifact add (constraint Artf_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Artifact add (constraint Artf_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Artifact add (constraint Artf_arf_spec_type_FK FOREIGN KEY (arf_spec_type) REFERENCES Artifact_Type(id));
alter table Artifact add (constraint Artf_arf_source_type_FK FOREIGN KEY (arf_source_type) REFERENCES Artifact_Source_Type(id));
alter table Artifact_Association add (constraint ArfAssn_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Artifact_Association add (constraint ArfAssn_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Artifact_Association add (constraint ArfAssn_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Artifact_Association add (constraint ArfAssn_assn_status_FK FOREIGN KEY (assn_status) REFERENCES Artifact_Association_Status(id));
alter table Artifact_Association add (constraint ArfAssn_assn_type_FK FOREIGN KEY (assn_type) REFERENCES Artifact_Association_Type(id));
alter table Artifact_Association add (constraint ArfAssn_arf_id_FK FOREIGN KEY (arf_id) REFERENCES Artifact(arf_id));
alter table Artifact_Association add (constraint ArfAssn_assoc_arf_id_FK FOREIGN KEY (assoc_arf_id) REFERENCES Artifact(arf_id));
alter table Artifact_Association add (constraint ArfAssn_person_id_FK FOREIGN KEY (person_id) REFERENCES Person(person_id));
alter table Artifact_Association add (constraint ArfAssn_org_id_FK FOREIGN KEY (org_id) REFERENCES Org(org_id));
alter table Artifact_Keyword add (constraint ArfKeyw_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Artifact_Keyword add (constraint ArfKeyw_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Artifact_Keyword add (constraint ArfKeyw_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Artifact_Keyword add (constraint ArfKeyw_arf_id_FK FOREIGN KEY (arf_id) REFERENCES Artifact(arf_id));
alter table Artifact_Keyword add (constraint ArfKeyw_person_id_FK FOREIGN KEY (person_id) REFERENCES Person(person_id));
alter table Artifact_Keyword add (constraint ArfKeyw_org_id_FK FOREIGN KEY (org_id) REFERENCES Org(org_id));
alter table Artifact_Event add (constraint ArfEvent_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Artifact_Event add (constraint ArfEvent_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Artifact_Event add (constraint ArfEvent_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Artifact_Event add (constraint ArfEvent_event_type_FK FOREIGN KEY (event_type) REFERENCES Artifact_Event_Type(id));
alter table Artifact_Event add (constraint ArfEvent_arf_id_FK FOREIGN KEY (arf_id) REFERENCES Artifact(arf_id));
alter table Artifact_Event add (constraint ArfEvent_related_arf_id_FK FOREIGN KEY (related_arf_id) REFERENCES Artifact(arf_id));
alter table Artifact_Event add (constraint ArfEvent_person_id_FK FOREIGN KEY (person_id) REFERENCES Person(person_id));
alter table Artifact_Event add (constraint ArfEvent_org_id_FK FOREIGN KEY (org_id) REFERENCES Org(org_id));
alter table Project add (constraint Prj_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Project add (constraint Prj_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Project add (constraint Prj_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Record_Status(id));
alter table Project add (constraint Prj_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Project(project_id));
alter table Project add (constraint Prj_project_status_FK FOREIGN KEY (project_status) REFERENCES Project_Status(id));
alter table ProjectOrg_Relation add (constraint PrjOrgRel_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table ProjectOrg_Relation add (constraint PrjOrgRel_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table ProjectOrg_Relation add (constraint PrjOrgRel_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Project_Relationship_Status(id));
alter table ProjectOrg_Relation add (constraint PrjOrgRel_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Project(project_id));
alter table ProjectOrg_Relation add (constraint PrjOrgRel_rel_type_FK FOREIGN KEY (rel_type) REFERENCES ProjectOrg_Relation_Type(id));
alter table ProjectOrg_Relation add (constraint PrjOrgRel_rel_org_id_FK FOREIGN KEY (rel_org_id) REFERENCES Org(org_id));
alter table ProjectPerson_Relation add (constraint PrjPerRel_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table ProjectPerson_Relation add (constraint PrjPerRel_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table ProjectPerson_Relation add (constraint PrjPerRel_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Project_Relationship_Status(id));
alter table ProjectPerson_Relation add (constraint PrjPerRel_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Project(project_id));
alter table ProjectPerson_Relation add (constraint PrjPerRel_rel_type_FK FOREIGN KEY (rel_type) REFERENCES ProjectPerson_Relation_Type(id));
alter table ProjectPerson_Relation add (constraint PrjPerRel_rel_person_id_FK FOREIGN KEY (rel_person_id) REFERENCES Person(person_id));
alter table Project_Event add (constraint PrjEvt_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Project_Event add (constraint PrjEvt_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Project_Event add (constraint PrjEvt_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Project_Event_Status(id));
alter table Project_Event add (constraint PrjEvt_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Project(project_id));
alter table Project_Event add (constraint PrjEvt_parent_event_id_FK FOREIGN KEY (parent_event_id) REFERENCES Project_Event(system_id));
alter table Project_Event add (constraint PrjEvt_event_type_FK FOREIGN KEY (event_type) REFERENCES Project_Event_Type(id));
alter table Project_Flag add (constraint PrjFlg_cr_person_id_FK FOREIGN KEY (cr_person_id) REFERENCES Person(person_id));
alter table Project_Flag add (constraint PrjFlg_cr_org_id_FK FOREIGN KEY (cr_org_id) REFERENCES Org(org_id));
alter table Project_Flag add (constraint PrjFlg_record_status_id_FK FOREIGN KEY (record_status_id) REFERENCES Project_Flag_Status(id));
alter table Project_Flag add (constraint PrjFlg_parent_id_FK FOREIGN KEY (parent_id) REFERENCES Project(project_id));
alter table Project_Flag add (constraint PrjFlg_flag_FK FOREIGN KEY (flag) REFERENCES Project_Flag_Type(id));
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
insert into Gender(id, caption) values (0, 'Male');
insert into Gender(id, caption) values (1, 'Female');
insert into Gender(id, caption) values (2, 'Not applicable');
insert into Marital_Status(id, caption) values (0, 'Unknown');
insert into Marital_Status(id, caption) values (1, 'Single');
insert into Marital_Status(id, caption) values (2, 'Married');
insert into Marital_Status(id, caption) values (3, 'Partner');
insert into Marital_Status(id, caption) values (4, 'Legally Separated');
insert into Marital_Status(id, caption) values (5, 'Divorced');
insert into Marital_Status(id, caption) values (6, 'Widowed');
insert into Marital_Status(id, caption) values (7, 'Not applicable');
insert into Person_Event_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Person_Event_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Person_Event_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Person_Flag_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Person_Flag_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Person_Flag_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Person_Identifier_Type(id, caption) values (0, 'Person ID Specific to Org');
insert into Person_Login_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Person_Login_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Person_Login_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Person_Role_Type(id, caption) values (0, 'Security Role (used for authorization)');
insert into Person_Role_Type(id, caption) values (1, 'Functional Role (used for business rules processing)');
insert into Person_Role_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Person_Role_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Person_Role_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Person_Relationship_Type(id, caption) values (0, 'Family');
insert into Person_Relationship_Type(id, caption) values (1, 'Other');
insert into Person_Relationship_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Person_Relationship_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Person_Relationship_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into PersonOrg_Rel_Type(id, caption) values (0, 'Member (person_id is a member of org_id)');
insert into PersonOrg_Rel_Type(id, caption) values (1, 'Client (person_id is a client of org_id)');
insert into PersonOrg_Rel_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into PersonOrg_Rel_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into PersonOrg_Rel_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Org_Ownership(id, caption) values (0, 'Public');
insert into Org_Ownership(id, caption) values (1, 'Private');
insert into Org_Ownership(id, caption) values (2, 'Subsidiary');
insert into Org_Ownership(id, caption) values (3, 'Other');
insert into Org_Type_Enum(id, caption) values (0, 'Company');
insert into Org_Type_Enum(id, caption) values (1, 'Branch');
insert into Org_Type_Enum(id, caption) values (2, 'Department');
insert into Org_Industry_Enum(id, caption) values (0, 'Agriculture');
insert into Org_Industry_Enum(id, caption) values (1, 'Apparel');
insert into Org_Industry_Enum(id, caption) values (2, 'Banking');
insert into Org_Industry_Enum(id, caption) values (3, 'Biotechnology');
insert into Org_Industry_Enum(id, caption) values (4, 'Communications');
insert into Org_Industry_Enum(id, caption) values (5, 'Construction');
insert into Org_Industry_Enum(id, caption) values (6, 'Consulting');
insert into Org_Industry_Enum(id, caption) values (7, 'Education');
insert into Org_Industry_Enum(id, caption) values (8, 'Electronics');
insert into Org_Industry_Enum(id, caption) values (9, 'Energy');
insert into Org_Industry_Enum(id, caption) values (10, 'Engineering');
insert into Org_Industry_Enum(id, caption) values (11, 'Entertainment');
insert into Org_Industry_Enum(id, caption) values (12, 'Environmental');
insert into Org_Industry_Enum(id, caption) values (13, 'Finance');
insert into Org_Industry_Enum(id, caption) values (14, 'Food and Beverage');
insert into Org_Industry_Enum(id, caption) values (15, 'Government');
insert into Org_Industry_Enum(id, caption) values (16, 'Healthcare');
insert into Org_Industry_Enum(id, caption) values (17, 'Hospitality');
insert into Org_Industry_Enum(id, caption) values (18, 'Insurance');
insert into Org_Industry_Enum(id, caption) values (19, 'Machinery');
insert into Org_Industry_Enum(id, caption) values (20, 'Manufacturing');
insert into Org_Industry_Enum(id, caption) values (21, 'Media');
insert into Org_Industry_Enum(id, caption) values (22, 'Not for Profit');
insert into Org_Industry_Enum(id, caption) values (23, 'Recreation');
insert into Org_Industry_Enum(id, caption) values (24, 'Retail');
insert into Org_Industry_Enum(id, caption) values (25, 'Shipping');
insert into Org_Industry_Enum(id, caption) values (26, 'Technology');
insert into Org_Industry_Enum(id, caption) values (27, 'Telecommunications');
insert into Org_Industry_Enum(id, caption) values (28, 'Transportation');
insert into Org_Industry_Enum(id, caption) values (29, 'Utilities');
insert into Org_Industry_Enum(id, caption) values (30, 'Other');
insert into Org_Relationship_Type(id, caption) values (0, 'Our Firm');
insert into Org_Relationship_Type(id, caption) values (1, 'Client');
insert into Org_Relationship_Type(id, caption) values (2, 'Vendor');
insert into Org_Relationship_Type(id, caption) values (3, 'Partner');
insert into Org_Relationship_Type(id, caption) values (1000, 'Ancestor of Org');
insert into Org_Relationship_Type(id, caption) values (1010, 'Parent of Org');
insert into Org_Relationship_Type(id, caption) values (1020, 'Sibling of Org');
insert into Org_Relationship_Type(id, caption) values (1030, 'Child of Org');
insert into Org_Relationship_Type(id, caption) values (1040, 'Descendent of Org');
insert into Org_Relationship_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Org_Relationship_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Org_Relationship_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Task_Relationship_Type(id, caption) values (0, 'Parent of Task (parent_id is child, task_id is parent)');
insert into Task_Relationship_Type(id, caption) values (1, 'Sibling of Task (parent_id and task_id are siblings)');
insert into Task_Relationship_Type(id, caption) values (2, 'Child of Task (parent_id is parent, task_id is child)');
insert into Task_Priority(abbrev, id, caption) values ('L', 0, 'Low');
insert into Task_Priority(abbrev, id, caption) values ('M', 1, 'Medium');
insert into Task_Priority(abbrev, id, caption) values ('H', 2, 'High');
insert into TaskPerson_Relation_Type(id, caption) values (0, 'Coordinator/Owner');
insert into TaskPerson_Relation_Type(id, caption) values (1, 'Member');
insert into Task_Relationship_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Task_Relationship_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Task_Relationship_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Task_Type(id, caption) values (0, 'Organization');
insert into Task_Type(id, caption) values (1, 'Project');
insert into Task_Type(id, caption) values (2, 'Personal');
insert into Task_Event_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Task_Event_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Task_Event_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Task_Flag_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Task_Flag_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Task_Flag_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Task_Impact(abbrev, id, caption) values ('N', 0, 'None');
insert into Task_Impact(abbrev, id, caption) values ('I', 1, 'Important');
insert into Task_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Task_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Task_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Task_Resolution(abbrev, id, caption) values ('N', 0, 'Incomplete');
insert into Task_Resolution(abbrev, id, caption) values ('I', 1, 'Complete');
insert into Artifact_Type(id, caption) values (0, 'Folder/Container');
insert into Artifact_Type(id, caption) values (1000, 'MIME Artifact');
insert into Artifact_Type(id, caption) values (2000, 'Internal Message (within Physia system)');
insert into Artifact_Type(id, caption) values (2100, 'E-mail Message');
insert into Artifact_Type(id, caption) values (3000, 'HL7 Message (Originating)');
insert into Artifact_Type(id, caption) values (4000, 'HL7 Message (Translated to XML/MDL)');
insert into Artifact_Type(id, caption) values (5000, 'Fax');
insert into Artifact_Event_Type(id, caption) values (0, 'Arrived');
insert into Artifact_Event_Type(id, caption) values (1, 'Reviewed');
insert into Artifact_Event_Type(id, caption) values (2, 'Filed');
insert into Artifact_Event_Type(id, caption) values (3, 'On-hold');
insert into Artifact_Event_Type(id, caption) values (4, 'Routed');
insert into Artifact_Event_Type(id, caption) values (5, 'Signed');
insert into Artifact_Source_Type(id, caption) values (0, 'Our firm');
insert into Artifact_Source_Type(id, caption) values (100, 'Person');
insert into Artifact_Source_Type(id, caption) values (200, 'Org');
insert into Artifact_Association_Type(id, caption) values (0, 'None');
insert into Artifact_Association_Type(id, caption) values (1, 'Parent');
insert into Artifact_Association_Type(id, caption) values (2, 'Child');
insert into Artifact_Association_Type(id, caption) values (3, 'Sibling');
insert into Artifact_Association_Type(id, caption) values (10, 'Translated from');
insert into Artifact_Association_Type(id, caption) values (1000, 'Owned by Person');
insert into Artifact_Association_Type(id, caption) values (1010, 'Owned by Organization');
insert into Artifact_Association_Type(id, caption) values (1020, 'Owned by Person in Organization');
insert into Artifact_Association_Type(id, caption) values (1030, 'Owned by Project');
insert into Artifact_Association_Type(id, caption) values (1040, 'Owned by Task');
insert into Artifact_Association_Type(id, caption) values (2000, 'Requested by Person');
insert into Artifact_Association_Type(id, caption) values (2010, 'Requested by Organization');
insert into Artifact_Association_Type(id, caption) values (2020, 'Requested by Person in Organization');
insert into Artifact_Association_Type(id, caption) values (3000, 'Requires review by Person');
insert into Artifact_Association_Type(id, caption) values (3010, 'Requires review by Organization');
insert into Artifact_Association_Type(id, caption) values (3020, 'Requires review by Person in Organization');
insert into Artifact_Association_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Artifact_Association_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Artifact_Association_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Project_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Project_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Project_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Project_Status(id, caption) values (100, 'Not Defined');
insert into Project_Status(id, caption) values (101, 'Proposed');
insert into Project_Status(id, caption) values (102, 'In Planning');
insert into Project_Status(id, caption) values (103, 'In Progress');
insert into Project_Status(id, caption) values (104, 'On Hold');
insert into Project_Status(id, caption) values (105, 'Complete');
insert into ProjectOrg_Relation_Type(abbrev, id, caption) values ('Owner', 1, 'Owner');
insert into ProjectOrg_Relation_Type(abbrev, id, caption) values ('mainc', 2, 'Main contractor');
insert into ProjectOrg_Relation_Type(abbrev, id, caption) values ('subc', 3, 'Sub contractor');
insert into ProjectPerson_Relation_Type(abbrev, id, caption) values ('LD', 1, 'Lead');
insert into ProjectPerson_Relation_Type(abbrev, id, caption) values ('ENG', 2, 'Member');
insert into Project_Relationship_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Project_Relationship_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Project_Relationship_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
insert into Project_Flag_Status(abbrev, id, caption) values ('I', 0, 'Inactive');
insert into Project_Flag_Status(abbrev, id, caption) values ('A', 1, 'Active');
insert into Project_Flag_Status(abbrev, id, caption) values ('U', 99, 'Unknown');
