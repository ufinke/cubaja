select   int_field
       , decimal_field
       , date_field
       , string_field
       , char_field
       , timestamp_field
from     basic_data
where    int_field >= ${fromConstant}
and      int_field <= ${toConstant}