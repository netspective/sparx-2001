
insert into Person(person_id, name_first, name_last, simple_name)
	values(Per_person_id_seq.nextval, 'wily', 'coyote', 'wily coyote');


insert into Person_Login(system_id, person_id,  user_id, password) 
	values(PerLg_system_id_seq.nextval, Per_person_id_seq.currval,  'wily', 'coyote');


commit;	
