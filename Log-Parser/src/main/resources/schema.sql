create table log
(

   id integer not null,
   logid varchar(50) not null,
   duration integer not null,
   host varchar(50) ,
   applType varchar(50) ,
   alert boolean,
   primary key(id)
   
);