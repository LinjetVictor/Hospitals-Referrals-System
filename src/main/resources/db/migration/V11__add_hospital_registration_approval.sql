alter table hospitals
    add column registration_status varchar(20) not null default 'APPROVED';

alter table hospitals
    add column registration_admin_id bigint null;

create index idx_hospitals_registration_status on hospitals (registration_status);
