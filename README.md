# Cancel/Deactivate jobs in AWA(uc4).

> Only works with PostgresSql as of now, you're welcome to add more :)

## Configs
> Make sure to add uc4.jar and psql jdbc driver to the lib directory of the project when working.



### Usage
```
usage: DataNova.jar
-c,--client <NUMBER>              UC4 client number
-CANCEL                           To cancel objects, only use if status
                                  selection is blocked - deactivate, if
                                  this flag is not mentioned
-DBPwd <PASSWORD>                 Password for the database user
-DBUser <USERNAME>                User name for the database
-dept,--uc4Department <DPT>       Department of UC4 user
-h,--host <servername>            Hostname or IP of the AE server
-IGNORE                           Overrides commented ignores
-jdbc <URL>                       JDBC URL
-key,--archiveKey <ARCHIVE_KEY>   Archive key for the tasks to be
                                  considered, '%' for all
-n <NUMBER>                       Number of days to be excluded from
                                  today,default is 30
-p,--port <NUMBER>                Port for UC4 CP
-status <arg>                     Can be 'ENDED_OK'(default),
                                  'ENDED_NOT_OK', 'ANY_OK', 'ANY_ABEND',
                                  'BLOCKED'
-u,--uc4User <USERNAME>           User name of the UC4 user
-uc4pwd <PASSWORD>                Password for the UC4
```

### example
```declarative
C:\DataNova>java -jar DataNova.jar -DBUser automic -DBPwd 1 -n 5 -key % -u UC -dept UC -h myaehost -uc4pwd UC -p 2217 -c 1 -jdbc jdbc:postgresql://mydbhost:5432/aedb
22102024/105111.169 [INFO] Connected to AE at :'1->UC4#CP002' successfully.
22102024/105111.367 [INFO] Connected to DB
22102024/105111.422 [INFO] Deactivating 993 tasks.
22102024/105111.642 [INFO] Tasks deactivated successfully.
22102024/105111.642 [INFO] Database connection closed
22102024/105111.644 [INFO] UC4 connection closed
```


### Some unnecessary explanations
-- database creds are passed as cline args to the thing, this is to avoid storing passwords as plain text anywhere, the idea of the code is to be run inside a UC4 job itself so that you can obscure the password
using a custom login type and running it with the job messenger (trust me, this guy is not just a messenger ;) ).

-- added support for only few job statuses, not because of any issues/limitations just because I'm lazy(kind of a limitation?)

-- I believe cancelled tasks should be deactivated as well, any disagreements?
