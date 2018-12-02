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

package services

import connectors.AdminConnector
import helpers.services.ServiceSpec
import play.api.mvc.{AnyContentAsEmpty, Session}
import play.api.test.FakeRequest

import scala.concurrent.ExecutionContext.Implicits.global

class LoginServiceSpec extends ServiceSpec {

  private val testService = new LoginService {
    override val adminConnector: AdminConnector = mockAdminConnector
  }

  private val testSessionMap = Map(
    "cookieId" -> generateTestSystemId(MANAGEMENT),
    "username" -> testAccount.username
  )

  implicit lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "processLoginAttempt" should {
    "return a Session" when {
      "the user has been authenticated" in {
        mockAuthenticateUser(authenticated = true)
        mockGetManagementUser(found = true)

        awaitAndAssert(testService.processLoginAttempt(creds = testCreds)) {
          _ mustBe Some(Session(data = testSessionMap))
        }
      }
    }

    "return none" when {
      "authenticate failed" in {
        mockAuthenticateUser(authenticated = false)

        awaitAndAssert(testService.processLoginAttempt(creds = testCreds)) {
          _ mustBe None
        }
      }

      "get management user failed" in {
        mockAuthenticateUser(authenticated = true)
        mockGetManagementUser(found = false)

        awaitAndAssert(testService.processLoginAttempt(creds = testCreds)) {
          _ mustBe None
        }
      }
    }
  }
}
