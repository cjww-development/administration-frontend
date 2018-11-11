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

import connectors.FeatureSwitchConnector
import helpers.services.ServiceSpec
import models.Feature
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.any
import play.api.test.FakeRequest
import play.api.test.Helpers.OK

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class FeatureSwitchServiceSpec extends ServiceSpec {

  val mockFeatureSwitchConnector = mock[FeatureSwitchConnector]

  val testService = new FeatureSwitchService {
    override val featureSwitchConnector: FeatureSwitchConnector = mockFeatureSwitchConnector
    override val serviceUrls: Map[String, String]               = Map(
      "service1" -> "/test/service1",
      "service2" -> "/test/service2"
    )
  }

  implicit lazy val request = FakeRequest()

  "getFeatures" should {
    "return a map of string -> list of features" in {
      when(mockFeatureSwitchConnector.getAllFeatureStates(any())(any()))
        .thenReturn(
          Future(List(Feature("feature", state = false))),
          Future(List())
        )

      awaitAndAssert(testService.getFeatures) {
        _ mustBe Map(
          "service1" -> List(Feature("feature", state = false)),
          "service2"  -> List()
        )
      }
    }
  }

  "setFeature" should {
    "return the supplied list of features" in {
      when(mockFeatureSwitchConnector.setFeatureState(any(), any(), any())(any()))
        .thenReturn(Future(fakeHttpResponse(OK)))

      awaitAndAssert(testService.setFeature("service1", List(Feature("feature", state = false)))) {
        _ mustBe List(Feature("feature", state = false))
      }
    }
  }
}
