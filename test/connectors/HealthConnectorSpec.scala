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

import com.cjwwdev.http.exceptions._
import com.cjwwdev.http.verbs.Http
import helpers.connectors.ConnectorSpec

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class HealthConnectorSpec extends ConnectorSpec {

  val testConnector = new HealthConnector {
    override val http: Http          = mockHttp
    override val healthRoute: String = "/test/health/route"
  }

  "getHealthStatus" should {
    "return an Ok" in {
      mockHttpGet(response = Future(fakeHttpResponse(OK)))

      awaitAndAssert(testConnector.getHealthStatus("/test/service")) {
        _ mustBe OK
      }
    }

    "return a Bad request" in {
      mockHttpGet(response = Future.failed(new BadRequestException("")))

      awaitAndAssert(testConnector.getHealthStatus("/test/service")) {
        _ mustBe BAD_REQUEST
      }
    }

    "return a Forbidden" in {
      mockHttpGet(response = Future.failed(new ForbiddenException("")))

      awaitAndAssert(testConnector.getHealthStatus("/test/service")) {
        _ mustBe FORBIDDEN
      }
    }

    "return a Not found" in {
      mockHttpGet(response = Future.failed(new NotFoundException("")))

      awaitAndAssert(testConnector.getHealthStatus("/test/service")) {
        _ mustBe NOT_FOUND
      }
    }

    "return a Not acceptable" in {
      mockHttpGet(response = Future.failed(new NotAcceptableException("")))

      awaitAndAssert(testConnector.getHealthStatus("/test/service")) {
        _ mustBe NOT_ACCEPTABLE
      }
    }

    "return a Conflict" in {
      mockHttpGet(response = Future.failed(new ConflictException("")))

      awaitAndAssert(testConnector.getHealthStatus("/test/service")) {
        _ mustBe CONFLICT
      }
    }

    "return some 4xx status" in {
      mockHttpGet(response = Future.failed(new ClientErrorException("", UNAUTHORIZED)))

      awaitAndAssert(testConnector.getHealthStatus("/test/service")) {
        _ mustBe UNAUTHORIZED
      }
    }

    "return some 5xx status" in {
      mockHttpGet(response = Future.failed(new ServerErrorException("", INTERNAL_SERVER_ERROR)))

      awaitAndAssert(testConnector.getHealthStatus("/test/service")) {
        _ mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
