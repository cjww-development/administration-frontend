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

package connectors

import com.cjwwdev.http.exceptions.ServerErrorException
import com.cjwwdev.http.verbs.Http
import helpers.connectors.ConnectorSpec
import models.Feature
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class FeatureSwitchConnectorSpec extends ConnectorSpec {

  val testConnector = new FeatureSwitchConnector {
    override val http: Http                                                  = mockHttp
    override val getAllFeaturesRoute: String                                 = "/test/route"
    override def setOneFeatureRoute(feature: String, state: Boolean): String = "/test/route"
    override val csrfBypassToken: String                                     = "testToken"
  }

  "getAllFeatureStates" should {
    "return a populated list" in {
      mockHttpGet(response = Future(fakeHttpResponse(OK, Json.parse(
        """
          |[
          |   {
          |       "feature" : "feature1",
          |       "state" : true
          |   },
          |   {
          |       "feature" : "feature2",
          |       "state" : false
          |   }
          |]
        """.stripMargin
      ))))

      awaitAndAssert(testConnector.getAllFeatureStates("testServiceUrl")) {
        _ mustBe List(Feature(name = "feature1", state = true), Feature(name = "feature2", state = false))
      }
    }

    "return an empty list" in {
      mockHttpGet(response = Future(fakeHttpResponse(OK, Json.parse("[]"))))

      awaitAndAssert(testConnector.getAllFeatureStates("testServiceUrl")) {
        _ mustBe List()
      }
    }

    "return an empty if there's a problem connection" in {
      mockHttpGet(response = Future.failed(new ServerErrorException("", 500)))

      awaitAndAssert(testConnector.getAllFeatureStates("testServiceUrl")) {
        _ mustBe List()
      }
    }
  }

  "setFeatureState" should {
    "return an Ok" in {
      mockHttpPostString(response = Future(fakeHttpResponse(OK)))

      awaitAndAssert(testConnector.setFeatureState("/test/url", "feature1", state = true)) {
        _.status mustBe OK
      }
    }
  }
}
