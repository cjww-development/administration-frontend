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

import connectors.ShutteringConnector
import helpers.services.ServiceSpec
import play.api.test.FakeRequest
import play.api.test.Helpers.NO_CONTENT
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.any
import play.api.mvc.AnyContentAsEmpty

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ShutteringServiceSpec extends ServiceSpec {

  private val mockShutteringConnector = mock[ShutteringConnector]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  private val testService = new ShutteringService {
    override val shutteringConnector: ShutteringConnector = mockShutteringConnector
    override val serviceUrls: Map[String, String]         = Map(
      "service-1" -> "/test/service-1",
      "service-2" -> "/test/service-2",
      "service-3" -> "/test/service-3"
    )
  }

  private val shutterValues = Map(
    "service-1" -> false,
    "service-2" -> true,
    "service-3" -> false
  )

  "shutterServices" should {
    "a map of services to shutter states" in {
      when(mockShutteringConnector.shutterService(any(), any())(any(), any()))
        .thenReturn(
          Future.successful(fakeHttpResponse(statusCode = NO_CONTENT)),
          Future.successful(fakeHttpResponse(statusCode = NO_CONTENT)),
          Future.successful(fakeHttpResponse(statusCode = NO_CONTENT))
        )

      awaitAndAssert(testService.shutterServices(shutterValues)) {
        _ mustBe shutterValues
      }
    }
  }

  "getShutterStates" should {
    "a map of services to shutter states" in {
      when(mockShutteringConnector.getShutterState(any())(any(), any()))
        .thenReturn(
          Future.successful(false),
          Future.successful(true),
          Future.successful(false)
        )

      awaitAndAssert(testService.getShutterStates) {
        _ mustBe shutterValues
      }
    }
  }
}
