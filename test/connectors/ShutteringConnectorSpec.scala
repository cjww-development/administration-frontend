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

import com.cjwwdev.http.verbs.Http
import helpers.connectors.ConnectorSpec

import scala.concurrent.ExecutionContext.Implicits.global

class ShutteringConnectorSpec extends ConnectorSpec {

  private val testConnector = new ShutteringConnector {
    override val http: Http           = mockHttp
    override val shutterRoute: String = "/test/uri"
  }

  "shutterService" should {
    "return a 204" in {
      mockHttpPatchString(response = fakeHttpResponse(statusCode = NO_CONTENT))

      awaitAndAssert(testConnector.shutterService("testService", shutter = true)) {
        _.status mustBe NO_CONTENT
      }
    }
  }

  "getShutterState" should {
    "return true" in {
      mockHttpGet(response = fakeHttpResponse(statusCode = OK, bodyContents = "true"))

      awaitAndAssert(testConnector.getShutterState("testService")) {
        _ mustBe true
      }
    }

    "return true if there is a problem connecting" in {
      mockHttpGet(response = fakeHttpResponse(statusCode = INTERNAL_SERVER_ERROR))

      awaitAndAssert(testConnector.getShutterState("testService")) {
        _ mustBe true
      }
    }

    "return false" in {
      mockHttpGet(response = fakeHttpResponse(statusCode = OK, bodyContents = "false"))

      awaitAndAssert(testConnector.getShutterState("testService")) {
        _ mustBe false
      }
    }

    "return true if there was a problem" in {
      mockHttpGet(response = fakeHttpResponse(statusCode = INTERNAL_SERVER_ERROR), error = true)

      awaitAndAssert(testConnector.getShutterState("testService")) {
        _ mustBe true
      }
    }
  }
}
