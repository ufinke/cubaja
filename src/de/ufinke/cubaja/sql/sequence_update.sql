update   ${tableName}
set      first_value = :new_value
where    seq_name = '${seqName}'
and      first_value = :old_value
