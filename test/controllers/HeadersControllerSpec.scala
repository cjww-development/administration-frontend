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
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents}
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class HeadersControllerSpec extends ControllerSpec {

  private val testController = new HeadersController {
    override val adminConnector: AdminConnector                       = mockAdminConnector
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
    override implicit val ec: ExecutionContext                        = global
  }

  lazy val requestWithSession: FakeRequest[AnyContentAsEmpty.type] = request.withSession(
    "cookieId" -> generateTestSystemId(MANAGEMENT),
    "username" -> testAccount.username
  )

  "headers" should {
    "return an Ok" in {
      mockGetManagementUser(found = true)

      assertResult(testController.headers()(addCSRFToken(requestWithSession))) { res =>
        status(res) mustBe OK
      }
    }
  }

  "headersSubmit" should {
    "return a Bad request" in {
      val requestWithForm = requestWithSession.withFormUrlEncodedBody(
        "appId"    -> "",
        "cookieId" -> ""
      )

      mockGetManagementUser(found = true)

      assertResult(testController.headersSubmit()(addCSRFToken(requestWithForm))) { res =>
        status(res) mustBe BAD_REQUEST
      }
    }

    "return an Ok" when {
      "only an appId is provided" in {
        val requestWithForm = requestWithSession.withFormUrlEncodedBody(
          "appId"    -> "testAppId",
          "cookieId" -> ""
        )

        mockGetManagementUser(found = true)

        assertResult(testController.headersSubmit()(addCSRFToken(requestWithForm))) { res =>
          status(res) mustBe OK
        }
      }

      "both appId and cookieId are provided" in {
        val requestWithForm = requestWithSession.withFormUrlEncodedBody(
          "appId"    -> "testAppId",
          "cookieId" -> "testCookieId"
        )

        mockGetManagementUser(found = true)

        assertResult(testController.headersSubmit()(addCSRFToken(requestWithForm))) { res =>
          status(res) mustBe OK
        }
      }
    }
  }
}
