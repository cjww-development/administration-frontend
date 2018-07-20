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
import com.cjwwdev.http.exceptions.{ForbiddenException, ServerErrorException}
import com.cjwwdev.http.responses.WsResponseHelpers
import com.cjwwdev.http.verbs.Http
import javax.inject.Inject

import models.{Account, AccountDetails, Credentials, Registration}
import play.api.libs.json.OFormat
import play.api.http.Status.{OK, NO_CONTENT}
import play.api.mvc.Request

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DefaultAdminConnector @Inject()(val http: Http,
                                      val configLoader: ConfigurationLoader) extends AdminConnector {
  override val adminUrl: String = configLoader.buildServiceUrl("administration")
}

trait AdminConnector extends WsResponseHelpers {
  val http: Http

  val adminUrl: String

  def registerNewUser(registration: Registration)(implicit format: OFormat[Registration], request: Request[_]): Future[Boolean] = {
    http.post(s"$adminUrl/register", registration) map {
      _ => true
    } recover {
      case _: ServerErrorException => false
    }
  }

  def authenticateUser(credentials: Credentials)(implicit writes: OFormat[Credentials], request: Request[_]): Future[Option[String]] = {
    http.post(s"$adminUrl/authenticate", credentials) map { resp =>
      resp.status match {
        case OK => Some(resp.toResponseString(needsDecrypt = true))
        case _  => None
      }
    } recover {
      case _: ForbiddenException => None
    }
  }

  def getManagementUser(managementId: String)(implicit request: Request[_]): Future[AccountDetails] = {
    http.get(s"$adminUrl/user/$managementId") map {
      _.toDataType[AccountDetails](needsDecrypt = true)
    }
  }

  def getAllManagementUsers(implicit request: Request[_]): Future[List[AccountDetails]] = {
    http.get(s"$adminUrl/users") map { resp =>
      resp.status match {
        case OK         => resp.toDataType[List[AccountDetails]](needsDecrypt = true)
        case NO_CONTENT => List.empty[AccountDetails]
      }
    }
  }

  def deleteManagementUser(managementId: String)(implicit request: Request[_]): Future[Boolean] = {
    http.delete(s"$adminUrl/user/$managementId") map {
      _ => true
    } recover {
      case _ => false
    }
  }
}
