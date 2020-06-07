package org.cloudbus.cloudsim.examples.RLcontainer;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.container.core.PowerContainer;
import org.cloudbus.cloudsim.container.schedulers.ContainerCloudletScheduler;

import java.util.ArrayList;
import java.util.List;

public class DeadlineContainer extends Container {

    private double cloudletDeadline = -1.0;
    private double estimateTimeToFinish = -1.0;
    private double estimateFinishTime = -1.0;

    public DeadlineContainer(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size,
                             String containerManager, ContainerCloudletScheduler containerCloudletScheduler, double schedulingInterval, double deadline) {
        super(id, userId, mips, numberOfPes, ram, bw, size, containerManager, containerCloudletScheduler, schedulingInterval);
        setCloudletDeadline(deadline);
    }

    public double getDeadline(){
        return this.cloudletDeadline;
    }

    public double getEstimateTimeToFinish(){ return this.estimateTimeToFinish; }

    public void setEstimateTimeToFinish(long cloudletLength){
        this.estimateTimeToFinish = cloudletLength / (getMips() * getNumberOfPes());
    }

    public void setCloudletDeadline(double deadline){
        this.cloudletDeadline = deadline;
    }

    public void setEstimateFinishTime(double time){
        this.estimateFinishTime = time;
    }

    public double getEstimateFinishTime(){
        return this.estimateFinishTime;
    }

    @Override
    public List<Double> getCurrentRequestedMips() {
        List<Double> currentRequestedMips = new ArrayList<>();

        for (int i = 0; i < getNumberOfPes(); i++) {
            currentRequestedMips.add(getMips());
        }
        return currentRequestedMips;
    }
}
