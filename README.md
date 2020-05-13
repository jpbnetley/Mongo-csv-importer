# Mongo-csv-importer
Imports csv items into mongo

#Injecting environment variables

Environment variables can be injected in 2 ways.
1. A config file.
2. The system environment variables.

When using the config file, create a config file `application.conf` located under  
`src/main/resources`

## Required Environment variables
```
mongo_address=
mongo_port=
mongo_db_name=
```

## Optional auth environment variables
```
mongo_auth_uname=
mongo_auth_pw=
```

# Usage
When the user gets prompted for their input to the path, the raw path to the files should be  
entered.
eg: `c:/users/username/desktop/csvFiles`  
The files under this directory should be `.csv`
