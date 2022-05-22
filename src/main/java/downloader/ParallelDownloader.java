package downloader;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ParallelDownloader extends Downloader {

    int count = 0;
    @Override
    public int process(List<String> urls) throws InterruptedException, ExecutionException {
        var exeService = Executors.newSingleThreadExecutor();
        var tasks = new HashSet<Callable<String>>();


        for (String url : urls) tasks.add(() -> saveUrl2File(url));

        var invokeTasks = exeService.invokeAll(tasks);

        for (var invokedTask : invokeTasks)
            if (invokedTask.get() != null)
                count++;

        exeService.shutdown();

        return count;
    }
}
