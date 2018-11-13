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

import connectors.HealthConnector
import helpers.services.ServiceSpec
import play.api.test.FakeRequest
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.any
import play.api.test.Helpers._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class HealthServiceSpec extends ServiceSpec {

  val mockHealthConnector = mock[HealthConnector]

  implicit lazy val request = FakeRequest()

  val testService = new HealthService {
    override val healthConnector: HealthConnector = mockHealthConnector
    override val serviceUrls: Map[String, String] = Map(
      "service1" -> "/test/service1",
      "service2" -> "/test/service2"
    )
  }

  "getServicesHealth" should {
    "return a map of service to health statuses (Healthy)" in {

      when(mockHealthConnector.getHealthStatus(any())(any()))
        .thenReturn(
          Future(OK),
          Future(OK)
        )

      awaitAndAssert(testService.getServicesHealth) {
        _ mustBe Map(
          "service1" -> Healthy,
          "service2" -> Healthy
        )
      }
    }

    "return a map of service to health statuses (Unknown)" in {

      when(mockHealthConnector.getHealthStatus(any())(any()))
        .thenReturn(
          Future(BAD_REQUEST),
          Future(FORBIDDEN)
        )

      awaitAndAssert(testService.getServicesHealth) {
        _ mustBe Map(
          "service1" -> Unknown,
          "service2" -> Unknown
        )
      }
    }

    "return a map of service to health statuses (Problems)" in {

      when(mockHealthConnector.getHealthStatus(any())(any()))
        .thenReturn(
          Future(INTERNAL_SERVER_ERROR),
          Future(INTERNAL_SERVER_ERROR)
        )

      awaitAndAssert(testService.getServicesHealth) {
        _ mustBe Map(
          "service1" -> Problems,
          "service2" -> Problems
        )
      }
    }
  }
}
