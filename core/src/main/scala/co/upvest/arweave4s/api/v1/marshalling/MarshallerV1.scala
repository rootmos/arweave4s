package co.upvest.arweave4s.api.v1.marshalling

import co.upvest.arweave4s.adt.Transaction.Signed
import co.upvest.arweave4s.adt._
import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor}

trait MarshallerV1 {

  implicit lazy val infoDecoder: Decoder[Info] = new Decoder[Info] {
    override def apply(c: HCursor): Result[Info] =
      for {
        network <- c.downField("network").as[String]
        version <- c.downField("version").as[Int]
        height  <- c.downField("height").as[BigInt]
        blocks  <- c.downField("blocks").as[BigInt]
        peers   <- c.downField("peers").as[Int]
      } yield Info(network, version, height, blocks, peers)
  }

  implicit lazy val peersDecoder: Decoder[Peer] =
    (c: HCursor) => c.as[String].map(Peer.apply)

  implicit lazy val hashDecoder: Decoder[Id] =
    (c: HCursor) => c.as[String].map(s => Id.fromB64urlEncoded(s))

  implicit lazy val winstonDecoder: Decoder[Winston] =
    (c: HCursor) => c.as[BigInt].map(Winston)

  implicit lazy val signatureDecoder: Decoder[Signature] =
    (c: HCursor) => c.as[String].map(_.getBytes).map(Signature)

  implicit lazy val ownerDecoder: Decoder[Owner] =
    (c: HCursor) => c.as[String].map(Owner.fromB64UrlEncoded)

  implicit lazy val transactionDecoder = new Decoder[Signed] {
    override def apply(c: HCursor): Result[Signed] =
      for {
        id       <- c.downField("id").as[Id]
        lastTx   <- c.downField("last_tx").as[Option[Id]]
        owner    <- c.downField("owner").as[Owner]
        target   <- c.downField("target").as[Id].map(h => Address(h.bytes))
        quantity <- c.downField("quantity").as[Winston]
        tpe      <- c.downField("type").as[String].map(Transaction.Type.apply)
        // Would be good to use laziness here.
        data      <- c.downField("data").as[String].map(_.getBytes).map(Data)
        reward    <- c.downField("reward").as[Winston]
        signature <- c.downField("signature").as[Signature]
      } yield Signed(id, lastTx, owner, target, quantity, tpe, data, signature, reward)
  }

  implicit lazy val walletDecoder = new Decoder[WalletResponse] {
    override def apply(c: HCursor): Result[WalletResponse] =
      for {
        addr    <- c.downField("wallet").as[Id].map(h => Address(h.bytes))
        quant   <- c.downField("quantity").as[BigInt]
        last_tx <- c.downField("last_tx").as[String]
      } yield WalletResponse(addr, quant, last_tx)
  }

  implicit lazy val blockDecoder = new Decoder[Block] {
    override def apply(c: HCursor): Result[Block] =
      for {
        nonce         <- c.downField("nonce").as[String]
        prev_block    <- c.downField("previous_block").as[Id]
        timestamp     <- c.downField("timestamp").as[Long]
        last_retarget <- c.downField("last_retarget").as[Long]
        diff          <- c.downField("diff").as[Int]
        height        <- c.downField("height").as[BigInt]
        hash          <- c.downField("hash").as[Id]
        indep_hash    <- c.downField("indep_hash").as[Id]
        txs           <- c.downField("txs").as[Seq[Signed]]
        hash_list     <- c.downField("hash_list").as[Seq[Id]]
        wallet_list   <- c.downField("wallet_list").as[Seq[WalletResponse]]
        reward_addr   <- c.downField("reward_addr").as[String]
      } yield
        Block(
          nonce = nonce,
          previousBlock = prev_block,
          timestamp = timestamp,
          lastRetarget = last_retarget,
          diff = diff,
          height = height,
          hash = hash,
          indepHash = indep_hash,
          txs = txs,
          hashList = hash_list,
          walletList = wallet_list,
          rewardAddr = reward_addr
        )
  }
}