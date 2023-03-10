package histories.workshop.chapter3;

import com.google.common.collect.ImmutableMap;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.common.RetryOptions;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

public class Chapter3Workflows {
    public static void startChapter1(WorkflowClient client) {
        // 1. start wf (no worker)
        WorkflowStub wf = client.newUntypedWorkflowStub("Chapter1WF1",
                WorkflowOptions.newBuilder()
                        .setWorkflowId("Chapter1 - WF1")
                        .setTaskQueue("c1f1tq")
                        .setSearchAttributes(getCustomSA("Chapter1 WF1"))
                        .setMemo(Collections.singletonMap("MyMemo", "Chapter1WF1Memo"))
                        .build());
        wf.start("some input");

        // 2. start wf (no worker) and signal it a bunch of times
        WorkflowStub wf2 = client.newUntypedWorkflowStub("Chapter1WF2",
                WorkflowOptions.newBuilder()
                        .setWorkflowId("Chapter1 - WF2")
                        .setTaskQueue("c1f2tq")
                        .setSearchAttributes(getCustomSA("Chapter1 WF2"))
                        .setMemo(Collections.singletonMap("MyMemo", "Chapter1WF2Memo"))
                        .build());
        wf2.start("some input2");
        for(int i=0;i<10;i++) {
            wf2.signal("mysignal1", "signalinput");
            wf2.signal("mysignal2", "signalinput2");
            wf2.signal("mysignal3", "signalinput3");
        }

        // 3. start wf (no worker) with run timeout
        WorkflowStub wf3 = client.newUntypedWorkflowStub("Chapter1WF3",
                WorkflowOptions.newBuilder()
                        .setWorkflowId("Chapter1 - WF3")
                        .setTaskQueue("c1f3tq")
                        .setWorkflowRunTimeout(Duration.ofSeconds(10))
                        .setSearchAttributes(getCustomSA("Chapter1 WF3"))
                        .setMemo(Collections.singletonMap("MyMemo", "Chapter1WF3Memo"))
                        .build());
        wf3.start("some input3");

        // 4. start wf (no worker) with run and exec timeout
        WorkflowStub wf4 = client.newUntypedWorkflowStub("Chapter1WF4",
                WorkflowOptions.newBuilder()
                        .setWorkflowId("Chapter1 - WF4")
                        .setTaskQueue("c1f4tq")
                        .setWorkflowRunTimeout(Duration.ofSeconds(20))
                        .setWorkflowExecutionTimeout(Duration.ofSeconds(10))
                        .setSearchAttributes(getCustomSA("Chapter1 WF4"))
                        .setMemo(Collections.singletonMap("MyMemo", "Chapter1WF4Memo"))
                        .build());
        wf4.start("some input4");

        // 4. start wf (no worker) with run and exec timeout and retries
        WorkflowStub wf5 = client.newUntypedWorkflowStub("Chapter1WF5",
                WorkflowOptions.newBuilder()
                        .setWorkflowId("Chapter1 - WF5")
                        .setTaskQueue("c1f5tq")
                        .setWorkflowRunTimeout(Duration.ofSeconds(10))
                        .setWorkflowExecutionTimeout(Duration.ofSeconds(40))
                        .setRetryOptions(RetryOptions.newBuilder()
                                .build())
                        .setSearchAttributes(getCustomSA("Chapter1 WF5"))
                        .setMemo(Collections.singletonMap("MyMemo", "Chapter1WF5Memo"))
                        .build());
        wf5.start("some input5");
    }

    private static Map<String, Object> getCustomSA(String value) {
        return ImmutableMap.of("CustomTextField", value);
    }
}
