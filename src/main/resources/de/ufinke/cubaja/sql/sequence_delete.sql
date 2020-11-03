delete
from     ${tableName}
where    seq_name = '${seqName}'
and      first_value = :old_value
