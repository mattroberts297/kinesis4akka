package org.typetastic.aws.kinesis

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.kinesis.{AmazonKinesisAsync => Underlying}
import org.typetastic.aws.handlers.PromiseHandlerFactory
import org.typetastic.aws.kinesis.converters.KinesisConverter
import org.typetastic.aws.kinesis.model._

import scala.concurrent.{ExecutionContext, Promise, Future}

class KinesisClient(
    val underlying: Underlying,
    val converter: KinesisConverter,
    val factory: PromiseHandlerFactory)(
    implicit ec: ExecutionContext) {
  import converter._
  import factory._

  def createStream(request: CreateStreamRequest): Future[CreateStreamResponse] = {
    invoke(request)(toAws)(underlying.createStreamAsync)(Void => CreateStreamResponse())
  }

  def deleteStream(request: DeleteStreamRequest): Future[DeleteStreamResponse] = {
    invoke(request)(toAws)(underlying.deleteStreamAsync)(Void => DeleteStreamResponse())
  }

  def describeStream(request: DescribeStreamRequest): Future[DescribeStreamResponse] = {
    invoke(request)(toAws)(underlying.describeStreamAsync)(fromAws)
  }

  def getRecords(request: GetRecordsRequest): Future[GetRecordsResponse] = {
    invoke(request)(toAws)(underlying.getRecordsAsync)(fromAws)
  }

  def getShardIterator(request: GetShardIteratorRequest): Future[GetShardIteratorResponse] = {
    invoke(request)(toAws)(underlying.getShardIteratorAsync)(fromAws)
  }

  def listStreams(request: ListStreamsRequest): Future[ListStreamsResponse] = {
    invoke(request)(toAws)(underlying.listStreamsAsync)(fromAws)
  }

  def mergeShards(request: MergeShardsRequest): Future[MergeShardsResponse] = {
    invoke(request)(toAws)(underlying.mergeShardsAsync)(Void => MergeShardsResponse())
  }

  def putRecord(request: PutRecordRequest): Future[PutRecordResponse] = {
    invoke(request)(toAws)(underlying.putRecordAsync)(fromAws)
  }

  def putRecords(request: PutRecordsRequest): Future[PutRecordsResponse] = {
    invoke(request)(toAws)(underlying.putRecordsAsync)(fromAws)
  }

  def splitShard(request: SplitShardRequest): Future[SplitShardResponse] = {
    invoke(request)(toAws)(underlying.splitShardAsync)(Void => SplitShardResponse())
  }

  // TODO PromiseHandler should be responsible for creating Promise and providing Future. Then create factory for that.
  private def invoke
      [Request, UnderlyingRequest <: AmazonWebServiceRequest, UnderlyingResponse, Response]
      (request: Request)
      (toAws: Request => UnderlyingRequest)
      (method: (UnderlyingRequest, AsyncHandler[UnderlyingRequest, UnderlyingResponse]) => _)
      (fromAws: UnderlyingResponse => Response): Future[Response] = {
    val promise = Promise[UnderlyingResponse]()
    method(toAws(request), create[UnderlyingRequest, UnderlyingResponse](promise))
    promise.future.map(fromAws)
  }
}
