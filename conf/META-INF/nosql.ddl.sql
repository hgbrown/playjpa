create table company (id int8 not null, financialHistory json, name varchar(255) not null, reg_number varchar(255) not null, primary key (id));
create table employee (id int8 not null, first_name varchar(255), last_name varchar(255), profile json, primary key (id));
alter table company add constraint UK_niu8sfil2gxywcru9ah3r4ec5  unique (name);
alter table company add constraint UK_gpeuerb77ng914pedqrah5nkb  unique (reg_number);
create sequence company_id_sequence;
create sequence employee_id_sequence;
