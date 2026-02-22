create table crm_pipeline_stages (
  id uuid primary key default gen_random_uuid(),
  org_id uuid not null references orgs(id) on delete cascade,
  name varchar(128) not null,
  sort_order int not null default 0,
  is_closed boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table crm_leads (
  id uuid primary key default gen_random_uuid(),
  org_id uuid not null references orgs(id) on delete cascade,
  owner_id uuid references users(id),
  stage_id uuid references crm_pipeline_stages(id),
  status varchar(64) not null default 'OPEN',
  source varchar(128),
  name varchar(255) not null,
  email varchar(255),
  phone varchar(64),
  company varchar(255),
  value numeric(18,2),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table crm_deals (
  id uuid primary key default gen_random_uuid(),
  org_id uuid not null references orgs(id) on delete cascade,
  owner_id uuid references users(id),
  stage_id uuid references crm_pipeline_stages(id),
  status varchar(64) not null default 'OPEN',
  title varchar(255) not null,
  amount numeric(18,2),
  expected_close_date date,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table crm_activities (
  id uuid primary key default gen_random_uuid(),
  org_id uuid not null references orgs(id) on delete cascade,
  lead_id uuid references crm_leads(id) on delete cascade,
  deal_id uuid references crm_deals(id) on delete cascade,
  owner_id uuid references users(id),
  activity_type varchar(64) not null,
  subject varchar(255) not null,
  notes text,
  due_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
