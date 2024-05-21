/*
 *
 *  * Copyright 2024 bbwres
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

#
# in your quartz properties file, you'll need to set
# org.quartz.jobstore.driverdelegateclass = org.quartz.impl.jdbcjobstore.stdjdbcdelegate
#
#
# by: ron cordell - roncordell
#  i didn't see this anywhere, so i thought i'd post it here. this is the script from quartz to create the tables in a mysql database, modified to use innodb instead of myisam.

drop table if exists T_QUARTZ_FIRED_TRIGGERS;
drop table if exists T_QUARTZ_PAUSED_TRIGGER_GRPS;
drop table if exists T_QUARTZ_SCHEDULER_STATE;
drop table if exists T_QUARTZ_LOCKS;
drop table if exists T_QUARTZ_SIMPLE_TRIGGERS;
drop table if exists T_QUARTZ_SIMPROP_TRIGGERS;
drop table if exists T_QUARTZ_CRON_TRIGGERS;
drop table if exists T_QUARTZ_BLOB_TRIGGERS;
drop table if exists T_QUARTZ_TRIGGERS;
drop table if exists T_QUARTZ_JOB_DETAILS;
drop table if exists T_QUARTZ_CALENDARS;

create table T_QUARTZ_JOB_DETAILS(
sched_name varchar(120) not null,
job_name varchar(190) not null,
job_group varchar(190) not null,
description varchar(250) null,
job_class_name varchar(250) not null,
is_durable varchar(1) not null,
is_nonconcurrent varchar(1) not null,
is_update_data varchar(1) not null,
requests_recovery varchar(1) not null,
job_data blob null,
primary key (sched_name,job_name,job_group))
engine=innodb;

create table T_QUARTZ_TRIGGERS (
sched_name varchar(120) not null,
trigger_name varchar(190) not null,
trigger_group varchar(190) not null,
job_name varchar(190) not null,
job_group varchar(190) not null,
description varchar(250) null,
next_fire_time bigint(13) null,
prev_fire_time bigint(13) null,
priority integer null,
trigger_state varchar(16) not null,
trigger_type varchar(8) not null,
start_time bigint(13) not null,
end_time bigint(13) null,
calendar_name varchar(190) null,
misfire_instr smallint(2) null,
job_data blob null,
primary key (sched_name,trigger_name,trigger_group),
foreign key (sched_name,job_name,job_group)
references T_QUARTZ_JOB_DETAILS(sched_name,job_name,job_group))
engine=innodb;

create table T_QUARTZ_SIMPLE_TRIGGERS (
sched_name varchar(120) not null,
trigger_name varchar(190) not null,
trigger_group varchar(190) not null,
repeat_count bigint(7) not null,
repeat_interval bigint(12) not null,
times_triggered bigint(10) not null,
primary key (sched_name,trigger_name,trigger_group),
foreign key (sched_name,trigger_name,trigger_group)
references T_QUARTZ_TRIGGERS(sched_name,trigger_name,trigger_group))
engine=innodb;

create table T_QUARTZ_CRON_TRIGGERS (
sched_name varchar(120) not null,
trigger_name varchar(190) not null,
trigger_group varchar(190) not null,
cron_expression varchar(120) not null,
time_zone_id varchar(80),
primary key (sched_name,trigger_name,trigger_group),
foreign key (sched_name,trigger_name,trigger_group)
references T_QUARTZ_TRIGGERS(sched_name,trigger_name,trigger_group))
engine=innodb;

create table T_QUARTZ_SIMPROP_TRIGGERS
  (
    sched_name varchar(120) not null,
    trigger_name varchar(190) not null,
    trigger_group varchar(190) not null,
    str_prop_1 varchar(512) null,
    str_prop_2 varchar(512) null,
    str_prop_3 varchar(512) null,
    int_prop_1 int null,
    int_prop_2 int null,
    long_prop_1 bigint null,
    long_prop_2 bigint null,
    dec_prop_1 numeric(13,4) null,
    dec_prop_2 numeric(13,4) null,
    bool_prop_1 varchar(1) null,
    bool_prop_2 varchar(1) null,
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
    references T_QUARTZ_TRIGGERS(sched_name,trigger_name,trigger_group))
engine=innodb;

create table T_QUARTZ_BLOB_TRIGGERS (
sched_name varchar(120) not null,
trigger_name varchar(190) not null,
trigger_group varchar(190) not null,
blob_data blob null,
primary key (sched_name,trigger_name,trigger_group),
index (sched_name,trigger_name, trigger_group),
foreign key (sched_name,trigger_name,trigger_group)
references T_QUARTZ_TRIGGERS(sched_name,trigger_name,trigger_group))
engine=innodb;

create table T_QUARTZ_CALENDARS (
sched_name varchar(120) not null,
calendar_name varchar(190) not null,
calendar blob not null,
primary key (sched_name,calendar_name))
engine=innodb;

create table T_QUARTZ_PAUSED_TRIGGER_GRPS (
sched_name varchar(120) not null,
trigger_group varchar(190) not null,
primary key (sched_name,trigger_group))
engine=innodb;

create table T_QUARTZ_FIRED_TRIGGERS (
sched_name varchar(120) not null,
entry_id varchar(95) not null,
trigger_name varchar(190) not null,
trigger_group varchar(190) not null,
instance_name varchar(190) not null,
fired_time bigint(13) not null,
sched_time bigint(13) not null,
priority integer not null,
state varchar(16) not null,
job_name varchar(190) null,
job_group varchar(190) null,
is_nonconcurrent varchar(1) null,
requests_recovery varchar(1) null,
primary key (sched_name,entry_id))
engine=innodb;

create table T_QUARTZ_SCHEDULER_STATE (
sched_name varchar(120) not null,
instance_name varchar(190) not null,
last_checkin_time bigint(13) not null,
checkin_interval bigint(13) not null,
primary key (sched_name,instance_name))
engine=innodb;

create table T_QUARTZ_LOCKS (
sched_name varchar(120) not null,
lock_name varchar(40) not null,
primary key (sched_name,lock_name))
engine=innodb;

create index idx_t_quartz_j_req_recovery on T_QUARTZ_JOB_DETAILS(sched_name,requests_recovery);
create index idx_t_quartz_j_grp on T_QUARTZ_JOB_DETAILS(sched_name,job_group);

create index idx_t_quartz_t_j on T_QUARTZ_TRIGGERS(sched_name,job_name,job_group);
create index idx_t_quartz_t_jg on T_QUARTZ_TRIGGERS(sched_name,job_group);
create index idx_t_quartz_t_c on T_QUARTZ_TRIGGERS(sched_name,calendar_name);
create index idx_t_quartz_t_g on T_QUARTZ_TRIGGERS(sched_name,trigger_group);
create index idx_t_quartz_t_state on T_QUARTZ_TRIGGERS(sched_name,trigger_state);
create index idx_t_quartz_t_n_state on T_QUARTZ_TRIGGERS(sched_name,trigger_name,trigger_group,trigger_state);
create index idx_t_quartz_t_n_g_state on T_QUARTZ_TRIGGERS(sched_name,trigger_group,trigger_state);
create index idx_t_quartz_t_next_fire_time on T_QUARTZ_TRIGGERS(sched_name,next_fire_time);
create index idx_t_quartz_t_nft_st on T_QUARTZ_TRIGGERS(sched_name,trigger_state,next_fire_time);
create index idx_t_quartz_t_nft_misfire on T_QUARTZ_TRIGGERS(sched_name,misfire_instr,next_fire_time);
create index idx_t_quartz_t_nft_st_misfire on T_QUARTZ_TRIGGERS(sched_name,misfire_instr,next_fire_time,trigger_state);
create index idx_t_quartz_t_nft_st_misfire_grp on T_QUARTZ_TRIGGERS(sched_name,misfire_instr,next_fire_time,trigger_group,trigger_state);

create index idx_t_quartz_ft_trig_inst_name on T_QUARTZ_FIRED_TRIGGERS(sched_name,instance_name);
create index idx_t_quartz_ft_inst_job_req_rcvry on T_QUARTZ_FIRED_TRIGGERS(sched_name,instance_name,requests_recovery);
create index idx_t_quartz_ft_j_g on T_QUARTZ_FIRED_TRIGGERS(sched_name,job_name,job_group);
create index idx_t_quartz_ft_jg on T_QUARTZ_FIRED_TRIGGERS(sched_name,job_group);
create index idx_t_quartz_ft_t_g on T_QUARTZ_FIRED_TRIGGERS(sched_name,trigger_name,trigger_group);
create index idx_t_quartz_ft_tg on T_QUARTZ_FIRED_TRIGGERS(sched_name,trigger_group);

commit;
