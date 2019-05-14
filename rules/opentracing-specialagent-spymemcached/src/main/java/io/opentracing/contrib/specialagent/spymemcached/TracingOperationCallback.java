/* Copyright 2019 The OpenTracing Authors
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

package io.opentracing.contrib.specialagent.spymemcached;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import java.util.HashMap;
import java.util.Map;
import net.spy.memcached.ops.OperationCallback;
import net.spy.memcached.ops.OperationStatus;

public class TracingOperationCallback implements OperationCallback {
  protected final OperationCallback operationCallback;
  private final Span span;

  public TracingOperationCallback(OperationCallback operationCallback, Span span) {
    this.operationCallback = operationCallback;
    this.span = span;
  }

  @Override
  public void receivedStatus(OperationStatus status) {
    Map<String, Object> event = new HashMap<>();
    event.put("status", status.getStatusCode());
    span.log(event);
    operationCallback.receivedStatus(status);
  }

  @Override
  public void complete() {
    try {
      operationCallback.complete();
    } finally {
      span.finish();
    }
  }

  void onError(Throwable thrown) {
    Tags.ERROR.set(span, Boolean.TRUE);
    final HashMap<String, Object> errorLogs = new HashMap<>(2);
    errorLogs.put("event", Tags.ERROR.getKey());
    errorLogs.put("error.object", thrown);
    span.log(errorLogs);
    span.finish();
  }
}
