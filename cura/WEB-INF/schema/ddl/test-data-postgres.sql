
/* create the Netspective organization record */
insert into Org (org_id, org_code, org_name)
	values(nextval('org_org_id_seq'), 'Netspective', 'Netspective Communications LLC');

/* create the SNSHAH personal record */
insert into Person(person_id, name_first, name_last, short_name, simple_name, complete_name, short_sortable_name, complete_sortable_name)
	values(nextval('Per_person_id_seq'), 'Shahid', 'Shah', 'S. Shah', 'Shahid Shah', 'Shahid N. Shah', 'Shah, S', 'Shah, Shahid N.');

/* make SNSHAH a member of Netspective */
insert into PersonOrg_Relationship(system_id, parent_id, rel_type, record_status_id, rel_org_id)
    values(nextval('PeORel_system_id_SEQ'), currval('Per_person_id_seq'), 0, 1, currval('org_org_id_seq'));

/* allow SNSHAH to log in */

insert into Person_Login(system_id, person_id, login_status, user_id, password, quantity) 
	values(nextval('PerLg_system_id_seq'), currval('Per_person_id_seq'), 1, 'cura', 'cura', 1024);

/* create the ACME organization record */
insert into Org (org_id, org_code, org_name)
	values(nextval('org_org_id_seq'), 'ACME', 'ACME Corporation');

/* make SNSHAH a member of ACME Corp */
insert into PersonOrg_Relationship(system_id, parent_id, rel_type, record_status_id, rel_org_id)
    values(nextval('PeORel_system_id_SEQ'), currval('Per_person_id_seq'), 0, 1, currval('org_org_id_seq'));
insert into Person_Address(cr_stamp, cr_person_id, cr_org_id, record_status_id, system_id, parent_id, address_name, mailing, line1, line2, city, county, state, zip, country)  values(now(), 1, currval('org_org_id_seq'), 1, nextval('PerAddr_system_id_SEQ'), currval('Per_person_id_seq'), 'WORK', null, '123 ACME ROAD', null, 'ACME CITY', null, 'AC', '99999', 'USA');

/* create Bugs Bunny personal record */
insert into Person(person_id, name_first, name_last, short_name, simple_name, complete_name, short_sortable_name, complete_sortable_name)
	values(nextval('Per_person_id_seq'), 'Bugs', 'Bunny', 'B. Bunny', 'Bugs Bunny', 'Bugs Bunny', 'Bunny, B', 'Bunny, Bugs');
/* create Bugs Bunny address record */
insert into Person_Address(cr_stamp, cr_person_id, cr_org_id, record_status_id, system_id, parent_id, address_name, mailing, line1, line2, city, county, state, zip, country)  values(now(), 1, currval('org_org_id_seq'), 1, nextval('PerAddr_system_id_SEQ'), currval('Per_person_id_seq'), 'WORK', null, '123 ACME ROAD', null, 'ACME CITY', null, 'AC', '99999', 'USA');
/* make Bugs Bunny a member of ACME Corp */
insert into PersonOrg_Relationship(system_id, parent_id, rel_type, record_status_id, rel_org_id)
    values(nextval('PeORel_system_id_SEQ'), currval('Per_person_id_seq'), 0, 1, currval('org_org_id_seq'));

/* create Road Runner personal record */
insert into Person(person_id, name_first, name_last, short_name, simple_name, complete_name, short_sortable_name, complete_sortable_name)
	values(nextval('Per_person_id_seq'), 'Road', 'Runner', 'R. Runner', 'Road Runner', 'Road Runner', 'Runner, R', 'Runner, Road');
insert into Person_Address(cr_stamp, cr_person_id, cr_org_id, record_status_id, system_id, parent_id, address_name, mailing, line1, line2, city, county, state, zip, country)  values(now(), 1, currval('org_org_id_seq'), 1, nextval('PerAddr_system_id_SEQ'), currval('Per_person_id_seq'), 'WORK', null, '123 ACME ROAD', null, 'ACME CITY', null, 'AC', '99999', 'USA');
/* make Road Runner a member of ACME Corp */
insert into PersonOrg_Relationship(system_id, parent_id, rel_type, record_status_id, rel_org_id)
    values(nextval('PeORel_system_id_SEQ'), currval('Per_person_id_seq'), 0, 1, currval('org_org_id_seq'));

/* create Wily Coyote personal record */
insert into Person(person_id, name_first, name_last, short_name, simple_name, complete_name, short_sortable_name, complete_sortable_name)
	values(nextval('Per_person_id_seq'), 'Wily', 'Coyote', 'W. Coyote', 'Wily Coyote', 'Wily E. Coyote', 'Coyote, W', 'Coyote, Wily E.');
insert into Person_Address(cr_stamp, cr_person_id, cr_org_id, record_status_id, system_id, parent_id, address_name, mailing, line1, line2, city, county, state, zip, country)  values(now(), 1, currval('org_org_id_seq'), 1, nextval('PerAddr_system_id_SEQ'), currval('Per_person_id_seq'), 'WORK', null, '123 ACME ROAD', null, 'ACME CITY', null, 'AC', '99999', 'USA');
/* make Bus Bunny a member of ACME Corp */
insert into PersonOrg_Relationship(system_id, parent_id, rel_type, record_status_id, rel_org_id)
    values(nextval('PeORel_system_id_SEQ'), currval('Per_person_id_seq'), 0, 1, currval('org_org_id_seq'));

/* create Taz personal record */
insert into Person(person_id, name_first, name_last, short_name, simple_name, complete_name, short_sortable_name, complete_sortable_name)
	values(nextval('Per_person_id_seq'), 'Taz', 'Devil', 'T. Devil', 'Taz Devil', 'Tazmanian Devil', 'Devil, T', 'Devil, Taz');
insert into Person_Address(cr_stamp, cr_person_id, cr_org_id, record_status_id, system_id, parent_id, address_name, mailing, line1, line2, city, county, state, zip, country) values(now(), 1, currval('org_org_id_seq'), 1, nextval('PerAddr_system_id_SEQ'), currval('Per_person_id_seq'), 'WORK', null, '123 ACME ROAD', null, 'ACME CITY', null, 'AC', '99999', 'USA');
/* make Bus Bunny a member of ACME Corp */
insert into PersonOrg_Relationship(system_id, parent_id, rel_type, record_status_id, rel_org_id)
    values(nextval('PeORel_system_id_SEQ'), currval('Per_person_id_seq'), 0, 1, currval('org_org_id_seq'));

commit;	
