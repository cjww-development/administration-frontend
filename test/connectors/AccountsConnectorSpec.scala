/*
 * Copyright 2019 CJWW Development
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

package connectors

import com.cjwwdev.http.verbs.Http
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.obfuscation.Obfuscation.stringObfuscate
import helpers.connectors.ConnectorSpec

import scala.concurrent.ExecutionContext.Implicits.global

class AccountsConnectorSpec extends ConnectorSpec {

  val testConnector = new AccountsConnector {
    override val http: Http = mockHttp
    override val accountsRoute: String = "/test/url"
  }

  "getUserId" should {
    "return a right string" when {
      "the user name exists and the corresponding user id is returned" in {
        mockHttpGet(response = fakeHttpResponse(statusCode = OK, bodyContents = "testUserId".encrypt))

        awaitAndAssert(testConnector.getUserId("testUserName")) {
          _ mustBe Right("testUserId")
        }
      }
    }

    "return a left WSResponse" when {
      "the user id fails decryption" in {
        val resp = fakeHttpResponse(statusCode = OK, bodyContents = "testUserId")

        mockHttpGet(response = resp)

        awaitAndAssert(testConnector.getUserId("testUserName")) {
          _ mustBe Left(resp)
        }
      }

      "the user doesn't exist" in {
        val resp = fakeHttpResponse(statusCode = NOT_FOUND)

        mockHttpGet(response = resp)

        awaitAndAssert(testConnector.getUserId("testUserName")) {
          _ mustBe Left(resp)
        }
      }
    }
  }
}
