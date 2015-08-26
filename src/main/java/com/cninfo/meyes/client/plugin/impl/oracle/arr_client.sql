SELECT spid,
         os_user,
         sid,
         SERIAL# SERIAL,
         status,
         TO_CHAR (logon_time, 'YYYY/MM/DD HH24:MI:SS') logon_time,
         username,
         machine,
         program
    FROM (  SELECT c.spid,
                   c.username os_user,
                   sid,
                   a.status,
                   a.SERIAL#,
                   a.logon_time,
                   a.username,
                   a.machine,
                   a.program
              FROM v$session a, v$sqltext b, v$process c
             WHERE     a.paddr = c.addr
                   AND a.sql_hash_value = b.HASH_VALUE(+)
                   AND a.username IS NOT NULL
                   AND a.program NOT IN ('toad.exe',
                                         'PLSQLDev.exe',
                                         'plsqldev.exe')
                   AND a.username NOT IN ('MON')
          ORDER BY sid, a.SERIAL#, b.piece)
GROUP BY spid,
         os_user,
         sid,
         SERIAL#,
         status,
         logon_time,
         username,
         machine,
         program