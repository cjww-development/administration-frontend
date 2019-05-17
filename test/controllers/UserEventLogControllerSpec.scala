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

package controllers

import connectors.AdminConnector
import helpers.controllers.ControllerSpec
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents}
import play.api.test.CSRFTokenHelper.addCSRFToken
import play.api.test.FakeRequest
import services.EventLogService
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext

class UserEventLogControllerSpec extends ControllerSpec {

  val testController = new UserEventLogController {
    override val eventLogService: EventLogService                     = mockEventLogService
    override val adminConnector: AdminConnector                       = mockAdminConnector
    override implicit val ec: ExecutionContext                        = stubControllerComponents().executionContext
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
  }

  lazy val requestWithSession: FakeRequest[AnyContentAsEmpty.type] = request.withSession(
    "cookieId" -> generateTestSystemId(MANAGEMENT),
    "username" -> testAccount.username
  )

  "show" should {
    "return an Ok" in {
      mockGetManagementUser(found = true)
      mockGetEventLogs(populated = false)

      assertResult(testController.show()(addCSRFToken(requestWithSession))) { res =>
        status(res) mustBe OK
      }
    }
  }

  "submit" should {
    "return an Ok" when {
      "there are logs to display" in {
        mockGetManagementUser(found = true)
        mockGetEventLogs(populated = true)

        assertResult(testController.submit()(addCSRFToken(requestWithSession.withFormUrlEncodedBody("userName" -> "testUserName")))) { res =>
          status(res) mustBe OK
        }
      }

      "there are no logs" in {
        mockGetManagementUser(found = true)
        mockGetEventLogs(populated = false)

        assertResult(testController.submit()(addCSRFToken(requestWithSession.withFormUrlEncodedBody("userName" -> "testUserName")))) { res =>
          status(res) mustBe OK
        }
      }
    }

    "return a Bad request" in {
      mockGetManagementUser(found = true)

      assertResult(testController.submit()(addCSRFToken(requestWithSession.withFormUrlEncodedBody("userNam" -> "")))) { res =>
        status(res) mustBe BAD_REQUEST
      }
    }
  }
}
