select job,
broken,
to_char(next_date,'YYYY/MM/DD HH24:MI:SS') next_date,what,
interval
from dba_jobs order by job