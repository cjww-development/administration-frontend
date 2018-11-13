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

package services

import com.cjwwdev.config.ConfigurationLoader
import connectors.HealthConnector
import javax.inject.Inject
import play.api.mvc.Request
import play.api.http.Status._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait HealthStatus
case object Healthy  extends HealthStatus
case object Unknown  extends HealthStatus
case object Problems extends HealthStatus

class DefaultHealthService @Inject()(val healthConnector: HealthConnector,
                                     val config: ConfigurationLoader) extends HealthService {
  override val serviceUrls: Map[String, String] = {
    config
      .get[Seq[String]]("microservice.features.services")
      .map(service => service -> config.getServiceUrl(service))
      .toMap
  }
}

trait HealthService {

  val healthConnector: HealthConnector

  val serviceUrls: Map[String, String]

  def getServicesHealth(implicit request: Request[_]): Future[Map[String, HealthStatus]] = {
    Future
      .sequence(serviceUrls.map { case (service, url) => getHealth(service, url) })
      .map(_.toMap)
  }

  private def getHealth(service: String, url: String)(implicit request: Request[_]): Future[(String, HealthStatus)] = {
    healthConnector.getHealthStatus(url).map {
      service -> matchStatus(_)
    }.recover {
      case _ => service -> Problems
    }
  }

  private val matchStatus: Int => HealthStatus = {
    case OK                       => Healthy
    case BAD_REQUEST              => Unknown
    case FORBIDDEN                => Unknown
    case NOT_FOUND                => Unknown
    case NOT_ACCEPTABLE           => Unknown
    case CONFLICT                 => Unknown
    case x if x >= 400 & x <= 499 => Unknown
    case x if x >= 500 & x <= 599 => Problems
  }
}
