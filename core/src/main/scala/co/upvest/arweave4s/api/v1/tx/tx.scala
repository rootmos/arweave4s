package co.upvest.arweave4s.api.v1.tx

import co.upvest.arweave4s.api.v1
import co.upvest.arweave4s.adt.Transaction
import com.softwaremill.sttp._

object tx {

  def getTxViaId(host: String, txId: Transaction.Id): Request[String, Nothing] =
    sttp.get(uri"$host/${v1.TxPath}/$txId")

  def getFilteredTxViaId(host: String, txId: Transaction.Id, field: String): Request[String, Nothing] =
    sttp.get(uri"$host/${v1.TxPath}/$txId/$field")

  def getBodyAsHtml(host: String, txId: Transaction.Id): Request[String, Nothing] =
    sttp.get(uri"$host/${v1.TxPath}/$txId/data.html")

  def postTx(host: String, payload: String): Request[String, Nothing] =
    sttp.body(payload).contentType("application/json").post(uri"$host/${v1.TxPath}")
}
