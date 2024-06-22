package io.github.xfacthd.rsctrlunit.common.emulator.interpreter;

import io.github.xfacthd.rsctrlunit.RedstoneControllerUnit;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.util.*;
import java.util.concurrent.*;

public final class InterpreterThreadPool
{
    private static final int TASK_INTERVAL_MS = 1;
    private static final Map<Interpreter, Future<?>> ACTIVE_INTEPRETERS = new IdentityHashMap<>();
    private static MinecraftServer currentServer = null;
    private static ScheduledExecutorService executors = null;

    public static void init()
    {
        NeoForge.EVENT_BUS.addListener(InterpreterThreadPool::onServerStarting);
        NeoForge.EVENT_BUS.addListener(InterpreterThreadPool::onServerStopped);
    }

    public static void addInterpreter(Interpreter interpreter)
    {
        Objects.requireNonNull(currentServer, "No server present!");
        Future<?> future = executors.scheduleAtFixedRate(new InterpreterTask(interpreter), 0, TASK_INTERVAL_MS, TimeUnit.MILLISECONDS);
        ACTIVE_INTEPRETERS.put(interpreter, future);
    }

    public static void removeInterpreter(Interpreter interpreter)
    {
        Future<?> future = ACTIVE_INTEPRETERS.remove(interpreter);
        Objects.requireNonNull(future, "Tried to remove unregistered interpreter");
        future.cancel(false);
    }

    private static void onServerStarting(ServerStartingEvent event)
    {
        currentServer = event.getServer();
        executors = Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory());
    }

    private static void onServerStopped(ServerStoppedEvent event)
    {
        executors.shutdownNow();
        try
        {
            if (!executors.awaitTermination(1000, TimeUnit.MILLISECONDS))
            {
                RedstoneControllerUnit.LOGGER.error("Interpreter thread pool failed to shut down");
            }
        }
        catch (InterruptedException ignored) { }
        executors = null;
        currentServer = null;
    }



    private record InterpreterTask(Interpreter interpreter) implements Runnable
    {
        @Override
        public void run()
        {
            if (!currentServer.isPaused())
            {
                if (!interpreter().isPaused() || interpreter.isStepRequested())
                {
                    interpreter.run();
                }
            }
        }
    }
}
