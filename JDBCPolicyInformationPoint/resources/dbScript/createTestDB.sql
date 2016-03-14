drop table PATIENT_CARER
create table PATIENT_CARER ( carer varchar(100), patient varchar(100));
insert into PATIENT_CARER (carer,patient) values ('carer01', 'patient01');
insert into PATIENT_CARER (carer,patient) values ('carer01', 'patient02');
insert into PATIENT_CARER (carer,patient) values ('carer01', 'patient03');
insert into PATIENT_CARER (carer,patient) values ('carer01', 'patient04');
insert into PATIENT_CARER (carer,patient) values ('carer01', 'patient05');
insert into PATIENT_CARER (carer,patient) values ('carer02', 'patient02');
insert into PATIENT_CARER (carer,patient) values ('carer02', 'patient04');
insert into PATIENT_CARER (carer,patient) values ('carer02', 'patient06');
insert into PATIENT_CARER (carer,patient) values ('carer03', 'patient06');
insert into PATIENT_CARER (carer,patient) values ('carer03', 'patient09');

drop table RELATION
create 	table 	RELATION (source_id varchar(100),  target_id varchar(100),  relation_id varchar(100));
insert 	into 	RELATION (source_id, target_id, relation_id) values ('patient01@carbon.super','questionnaire_id_01','owner');
insert 	into 	RELATION (source_id, target_id, relation_id) values ('patient01@carbon.super','questionnaire_id_03','owner');
insert 	into 	RELATION (source_id, target_id, relation_id) values ('patient01@carbon.super','questionnaire_id_05','owner');
insert 	into 	RELATION (source_id, target_id, relation_id) values ('patient02@carbon.super','questionnaire_id_02','owner');
insert 	into 	RELATION (source_id, target_id, relation_id) values ('patient02@carbon.super','questionnaire_id_04','owner');
insert 	into 	RELATION (source_id, target_id, relation_id) values ('patient02@carbon.super','questionnaire_id_06','owner');

drop table USER_ROLE
CREATE TABLE USER_ROLE (user_name varchar(100), role_name varchar(100));
insert into USER_ROLE (user_name, role_name) values ('clinitian01@carbon.super','clinitian');
insert into USER_ROLE (user_name, role_name) values ('clinitian02@carbon.super','clinitian');
insert into USER_ROLE (user_name, role_name) values ('clinitian03@carbon.super','clinitian');
insert into USER_ROLE (user_name, role_name) values ('therapist01@carbon.super','therapist');
insert into USER_ROLE (user_name, role_name) values ('therapist02@carbon.super','therapist');
insert into USER_ROLE (user_name, role_name) values ('therapist03@carbon.super','therapist');
insert into USER_ROLE (user_name, role_name) values ('patient01@carbon.super','patient');
insert into USER_ROLE (user_name, role_name) values ('patient02@carbon.super','patient');
insert into USER_ROLE (user_name, role_name) values ('patient03@carbon.super','patient');