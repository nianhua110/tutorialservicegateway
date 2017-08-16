/*
 * Copyright (c) 2017, CipherGateway and/or its affiliates. All rights  reserved.
 *
 */

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;

public class TestVerticle extends AbstractVerticle {
  @Override
  public void start() throws Exception {
    JsonObject config = new JsonObject().put("host", "localhost");
    config.put("port", 5432);
    config.put("database", "library");
    config.put("username", "cg");
    config.put("password", "cg");
    SQLClient client = PostgreSQLClient.createShared(vertx, config);


    Router router = Router.router(vertx);
    router.route("/api/*").handler(BodyHandler.create());
    router.route("/api/users").handler(ctx -> {
      client.getConnection(conn -> {
        SQLConnection sqlConnection = conn.result();
        sqlConnection.query("select * from author", resultSet -> {
          List<JsonObject> list = resultSet.result().getRows();
          System.out.println(list);
          JsonObject jsonObject = new JsonObject();
          jsonObject.put("success", true);
          jsonObject.put("list", list);
          ctx.response().end(jsonObject.encode());
        });
      });
    });
    vertx.createHttpServer()
        .requestHandler(router::accept)
        .listen(8909);
  }


  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new TestVerticle());
  }
}
