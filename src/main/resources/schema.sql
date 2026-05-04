create table if not exists accounts (
    account_id bigserial primary key,
    username varchar(100) not null unique,
    password_digest varchar(255) not null,
    account_role varchar(20) not null,
    registered_at timestamp not null default now()
);

create table if not exists otp_policy (
    policy_id smallint primary key,
    digits_count integer not null,
    ttl_seconds integer not null,
    constraint single_otp_policy check (policy_id = 1)
);

create table if not exists verification_codes (
    ticket_id bigserial primary key,
    account_id bigint not null references accounts(account_id) on delete cascade,
    business_key varchar(150) not null,
    secret_code varchar(20) not null,
    code_state varchar(20) not null,
    issued_at timestamp not null default now(),
    valid_until timestamp not null,
    consumed_at timestamp null
);

create index if not exists idx_verification_codes_lookup
    on verification_codes(account_id, business_key, code_state);

insert into otp_policy(policy_id, digits_count, ttl_seconds)
values (1, 6, 300)
on conflict (policy_id) do nothing;
