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
import com.cjwwdev.http.responses.WsResponseHelpers
import com.cjwwdev.http.verbs.Http
import javax.inject.Inject
import models.Feature
import play.api.libs.ws.WSResponse
import play.api.mvc.Request

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultFeatureSwitchConnector @Inject()(config: ConfigurationLoader,
                                              val http: Http) extends FeatureSwitchConnector {
  override val getAllFeaturesRoute: String = config.get[String]("microservice.features.routes.getAll")
  override val csrfBypassToken: String = config.get[String]("play.filters.csrf.bypassValue")
  override def setOneFeatureRoute(feature: String, state: Boolean): String = {
    config
      .get[String]("microservice.features.routes.setOne")
      .replace(":feature", feature)
      .replace(":state", state.toString)
  }
}

trait FeatureSwitchConnector extends WsResponseHelpers {

  val http: Http

  val getAllFeaturesRoute: String
  def setOneFeatureRoute(feature: String, state: Boolean): String

  val csrfBypassToken: String

  def getAllFeatureStates(serviceUrl: String)(implicit request: Request[_], ec: ExC): Future[List[Feature]] = {
    http.get(s"$serviceUrl$getAllFeaturesRoute") map {
      case SuccessResponse(resp) => resp.json.as[List[Feature]]
      case ErrorResponse(_)      => List.empty[Feature]
    } recover {
      case _ => List.empty[Feature]
    }
  }

  def setFeatureState(serviceUrl: String, feature: String, state: Boolean)(implicit request: Request[_], ec: ExC): Future[WSResponse] = {
    http.postString(s"$serviceUrl${setOneFeatureRoute(feature, state)}", "", secure = false, headers = Seq(bypassHeader)) map {
      case SuccessResponse(resp) => resp
      case ErrorResponse(resp)   => resp
    }
  }

  private val bypassHeader: (String, String) = "Csrf-Bypass" -> csrfBypassToken
}
