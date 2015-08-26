select t1.tablespace_name,ext EXTEND,
SIZE_MB "SIZE",
(size_MB-free_MB) "USED",
FREE_MB "FREE",
trunc((size_MB-free_MB)*100/size_MB) "USED_SIZE",
t1.maxsize_MB "MAX",
trunc((size_MB-free_MB)*100/maxsize_MB) "USED_MAX"
from (
select 
tablespace_name,case when sum(decode(autoextensible,'YES',1,0))>0 then 'YES'
else 'NO' end ext,
trunc(sum(bytes)/1024/1024) SIZE_MB,
trunc(sum(case when autoextensible='YES' then maxbytes else bytes
end)/1024/1024) MAXSIZE_MB
from dba_data_files 
group by tablespace_name 
) t1,
(
select tablespace_name,trunc(sum(bytes)/1024/1024) FREE_MB
from dba_free_space
group by tablespace_name
) t2,
(select tablespace_name
from dba_tablespaces
where contents='PERMANENT') t3
where t1.tablespace_name=t2.tablespace_name and t3.tablespace_name=t1.tablespace_name
order by t1.tablespace_name