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

package controllers

import connectors.AdminConnector
import helpers.controllers.ControllerSpec
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._

class DashboardControllerSpec extends ControllerSpec {

  val testController = new DashboardController {
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
    override val adminConnector: AdminConnector                       = mockAdminConnector
  }

  lazy val requestWithSession = request.withSession(
    "cookieId" -> generateTestSystemId(MANAGEMENT),
    "username" -> testAccount.username
  )

  "dashboard" should {
    "return an Ok" in {
      mockGetManagementUser(found = true)

      assertResult(testController.dashboard()(requestWithSession)) { res =>
        status(res) mustBe OK
      }
    }

    "return a See other and redirect to login" in {
      assertResult(testController.dashboard()(request)) { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some(controllers.routes.LoginController.login().url)
      }
    }
  }

  "registerUser" should {
    "return an Ok" in {
      mockGetManagementUser(found = true)

      assertResult(testController.registerUser()(addCSRFToken(requestWithSession))) { res =>
        status(res) mustBe OK
      }
    }

    "return a forbidden" when {
      "the user doesn't have permissions to view the page" in {
        mockGetManagementUser(found = true, permissions = List("invalid"))

        assertResult(testController.registerUser()(addCSRFToken(requestWithSession))) { res =>
          status(res) mustBe FORBIDDEN
        }
      }
    }
  }

  "registerUserSubmit" should {
    "return a Bad request" in {
      val requestWithForm = requestWithSession.withFormUrlEncodedBody(
        "username"        -> "",
        "email"           -> "",
        "password"        -> "",
        "confirmPassword" -> "",
        "permissions"     -> ""
      )

      mockGetManagementUser(found = true)

      assertResult(testController.registerUserSubmit()(addCSRFToken(requestWithForm))) { res =>
        status(res) mustBe BAD_REQUEST
      }
    }

    "return a See other" in {
      val requestWithForm = requestWithSession.withFormUrlEncodedBody(
        "username"        -> "testUserN",
        "email"           -> "test@email.com",
        "password"        -> "Test1234567",
        "confirmPassword" -> "Test1234567",
        "permissions"     -> "all"
      )

      mockGetManagementUser(found = true)

      mockRegisterNewUser(registered = true)

      assertResult(testController.registerUserSubmit()(addCSRFToken(requestWithForm))) { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some(routes.DashboardController.usersOverview().url)
      }
    }

    "return an Internal server error" in {
      val requestWithForm = requestWithSession.withFormUrlEncodedBody(
        "username"        -> "testUserN",
        "email"           -> "test@email.com",
        "password"        -> "Test1234567",
        "confirmPassword" -> "Test1234567",
        "permissions"     -> "all"
      )

      mockGetManagementUser(found = true)

      mockRegisterNewUser(registered = false)

      assertResult(testController.registerUserSubmit()(addCSRFToken(requestWithForm))) { res =>
        status(res) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "usersOverview" should {
    "return an Ok" in {
      mockGetManagementUser(found = true)

      mockGetAllManagementUsers(populated = true)

      assertResult(testController.usersOverview()(requestWithSession)) { res =>
        status(res) mustBe OK
      }
    }
  }

  "viewUser" should {
    "return an Ok" in {
      mockGetManagementUser(found = true)

      assertResult(testController.viewUser(generateTestSystemId(MANAGEMENT))(requestWithSession)) { res =>
        status(res) mustBe OK
      }
    }
  }

  "deleteUser" should {
    "return a See other" in {
      mockGetManagementUser(found = true)

      mockDeleteManagementUser(deleted = true)

      assertResult(testController.deleteUser(generateTestSystemId(MANAGEMENT))(requestWithSession)) { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some(routes.DashboardController.usersOverview().url)
      }
    }
  }
}
