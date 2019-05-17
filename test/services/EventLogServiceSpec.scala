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

package services

import connectors.{AccountsConnector, MessagingHubConnector}
import helpers.services.ServiceSpec
import models.EventFiltering
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest

import scala.concurrent.ExecutionContext.Implicits.global

class EventLogServiceSpec extends ServiceSpec {

  val testService = new EventLogService {
    override val accountsConnector: AccountsConnector         = mockAccountsConnector
    override val messagingHubConnector: MessagingHubConnector = mockMessagingHubConnector
  }

  implicit val req = FakeRequest()

  "getEventLogs" should {
    "return a populated list of events" in {
      mockGetUserId(found = true)
      mockGetAuditEvents(populated = true)

      awaitAndAssert(testService.getEventLogs(EventFiltering.empty.copy(userName = "testUserName"))) {
        _ mustBe List(Json.parse(testJson))
      }
    }

    "return an empty list" when {
      "a user id was found but no events had been yet generated" in {
        mockGetUserId(found = true)
        mockGetAuditEvents(populated = false)

        awaitAndAssert(testService.getEventLogs(EventFiltering.empty.copy(userName = "testUserName"))) {
          _ mustBe List.empty[JsValue]
        }
      }

      "a no user could be found" in {
        mockGetUserId(found = false)

        awaitAndAssert(testService.getEventLogs(EventFiltering.empty.copy(userName = "testUserName"))) {
          _ mustBe List.empty[JsValue]
        }
      }
    }
  }
}
