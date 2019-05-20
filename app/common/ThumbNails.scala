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

package common

import models.AccountDetails
import play.api.i18n.{Lang, MessagesApi}
import play.twirl.api.Html
import views.html.thumbnails._
import common.Permissions._

object ThumbNails {
  def thumbnailList()(implicit user: AccountDetails, messages: MessagesApi, lang: Lang): List[List[Html]] = {
    val rootThumbs: List[Html]          = if(permissionGranted(rootOnly)) {
      List(RegisterThumbnail(), UsersOverviewThumbnail(), CustomDataSecurityThumbnail())
    } else {
      List()
    }

    val encDecThumbs: List[Html]        = if(permissionGranted(encDec)) List(DataSecurityThumbnail(), SHA512Thumbnail()) else List()
    val headerThumbs: List[Html]        = if(permissionGranted(headers)) List(HeadersThumbnail()) else List()
    val shutteringThumbs: List[Html]    = if(permissionGranted(shuttering)) List(ShutteringThumbnail()) else List()
    val featureSwitchThumbs: List[Html] = if(permissionGranted(featureSwitch)) List(FeatureSwitchThumbnail()) else List()
    val general: List[Html]             = List(UUIDThumbnail(), ServiceHealthThumbnail())
    val eventLog: List[Html]            = if(permissionGranted(support)) List(UserEventLogThumbnail()) else List()
    (rootThumbs ++ encDecThumbs ++ headerThumbs ++ shutteringThumbs ++ featureSwitchThumbs ++ general ++ eventLog).grouped(4).toList
  }
}
