/*
 * Copyright 2016 HM Revenue & Customs
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

import connectors.NpsConnector
import play.api.libs.json.{JsObject, JsResult}
import uk.gov.hmrc.play.http.{HeaderCarrier}
import play.api.http.Status

import scala.concurrent.{ExecutionContext, Future}
import util.{NinoHelper, Transformers}
import model.HttpResponseDetails

object ProtectionService extends ProtectionService {
  override val nps: NpsConnector = NpsConnector
}

trait ProtectionService {

  val nps: NpsConnector

  def applyForProtection(nino: String, applicationRequestBody: JsObject)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponseDetails] = {
    val (ninoWithoutSuffix, lastNinoCharOpt) = NinoHelper.dropNinoSuffix(nino)
    val npsRequestBody: JsResult[JsObject] = Transformers.transformApplyRequestBody(ninoWithoutSuffix, applicationRequestBody)
    npsRequestBody.fold(
      errors => Future.successful(HttpResponseDetails(Status.BAD_REQUEST, npsRequestBody)),
      req => nps.applyForProtection(nino, req) map { npsResponse =>
        val transformedResponseJs = npsResponse.body.flatMap { Transformers.transformApplyResponseBody(lastNinoCharOpt.get, _) }
        HttpResponseDetails(npsResponse.status, transformedResponseJs)
      }
    )
  }

  def readExistingProtections(nino: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponseDetails] = {
    val (ninoWithoutSuffix, lastNinoCharOpt) = NinoHelper.dropNinoSuffix(nino)

    nps.readExistingProtections(nino) map { npsResponse =>
        val transformedResponseJs = npsResponse.body.flatMap { Transformers.transformReadResponseBody(lastNinoCharOpt.get, _) }
        HttpResponseDetails(npsResponse.status, transformedResponseJs)
      }
  }

}
