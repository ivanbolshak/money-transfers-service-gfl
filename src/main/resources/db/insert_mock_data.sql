INSERT INTO transfer."user" (uuid, created_at, email, f_name, l_name)
VALUES  ('60482aae-c95e-4b6d-b1bf-b3d5179d1402'::uuid, '2023-10-15 14:51:39.898'::timestamp without time zone, 'user1@gmail.com'::character varying, 'User1'::character varying, 'User1'::character varying),
        ('f5b71c77-157e-40aa-9c8d-d02426984d3b'::uuid, '2023-10-22 14:51:39.898'::timestamp without time zone, 'user2@gmail.com'::character varying, 'User2'::character varying, 'User2'::character varying);


INSERT INTO transfer.account (user_id, deleted, balance, cur_dig_code, created_at, opt_lock_version, serial)
VALUES  ('60482aae-c95e-4b6d-b1bf-b3d5179d1402'::uuid, false::boolean, '100'::numeric, '840'::integer, '2023-10-15 17:00:00.000'::timestamp without time zone, '1'::bigint, 'AABB1122'::character varying),
        ('60482aae-c95e-4b6d-b1bf-b3d5179d1402'::uuid, false::boolean, '0'::numeric, '980'::integer, '2023-10-16 11:00:00.000'::timestamp without time zone, '1'::bigint, 'AACC1133'::character varying),
        ('f5b71c77-157e-40aa-9c8d-d02426984d3b'::uuid, false::boolean, '10'::numeric, '840'::integer, '2023-10-23 14:00:00.000'::timestamp without time zone, '1'::bigint, 'BBDD2211'::character varying);

INSERT INTO transfer.transaction (uuid, amount, cur_dig_code, account_id, created_at, dest_account_serial, src_account_serial)
VALUES ('7571d39a-efd5-4e85-af8a-e6ab8fe39560'::uuid, '5'::numeric, '840'::integer, '1'::bigint, '2023-10-25 10:00:00.000'::timestamp without time zone, 'AABB1122'::character varying, 'KKTT7733'::character varying);
