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

package helpers.connectors

import com.cjwwdev.http.exceptions.NotFoundException
import connectors.AdminConnector
import helpers.other.Fixtures
import models.AccountDetails
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MockAdminConnector extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockAdminConnector: AdminConnector = mock[AdminConnector]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAdminConnector)
  }

  def mockRegisterNewUser(registered: Boolean): OngoingStubbing[Future[Boolean]] = {
    when(mockAdminConnector.registerNewUser(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(registered))
  }

  def mockAuthenticateUser(authenticated: Boolean): OngoingStubbing[Future[Option[String]]] = {
    when(mockAdminConnector.authenticateUser(ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future(if(authenticated) Some(generateTestSystemId(MANAGEMENT)) else None))
  }

  def mockGetManagementUser(found: Boolean, permissions: List[String] = List.empty[String]): OngoingStubbing[Future[AccountDetails]] = {
    when(mockAdminConnector.getManagementUser(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(if(found) {
        Future(if(permissions.isEmpty) {
          testAccountDetails
        } else {
          testAccountDetails.copy(permissions = permissions)
        })
      } else {
        Future.failed(new NotFoundException(""))
      })
  }

  def mockGetAllManagementUsers(populated: Boolean): OngoingStubbing[Future[List[AccountDetails]]] = {
    when(mockAdminConnector.getAllManagementUsers(ArgumentMatchers.any()))
      .thenReturn(Future(if(populated) List(testAccountDetails) else List.empty[AccountDetails]))
  }

  def mockDeleteManagementUser(deleted: Boolean): OngoingStubbing[Future[Boolean]] = {
    when(mockAdminConnector.deleteManagementUser(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future(deleted))
  }
}
