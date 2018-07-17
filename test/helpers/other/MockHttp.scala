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

package helpers.other

import com.cjwwdev.http.verbs.Http
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.ws.WSResponse

import scala.concurrent.Future

trait MockHttp extends BeforeAndAfterEach with MockitoSugar with TestDataGenerator {
  self: PlaySpec =>

  val mockHttp = mock[Http]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockHttp)
  }

  def mockHttpHead(response: Future[WSResponse]): OngoingStubbing[Future[WSResponse]] = {
    when(mockHttp.head(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(response)
  }

  def mockHttpGet(response: Future[WSResponse]): OngoingStubbing[Future[WSResponse]] = {
    when(mockHttp.get(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(response)
  }

  def mockHttpPost(response: Future[WSResponse]): OngoingStubbing[Future[WSResponse]] = {
    when(mockHttp.post(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(response)
  }

  def mockHttpPostString(response: Future[WSResponse]): OngoingStubbing[Future[WSResponse]] = {
    when(mockHttp.postString(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(response)
  }

  def mockHttpPut(response: Future[WSResponse]): OngoingStubbing[Future[WSResponse]] = {
    when(mockHttp.put(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(response)
  }

  def mockHttpPatch(response: Future[WSResponse]): OngoingStubbing[Future[WSResponse]] = {
    when(mockHttp.patch(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(response)
  }

  def mockHttpDelete(response: Future[WSResponse]): OngoingStubbing[Future[WSResponse]] = {
    when(mockHttp.delete(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(response)
  }
}
