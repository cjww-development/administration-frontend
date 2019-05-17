/*
 * Copyright 2019 CJWW Development
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
import models.EventFiltering
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext.Implicits.global

class MessagingHubConnectorSpec extends ConnectorSpec {

  val testConnector = new MessagingHubConnector {
    override val http: Http = mockHttp
    override val messagingHubRoute: String = "/test/url"
  }

  val testJson ="""
      |{
      |    "correlationId" : "correlationId-f8346985-f653-47fc-a01d-7d5183e6adf3",
      |    "messageType" : "AUDIT_EVENT",
      |    "service" : "accounts-microservice",
      |    "appId" : "9d64c41e-0cc1-49e9-9279-cfba720e485a",
      |    "createdAt" : "2019-05-09T15:24:50.026",
      |    "sessionId" : "session-e2fbb2ae-dbb5-41ae-a1fd-d5424a72d686",
      |    "userId" : "user-23337d42-14de-4bb2-ad7c-2ab7d22678e3",
      |    "requestId" : "requestId-392f4033-628a-4a50-9203-a4bbe94bafe2",
      |    "deviceId" : "NOT-IMPLEMENTED",
      |    "ipAddress" : "0.0.0.0",
      |    "eventCode" : 200,
      |    "detail" : {
      |        "previousDetails" : {
      |            "firstName" : "Christopher J W",
      |            "lastName" : "Walker",
      |            "email" : "cjw@walker54.plus.com"
      |        },
      |        "updatedDetails" : {
      |            "firstName" : "Chris",
      |            "lastName" : "Walker",
      |            "email" : "cjw@walker54.plus.com"
      |        }
      |    }
      |}
    """.stripMargin

  "getAuditEvents" should {
    "a list of audit events" in {
      mockHttpGet(response = fakeHttpResponseWithPadding(statusCode = OK, bodyContents = Json.arr(Json.parse(testJson))))

      awaitAndAssert(testConnector.getAuditEvents("testUserId", EventFiltering.empty)) {
        _ mustBe List(Json.parse(testJson))
      }
    }

    "no audit events" when {
      "there are no events for the user" in {
        mockHttpGet(response = fakeHttpResponseWithPadding(statusCode = OK, bodyContents = Json.toJson(List.empty[JsValue])))

        awaitAndAssert(testConnector.getAuditEvents("testUserId", EventFiltering.empty)) {
          _ mustBe List.empty[JsValue]
        }
      }

      "there was a problem with the query params" in {
        mockHttpGet(response = fakeHttpResponse(statusCode = BAD_REQUEST))

        awaitAndAssert(testConnector.getAuditEvents("testUserId", EventFiltering.empty)) {
          _ mustBe List.empty[JsValue]
        }
      }
    }
  }
}
