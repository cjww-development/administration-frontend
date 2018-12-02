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

import com.cjwwdev.http.verbs.Http
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.obfuscation.Obfuscation._
import helpers.connectors.ConnectorSpec
import models.AccountDetails

import scala.concurrent.ExecutionContext.Implicits.global

class AdminConnectorSpec extends ConnectorSpec {

  private val testConnector = new AdminConnector {
    override val http: Http       = mockHttp
    override val adminUrl: String = "/test/url"
  }

  "registerNewUser" should {
    "return true" when {
      "the user has been registered" in {
        mockHttpPost(response = fakeHttpResponse(statusCode = OK))

        awaitAndAssert(testConnector.registerNewUser(registration = testRegistration)) { registered =>
          assert(registered)
        }
      }
    }

    "return false" when {
      "the user has not been registered" in {
        mockHttpPost(response = fakeHttpResponse(statusCode = INTERNAL_SERVER_ERROR))

        awaitAndAssert(testConnector.registerNewUser(registration = testRegistration)) { registered =>
          assert(!registered)
        }
      }
    }
  }

  "authenticateUser" should {
    "return a managementId" when {
      "the user has been authenticated" in {
        mockHttpPost(response = fakeHttpResponse(statusCode = OK, bodyContents = generateTestSystemId(MANAGEMENT).encrypt))

        awaitAndAssert(testConnector.authenticateUser(credentials = testCreds)) {
          _ mustBe Some(generateTestSystemId(MANAGEMENT))
        }
      }
    }

    "return none" when {
      "the user could not be authenticated" in {
        mockHttpPost(response = fakeHttpResponse(statusCode = FORBIDDEN))

        awaitAndAssert(testConnector.authenticateUser(credentials = testCreds)) {
          _ mustBe None
        }
      }

      "a Forbidden exception was thrown" in {
        mockHttpPost(response = fakeHttpResponse(statusCode = FORBIDDEN))

        awaitAndAssert(testConnector.authenticateUser(credentials = testCreds)) {
          _ mustBe None
        }
      }
    }
  }

  "getManagementUser" should {
    "return an account details" in {
      mockHttpGet(response = fakeHttpResponse(statusCode = OK, bodyContents = testAccountDetails.encrypt))

      awaitAndAssert(testConnector.getManagementUser(managementId = generateTestSystemId(MANAGEMENT))) {
        _ mustBe testAccountDetails
      }
    }
  }

  "getAllManagementUsers" should {
    "a populated list" in {
      mockHttpGet(response = fakeHttpResponse(statusCode = OK, bodyContents = List(testAccountDetails).encrypt))

      awaitAndAssert(testConnector.getAllManagementUsers) { res =>
        assert(res.nonEmpty)
        res mustBe List(testAccountDetails)
      }
    }

    "an empty list" in {
      mockHttpGet(response = fakeHttpResponse(statusCode = NO_CONTENT))

      awaitAndAssert(testConnector.getAllManagementUsers) { res =>
        assert(res.isEmpty)
        res mustBe List.empty[AccountDetails]
      }
    }
  }

  "deleteManagementUser" should {
    "return true" in {
      mockHttpDelete(response = fakeHttpResponse(statusCode = NO_CONTENT))

      awaitAndAssert(testConnector.deleteManagementUser(generateTestSystemId(MANAGEMENT))) { bool =>
        assert(bool)
      }
    }

    "return false" in {
      mockHttpDelete(response = fakeHttpResponse(statusCode = INTERNAL_SERVER_ERROR))

      awaitAndAssert(testConnector.deleteManagementUser(generateTestSystemId(MANAGEMENT))) { bool =>
        assert(!bool)
      }
    }
  }
}
