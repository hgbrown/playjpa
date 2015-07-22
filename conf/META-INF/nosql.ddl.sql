CREATE TABLE company ( id INT8 NOT NULL, name VARCHAR(255) NOT NULL, reg_number VARCHAR(255) NOT NULL, PRIMARY KEY (id) );
CREATE TABLE employee ( id INT8 NOT NULL, first_name VARCHAR(255), last_name  VARCHAR(255), PRIMARY KEY (id) );
ALTER TABLE company ADD CONSTRAINT UK_niu8sfil2gxywcru9ah3r4ec5 UNIQUE (name);
ALTER TABLE company ADD CONSTRAINT UK_gpeuerb77ng914pedqrah5nkb UNIQUE (reg_number);
CREATE SEQUENCE company_id_sequence;
CREATE SEQUENCE employee_id_sequence;
