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
import connectors.FeatureSwitchConnector
import javax.inject.Inject
import models.Feature
import play.api.mvc.Request

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DefaultFeatureSwitchService @Inject()(val featureSwitchConnector: FeatureSwitchConnector,
                                            val config: ConfigurationLoader) extends FeatureSwitchService {
  override val serviceUrls: Map[String, String] = {
    config
      .get[Seq[String]]("microservice.features.services")
      .map(service => service -> config.getServiceUrl(service))
      .toMap
  }
}

trait FeatureSwitchService {

  val featureSwitchConnector: FeatureSwitchConnector

  val serviceUrls: Map[String, String]

  def getFeatures(implicit request: Request[_]): Future[Map[String, List[Feature]]] = {
    Future
      .sequence(serviceUrls map { case (service, url) => featureSwitchConnector.getAllFeatureStates(url) map(service -> _)})
      .map(_.toMap)
  }

  def setFeature(service: String, featureSet: List[Feature])(implicit request: Request[_]): Future[List[Feature]] = {
    Future
      .sequence(featureSet collect { case feature if !feature.name.contains("[")  =>
        featureSwitchConnector.setFeatureState(serviceUrls(service), feature.name, feature.state)
      })
      .map(_ => featureSet)
  }
}
