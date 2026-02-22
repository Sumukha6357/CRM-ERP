update users
set password_hash = '$2a$10$jYOggSBa5taHtkhB8hZaeOBE6bCX851CKeuzmJ52cYlE452Ljo8da'
where org_id = '00000000-0000-0000-0000-000000000001'
  and email = 'admin@ggos.local';
