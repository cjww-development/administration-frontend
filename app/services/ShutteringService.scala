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
import connectors.ShutteringConnector
import javax.inject.Inject
import play.api.mvc.Request

import scala.concurrent.{ExecutionContext => ExC, Future}

class DefaultShutteringService @Inject()(val shutteringConnector: ShutteringConnector,
                                         val config: ConfigurationLoader) extends ShutteringService {

  override val serviceUrls: Map[String, String] = {
    config.get[Seq[String]]("microservice.shuttering.services")
      .map(service => service -> config.getServiceUrl(service))
      .toMap
  }
}

trait ShutteringService {

  val shutteringConnector: ShutteringConnector

  val serviceUrls: Map[String, String]

   def shutterServices(shutterValues: Map[String, Boolean])(implicit request: Request[_], ec: ExC): Future[Map[String, Boolean]] = {
     val valuesAndUrls = shutterValues.map { case (service, value) => serviceUrls(service) -> value }
     Future
       .sequence(valuesAndUrls.map { case (service, value) => shutteringConnector.shutterService(service, value) })
       .map(_ => shutterValues)
   }

  def getShutterStates(implicit request: Request[_], ec: ExC): Future[Map[String, Boolean]] = {
    Future
      .sequence(serviceUrls map { case (service, url) => shutteringConnector.getShutterState(url).map(service -> _)})
      .map(_.toMap)
  }
}
