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

package connectors

import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.http.verbs.Http
import helpers.connectors.ConnectorSpec

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class AdminConnectorSpec extends ConnectorSpec {

  val testConnector = new AdminConnector {
    override val http: Http       = mockHttp
    override val adminUrl: String = "/test/url"
  }

  "registerNewUser" should {
    "return true" when {
      "the user has been registered" in {
        mockHttpPost(response = Future(fakeHttpResponse(statusCode = OK)))

        awaitAndAssert(testConnector.registerNewUser(account = testAccount)) { registered =>
          assert(registered)
        }
      }
    }

    "return false" when {
      "the user has not been registered" in {
        mockHttpPost(response = Future(fakeHttpResponse(statusCode = INTERNAL_SERVER_ERROR)))

        awaitAndAssert(testConnector.registerNewUser(account = testAccount)) { registered =>
          assert(registered)
        }
      }
    }
  }

  "authenticateUser" should {
    "return a managementId" when {
      "the user has been authenticated" in {
        mockHttpPost(response = Future(fakeHttpResponse(statusCode = OK, bodyContents = generateTestSystemId(MANAGEMENT).encrypt)))

        awaitAndAssert(testConnector.authenticateUser(credentials = testCreds)) {
          _ mustBe Some(generateTestSystemId(MANAGEMENT))
        }
      }
    }

    "return none" when {
      "the user could not be authenticated" in {
        mockHttpPost(response = Future(fakeHttpResponse(statusCode = FORBIDDEN)))

        awaitAndAssert(testConnector.authenticateUser(credentials = testCreds)) {
          _ mustBe None
        }
      }
    }
  }

  "getManagementUser" should {
    "return an account details" in {
      mockHttpGet(response = Future(fakeHttpResponse(statusCode = OK, bodyContents = testAccountDetails.encryptType)))

      awaitAndAssert(testConnector.getManagementUser(managementId = generateTestSystemId(MANAGEMENT))) {
        _ mustBe testAccountDetails
      }
    }
  }
}
