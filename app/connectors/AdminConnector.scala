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
import models.{AccountDetails, Credentials, Registration}
import play.api.http.Status.{NO_CONTENT, OK}
import play.api.libs.json.OFormat
import play.api.mvc.Request

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultAdminConnector @Inject()(val config: ConfigurationLoader,
                                      val http: Http) extends AdminConnector {
  override protected val adminUrl: String = config.getServiceUrl("administration")
}

trait AdminConnector extends WsResponseHelpers {
  protected val http: Http

  protected val adminUrl: String

  def registerNewUser(registration: Registration)(implicit format: OFormat[Registration], request: Request[_], ec: ExC): Future[Boolean] = {
    http.post(s"$adminUrl/register", registration) map {
      _.isLeft
    }
  }

  def authenticateUser(credentials: Credentials)(implicit writes: OFormat[Credentials], request: Request[_], ec: ExC): Future[Option[String]] = {
    http.post(s"$adminUrl/authenticate", credentials) map {
      case SuccessResponse(resp) => resp.toResponseString(needsDecrypt = true).fold(Some(_), _ => None)
      case ErrorResponse(_)      => None
    }
  }

  def getManagementUser(managementId: String)(implicit request: Request[_], ec: ExC): Future[AccountDetails] = {
    http.get(s"$adminUrl/user/$managementId") map {
      case SuccessResponse(resp) => resp.toDataType[AccountDetails](needsDecrypt = true).left.get
    }
  }

  def getAllManagementUsers(implicit request: Request[_], ec: ExC): Future[List[AccountDetails]] = {
    http.get(s"$adminUrl/users") map {
      case SuccessResponse(resp) => resp.status match {
        case OK         => resp.toDataType[List[AccountDetails]](needsDecrypt = true).fold(identity, _ => List.empty[AccountDetails])
        case NO_CONTENT => List.empty[AccountDetails]
      }
      case ErrorResponse(_) => List.empty[AccountDetails]
    }
  }

  def deleteManagementUser(managementId: String)(implicit request: Request[_], ec: ExC): Future[Boolean] = {
    http.delete(s"$adminUrl/user/$managementId") map {
      case SuccessResponse(_) => true
      case ErrorResponse(_)   => false
    }
  }
}
