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

import com.cjwwdev.logging.Logging
import connectors.AdminConnector
import javax.inject.Inject
import models.{AccountDetails, Credentials}
import play.api.mvc.{Request, Session}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DefaultLoginService @Inject()(val adminConnector: AdminConnector) extends LoginService

trait LoginService extends Logging {

  val adminConnector: AdminConnector

  def processLoginAttempt(creds: Credentials)(implicit request: Request[_]): Future[Option[Session]] = {
    adminConnector.authenticateUser(creds) flatMap {
      _.fold(Future(Option.empty[Session])) { managementId =>
        adminConnector.getManagementUser(managementId) map { user =>
          Some(Session(sessionMap(user)))
        } recover {
          case _ => None
        }
      }
    }
  }

  private def sessionMap(user: AccountDetails): Map[String, String] = Map(
    "cookieId" -> user.managementId,
    "username" -> user.username
  )
}
