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

class DashboardControllerSpec extends ControllerSpec {

  val testController = new DashboardController {
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
    override val adminConnector: AdminConnector                       = mockAdminConnector
  }

  "dashboard" should {
    "return an Ok" in {
      val requestWithSession = request.withSession(
        "cookieId" -> generateTestSystemId(MANAGEMENT),
        "username" -> testAccount.username
      )

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
}
