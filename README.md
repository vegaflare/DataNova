# Just for fun project - not an enterprise grade solution nor am I a Java programmer, so take it as it is.

> Only works with PostgresSql as of now, you're welcome to add more :)

## Configs
> Make sure to add uc4.jar(v21) and psql jdbc driver to the working directory of the project when working.

> config.properties.ini is available as an example in the dir /config-examples in the project base dir, you can copy the ini file to the base dir and modify accordingly


### Some unnecessary explanations
-- database creds are passed as cline args to the thing, this is to avoid storing passwords as plain text anywhere, the idea of the code is to be run inside a UC4 job itself so that you can obscure the password
   using a custom login type and running it with the job messenger (trust me, this guy is not just a messenger ;) ).

-- added support for only few job statuses, not because of any issues/limitations just because I'm lazy(kind of a limitation?)

-- I believe cancelled tasks should be deactivated as well, any disagreements?


### example
C:\temp>java -jar DataNova.jar -key % -d 20 -DBUser uc4 -DBPwd 1234
2024-09-30 10:42:16.864:INFO::main: Logging initialized @302ms to org.eclipse.jetty.util.log.StdErrLog
[INFO] 30092024/104218 : Connected to AE at :'14->UC4#CP002' successfully.
[INFO] 30092024/104218 : Connected to DB
[INFO] 30092024/104218 : Deactivating 993 tasks.
[INFO] 30092024/104225 : Tasks deactivated successfully.
