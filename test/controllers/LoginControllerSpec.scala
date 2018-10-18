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
import com.cjwwdev.implicits.ImplicitDataSecurity._
import helpers.controllers.ControllerSpec
import play.api.mvc.{AnyContentAsFormUrlEncoded, ControllerComponents}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.LoginService

class LoginControllerSpec extends ControllerSpec {

  val testController = new LoginController {
    override val loginService: LoginService                           = mockLoginService
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
  }

  "login" should {
    "return an Ok" in {
      assertResult(testController.login()(addCSRFToken(request))) { res =>
        status(res) mustBe OK
      }
    }
  }

  "loginSubmit" should {
    "redirect to login" when {
      "the user has successfully logged in" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest()
          .withHeaders("cjww-headers" -> HeaderPackage("testAppId", Some(generateTestSystemId(MANAGEMENT))).encrypt)
          .withFormUrlEncodedBody(
            "username" -> "invalid",
            "password" -> "invalid"
          )

        mockProcessLogin(authenticated = true)

        assertResult(testController.loginSubmit()(addCSRFToken(request))) { res =>
          status(res)           mustBe SEE_OTHER
          redirectLocation(res) mustBe Some(controllers.routes.DashboardController.dashboard().url)
        }
      }
    }

    "return a Bad request" when {
      "the user provided no form values" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest()
          .withHeaders("cjww-headers" -> HeaderPackage("testAppId", Some(generateTestSystemId(MANAGEMENT))).encrypt)
          .withFormUrlEncodedBody(
            "username" -> "",
            "password" -> ""
          )

        assertResult(testController.loginSubmit()(addCSRFToken(request))) { res =>
          status(res) mustBe BAD_REQUEST
        }
      }

      "the user was not successfully logged" in {
        val request: FakeRequest[AnyContentAsFormUrlEncoded] = FakeRequest()
          .withHeaders("cjww-headers" -> HeaderPackage("testAppId", Some(generateTestSystemId(MANAGEMENT))).encrypt)
          .withFormUrlEncodedBody(
            "username" -> "invalid",
            "password" -> "invalid"
          )

        mockProcessLogin(authenticated = false)

        assertResult(testController.loginSubmit()(addCSRFToken(request))) { res =>
          status(res) mustBe BAD_REQUEST
        }
      }
    }
  }

  "signOut" should {
    "redirect to the login page" in {
      assertResult(testController.signOut()(addCSRFToken(request))) { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some(controllers.routes.LoginController.login().url)
      }
    }
  }
}
