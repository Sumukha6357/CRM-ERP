create index if not exists idx_kpi_org_metric_date on kpi_snapshots (org_id, metric_code, snapshot_date desc);

insert into permissions (id, code, name)
select gen_random_uuid(), 'SYS_ADMIN', 'System Administrator'
where not exists (select 1 from permissions where code = 'SYS_ADMIN');

insert into role_permissions (role_id, permission_id)
select r.id, p.id
from roles r
join permissions p on p.code = 'SYS_ADMIN'
where r.code = 'ADMIN'
  and not exists (
    select 1 from role_permissions rp where rp.role_id = r.id and rp.permission_id = p.id
  );
