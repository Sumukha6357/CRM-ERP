create extension if not exists "pgcrypto";

create table orgs (
  id uuid primary key default gen_random_uuid(),
  code varchar(64) not null unique,
  name varchar(255) not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table users (
  id uuid primary key default gen_random_uuid(),
  org_id uuid not null references orgs(id) on delete cascade,
  email varchar(255) not null,
  password_hash varchar(255) not null,
  full_name varchar(255) not null,
  status varchar(32) not null default 'ACTIVE',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (org_id, email)
);

create table roles (
  id uuid primary key default gen_random_uuid(),
  org_id uuid not null references orgs(id) on delete cascade,
  code varchar(64) not null,
  name varchar(255) not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (org_id, code)
);

create table permissions (
  id uuid primary key default gen_random_uuid(),
  code varchar(64) not null unique,
  name varchar(255) not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table user_roles (
  user_id uuid not null references users(id) on delete cascade,
  role_id uuid not null references roles(id) on delete cascade,
  primary key (user_id, role_id)
);

create table role_permissions (
  role_id uuid not null references roles(id) on delete cascade,
  permission_id uuid not null references permissions(id) on delete cascade,
  primary key (role_id, permission_id)
);

create table audit_log (
  id uuid primary key default gen_random_uuid(),
  org_id uuid not null references orgs(id) on delete cascade,
  actor_user_id uuid references users(id),
  entity_type varchar(128) not null,
  entity_id uuid,
  action varchar(64) not null,
  before_state jsonb,
  after_state jsonb,
  ip_address varchar(64),
  user_agent varchar(512),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table notifications (
  id uuid primary key default gen_random_uuid(),
  org_id uuid not null references orgs(id) on delete cascade,
  user_id uuid not null references users(id) on delete cascade,
  title varchar(255) not null,
  body text,
  type varchar(64) not null,
  data jsonb,
  read_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table workflow_defs (
  id uuid primary key default gen_random_uuid(),
  org_id uuid not null references orgs(id) on delete cascade,
  code varchar(128) not null,
  name varchar(255) not null,
  config jsonb not null,
  active boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (org_id, code)
);

create table workflow_instances (
  id uuid primary key default gen_random_uuid(),
  org_id uuid not null references orgs(id) on delete cascade,
  definition_id uuid not null references workflow_defs(id) on delete cascade,
  status varchar(32) not null,
  started_by uuid not null references users(id),
  current_step varchar(128),
  data jsonb,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table job_runs (
  id uuid primary key default gen_random_uuid(),
  org_id uuid references orgs(id) on delete cascade,
  job_code varchar(128) not null,
  status varchar(32) not null,
  started_at timestamptz not null default now(),
  ended_at timestamptz,
  error_message text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table kpi_snapshots (
  id uuid primary key default gen_random_uuid(),
  org_id uuid not null references orgs(id) on delete cascade,
  metric_code varchar(128) not null,
  metric_value numeric(18,2) not null,
  snapshot_date date not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (org_id, metric_code, snapshot_date)
);

insert into orgs (id, code, name) values
  ('00000000-0000-0000-0000-000000000001', 'default', 'Default Org');

insert into roles (id, org_id, code, name) values
  ('00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000001', 'ADMIN', 'Administrator');

insert into permissions (id, code, name) values
  ('00000000-0000-0000-0000-000000000101', 'CRM_LEAD_READ', 'Read CRM Leads'),
  ('00000000-0000-0000-0000-000000000102', 'CRM_LEAD_WRITE', 'Write CRM Leads'),
  ('00000000-0000-0000-0000-000000000103', 'CRM_DEAL_READ', 'Read CRM Deals'),
  ('00000000-0000-0000-0000-000000000104', 'CRM_DEAL_WRITE', 'Write CRM Deals'),
  ('00000000-0000-0000-0000-000000000105', 'CRM_ACTIVITY_READ', 'Read CRM Activities'),
  ('00000000-0000-0000-0000-000000000106', 'CRM_ACTIVITY_WRITE', 'Write CRM Activities'),
  ('00000000-0000-0000-0000-000000000107', 'WORKFLOW_READ', 'Read Workflows'),
  ('00000000-0000-0000-0000-000000000108', 'WORKFLOW_WRITE', 'Write Workflows'),
  ('00000000-0000-0000-0000-000000000109', 'NOTIFICATION_READ', 'Read Notifications'),
  ('00000000-0000-0000-0000-000000000110', 'NOTIFICATION_WRITE', 'Write Notifications'),
  ('00000000-0000-0000-0000-000000000111', 'AUDIT_READ', 'Read Audit Log'),
  ('00000000-0000-0000-0000-000000000112', 'KPI_READ', 'Read KPI Snapshots');

insert into role_permissions (role_id, permission_id)
select '00000000-0000-0000-0000-000000000010', id from permissions;

insert into users (id, org_id, email, password_hash, full_name, status) values
  ('00000000-0000-0000-0000-000000000100', '00000000-0000-0000-0000-000000000001', 'admin@ggos.local',
   '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5Q1CDe7v4xYueAA6QbV0I1f3wWg5G', 'Admin User', 'ACTIVE');

insert into user_roles (user_id, role_id) values
  ('00000000-0000-0000-0000-000000000100', '00000000-0000-0000-0000-000000000010');
