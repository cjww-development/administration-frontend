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

import com.cjwwdev.security.obfuscation.Obfuscation._
import com.cjwwdev.implicits.ImplicitDataSecurity._
import connectors.AdminConnector
import helpers.controllers.ControllerSpec
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents}
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class EncDecControllerSpec extends ControllerSpec {

  private val testController = new EncDecController {
    override val adminConnector: AdminConnector                       = mockAdminConnector
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
    override implicit val ec: ExecutionContext                        = global
  }

  lazy val requestWithSession: FakeRequest[AnyContentAsEmpty.type] = request.withSession(
    "cookieId" -> generateTestSystemId(MANAGEMENT),
    "username" -> testAccount.username
  )

  "showEncDecOptions" should {
    "return an Ok" in {
      mockGetManagementUser(found = true)

      assertResult(testController.showEncDecOptions()(addCSRFToken(requestWithSession))) { res =>
        status(res) mustBe OK
      }
    }
  }

  "showSHA512" should {
    "return an Ok" in {
      mockGetManagementUser(found = true)

      assertResult(testController.showSHA512()(addCSRFToken(requestWithSession))) { res =>
        status(res) mustBe OK
      }
    }
  }

  "submitSHA512" should {
    "return a Bad request" in {
      val req = requestWithSession.withFormUrlEncodedBody(
        "data" -> ""
      )

      mockGetManagementUser(found = true)

      assertResult(testController.submitSHA512()(addCSRFToken(req))) { res =>
        status(res) mustBe BAD_REQUEST
      }
    }

    "return an Ok" in {
      val req = requestWithSession.withFormUrlEncodedBody(
        "data" -> "test"
      )

      mockGetManagementUser(found = true)

      assertResult(testController.submitSHA512()(addCSRFToken(req))) { res =>
        status(res) mustBe OK
      }
    }
  }

  "showDataSecurity" should {
    "return an Ok" in {
      mockGetManagementUser(found = true)

      assertResult(testController.showDataSecurity()(addCSRFToken(requestWithSession))) { res =>
        status(res) mustBe OK
      }
    }
  }

  "submitDataSecurity" should {
    "return a Bad request" in {
      val req = requestWithSession.withFormUrlEncodedBody(
        "data"      -> "",
        "data-type" -> "",
        "mode"      -> ""
      )

      mockGetManagementUser(found = true)

      assertResult(testController.submitDataSecurity()(addCSRFToken(req))) { res =>
        status(res) mustBe BAD_REQUEST
      }
    }

    "return an Ok" when {
      "data type is string and mode is enc" in {
        val req = requestWithSession.withFormUrlEncodedBody(
          "data"      -> "test",
          "data-type" -> "string",
          "mode"      -> "enc"
        )

        mockGetManagementUser(found = true)

        assertResult(testController.submitDataSecurity()(addCSRFToken(req))) { res =>
          status(res) mustBe OK
        }
      }

      "data type is string and mode is dec" in {
        val req = requestWithSession.withFormUrlEncodedBody(
          "data"      -> "test".encrypt,
          "data-type" -> "string",
          "mode"      -> "dec"
        )

        mockGetManagementUser(found = true)

        assertResult(testController.submitDataSecurity()(addCSRFToken(req))) { res =>
          status(res) mustBe OK
        }
      }

      "data type is json and mode is enc" in {
        val req = requestWithSession.withFormUrlEncodedBody(
          "data"      -> """{ "abc" : "xyz" }""",
          "data-type" -> "json",
          "mode"      -> "enc"
        )

        mockGetManagementUser(found = true)

        assertResult(testController.submitDataSecurity()(addCSRFToken(req))) { res =>
          status(res) mustBe OK
        }
      }

      "data type is json and mode is dec" in {
        val req = requestWithSession.withFormUrlEncodedBody(
          "data"      -> """{ "abc" : "xyz" }""".encrypt,
          "data-type" -> "json",
          "mode"      -> "dec"
        )

        mockGetManagementUser(found = true)

        assertResult(testController.submitDataSecurity()(addCSRFToken(req))) { res =>
          status(res) mustBe OK
        }
      }
    }
  }
}
