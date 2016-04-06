-- Table: public.some_table

-- DROP TABLE public.some_table;

CREATE TABLE public.some_table
(
  id integer NOT NULL,
  name character varying(30),
  val character varying(30),
  CONSTRAINT some_table_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.some_table
  OWNER TO postgres;
  

  INSERT INTO public.some_table(
            id, name, val)
    VALUES (1, 'value','demo');

  