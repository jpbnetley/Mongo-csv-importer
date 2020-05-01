package Util.models

final case class SystemConfigPropertiesResponse(mongo_address: String,
                                                mongo_port: Int,
                                                mongo_db_name: String,
                                                mongo_auth_uname: Option[String],
                                                mongo_auth_pw: Option[Array[Char]])
