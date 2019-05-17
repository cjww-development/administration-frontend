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

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.responses.EvaluateResponse.{ErrorResponse, SuccessResponse}
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.implicits.ImplicitJsValues._
import javax.inject.Inject
import models.EventFiltering
import play.api.libs.json.JsValue
import play.api.mvc.Request

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultMessagingHubConnector @Inject()(val http: Http,
                                             val config: ConfigurationLoader) extends MessagingHubConnector {
  override val messagingHubRoute: String = config.getServiceUrl("messaging-hub")
}

trait MessagingHubConnector {

  val http: Http

  val messagingHubRoute: String

  def getAuditEvents(userId: String, filters: EventFiltering)(implicit req: Request[_], ec: ExC): Future[List[JsValue]] = {
    http.get(s"$messagingHubRoute/messages/auditing/events${filters.toQueryParams(userId)}").map {
      case SuccessResponse(resp) => resp.json.get[List[JsValue]]("body")
      case ErrorResponse(_)      => List.empty[JsValue]
    }
  }
}
