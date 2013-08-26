insert into ${tableName} (
  seq_name
, lock_flag
, first_value
, last_value
) values (
  '${seqName}'
, 'i'
, :first_value
, :last_value
)