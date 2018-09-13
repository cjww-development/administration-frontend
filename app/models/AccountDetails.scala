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

import com.cjwwdev.security.deobfuscation.{DeObfuscation, DeObfuscator, DecryptionError}
import play.api.libs.json.{Json, OFormat}

case class AccountDetails(managementId: String,
                          username: String,
                          email: String,
                          permissions: List[String])

object AccountDetails {
  implicit val format: OFormat[AccountDetails] = Json.format[AccountDetails]

  implicit val deObfuscator: DeObfuscator[AccountDetails] = new DeObfuscator[AccountDetails] {
    override def decrypt(value: String): Either[AccountDetails, DecryptionError] = DeObfuscation.deObfuscate(value)
  }

  implicit val listDeOfuscator: DeObfuscator[List[AccountDetails]] = new DeObfuscator[List[AccountDetails]] {
    override def decrypt(value: String): Either[List[AccountDetails], DecryptionError] = {
      DeObfuscation.deObfuscate[List[AccountDetails]](value)
    }
  }
}
