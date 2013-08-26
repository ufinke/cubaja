select   first_value
       , last_value
from     ${tableName}
where    seq_name = '${seqName}'
order by first_value
