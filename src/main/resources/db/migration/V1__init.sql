create schema if not exists transfer;

create table if not exists transfer.account 
(
    id bigserial not null,
    user_id uuid,
    deleted boolean not null,
    balance numeric(38,2) not null,
    cur_dig_code integer not null,
    created_at timestamp(6),
    opt_lock_version bigint,
    serial varchar(255) not null unique,  
    primary key (id)
);

create table if not exists transfer.transaction
(
    uuid uuid not null,
    amount numeric(38,2) not null,cur_dig_code integer not null,
    account_id bigint,
    created_at timestamp(6) not null,
    dest_account_serial varchar(255) not null,
    src_account_serial varchar(255) not null,
    primary key (uuid)
);

create table if not exists transfer.user
(
    uuid uuid not null,
    created_at timestamp(6),
    email varchar(255) not null unique,
    f_name varchar(255) not null,
    l_name varchar(255) not null,
    primary key (uuid)
);

alter table if exists transfer.account
    add constraint fk_transfer_account_user_id foreign key (user_id) references transfer.user;

alter table if exists transfer.transaction
    add constraint fk_transfer_transaction_account_id foreign key (account_id) references transfer.account;