SELECT owner||'.'||object_name object_name,object_type,status
  FROM SYS.dba_objects
 WHERE status <> 'VALID' AND owner not in('SYS','SYSTEM','PUBLIC','DBSNMP','QUEST','WMSYS')
UNION ALL
SELECT owner || '.' || index_name object_name, 'INDEX' object_type, status
  FROM SYS.dba_indexes
 WHERE status = 'UNUSABLE'
   AND owner NOT IN ('SYS', 'SYSTEM', 'PUBLIC', 'DBSNMP', 'QUEST', 'WMSYS', 'EXFSYS')
UNION ALL
SELECT index_owner || '.' || index_name object_name, 'INDEX' object_type,status
  FROM SYS.dba_ind_partitions
 WHERE status = 'UNUSABLE'
   AND index_owner NOT IN ('SYS', 'SYSTEM', 'PUBLIC', 'DBSNMP', 'QUEST', 'WMSYS', 'EXFSYS')