import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: movie.proto")
public final class MovieServiceGrpc {

  private MovieServiceGrpc() {}

  public static final String SERVICE_NAME = "MovieService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Movie.GetMoviesRequest,
      Movie.MovieList> METHOD_GET_MOVIES =
      io.grpc.MethodDescriptor.<Movie.GetMoviesRequest, Movie.MovieList>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "MovieService", "GetMovies"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Movie.GetMoviesRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Movie.MovieList.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Movie.GetMoviesRequest,
      Movie.MovieList> METHOD_GET_MOVIE_BY_ACTOR =
      io.grpc.MethodDescriptor.<Movie.GetMoviesRequest, Movie.MovieList>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "MovieService", "GetMovieByActor"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Movie.GetMoviesRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Movie.MovieList.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Movie.GetMoviesRequest,
      Movie.MovieList> METHOD_GET_MOVIE_BY_GENRE =
      io.grpc.MethodDescriptor.<Movie.GetMoviesRequest, Movie.MovieList>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "MovieService", "GetMovieByGenre"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Movie.GetMoviesRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Movie.MovieList.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Movie.DeleteMovieRequest,
      Movie.Response> METHOD_DELETE_MOVIE =
      io.grpc.MethodDescriptor.<Movie.DeleteMovieRequest, Movie.Response>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "MovieService", "DeleteMovie"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Movie.DeleteMovieRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Movie.Response.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Movie.MoviesData,
      Movie.Response> METHOD_UPDATE_MOVIE =
      io.grpc.MethodDescriptor.<Movie.MoviesData, Movie.Response>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "MovieService", "UpdateMovie"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Movie.MoviesData.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Movie.Response.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Movie.MoviesData,
      Movie.Response> METHOD_ADD_MOVIE =
      io.grpc.MethodDescriptor.<Movie.MoviesData, Movie.Response>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "MovieService", "AddMovie"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Movie.MoviesData.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Movie.Response.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MovieServiceStub newStub(io.grpc.Channel channel) {
    return new MovieServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MovieServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new MovieServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MovieServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new MovieServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class MovieServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getMovies(Movie.GetMoviesRequest request,
        io.grpc.stub.StreamObserver<Movie.MovieList> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_MOVIES, responseObserver);
    }

