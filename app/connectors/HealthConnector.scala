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
import com.cjwwdev.http.verbs.Http
import javax.inject.Inject
import play.api.mvc.Request

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultHealthConnector @Inject()(val http: Http,
                                       val config: ConfigurationLoader) extends HealthConnector {
  override val healthRoute: String = config.get[String]("microservice.health.route")
}

trait HealthConnector {

  val http: Http

  val healthRoute: String

  def getHealthStatus(serviceUrl: String)(implicit request: Request[_], ec: ExC): Future[Int] = {
    http.get(s"$serviceUrl$healthRoute") map {
      _.fold(_.status, _.status)
    }
  }
}
