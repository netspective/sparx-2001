
/* create the COMSYS organization record */
insert into Org (org_id, org_code, org_name)
	values(org_org_id_seq.nextval, 'COMSYS', 'COMSYS Information Systems');

/* create the SNSHAH personal record */
insert into Person(person_id, name_first, name_last, short_name, simple_name, complete_name, short_sortable_name, complete_sortable_name)
	values(Per_person_id_seq.nextval, 'Shahid', 'Shah', 'S. Shah', 'Shahid Shah', 'Shahid N. Shah', 'Shah, S', 'Shah, Shahid N.');

/* make SNSHAH a member of COMSYS */
insert into PersonOrg_Relationship(system_id, parent_id, rel_type, record_status_id, rel_org_id)
	values(PerRel_system_id_seq.nextval, Per_person_id_seq.currval, 0, 1, org_org_id_seq.currval);

/* allow SNSHAH to log in */

insert into Person_Login(system_id, person_id, login_status, user_id, password, quantity) 
	values(PerLg_system_id_seq.nextval, Per_person_id_seq.currval, 1, 'cura', 'cura', 1024);

/* create the ACME organization record */
insert into Org (org_id, org_code, org_name)
	values(org_org_id_seq.nextval, 'ACME', 'ACME Corporation');

/* make SNSHAH a member of ACME Corp */
insert into PersonOrg_Relationship(system_id, parent_id, rel_type, record_status_id, rel_org_id)
	values(PerRel_system_id_seq.nextval, Per_person_id_seq.currval, 0, 1, org_org_id_seq.currval);

/* create Bugs Bunny personal record */
insert into Person(person_id, name_first, name_last, short_name, simple_name, complete_name, short_sortable_name, complete_sortable_name)
	values(Per_person_id_seq.nextval, 'Bugs', 'Bunny', 'B. Bunny', 'Bugs Bunny', 'Bugs Bunny', 'Bunny, B', 'Bunny, Bugs');

/* make Bugs Bunny a member of ACME Corp */
insert into PersonOrg_Relationship(system_id, parent_id, rel_type, record_status_id, rel_org_id)
	values(PerRel_system_id_seq.nextval, Per_person_id_seq.currval, 0, 1, org_org_id_seq.currval);

/* create Road Runner personal record */
insert into Person(person_id, name_first, name_last, short_name, simple_name, complete_name, short_sortable_name, complete_sortable_name)
	values(Per_person_id_seq.nextval, 'Road', 'Runner', 'R. Runner', 'Road Runner', 'Road Runner', 'Runner, R', 'Runner, Road');

/* make Bus Bunny a member of ACME Corp */
insert into PersonOrg_Relationship(system_id, parent_id, rel_type, record_status_id, rel_org_id)
	values(PerRel_system_id_seq.nextval, Per_person_id_seq.currval, 0, 1, org_org_id_seq.currval);

/* create Wily Coyote personal record */
insert into Person(person_id, name_first, name_last, short_name, simple_name, complete_name, short_sortable_name, complete_sortable_name)
	values(Per_person_id_seq.nextval, 'Wily', 'Coyote', 'W. Coyote', 'Wily Coyote', 'Wily E. Coyote', 'Coyote, W', 'Coyote, Wily E.');

/* make Bus Bunny a member of ACME Corp */
insert into PersonOrg_Relationship(system_id, parent_id, rel_type, record_status_id, rel_org_id)
	values(PerRel_system_id_seq.nextval, Per_person_id_seq.currval, 0, 1, org_org_id_seq.currval);

/* create Taz personal record */
insert into Person(person_id, name_first, name_last, short_name, simple_name, complete_name, short_sortable_name, complete_sortable_name)
	values(Per_person_id_seq.nextval, 'Taz', 'Devil', 'T. Devil', 'Taz Devil', 'Tazmanian Devil', 'Devil, T', 'Devil, Taz');

/* make Bus Bunny a member of ACME Corp */
insert into PersonOrg_Relationship(system_id, parent_id, rel_type, record_status_id, rel_org_id)
	values(PerRel_system_id_seq.nextval, Per_person_id_seq.currval, 0, 1, org_org_id_seq.currval);

commit;	
