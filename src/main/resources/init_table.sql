create table task_info
(
	id integer not null
		constraint task_info_pk
			primary key autoincrement,
	job_name text not null,
	job_desc text,
	cron_expression text,
	targeturl text,
	job_status int not null,
	create_time text not null,
	update_time text not null
);

create unique index task_info_id_uindex
	on task_info (id);