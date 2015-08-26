select dest_name,type,database_mode,recovery_mode,protection_mode,destination,archived_seq# archived_seq,applied_seq# applied_seq,
synchronization_status,archived_seq#-applied_seq# LOG_GAP,'('||status||') '||synchronization_status STATUS,synchronized sync
from V$ARCHIVE_DEST_STATUS
where status <>'INACTIVE'