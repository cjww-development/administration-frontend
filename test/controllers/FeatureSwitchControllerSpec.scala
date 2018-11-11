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
import models.Feature
import play.api.mvc.ControllerComponents
import services.FeatureSwitchService
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.any

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class FeatureSwitchControllerSpec extends ControllerSpec {

  val mockFeatureSwitchService = mock[FeatureSwitchService]

  val testController = new FeatureSwitchController {
    override val featureSwitchService: FeatureSwitchService           = mockFeatureSwitchService
    override val services: List[String]                               = List("service1", "service2")
    override val adminConnector: AdminConnector                       = mockAdminConnector
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
  }

  lazy val requestWithSession = request.withSession(
    "cookieId" -> generateTestSystemId(MANAGEMENT),
    "username" -> testAccount.username
  )

  "show" should {
    "return an Ok" in {
      mockGetManagementUser(found = true)

      when(mockFeatureSwitchService.getFeatures(any()))
        .thenReturn(Future(Map(
          "service1" -> List(Feature(name = "feature", state = false)),
          "service2" -> List()
        )))

      assertResult(testController.show()(addCSRFToken(requestWithSession))) { res =>
        status(res) mustBe OK
      }
    }
  }

  "submit" should {
    "return a See Other" in {
      mockGetManagementUser(found = true)

      val req = requestWithSession.withFormUrlEncodedBody(
        "service1[feature]" -> "true"
      )

      when(mockFeatureSwitchService.setFeature(any(), any())(any()))
        .thenReturn(Future(List(Feature(name = "feature", state = false))))

      assertResult(testController.submit()(addCSRFToken(req))) { res =>
        status(res)           mustBe SEE_OTHER
        redirectLocation(res) mustBe Some(routes.FeatureSwitchController.show().url)
      }
    }
  }
}
