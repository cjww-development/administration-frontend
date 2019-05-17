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
import javax.inject.Inject
import models.EventFiltering
import play.api.libs.json.JsValue
import play.api.mvc.Request

import scala.concurrent.{ExecutionContext => ExC, Future}

class DefaultEventLogService @Inject()(val accountsConnector: AccountsConnector,
                                       val messagingHubConnector: MessagingHubConnector) extends EventLogService

trait EventLogService {

  val accountsConnector: AccountsConnector
  val messagingHubConnector: MessagingHubConnector

  def getEventLogs(filter: EventFiltering)(implicit req: Request[_], ex: ExC): Future[List[JsValue]] = {
    accountsConnector.getUserId(filter.userName).flatMap {
      case Right(userId) => messagingHubConnector.getAuditEvents(userId, filter)
      case Left(_)       => Future.successful(List.empty[JsValue])
    }
  }
}
