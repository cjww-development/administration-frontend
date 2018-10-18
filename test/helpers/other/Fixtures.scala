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
package helpers.other

import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import models.{Account, AccountDetails, Credentials, Registration}
import play.api.libs.json.Json
import play.api.mvc.Session

trait Fixtures extends TestDataGenerator {

  val testAccount = Account(
    username    = "testUserName",
    email       = "testEmail",
    password    = "testPassword",
    permissions = List("all")
  )

  val testRegistration = Registration(
    username        = "testUserName",
    email           = "testEmail",
    password        = "testPassword",
    confirmPassword = "testPassword",
    permissions     = "all"
  )

  val testCreds = Credentials(
    username = "testUserName",
    password = "testPassword"
  )

  val testAccountDetails = AccountDetails(
    managementId = generateTestSystemId(MANAGEMENT),
    username     = "testUserName",
    email        = "test@email.com",
    permissions  = List("all")
  )

  val testSession = Session(data = Map(
    "cookieId" -> generateTestSystemId(MANAGEMENT),
    "username" -> testAccount.username
  ))

  implicit val accDetailsObfuscator: Obfuscator[AccountDetails] = new Obfuscator[AccountDetails] {
    override def encrypt(value: AccountDetails): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }

  implicit val accDetailsListObfuscator: Obfuscator[List[AccountDetails]] = new Obfuscator[List[AccountDetails]] {
    override def encrypt(value: List[AccountDetails]): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }
}