    /**
     */
    public void getMovieByActor(Movie.GetMoviesRequest request,
        io.grpc.stub.StreamObserver<Movie.MovieList> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_MOVIE_BY_ACTOR, responseObserver);
    }

    /**
     */
    public void getMovieByGenre(Movie.GetMoviesRequest request,
        io.grpc.stub.StreamObserver<Movie.MovieList> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_MOVIE_BY_GENRE, responseObserver);
    }

    /**
     */
    public void deleteMovie(Movie.DeleteMovieRequest request,
        io.grpc.stub.StreamObserver<Movie.Response> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_DELETE_MOVIE, responseObserver);
    }

    /**
     */
    public void updateMovie(Movie.MoviesData request,
        io.grpc.stub.StreamObserver<Movie.Response> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_UPDATE_MOVIE, responseObserver);
    }

    /**
     */
    public void addMovie(Movie.MoviesData request,
        io.grpc.stub.StreamObserver<Movie.Response> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ADD_MOVIE, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_GET_MOVIES,
            asyncUnaryCall(
              new MethodHandlers<
                Movie.GetMoviesRequest,
                Movie.MovieList>(
                  this, METHODID_GET_MOVIES)))
          .addMethod(
            METHOD_GET_MOVIE_BY_ACTOR,
            asyncUnaryCall(
              new MethodHandlers<
                Movie.GetMoviesRequest,
                Movie.MovieList>(
                  this, METHODID_GET_MOVIE_BY_ACTOR)))
          .addMethod(
            METHOD_GET_MOVIE_BY_GENRE,
            asyncUnaryCall(
              new MethodHandlers<
                Movie.GetMoviesRequest,
                Movie.MovieList>(
                  this, METHODID_GET_MOVIE_BY_GENRE)))
          .addMethod(
            METHOD_DELETE_MOVIE,
            asyncUnaryCall(
              new MethodHandlers<
                Movie.DeleteMovieRequest,
                Movie.Response>(
                  this, METHODID_DELETE_MOVIE)))
          .addMethod(
            METHOD_UPDATE_MOVIE,
            asyncUnaryCall(
              new MethodHandlers<
                Movie.MoviesData,
                Movie.Response>(
                  this, METHODID_UPDATE_MOVIE)))
          .addMethod(
            METHOD_ADD_MOVIE,
            asyncUnaryCall(
              new MethodHandlers<
                Movie.MoviesData,
                Movie.Response>(
                  this, METHODID_ADD_MOVIE)))
          .build();
    }
  }

  /**
   */
  public static final class MovieServiceStub extends io.grpc.stub.AbstractStub<MovieServiceStub> {
    private MovieServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MovieServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MovieServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MovieServiceStub(channel, callOptions);
    }

    /**
     */
    public void getMovies(Movie.GetMoviesRequest request,
        io.grpc.stub.StreamObserver<Movie.MovieList> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_MOVIES, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getMovieByActor(Movie.GetMoviesRequest request,
        io.grpc.stub.StreamObserver<Movie.MovieList> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_MOVIE_BY_ACTOR, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getMovieByGenre(Movie.GetMoviesRequest request,
        io.grpc.stub.StreamObserver<Movie.MovieList> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_MOVIE_BY_GENRE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteMovie(Movie.DeleteMovieRequest request,
        io.grpc.stub.StreamObserver<Movie.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_DELETE_MOVIE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateMovie(Movie.MoviesData request,
        io.grpc.stub.StreamObserver<Movie.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_UPDATE_MOVIE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addMovie(Movie.MoviesData request,
        io.grpc.stub.StreamObserver<Movie.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ADD_MOVIE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class MovieServiceBlockingStub extends io.grpc.stub.AbstractStub<MovieServiceBlockingStub> {
    private MovieServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MovieServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MovieServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MovieServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public Movie.MovieList getMovies(Movie.GetMoviesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_MOVIES, getCallOptions(), request);
    }

    /**
     */
    public Movie.MovieList getMovieByActor(Movie.GetMoviesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_MOVIE_BY_ACTOR, getCallOptions(), request);
    }

    /**
     */
    public Movie.MovieList getMovieByGenre(Movie.GetMoviesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_MOVIE_BY_GENRE, getCallOptions(), request);
    }

    /**
     */
    public Movie.Response deleteMovie(Movie.DeleteMovieRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_DELETE_MOVIE, getCallOptions(), request);
    }

    /**
     */
    public Movie.Response updateMovie(Movie.MoviesData request) {
      return blockingUnaryCall(
          getChannel(), METHOD_UPDATE_MOVIE, getCallOptions(), request);
    }

    /**
     */
    public Movie.Response addMovie(Movie.MoviesData request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ADD_MOVIE, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class MovieServiceFutureStub extends io.grpc.stub.AbstractStub<MovieServiceFutureStub> {
    private MovieServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MovieServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MovieServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MovieServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Movie.MovieList> getMovies(
        Movie.GetMoviesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_MOVIES, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Movie.MovieList> getMovieByActor(
        Movie.GetMoviesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_MOVIE_BY_ACTOR, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Movie.MovieList> getMovieByGenre(
        Movie.GetMoviesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_MOVIE_BY_GENRE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Movie.Response> deleteMovie(
        Movie.DeleteMovieRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_DELETE_MOVIE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Movie.Response> updateMovie(
        Movie.MoviesData request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_UPDATE_MOVIE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Movie.Response> addMovie(
        Movie.MoviesData request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ADD_MOVIE, getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_MOVIES = 0;
  private static final int METHODID_GET_MOVIE_BY_ACTOR = 1;
  private static final int METHODID_GET_MOVIE_BY_GENRE = 2;
  private static final int METHODID_DELETE_MOVIE = 3;
  private static final int METHODID_UPDATE_MOVIE = 4;
  private static final int METHODID_ADD_MOVIE = 5;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final MovieServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(MovieServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_MOVIES:
          serviceImpl.getMovies((Movie.GetMoviesRequest) request,
              (io.grpc.stub.StreamObserver<Movie.MovieList>) responseObserver);
          break;
        case METHODID_GET_MOVIE_BY_ACTOR:
          serviceImpl.getMovieByActor((Movie.GetMoviesRequest) request,
              (io.grpc.stub.StreamObserver<Movie.MovieList>) responseObserver);
          break;
        case METHODID_GET_MOVIE_BY_GENRE:
          serviceImpl.getMovieByGenre((Movie.GetMoviesRequest) request,
              (io.grpc.stub.StreamObserver<Movie.MovieList>) responseObserver);
          break;
        case METHODID_DELETE_MOVIE:
          serviceImpl.deleteMovie((Movie.DeleteMovieRequest) request,
              (io.grpc.stub.StreamObserver<Movie.Response>) responseObserver);
          break;
        case METHODID_UPDATE_MOVIE:
          serviceImpl.updateMovie((Movie.MoviesData) request,
              (io.grpc.stub.StreamObserver<Movie.Response>) responseObserver);
          break;
        case METHODID_ADD_MOVIE:
          serviceImpl.addMovie((Movie.MoviesData) request,
              (io.grpc.stub.StreamObserver<Movie.Response>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class MovieServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Movie.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (MovieServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MovieServiceDescriptorSupplier())
              .addMethod(METHOD_GET_MOVIES)
              .addMethod(METHOD_GET_MOVIE_BY_ACTOR)
              .addMethod(METHOD_GET_MOVIE_BY_GENRE)
              .addMethod(METHOD_DELETE_MOVIE)
              .addMethod(METHOD_UPDATE_MOVIE)
              .addMethod(METHOD_ADD_MOVIE)
              .build();
        }
      }
    }
    return result;
  }
}
