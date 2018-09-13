/*
 * Copyright 2018 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import com.cjwwdev.security.sha.SHA512
import play.api.libs.json._

case class Registration(username: String,
                        email: String,
                        password: String,
                        confirmPassword: String,
                        permissions: String)

object Registration {
  implicit val format: OFormat[Registration] = new OFormat[Registration] {
    override def writes(reg: Registration): JsObject = Json.obj(
      "username"    -> reg.username,
      "email"       -> reg.email,
      "password"    -> SHA512.encrypt(reg.password),
      "permissions" -> Json.toJson(reg.permissions.replace(" ", "").split(",").toList)
    )

    override def reads(json: JsValue): JsResult[Registration] = Json.fromJson(json)(Json.reads[Registration])
  }

  implicit val obfuscator: Obfuscator[Registration] = new Obfuscator[Registration] {
    override def encrypt(value: Registration): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }
}


