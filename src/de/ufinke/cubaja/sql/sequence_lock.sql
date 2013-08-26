update   ${tableName}
set      lock_flag = :lock_flag
where    seq_name = '${seqName}'
