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
import services.{HealthService, Healthy}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.any

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class AppStateControllerSpec extends ControllerSpec {

  val mockHealthService = mock[HealthService]

  val testController = new AppStateController {
    override val healthService: HealthService                         = mockHealthService
    override val adminConnector: AdminConnector                       = mockAdminConnector
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
  }

  lazy val requestWithSession = request.withSession(
    "cookieId" -> generateTestSystemId(MANAGEMENT),
    "username" -> testAccount.username
  )

  "showUUIDGenerator" should {
    "return an Ok" in {
      mockGetManagementUser(found = true)

      when(mockHealthService.getServicesHealth(any()))
        .thenReturn(Future(Map(
          "service1" -> Healthy,
          "service2" -> Healthy
        )))

      assertResult(testController.show()(requestWithSession)) { res =>
        status(res) mustBe OK
      }
    }
  }
}
