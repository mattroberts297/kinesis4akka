package org.typetastic.aws.kinesis.model

case class GetRecordsRequest(
    shardIterator: String,
    limit: Option[Int]) extends Command
