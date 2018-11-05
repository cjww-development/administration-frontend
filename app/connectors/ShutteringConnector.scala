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

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.responses.WsResponseHelpers
import com.cjwwdev.http.verbs.Http
import javax.inject.Inject
import play.api.libs.ws.WSResponse
import play.api.mvc.Request

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DefaultShutteringConnector @Inject()(val config: ConfigurationLoader,
                                           val http: Http) extends ShutteringConnector {
  override val shutterRoute: String = config.get[String]("microservice.shuttering.route")
}

trait ShutteringConnector extends WsResponseHelpers {
  val http: Http

  val shutterRoute: String

  def shutterService(serviceUrl: String, shutter: Boolean)(implicit request: Request[_]): Future[WSResponse] = {
    http.patchString(s"$serviceUrl$shutterRoute/$shutter", "")
  }

  def getShutterState(serviceUrl: String)(implicit request: Request[_]): Future[Boolean] = {
    http.get(s"$serviceUrl$shutterRoute/state") map(_.toResponseString(needsDecrypt = false).toBoolean)
  }
}