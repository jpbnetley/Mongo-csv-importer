[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Scala CI](https://github.com/jpbnetley/Mongo-csv-importer/actions/workflows/scala.yml/badge.svg)](https://github.com/jpbnetley/Mongo-csv-importer/actions/workflows/scala.yml)

# Mongo-csv-importer
Imports csv items into mongo

# Injecting environment variables

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
