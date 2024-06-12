package com.rooxchicken.overload.Tasks;

import com.rooxchicken.overload.Overload;

public abstract class Task
{
    private Overload plugin;
    public int id;

    private int tick = 0;
    public int tickThreshold = 1;
    public boolean cancel = false;

    public Task(Overload _plugin) { plugin = _plugin; }

    public void tick()
    {
        tick++;
        if(tick < tickThreshold-1)
            return;

        run();
        tick = 0;
    }

    public void run() {}
    public void onCancel() {}
}
