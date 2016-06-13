DROP SCHEMA tenant_carbon_super;
CREATE SCHEMA tenant_carbon_super;
CREATE TABLE tenant_carbon_super.some_table (id integer NOT NULL,  name character varying(30),  val character varying(30),  CONSTRAINT some_table_pkey PRIMARY KEY (id));
INSERT INTO tenant_carbon_super.some_table(id, name, val)
VALUES (1, 'tenant','carbon_super');

DROP SCHEMA tenant_t1_org;
CREATE SCHEMA tenant_t1_org;
CREATE TABLE tenant_t1_org.some_table(  id integer NOT NULL,  name character varying(30),  val character varying(30),  CONSTRAINT some_table_pkey PRIMARY KEY (id));
INSERT INTO tenant_t1_org.some_table(id, name, val)
VALUES (1, 'tenant','t1.org');

DROP SCHEMA tenant_t2_org;
CREATE SCHEMA tenant_t2_org;
CREATE TABLE tenant_t2_org.some_table(  id integer NOT NULL,  name character varying(30),  val character varying(30),  CONSTRAINT some_table_pkey PRIMARY KEY (id));
INSERT INTO tenant_t2_org.some_table(id, name, val)
VALUES (1, 'tenant','t2.org');
