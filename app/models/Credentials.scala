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

case class Credentials(username: String, password: String)

object Credentials {
  implicit val format: OFormat[Credentials] = new OFormat[Credentials] {
    override def writes(creds: Credentials): JsObject = Json.obj(
      "username" -> creds.username,
      "password" -> SHA512.encrypt(creds.password)
    )

    override def reads(json: JsValue): JsResult[Credentials] = Json.fromJson(json)(Json.reads[Credentials])
  }

  implicit val writes: OWrites[Credentials] = OWrites[Credentials] { creds =>
    Json.obj(
      "username" -> creds.username,
      "password" -> SHA512.encrypt(creds.password)
    )
  }

  implicit val obfuscator: Obfuscator[Credentials] = new Obfuscator[Credentials] {
    override def encrypt(value: Credentials): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }
}
