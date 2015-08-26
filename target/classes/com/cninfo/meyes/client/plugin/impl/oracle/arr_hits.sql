SELECT  'buffer' name, 1-(SUM(DECODE(name, 'physical reads', VALUE, 0))/(SUM(DECODE(name,'db block gets',VALUE,0))+(SUM(DECODE(name,'consistent gets',VALUE,0))))) value
  FROM v$sysstat
  union  
select 'shared_pool' name,sum(pinhits-reloads)/sum(pins) value from v$librarycache
  union
select 'library' name,sum(pins-reloads)/sum(pins) value from v$librarycache