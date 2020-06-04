package org.cloudbus.cloudsim.examples.RLcontainer;

import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.container.schedulers.ContainerCloudletScheduler;

public class DeadlineContainer extends Container {

    private double cloudletDeadline = -1;
    private double finishTime = -1;

    public DeadlineContainer(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size,
            String containerManager, ContainerCloudletScheduler containerCloudletScheduler, double schedulingInterval) {
        super(id, userId, mips, numberOfPes, ram, bw, size, containerManager, containerCloudletScheduler, schedulingInterval);
    }

    public DeadlineContainer(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size,
                             String containerManager, ContainerCloudletScheduler containerCloudletScheduler, double schedulingInterval, double deadline) {
        super(id, userId, mips, numberOfPes, ram, bw, size, containerManager, containerCloudletScheduler, schedulingInterval);
        setDeadline(deadline);
    }

    public double getDeadline(){
        return this.cloudletDeadline;
    }

    public void setDeadline(double deadline){
        this.cloudletDeadline = deadline;
    }

    public void setFinishTime(long cloudletLength){
        this.finishTime = cloudletLength / (this.getNumberOfPes() * this.getMips());
    }

    public double getFinishTime(){
        return this.finishTime;
    }
}
