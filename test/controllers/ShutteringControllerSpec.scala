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

import com.cjwwdev.http.headers.HeaderPackage
import connectors.AdminConnector
import helpers.controllers.ControllerSpec
import play.api.mvc.{AnyContentAsFormUrlEncoded, ControllerComponents}
import com.cjwwdev.implicits.ImplicitDataSecurity._
import play.api.test.CSRFTokenHelper.addCSRFToken
import play.api.test.FakeRequest
import services.ShutteringService
import play.api.test.Helpers._

class ShutteringControllerSpec extends ControllerSpec {

  val testController = new ShutteringController {
    override val shutteringService: ShutteringService                 = mockShutteringService
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
    override val adminConnector: AdminConnector                       = mockAdminConnector
  }

  lazy val requestWithSession = request.withSession(
    "cookieId" -> generateTestSystemId(MANAGEMENT),
    "username" -> testAccount.username
  )

  "show" should {
    "return an Ok" in {
      mockGetManagementUser(found = true)
      mockGetShutterStates

      assertResult(testController.show()(addCSRFToken(requestWithSession))) { res =>
        status(res) mustBe OK
      }
    }
  }

  "submit" should {
    "return a See other" in {
      val request: FakeRequest[AnyContentAsFormUrlEncoded] = requestWithSession
        .withHeaders("cjww-headers" -> HeaderPackage("testAppId", Some(generateTestSystemId(MANAGEMENT))).encrypt)
        .withFormUrlEncodedBody(
          "shutterState[service1]" -> "true"
        )

      mockGetManagementUser(found = true)
      mockShutterService

      assertResult(testController.submit()(addCSRFToken(request))) { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some(controllers.routes.ShutteringController.show().url)
      }
    }
  }
}
